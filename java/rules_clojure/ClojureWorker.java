package rules_clojure;

import com.google.devtools.build.lib.worker.WorkerProtocol.WorkRequest;
import com.google.devtools.build.lib.worker.WorkerProtocol.WorkResponse;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.Reader;
import java.io.Writer;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Vector;
import org.projectodd.shimdandy.ClojureRuntimeShim;

// Clojure is a compiled language. When loading source files or
// evaling, the Clojure compiler generates a .class file, and then
// loads it via standard Java classloaders. During non-AOT operation,
// the .classfile exists in memory without being written to disk.

// When AOT'ing, the compiler writes a .class file that corresponds to the
// name of the source file. The clojure namespace `foo-bar.core` will
// produce a file `foo_bar/core.class`.

// Classloaders form a hierarchy. A classloader first asks its parent
// to load a class, and if the parent can't, the current CL attempts
// to load. In normal clojure operation, `java -cp` creates a
// URLClassloader containing URLs pointing at jars and source
// dirs. `clojure.main` then creates a clojure.lang.DynamicClassLoader
// as a child of the URL classloader. Finally, clojure.lang.Compiler
// creates its own private c.l.DCL.

// Classloaders are also responsible for loading resources, using the
// same hierarchy.

// When executing `(load "foo-bar.core")`, clojure.lang.RT/load looks
// for the resources `foo-bar/core.clj` and `foo_bar/core.class`,
// starting from the classloader that contains 'this'
// clojure.lang.RT. If only the class file is present, it is
// loaded. If only the source exists, it is compiled and then
// loaded. If both are present, the one with the newer file
// modification time is loaded.

// If foo-bar.core was AOT'd, it will be loaded
// by the URLClassloader (because the .classfile exists). If it had to
// be compiled, it will be loaded by the compiler's DCL.

// In the JVM, classes are not unique, they are unique _per
// classloader_. Two classes with the same name in different
// classloaders will not be identical, which breaks protocols, among
// other things.

// DCL contains a static class cache, shared by all instances in the
// same classloader (static class variables are shared among all
// instances of the same class, _from the same classloader_)

// When compiling, defprotocol and deftype/defrecord create new
// classes. If a compile reloads a protocol, that breaks all existing
// users of the protocol loaded by the DCL or a child CL. If the class
// is loaded in two separate classloaders, that will break some users.

// If an AOT'd ns contains a protocol or deftype, the resulting
// classfile should appear in exactly one jar (if the classfiles
// appear in multiple jars, there's a chance both definitions could
// get loaded, and then some users will break).

// When loading an AOT'd use of a protocol, the
// definition must be AOT'd and on the classpath (because otherwise
// the definition will be loaded from source, and the source and
// consumer protocol definitions will be in separate classloaders, and therefore
// be not=

// We want to keep the worker up and incrementally load code in the
// same worker, because reloading the environment on every namespace
// is slow.

// therefore: create a mostly-persistent classloader containing all
// jars that compile requests have asked for. When AOT'ing, keeping
// both the source and AOT classes in the classloader hierarchy around
// can cause us to violate one of the above rules, so periodically
// we'll have to discard the classloader and start over.

// the clojure compiler works by binding *compile-files* true and then
// calling `load`. `load` looks for either the source file or
// .class. If the .class file is present and has a newer file
// modification time, it is loaded as a normal java class. Otherwise
// if the src file is present the compiler runs, and .class files are
// produced as a side effect of the load. If the .class file is
// loaded, the compiler will not run and no .class files will be
// produced.

// rules-clojure.jar has dependencies to implement non-transitive
// compilation. rules clojure also wants to be AOT'd, for speed. It is
// possible for dependencies to conflict between rules-clojure and
// client code. Therefore, use two classloaders, to create separate
// environments: one to generate the compilation script and assemble
// jars, and a second to do compilation.

class ClojureCompileRequest {
  String[] compile_classpath;
  String[] jar_classpath;
}

class ClojureWorker {

  public static void main(String[] args) throws Exception {
    if (args.length > 0 && args[0].equals("--persistent_worker")) {
      persistentWorkerMain(args);
    } else {
      ephemeralWorkerMain(args);
    }
  }

  public static void persistentWorkerMain(String[] args) throws Exception {
    System.err.println("ClojureWorker persistentWorkerMain");
    PrintStream real_stdout = System.out;
    PrintStream real_stderr = System.err;
    InputStream stdin = System.in;
    ByteArrayOutputStream outStream = new ByteArrayOutputStream();
    PrintStream out = new PrintStream(outStream, true, "UTF-8");

    Reader stdin_reader = new InputStreamReader(stdin);
    Writer stdout_writer = new OutputStreamWriter(out);

    System.setOut(out);

    try {
      while (true) {
        outStream.reset();

        WorkRequest request = WorkRequest.parseDelimitedFrom(stdin);

        // The request will be null if stdin is closed.  We're
        // not sure if this happens in TheRealWorld™ but it is
        // useful for testing (to shut down a persistent
        // worker process).
        if (request == null) {
          real_stderr.println("null request, break");
          break;
        }

        try {
          System.setErr(out);
          processRequest(request);
        } catch (Throwable e) {
          e.printStackTrace(real_stderr);
          throw e;
        } finally {
          System.setErr(real_stderr);
          String out_str = outStream.toString();
          if (out_str.length() > 0) {
            real_stderr.println("worker stderr:" + out_str);
          }
        }
        out.flush();

        WorkResponse.newBuilder()
            .setExitCode(0)
            .setOutput(outStream.toString())
            .build()
            .writeDelimitedTo(real_stdout);
      }
    } catch (Throwable t) {
      real_stderr.println(t.getMessage());
      t.printStackTrace(real_stderr);
      throw t;
    }
  }

  public static ClojureRuntimeShim jar_runtime = null;

  public static void ensureJarRuntime(ClojureCompileRequest compile_request)
      throws Exception {
    if (jar_runtime != null) {
      return;
    }
    Vector<URL> urls = new Vector<>();
    for (String path : compile_request.jar_classpath) {
      urls.add(new File(path).toURI().toURL());
    }
    URLClassLoader class_loader = new URLClassLoader(
        urls.toArray(new URL[0]), ClojureWorker.class.getClassLoader());
    jar_runtime = ClojureRuntimeShim.newRuntime(class_loader, "jar-worker");
    jar_runtime.require("rules-clojure.jar");
  }

  public static ClojureRuntimeShim
  getCompileRuntime(ClojureCompileRequest compile_request) throws Exception {
    Vector<URL> urls = new Vector<>();
    for (String path : compile_request.compile_classpath) {
      urls.add(new File(path).toURI().toURL());
    }
    URLClassLoader class_loader = new URLClassLoader(
        urls.toArray(new URL[0]), ClassLoader.getSystemClassLoader());
    return ClojureRuntimeShim.newRuntime(class_loader, "compile-worker");
  }

  public static void processRequest(WorkRequest work_request) throws Exception {
    Gson gson =
        new GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_DASHES)
            .create();
    ClojureCompileRequest compile_request = gson.fromJson(
        work_request.getArguments(0), ClojureCompileRequest.class);

    ensureJarRuntime(compile_request);
    ClojureRuntimeShim compile_runtime = getCompileRuntime(compile_request);

    String json = work_request.getArguments(0);
    Object compile_script;
    try {
      compile_script = jar_runtime.invoke(
          "rules-clojure.jar/get-compilation-script-json", json);
    } catch (Throwable t) {
      System.err.println("during `get-compilation-script`:" + json);
      throw t;
    }

    Object read_script =
        compile_runtime.invoke("clojure.core/read-string", compile_script);

    try {
      compile_runtime.invoke("clojure.core/eval", read_script);
      jar_runtime.invoke("rules-clojure.jar/create-jar-json",
                         work_request.getArguments(0));
    } catch (Throwable t) {
      System.err.println("req:" + json);
      System.err.println("script:" + read_script.toString());
      throw t;
    }
    compile_runtime.close();
  }

  public static void ephemeralWorkerMain(String[] args) {
    System.err.println("ClojureWorker ephemeral");
  }
}

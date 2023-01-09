load("//:rules.bzl", "clojure_binary", "clojure_library")

def _add_deps_edn(repository_ctx):
    # repository_ctx.delete(repository_ctx.path("deps.edn"))
    repository_ctx.symlink(
        repository_ctx.path(repository_ctx.attr.deps_edn),
        repository_ctx.path("deps.edn"),
    )

def aliases_str(aliases):
    return str("[" + " ".join([(":%s" % (a)) for a in aliases]) + "]")

def _install_scripts(repository_ctx):
    repository_ctx.file(
        repository_ctx.path("scripts/BUILD.bazel"),
        executable = True,
        content = """
package(default_visibility = ["//visibility:public"])

java_binary(name="gen_srcs",
    main_class="rules_clojure.gen_build",
    runtime_deps=["@rules_clojure//src/rules_clojure:libgen_build"],
    args=["srcs",
          ":deps-edn-path", "{deps_edn_path}",
          ":repository-dir", "{repository_dir}",
          ":deps-build-dir", "{deps_build_dir}",
          ":deps-repo-tag", "{deps_repo_tag}",
          ":aliases", "\\"{aliases}\\""],
    data=["@{workspace_name}{deps_edn_label}"])

 """.format(
            deps_repo_tag = "@" + repository_ctx.attr.name,
            deps_edn_label = repository_ctx.attr.deps_edn,
            deps_edn_path = repository_ctx.path(repository_ctx.attr.deps_edn),
            repository_dir = repository_ctx.path("repository"),
            deps_build_dir = repository_ctx.path(""),
            aliases = aliases_str(repository_ctx.attr.aliases),
            workspace_name = repository_ctx.attr.deps_edn.workspace_name,
        ),
    )

def _symlink_repository(repository_ctx):
    maven_cache_dir = repository_ctx.os.environ["HOME"] + "/.m2/repository"
    repository_ctx.execute(["mkdir", "-p", maven_cache_dir], quiet = False)
    repository_ctx.symlink(maven_cache_dir, repository_ctx.path("repository"))

def _run_gen_build(repository_ctx):
    maven_deps_path = repository_ctx.path(Label("@rules_clojure_maven//:pin.sh")).dirname

    # TODO: find a better way to collect the transitive dependencies
    find_jars = repository_ctx.execute(["find", maven_deps_path, "-name", "*.jar"])
    cp = ":".join(find_jars.stdout.split("\n") +
                  [str(repository_ctx.path("../rules_clojure/src"))])
    args = [
        "java",
        "-Dclojure.main.report=stderr",
        "-classpath",
        cp,
        "clojure.main",
        "-m",
        "rules-clojure.gen-build",
        "deps",
        ":deps-edn-path",
        repository_ctx.path(repository_ctx.attr.deps_edn),
        ":repository-dir",
        repository_ctx.path("repository/"),
        ":deps-build-dir",
        repository_ctx.path(""),
        ":deps-repo-tag",
        "@" + repository_ctx.attr.name,
        ":workspace-root",
        repository_ctx.attr.deps_edn.workspace_root,
        ":aliases",
        aliases_str(repository_ctx.attr.aliases),
    ]
    ret = repository_ctx.execute(args, quiet = False)
    if ret.return_code > 0:
        fail("gen build failed:", ret.return_code, ret.stdout, ret.stderr)

def _tools_deps_impl(repository_ctx):
    _add_deps_edn(repository_ctx)
    _symlink_repository(repository_ctx)
    _install_scripts(repository_ctx)
    _run_gen_build(repository_ctx)
    return None

clojure_tools_deps = repository_rule(
    implementation = _tools_deps_impl,
    local = True,
    attrs = {
        "deps_edn": attr.label(allow_single_file = True),
        "aliases": attr.string_list(default = [], doc = "extra aliases in deps.edn to merge in while resolving deps"),
        "clj_version": attr.string(default = "1.10.1.763"),
        "_rules_clj_src": attr.label_list(default = [
            "@rules_clojure//:src",
            "@rules_clojure_maven//:pin.sh",
        ]),
    },
)

def clojure_gen_srcs(name):
    native.alias(
        name = name,
        actual = "@deps//scripts:gen_srcs",
    )

def clojure_gen_namespace_loader(name, output_filename, output_ns_name, output_fn_name, in_dirs, exclude_nses, platform, deps_edn):
    native.java_binary(
        name = name,
        runtime_deps = ["@rules_clojure//src/rules_clojure:libgen_build"],
        data = [deps_edn],
        main_class = "rules_clojure.gen_build",
        args = [
            "ns-loader",
            ":output-filename",
            output_filename,
            ":output-ns-name",
            output_ns_name,
            ":output-fn-name",
            output_fn_name,
            ":in-dirs",
            "[%s]" % " ".join(["\\\"%s\\\"" % d for d in in_dirs]),
            ":exclude-nses",
            "[%s]" % " ".join(exclude_nses),
            ":platform",
            platform,
        ],
    )

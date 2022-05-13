load("@rules_clojure//:rules.bzl", "clojure_binary", "clojure_library", "clojure_repl", "clojure_test")
load("@rules_java//java:defs.bzl", "java_binary")

clojure_library(
    name = "unconditionally-passing-test-lib",
    # TODO (thor) srs and aot (or only sources) should work, but don't
    #srcs = ["unconditionally-passing-test.clj"],
    #aot = ["tests.unconditionally-passing-test"],
    resources = ["unconditionally_passing_test.clj"],
)

clojure_test(
    name = "unconditionally-passing-test",
    size = "small",
    test_ns = "tests.unconditionally-passing-test",
    deps = [":unconditionally-passing-test-lib"],
)

sh_test(
    name = "unconditionally-passing-test-report",
    size = "small",
    srcs = ["assert/test.sh"],
    args = [
        "$(location :unconditionally-passing-test)",
        """'Testing tests.unconditionally-passing-test'""",
    ],
    data = [":unconditionally-passing-test"],
)

#clojure_test(
#    name = "unconditionally-failing-test",
#    size = "small",
#    # TODO (thor) this shouldn't be here, expects java sources
#    #srcs = ["unconditionally-failing-test.clj"],
#    tags = ["manual"],
#    # TODO (thor) this needed to be added
#    test_ns = "unconditionally-failing-test.clj",
#)
#
#sh_test(
#    name = "unconditionally-failing-test-report",
#    size = "small",
#    srcs = ["assert/test.sh"],
#    args = [
#        "$(location :unconditionally-failing-test)",
#        """'FAIL in (must-fail)'""",
#    ],
#    data = [":unconditionally-failing-test"],
#)
#
#clojure_repl(
#    name = "repl",
#)
#
#sh_test(
#    name = "repl-test",
#    size = "small",
#    srcs = ["assert/repl.sh"],
#    args = [
#        "$(location :repl)",
#        """'(+ 2 2)'""",
#        """'user=> 4'""",
#    ],
#    data = [":repl"],
#)
#
#clojure_library(
#    name = "ns-to-file-path",
#    srcs = ["ns-to-file-path.clj"],
#    # TODO (thor) aot is a required field, but was missing
#    aot = ["ns-to-file-path"],
#)
#
#clojure_repl(
#    name = "ns-to-file-path-repl",
#    deps = [":ns-to-file-path"],
#)
#
#sh_test(
#    name = "ns-to-file-path-repl-test",
#    size = "small",
#    srcs = ["assert/repl.sh"],
#    args = [
#        "$(location :ns-to-file-path-repl)",
#        """'(load "/does/not/match/file_path")\n(all-ns)'""",
#        """'does.not.match.file-path'""",
#    ],
#    data = [":ns-to-file-path-repl"],
#)
#
#clojure_library(
#    name = "library",
#    srcs = ["library.clj"],
#    # TODO (thor) aot is a required field, but was missing
#    aot = ["library"],
#)
#
#clojure_test(
#    name = "library-test",
#    size = "small",
#    # TODO (thor) this shouldn't be here, expects java sources
#    #srcs = ["library-test.clj"],
#    # TODO (thor) this needed to be added
#    test_ns = "library-test",
#    deps = [":library"],
#)
#
#clojure_binary(
#    name = "library-binary",
#    # TODO (thor) 'main' arg isn't there
#    main_class = "tests.library",
#    deps = [":library"],
#)
#
#sh_test(
#    name = "library-binary-test",
#    size = "small",
#    srcs = ["assert/binary.sh"],
#    args = [
#        "$(location :library-binary)",
#        """input""",
#        """'library main input'""",
#    ],
#    data = [":library-binary"],
#)
#
#clojure_repl(
#    name = "library-repl",
#    ns = "tests.library",
#    deps = [":library"],
#)
#
#sh_test(
#    name = "library-repl-test",
#    size = "small",
#    srcs = ["assert/repl.sh"],
#    args = [
#        "$(location :library-repl)",
#        """'(echo "message")'""",
#        """'tests.library=> "library message"'""",
#    ],
#    data = [":library-repl"],
#)
#
#clojure_library(
#    name = "app-transitive",
#    srcs = ["app.clj"],
#    # TODO (thor) aot is a required field, but was missing
#    aot = ["app"],
#    deps = [":library"],
#)
#
#clojure_test(
#    name = "app-transitive-test",
#    size = "small",
#    # TODO (thor) this shouldn't be here, expects java sources
#    #srcs = ["app-test.clj"],
#    # TODO (thor) this needed to be added
#    test_ns = "app-test",
#    deps = [":app-transitive"],
#)
#
#clojure_repl(
#    name = "app-transitive-repl",
#    ns = "tests.app",
#    deps = [":app-transitive"],
#)
#
#sh_test(
#    name = "app-transitive-repl-test",
#    size = "small",
#    srcs = ["assert/repl.sh"],
#    args = [
#        "$(location :app-transitive-repl)",
#        """'(echo "message")'""",
#        """'tests.app=> "app library message"'""",
#    ],
#    data = [":app-transitive-repl"],
#)
#
#clojure_binary(
#    name = "app-transitive-binary",
#    # TODO (thor) 'main' arg isn't there
#    main_class = "tests.app",
#    deps = [":app-transitive"],
#)
#
#sh_test(
#    name = "app-transitive-binary-test",
#    size = "small",
#    srcs = ["assert/binary.sh"],
#    args = [
#        "$(location :app-transitive-binary)",
#        """input""",
#        """'app main library input'""",
#    ],
#    data = [":app-transitive-binary"],
#)
#
#clojure_library(
#    name = "app-bundled",
#    srcs = [
#        "app.clj",
#        "library.clj",
#    ],
#    # TODO (thor) aot is a required field, but was missing
#    aot = [
#        "app",
#        "library",
#    ],
#)
#
#clojure_test(
#    name = "app-bundled-test",
#    size = "small",
#    # TODO (thor) this shouldn't be here
#    # srcs = ["app-test.clj"],
#    # TODO (thor) this needed to be added
#    test_ns = "app-test",
#    deps = [":app-bundled"],
#)
#
#clojure_repl(
#    name = "app-bundled-repl",
#    ns = "tests.app",
#    deps = [":app-bundled"],
#)
#
#sh_test(
#    name = "app-bundled-repl-test",
#    size = "small",
#    srcs = ["assert/repl.sh"],
#    args = [
#        "$(location :app-bundled-repl)",
#        """'(echo "message")'""",
#        """'tests.app=> "app library message"'""",
#    ],
#    data = [":app-bundled-repl"],
#)
#
#clojure_binary(
#    name = "app-bundled-binary",
#    # TODO (thor) this arg isn't there
#    main_class = "tests.app",
#    deps = [":app-bundled"],
#)
#
#sh_test(
#    name = "app-bundled-binary-test",
#    size = "small",
#    srcs = ["assert/binary.sh"],
#    args = [
#        "$(location :app-bundled-binary)",
#        """input""",
#        """'app main library input'""",
#    ],
#    data = [":app-bundled-binary"],
#)
#
## AOT with clojure_java_library
## TODO: Move to aot package
## TODO: Add tests for multiple namespaces and multiple dependencies
## TODO: Do we need multiple namespaces and dependencies since clojure compiles everything transitively?
#
## TODO (thor) rules does not seem to exist
##clojure_java_library(
##    name = "library-java",
##    namespaces = ["tests.library"],
##    deps = [":library"],
##)
##
##java_binary(
##    name = "library-java-binary",
##    main_class = "tests.library",
##    runtime_deps = [":library-java"],
##)
##
##sh_test(
##    name = "library-java-binary-test",
##    size = "small",
##    srcs = ["assert/binary.sh"],
##    args = [
##        "$(location :library-java-binary)",
##        """input""",
##        """'library main input'""",
##    ],
##    data = [":library-java-binary"],
##)
#
## TODO (thor) does not seem to exist
##clojure_java_library(
##    name = "app-bundled-java",
##    namespaces = ["tests.app"],
##    deps = [":app-bundled"],
##)
##
##clojure_test(
##    name = "app-bundled-java-test",
##    size = "small",
##    srcs = ["app-java-test.clj"],
##    # TODO (thor) this needed to be added
##    test_ns = "app-java-test",
##    deps = [":app-bundled-java"],
##)
#
## TODO (thor) dep had to be commented out
##java_binary(
##    name = "app-bundled-java-binary",
##    main_class = "aot.CompiledAppClass",
##    runtime_deps = [":app-bundled-java"],
##)
#
## TODO (thor) dep had to be commented out
##sh_test(
##    name = "app-bundled-java-binary-test",
##    size = "small",
##    srcs = ["assert/binary.sh"],
##    args = [
##        "$(location :app-bundled-java-binary)",
##        """input""",
##        """'app main library input'""",
##    ],
##    data = [":app-bundled-java-binary"],
##)
#
## TODO (thor) don't see where clojure_java_library would be defined?
##clojure_java_library(
##    name = "app-transitive-java",
##    namespaces = ["tests.app"],
##    deps = [":app-transitive"],
##)
##
##clojure_test(
##    name = "app-transitive-java-test",
##    size = "small",
##    srcs = ["app-java-test.clj"],
##    # TODO (thor) this needed to be added
##    test_ns = "app-java-test",
##    deps = [":app-transitive-java"],
##)
##
##java_binary(
##    name = "app-transitive-java-binary",
##    main_class = "aot.CompiledAppClass",
##    runtime_deps = [":app-transitive-java"],
##)
##
##sh_test(
##    name = "app-transitive-java-binary-test",
##    size = "small",
##    srcs = ["assert/binary.sh"],
##    args = [
##        "$(location :app-transitive-java-binary)",
##        """input""",
##        """'app main library input'""",
##    ],
##    data = [":app-transitive-java-binary"],
##)

load("//:rules.bzl", "clojure_binary", "clojure_library", "clojure_repl", "clojure_test")
load("@rules_java//java:defs.bzl", "java_binary")

clojure_library(
    name = "unconditionally-passing-test-lib",
    resources = ["unconditionally_passing_test.clj"],
    resource_strip_prefix = "tests",
)

clojure_test(
    name = "unconditionally-passing-test",
    size = "small",
    deps = [":unconditionally-passing-test-lib"],
    test_ns = "rules-clojure.unconditionally-passing-test",
)

sh_test(
    name = "unconditionally-passing-test-report",
    size = "small",
    srcs = ["assert/test.sh"],
    args = [
        "$(location :unconditionally-passing-test)",
        "rules-clojure.unconditionally-passing-test",
        """'Testing rules-clojure.unconditionally-passing-test'""",
    ],
    data = [":unconditionally-passing-test"],
)

clojure_library(
    name = "unconditionally-failing-test-lib",
    resources = ["unconditionally_failing_test.clj"],
    resource_strip_prefix = "tests",
)

clojure_test(
    name = "unconditionally-failing-test",
    size = "small",
    tags = ["manual"],
    test_ns = "rules-clojure.unconditionally-failing-test",
    deps = [":unconditionally-failing-test-lib"]
)

sh_test(
    name = "unconditionally-failing-test-report",
    size = "small",
    srcs = ["assert/test.sh"],
    args = [
        "$(location :unconditionally-failing-test)",
        "rules-clojure.unconditionally-failing-test",
        """'FAIL in (must-fail)'""",
    ],
    data = [":unconditionally-failing-test"],
)

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

clojure_library(
    name = "ns-to-file-path",
    resources = ["ns-to-file-path.clj"],
)

clojure_repl(
    name = "ns-to-file-path-repl",
    deps = [":ns-to-file-path"],
)

sh_test(
    name = "ns-to-file-path-repl-test",
    size = "small",
    srcs = ["assert/repl.sh"],
    args = [
        "$(location :ns-to-file-path-repl)",
        """'Could not locate does/not/match/file-path'""",
    ],
    data = [":ns-to-file-path-repl"],
)

clojure_library(
    name = "library",
    srcs = ["library.clj"],
    aot = ["rules-clojure.library"],
    resource_strip_prefix = "tests",
)

clojure_library(
    name = "library-test-lib",
    resources = ["library_test.clj"],
    resource_strip_prefix = "tests",
)

clojure_test(
    name = "library-test",
    size = "small",
    resources = ["library_test.clj"],
    test_ns = "rules-clojure.library-test",
    deps = [
        ":library",
        ":library-test-lib"
        ],
)

clojure_binary(
    name = "library-binary",
    main_class = "rules_clojure.library",
    deps = [":library"],
)

sh_test(
    name = "library-binary-test",
    size = "small",
    srcs = ["assert/binary.sh"],
    args = [
        "$(location :library-binary)",
        """input""",
        """'library main input'""",
    ],
    data = [":library-binary"],
)

#clojure_repl(
#    name = "library-repl",
#    ns = "rules-clojure.library",
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
#        """'rules-clojure.library=> "library message"'""",
#    ],
#    data = [":library-repl"],
#)

clojure_library(
    name = "app-transitive",
    srcs = ["app.clj"],
    aot = ["rules-clojure.app"],
    deps = [":library"],
    resource_strip_prefix = "tests",
)

clojure_library(
    name = "app-transitive-test-lib",
    resources = ["app_test.clj"],
    deps = [":library"],
    resource_strip_prefix = "tests",
)

clojure_test(
    name = "app-transitive-test",
    size = "small",
    deps = [":app-transitive",
            ":app-transitive-test-lib"],
    test_ns = "rules-clojure.app-test",
)

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

clojure_binary(
    name = "app-transitive-binary",
    main_class = "aot/CompiledAppClass",
    deps = [":app-transitive"],
)

sh_test(
    name = "app-transitive-binary-test",
    size = "small",
    srcs = ["assert/binary.sh"],
    args = [
        "$(location :app-transitive-binary)",
        """input""",
        """'app main library input'""",
    ],
    data = [":app-transitive-binary"],
)

clojure_library(
    name = "app-bundled",
    srcs = [
        "app.clj",
        "library.clj",
    ],
    aot = [
        "rules-clojure.app",
        "rules-clojure.library",
        ],
    resource_strip_prefix = "tests",
)

clojure_library(
    name = "app-bundled-test-lib",
    resources = [
        "app_test.clj",
    ],
    resource_strip_prefix = "tests",
)

clojure_test(
    name = "app-bundled-test",
    size = "small",
    deps = [
        ":app-bundled",
        ":app-bundled-test-lib",
        ],
    test_ns = "rules-clojure.app-test",
)

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

clojure_binary(
    name = "app-bundled-binary",
    main_class = "aot/CompiledAppClass",
    deps = [":app-bundled"],
)

sh_test(
    name = "app-bundled-binary-test",
    size = "small",
    srcs = ["assert/binary.sh"],
    args = [
        "$(location :app-bundled-binary)",
        """input""",
        """'app main library input'""",
    ],
    data = [":app-bundled-binary"],
)

# AOT with clojure_java_library
# TODO: Move to aot package
# TODO: Add tests for multiple namespaces and multiple dependencies
# TODO: Do we need multiple namespaces and dependencies since clojure compiles everything transitively?

clojure_library(
    name = "library-aot",
    aot = ["rules-clojure.library"],
    srcs = ["library.clj"],
    resource_strip_prefix = "tests",
)

java_binary(
    name = "library-java-binary",
    main_class = "rules_clojure.library",
    runtime_deps = [":library-aot"],
)

sh_test(
    name = "library-java-binary-test",
    size = "small",
    srcs = ["assert/binary.sh"],
    args = [
        "$(location :library-java-binary)",
        """input""",
        """'library main input'""",
    ],
    data = [":library-java-binary"],
)

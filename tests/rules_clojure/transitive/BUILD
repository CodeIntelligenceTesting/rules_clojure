load("@rules_clojure//:rules.bzl", "clojure_library", "clojure_test")

clojure_library(
    name = "app",
    srcs = ["app.clj"],
    aot = ["rules-clojure.transitive.app"],
    deps = ["//tests/rules_clojure/transitive/greeter"],
    resource_strip_prefix = "tests",
)

clojure_library(
    name = "app_test_lib",
    resources = ["test.clj"],
    resource_strip_prefix = "tests",
)

clojure_test(
    name = "test",
    size = "small",
    deps = [":app",
            ":app_test_lib"],
    test_ns = "rules-clojure.transitive.app",
)

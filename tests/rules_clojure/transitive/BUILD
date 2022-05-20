load("@rules_clojure//:rules.bzl", "clojure_library", "clojure_test")

clojure_library(
    name = "app",
    srcs = ["app.clj"],
    aot = ["rules-clojure.transitive.app"],
    resource_strip_prefix = "tests",
    deps = ["//tests/rules_clojure/transitive/greeter"],
)

clojure_library(
    name = "app_test_lib",
    resource_strip_prefix = "tests",
    resources = ["test.clj"],
)

clojure_test(
    name = "test",
    size = "small",
    test_ns = "rules-clojure.transitive.app",
    deps = [
        ":app",
        ":app_test_lib",
    ],
)

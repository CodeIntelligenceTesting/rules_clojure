#!/bin/bash

set -eux pipefail

bazel build //...
bazel test //... --test_output=errors

echo "Running tests for example 'default'"
cd examples/setup/default

bazel build //...
bazel test //... --test_output=errors

echo "Running tests for example 'gen_deps'"
cd ../gen_deps

bazel build //...
bazel test //... --test_output=errors

load(":runtime_definitions_test.bzl", "runtime_definitions_test_suite")

package(
    default_applicable_licenses = ["//:j2cl_license"],
    licenses = ["reciprocal"],
)

config_setting(
    name = "optimized",
    values = {"compilation_mode": "opt"},
)

runtime_definitions_test_suite(
    name = "runtime_definitions_test",
)

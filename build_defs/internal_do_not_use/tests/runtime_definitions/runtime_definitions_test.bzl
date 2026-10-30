"""Tests runtime definitions through the public browser test macros."""

load("@bazel_skylib//lib:unittest.bzl", "asserts", "unittest")
load("//build_defs:rules.bzl", "j2cl_test", "j2wasm_test")

def _forwarding_test_impl(ctx):
    env = unittest.begin(ctx)
    asserts.equals(env, ctx.attr.expected, ctx.attr.actual)
    return unittest.end(env)

_forwarding_test = unittest.make(
    _forwarding_test_impl,
    attrs = {
        "actual": attr.string_dict(),
        "expected": attr.string_dict(),
    },
)

def _forwarding_case(name, test_macro, junit, expected, **kwargs):
    subject = name + "_subject"
    test_macro(
        name = subject,
        srcs = ["RuntimeDefinitionsTest.java"],
        test_class = "example.RuntimeDefinitionsTest",
        deps = [junit],
        browsers = ["//build_defs/internal_do_not_use/browser:chrome-linux"],
        tags = ["manual"],
        **kwargs
    )

    # The generated HTML rule is the final recipient of the runtime definitions.
    html_rules = [
        rule
        for rule in native.existing_rules().values()
        if rule.get("test_file_js") == subject + "_bin.js"
    ]
    if len(html_rules) != 1:
        fail("Expected one generated HTML rule for " + subject)
    _forwarding_test(
        name = name,
        actual = html_rules[0].get("runtime_defines", {}),
        expected = expected,
    )

def runtime_definitions_test_suite(name):
    """Checks defaults, string values, and selectors for both test backends."""
    tests = []
    for backend, test_macro, junit in [
        ("j2cl", j2cl_test, "//:junit"),
        ("j2wasm", j2wasm_test, "//:junit-j2wasm"),
    ]:
        for case, definitions in [
            ("strings", {
                "example.environment": "production",
                "example.enabled": "false",
            }),
            ("selected", select({
                ":optimized": {
                    "example.environment": "production",
                    "example.enabled": "false",
                },
                "//conditions:default": {
                    "example.environment": "development",
                    "example.enabled": "true",
                },
            })),
        ]:
            test_name = name + "_" + backend + "_" + case
            _forwarding_case(test_name, test_macro, junit, definitions, runtime_defines = definitions)
            tests.append(":" + test_name)

        test_name = name + "_" + backend + "_default"
        _forwarding_case(test_name, test_macro, junit, {})
        tests.append(":" + test_name)

    native.test_suite(name = name, tests = tests)

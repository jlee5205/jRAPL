BENCHMARK_SRCS = [
    "JCarbonCallback.java",
    "JCarbonPlugin.java",
]

java_library(
    name = "benchmarks",
    srcs = glob(["*.java"], exclude = BENCHMARK_SRCS),
    deps = [
        "//benchmarks/src/main/java/jcarbon/benchmarks/util",
        "//src/jcarbon/src/main/java/jcarbon",
        "//src/jcarbon/src/main/java/jcarbon/cpu/eflect",
        "//src/jcarbon/src/main/java/jcarbon/emissions",
    ],
)

java_binary(
    name = "dacapo",
    main_class = "Harness",
    srcs = ["JCarbonCallback.java"],
    deps = [
        ":benchmarks",
        "//src/jcarbon/src/main/java/jcarbon",
        "@dacapo",
    ],
    args = [
        "--callback jcarbon.benchmarks.JCarbonCallback",
        "--no-validation",
    ]
)

java_binary(
    name = "renaissance",
    main_class = "org.renaissance.core.Launcher",
    srcs = ["JCarbonPlugin.java"],
    deps = [
        ":benchmarks",
        "//src/jcarbon/src/main/java/jcarbon",
        "@renaissance//jar",
    ],
    args = [
        "--plugin !jcarbon.benchmarks.JCarbonPlugin",
    ]
)

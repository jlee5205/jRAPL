java_plugin(
    name = "collection_test_jmh_annotation_processor",
    deps = ["@org_openjdk_jmh_jmh_generator_annprocess"],
    processor_class = "org.openjdk.jmh.generators.BenchmarkProcessor",
    visibility = ["//visibility:private"],
)

java_binary(
    name = "msr_update",
    main_class = "jcarbon.benchmarks.cpu.rapl.MsrUpdateBenchmark",
    srcs = ["MsrUpdateBenchmark.java"],
    deps = [
        "//benchmarks/src/main/java/jcarbon/benchmarks/data",
        "//src/jcarbon/src/main/java/jcarbon/cpu/rapl",
        "//src/jcarbon/src/main/java/jcarbon/data",
        "@org_openjdk_jmh_jmh_core",
    ],
    plugins = [":collection_test_jmh_annotation_processor"],
)

java_binary(
    name = "powercap_update",
    main_class = "jcarbon.benchmarks.cpu.rapl.PowercapUpdateBenchmark",
    srcs = ["PowercapUpdateBenchmark.java"],
    deps = [
        "//benchmarks/src/main/java/jcarbon/benchmarks/data",
        "//src/jcarbon/src/main/java/jcarbon/cpu/rapl",
        "//src/jcarbon/src/main/java/jcarbon/data",
        "@org_openjdk_jmh_jmh_core",
    ],
    plugins = [":collection_test_jmh_annotation_processor"],
)

alias(
    name = "jcarbon",
    actual = "//src/jcarbon/src/main/java/jcarbon",
)

java_binary(
    name = "rapl_smoke_test",
    main_class = "jcarbon.cpu.rapl.RaplSmokeTest",
    runtime_deps = [":jcarbon"],
)

java_binary(
    name = "rapl_monitor",
    main_class = "jcarbon.cpu.rapl.RaplMonitor",
    runtime_deps = [":jcarbon"],
)

java_binary(
    name = "cpufreq_monitor",
    main_class = "jcarbon.cpu.freq.CpuFreqMonitor",
    runtime_deps = [":jcarbon"],
)

alias(
    name = "emissions_data",
    actual = "//src/main/resources/emissions/WorldIntensity.csv",
)

load("@bazel_tools//tools/build_defs/repo:http.bzl", "http_archive", "http_jar")

http_archive(
    name = "dacapo",
    urls = ["https://clerk-deps.s3.amazonaws.com/dacapo.zip"],
)

RULES_JVM_EXTERNAL_TAG = "3.3"
RULES_JVM_EXTERNAL_SHA = "d85951a92c0908c80bd8551002d66cb23c3434409c814179c0ff026b53544dab"

http_archive(
    name = "rules_jvm_external",
    strip_prefix = "rules_jvm_external-%s" % RULES_JVM_EXTERNAL_TAG,
    sha256 = RULES_JVM_EXTERNAL_SHA,
    url = "https://github.com/bazelbuild/rules_jvm_external/archive/%s.zip" % RULES_JVM_EXTERNAL_TAG,
)

load("@rules_jvm_external//:defs.bzl", "maven_install")
maven_install(
    name = "org_openjdk_jmh_jmh_core",
    artifacts = [
      "org.openjdk.jmh:jmh-core:1.27",
    ],
    repositories = ["https://repo1.maven.org/maven2"],
)
maven_install(
    name = "org_openjdk_jmh_jmh_generator_annprocess",
    artifacts = [
      "org.openjdk.jmh:jmh-generator-annprocess:1.27"
    ],
    repositories = ["https://repo1.maven.org/maven2"],
)

http_jar(
  name = "renaissance",
  urls = [
    "https://github.com/renaissance-benchmarks/renaissance/releases/download/v0.15.0/renaissance-gpl-0.15.0.jar"
  ],
)

maven_install(
    artifacts = [
      "org.json:json:20231013",
    ],
    generate_compat_repositories = True,
    repositories = [
        "https://repo.maven.apache.org/maven2/",
    ],
)
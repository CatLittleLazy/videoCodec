# BUILD 加载【foo.bzl】文件中的【foo_binary】方法
load(":foo.bzl", "foo_binary")
load(":foo.bzl", "hello_world")

# 打印日志
print("BUILD file")

# 调用方法, name后名称为build名，并非入参，可以理解为gradle中的task或bp文件中的二进制name名称,
foo_binary(
    name = "bin1",
    username = "Alice",
#    srcs = ["foo.bzl"],
)

# 再次调用方法
foo_binary(name = "bin2")

# 该文件中存在 bin1、bin2两个可执行task
# bazel build bin1-->执行bin1
# bazel build bin1-->执行bin1

# bazel build :all 执行全部方法
# bazel query :all 查询BUILD中所有可执行方法
# bazel cquery :all 查询并打印方法唯一标识

# 使用模板生成cc文件, 生成文件：bazel-bin/myBazelTest/hello.cc
hello_world(
    name = "hello",
    username = "Alice",
    # 输入文件
    template = "file.cc.tpl",
)

# cc_binary为内置方法，用于生成可执行文件，生成文件：bazel-bin/myBazelTest/hello_bin
cc_binary(
    name = "hello_bin",
    # task名称
    srcs = [":hello"]
)
def _foo_binary_impl(ctx):
    # 获取task name的名称
    out = ctx.actions.declare_file(ctx.label.name)
    print("analyzing", ctx.label.name);
    # 写文件操作
    ctx.actions.write(
        output = out, # 文件名
        # 文件内容: Hello + {}, build文件中定义的username
        content = "Hello {}\n".format(ctx.attr.username),
    )
    # 告知 Bazel 文件是规则的输出，而不是规则实现中使用的临时文件
    # 生成文件路径： bazel-bin/myBazelTest/bin1(执行task为bin1时)
    return [DefaultInfo(files = depset([out]))]

foo_binary = rule(
    implementation = _foo_binary_impl,
    attrs = {
        "username": attr.string(),
    },
)

print("bzl file evaluation")

def _hello_world_impl(ctx):
    out = ctx.actions.declare_file(ctx.label.name + ".cc")
    ctx.actions.expand_template(
        output = out,
        template = ctx.file.template,
        substitutions = {"{NAME" : ctx.attr.username},
    )
    return [DefaultInfo(files = depset([out]))]

hello_world = rule(
    implementation = _hello_world_impl,
    attrs = {
        "username" : attr.string(default = "unknown person"),
        "template" : attr.label(
            allow_single_file = [".cc.tpl"],
            mandatory = True,
        ),
    },
)
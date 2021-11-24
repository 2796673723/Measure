let url;

function newUser() {
  $("#dlg").dialog("open").dialog("center").dialog("setTitle", "添加人员");
  $("#fm").form("clear");
  url = "./add_person";
}

function editUser() {
  const row = $("#dg").datagrid("getSelected");
  if (row) {
    $("#dlg").dialog("open").dialog("center").dialog("setTitle", "编辑人员");
    $("#fm").form("load", row);
    url = "./update_person?id=" + row.id;
  }
}

function saveUser() {
  $("#fm").form("submit", {
    url: url,
    iframe: false,
    onSubmit: function () {
      return $(this).form("validate");
    },
    success: function (result) {
      let res = JSON.parse(result);
      if (res.status) {
        $("#dlg").dialog("close"); // close the dialog
        $("#dg").datagrid("reload"); // reload the user data
      } else {
        $.messager.alert({
          title: "操作提示",
          msg: res.msg ?? "失败",
          icon: "error",
          width: 200,
        });
      }
    },
  });
}

function destroyUser() {
  const row = $("#dg").datagrid("getSelected");
  if (row) {
    $.messager.defaults = { ok: "是", cancel: "否" };
    $.messager.confirm({
      width: 200,
      title: "操作提醒！",
      msg: "确定是否删除该用户?",
      ok: "是",
      cancel: "否",
      fn: function (r) {
        if (r) {
          $.post(
            "./delete_person",
            { id: row.id },
            function (result) {
              if (result.status) {
                $("#dg").datagrid("reload"); // reload the user data
              } else {
                $.messager.alert({
                  title: "操作提示",
                  msg: "删除失败！",
                  icon: "error",
                  width: 200,
                });
              }
            },
            "json"
          );
        }
      },
    });
  }
}

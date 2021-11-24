let url;
let formItem;
let formBody;

function Download() {
  let row = $("#dg").datagrid("getSelected");
  if (row) {
    window.location.href = `./download_information?prefix=${row.path}`;
  }
}

function uploadFile() {
  $("#f-form").dialog("open").dialog("center").dialog("setTitle", "上传文件");
  let fm = $("#fm");
  fm.form("clear");
  fm.form("load", { prefix: $("#cc").combobox("getValue") });
  url = "./upload_information";
  formItem = "#fm";
  formBody = "#f-form";
}

function saveEdit() {
  $(formItem).form("submit", {
    url: url,
    iframe: false,
    onsubmit: function () {
      return $(this).form("valida");
    },
    success: function (result) {
      let res = JSON.parse(result);
      if (res.status) {
        $(formBody).dialog("close"); // close the dialog
        $("#dg").datagrid("reload"); // reload the user data
      } else {
        $.messager.alert({
          title: "操作提示",
          msg: res.msg ?? "失败",
          icon: "error",
          width: 200,
        });
        // $.messager.alert("操作提示", res.msg ?? "失败", "error");
      }
    },
  });
}

function deleteFile() {
  const row = $("#dg").datagrid("getSelected");
  console.log(row);
  if (row) {
    $.messager.confirm({
      width: 200,
      title: "操作提醒！",
      msg: "是否删除该文件或文件夹?",
      ok: "是",
      cancel: "否",
      fn: function (r) {
        if (r) {
          $.post(
            "./delete_information",
            { prefix: row.path },
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

function preview() {
  let row = $("#dg").datagrid("getSelected");
  window.open(`./preview_information?prefix=${row.path}`);
}

window.onload = () => {
  $("#cc").combobox({
    url: "./get_information_total",
    valueField: "id",
    textField: "name",
    onChange: function (newValue, oldValue) {
      $("#dg").datagrid({
        url: `./get_information?prefix=${newValue}`,
        rownumbers: true,
        idField: "id",
        pagination: true,
        singleSelect: true,
        columns: [
          [
            { title: "名称", field: "name", width: "45%" },
            { title: "大小", field: "size", width: "20%" },
            { title: "上传日期", field: "lastModified", width: "35%" },
          ],
        ],
      });
    },
  });
};

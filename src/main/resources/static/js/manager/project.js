let url;
let formItem;
let formBody;

function Download() {
  let row = $("#tb").datagrid("getSelected");
  if (row) {
    window.location.href = `./download_project?prefix=${row.path}`;
  }
}

function uploadFile() {
  $("#f-form").dialog("open").dialog("center").dialog("setTitle", "上传文件");
  let fm = $("#fm");
  fm.form("clear");
  url = "./upload_project";
  formItem = "#fm";
  formBody = "#f-form";
}

// function uploadAssignFile() {
//   let row = $("#tb").datagrid("getSelected");
//   if (row.path.endsWith("/")) {
//     $("#file-form")
//       .dialog("open")
//       .dialog("center")
//       .dialog("setTitle", "上传文件");
//     let fam = $("#fam");
//     fam.form("clear");
//     fam.form("load", { prefix: row.path });
//     url = "./upload_assign_project";
//     formItem = "#fam";
//     formBody = "#file-form";
//   } else {
//     $.messager.alert("操作提示", "请选择项目名！", "question");
//   }
// }

function uploadAssignFile() {
  $("#file-form")
    .dialog("open")
    .dialog("center")
    .dialog("setTitle", "上传文件");
  let fam = $("#fam");
  fam.form("clear");
  url = "./upload_assign_project";
  formItem = "#fam";
  formBody = "#file-form";
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
        console.log(res);
        $(formBody).dialog("close"); // close the dialog
        $("#tb").treegrid("reload"); // reload the user data
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

function deleteFile() {
  const row = $("#tb").treegrid("getSelected");
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
            "./delete_project",
            { prefix: row.path },
            function (result) {
              if (result.status) {
                $("#tb").treegrid("reload"); // reload the user data
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
  let row = $("#tb").treegrid("getSelected");
  if (row.lastModified !== "") {
    window.open(`./preview_project?prefix=${row.path}`);
  } else {
    $.messager.show({
      title: "错误",
      msg: "选择的不是文件",
    });
  }
}

window.onload = () => {
  $("#tb").treegrid({
    url: `./get_project`,
    rownumbers: false,
    idField: "id",
    treeField: "name",
    columns: [
      [
        { title: "名称", field: "name", width: "45%" },
        { title: "大小", field: "size", width: "20%" },
        { title: "上传日期", field: "lastModified", width: "35%" },
      ],
    ],
  });
};

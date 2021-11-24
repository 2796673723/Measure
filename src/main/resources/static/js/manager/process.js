let url;
let formItem = "#fm";
let end_data = {};

function addProcess() {
  $("#dlg").dialog("open").dialog("center").dialog("setTitle", "添加");
  let fm = $("#fm");
  fm.form("clear");
  fm.form("load", { type: $("#cc").combobox("getValue") });
  url = "./add_process";
}

function editProcess() {
  const row = $("#dg").datagrid("getSelected");
  $("#dlg").dialog("open").dialog("center").dialog("setTitle", "编辑");
  let fm = $("#fm");
  fm.form("clear");
  fm.form("load", { ...row, type: $("#cc").combobox("getValue") });
  url = "./update_process?id=" + row.id;
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
        $("#dlg").dialog("close"); // close the dialog
        initProcessDiv(end_data, $("#cc").combobox("getValue"));
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

function deleteProcess() {
  const row = $("#dg").datagrid("getSelected");
  console.log(row);
  if (row) {
    $.messager.confirm({
      width: 200,
      title: "操作提醒！",
      msg: "是否删除该信息?",
      ok: "是",
      cancel: "否",
      fn: function (r) {
        if (r) {
          $.post(
            "./delete_process",
            { id: row.id },
            function (result) {
              if (result.status) {
                initProcessDiv(end_data, $("#cc").combobox("getValue"));
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

// "平面-CPⅠ网", "平面-CPⅡ网", "平面-CPⅢ网",  "水准-线上加密水准网", "水准-线下水准基点网","水准-CPⅢ高程网"
function loadData() {
  return [
    { id: 0, name: "平面-CPⅠ网" },
    { id: 1, name: "平面-CPⅡ网" },
    { id: 2, name: "平面-CPⅢ网" },
    { id: 3, name: "水准-CPⅢ高程网" },
    { id: 4, name: "水准-线上加密水准网" },
    { id: 5, name: "水准-线下水准基点网" },
  ];
}

function loadColumns() {
  return [
    { title: "起始里程", field: "start", width: "35%" },
    { title: "终点里程", field: "end", width: "35%" },
    { title: "创建时间", field: "date", width: "30%" },
  ];
}

async function loadProcessList(typeId) {
  let res = await fetch(`./list_process?type=${typeId}`, { method: "POST" });
  return await res.json();
}

async function initProcessDiv(end_data, typeId) {
  let data = await loadProcessList(typeId);
  let process = document.getElementById("process");
  process.innerHTML = createProcess(end_data, data).context;
}

window.onload = async () => {
  let res = await fetch(`./get_project_info`, { method: "POST" });
  end_data = await res.json();
  $("#cc").combobox({
    valueField: "id",
    textField: "name",
    data: loadData(),
    onSelect: function (rec) {
      initProcessDiv(end_data, rec.id);
      $("#dg").datagrid({
        url: `./list_process?type=${rec.id}`,
        rownumbers: true,
        idField: "id",
        pagination: true,
        singleSelect: true,
        columns: [loadColumns()],
      });
    },
  });
};

let types = [
  { id: 0, name: "平面-CPⅠ网" },
  { id: 1, name: "平面-CPⅡ网" },
  { id: 2, name: "平面-CPⅢ网" },
  { id: 3, name: "水准-CPⅢ高程网" },
  { id: 4, name: "水准-线上加密水准网" },
  { id: 5, name: "水准-线下水准基点网" },
];

async function init(projectId) {
  let res = await fetch(`./get_project_info?id=${projectId}`, {
    method: "POST",
  });
  res = await res.json();
  let end_data = { start: res.start, end: res.end };

  let promise_array = [];
  for (const type of types) {
    let promise = fetch(`./list_process?type=${type.id}&id=${projectId}`, {
      method: "POST",
    });
    promise_array.push(promise.then((res) => res.json()));
  }

  Promise.all(promise_array).then((values) => {
    let content = `<b style="font-size: 16px">各项进度</b><br>`;
    let total_percent = 0;
    for (const index in values) {
      let { context, percent } = createProcess(end_data, values[index]);
      total_percent += percent;
      content +=
        `<b style="font-size: 14px">${types[index].name}:</b><br>` +
        context +
        `<br>`;
    }
    let per = total_percent / types.length;
    console.log(per);
    let project_info = `
    <div style="font-size: 14px">
      <b style="font-size: 16px">项目信息</b><br>
      <b>起始里程：</b>${res.start}<br>
      <b>终点里程：</b>${res.end}<br>
      <b>总体进度：</b><br>
      ${createTotalProcess(per)}
    </div><br>`.trim();
    document.getElementById("process").innerHTML =
      project_info + "<hr>" + content;
  });
}

window.onload = async () => {
  $("#cc").combobox({
    url: "./get_project_total",
    valueField: "id",
    textField: "name",
    onSelect: function (rec) {
      init(rec.id);
    },
  });
};

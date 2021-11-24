function createProcess(end_data, data_s) {
  function mergeData(data = []) {
    //sort
    let sort_data = [...data].sort((a, b) => a.start - b.start);
    //merge
    let merge_data = [];
    let pre_start = sort_data[0].start;
    let pre_end = sort_data[0].end;
    for (let i = 1; i < sort_data.length; i++) {
      let data = sort_data[i];
      if (data.start > pre_end) {
        merge_data.push({ start: pre_start, end: pre_end });
        pre_start = data.start;
        pre_end = data.end;
        continue;
      }
      pre_end = Math.max(pre_end, data.end);
    }
    merge_data.push({ start: pre_start, end: pre_end });
    return merge_data;
  }

  function computeProcess(end_data, data_s) {
    let res = [];
    let len = end_data.end - end_data.start;
    console.log(len);

    let pre_end = end_data.start;
    for (const data of data_s) {
      let d = ((data.start - pre_end) * 100) / len;
      if (d > 0) {
        res.push({ type: false, d: d });
      }
      d = ((data.end - data.start) * 100) / len;
      res.push({ type: true, d: d, start: data.start, end: data.end });
      pre_end = data.end;
    }
    let d = ((end_data.end - pre_end) * 100) / len;
    if (d > 0) {
      res.push({ type: false, d: d });
    }
    console.log(res);
    return res;
  }

  function getDiv(item) {
    if (item.type) {
      let d = item.d.toFixed(2);
      let content = `${d}%\n起始里程: ${item.start}\n终点里程: ${item.end}`;
      return `<div title="${content}" style="background: dodgerblue;"></div>`;
    } else {
      return `<div style="background: lightgrey;"></div>`;
    }
  }

  data_s = data_s.length > 0 ? mergeData(data_s) : data_s;
  let res = computeProcess(end_data, data_s);

  let items = "";
  let grids = "";
  let percent = 0;
  for (const item of res) {
    percent += item.type ? item.d : 0;
    items += getDiv(item);
    grids += ` ${item.d}%`;
  }

  let processDiv = document.createElement("div");
  processDiv.style.cssText = `display: grid;width: 100%;height: 25px;`;
  processDiv.style.cssText += `grid-template-columns:${grids}`;
  processDiv.innerHTML = items;
  let per_text = percent.toFixed(2);
  let text_class =
    "display: flex;align-items: center;justify-content: center;text-align: justify;font-size:15px";
  let context = `<div style="width: 100%;display: grid;grid-template-columns: 10% 90%">
         <div style="${text_class}">${per_text}%</div>
         <div>${processDiv.outerHTML}</div>
       </div>`.trim();
  return { context: context, percent: percent };
}

function createTotalProcess(value) {
  function getDiv(item) {
    if (item.type) {
      return `<div style="background: blue;"></div>`;
    } else {
      return `<div style="background: lightgrey;"></div>`;
    }
  }

  let processDiv = document.createElement("div");
  processDiv.style.cssText = `display: grid;width: 100%;height: 25px;`;
  let grids = ` ${value}% ${100 - value}%`;
  processDiv.style.cssText += `grid-template-columns:${grids}`;

  let items_str = "";
  let items = [{ type: true }, { type: false }];
  for (const item of items) {
    items_str += getDiv(item);
  }
  processDiv.innerHTML = items_str;

  let text_class =
    "display: flex;align-items: center;justify-content: center;text-align: justify;font-size:15px";
  return `<div style="width: 100%;display: grid;grid-template-columns: 10% 90%">
            <div style="${text_class}">${value.toFixed(2)}%</div>
            <div>${processDiv.outerHTML}</div>
          </div>`.trim();
}

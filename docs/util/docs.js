// node获取某个相对目录下的所有文档树结构，参数为目录路径，返回值为文档树结构
// const fs = require("fs");
import fs from "node:fs";
import path from "node:path";

function getDocsTree() {
  return new Promise((resolve, reject) => {
    const docsTree = {};
    const docs = fs.readdirSync(path.join(__dirname, "../docs/" + path));
    docs.forEach((doc) => {
      const docPath = path.join(__dirname, "../docs/" + path + "/" + doc);
      const stat = fs.statSync(docPath);
      if (stat.isDirectory()) {
        docsTree[doc] = getDocsTree(path + "/" + doc);
      } else {
        docsTree[doc] = docPath;
      }
    });
    resolve(docsTree);
  }).catch((err) => {
    console.log(err);
  });
}

export default getDocsTree;

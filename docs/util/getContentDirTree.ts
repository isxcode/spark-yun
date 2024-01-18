import { DirNode } from "~/interface/docs.interface";
function getContentDirTree(htmlData: string): DirNode[] {
  if (!htmlData) {
    return [];
  }
  let hDomList = htmlData.match(/<h[1-6]{1}[^>]*>([\s\S]*?)<\/h[1-6]{1}>/g);
  if (!Array.isArray(hDomList)) {
    return [];
  }
  const dirTree: DirNode[] = [];
  let preDir: DirNode | undefined;
  hDomList.forEach((hDom, index) => {
    const startIndex = hDom.indexOf('id="');
    const endIndex = hDom.indexOf('">');
    if (startIndex != -1 && endIndex != -1) {
      const domId = hDom.slice(startIndex + 4, endIndex);
      const re = /<h([1-6]{1})[^>]*>([\s\S]*?)<\/h[1-6]{1}>/;
      let hLevel = Number(hDom.replace(re, "$1"));
      let titleContent = hDom.replace(re, "$2");
      let title = titleContent.replaceAll(/<([\s\S]*?)>|<\/([\s\S]*?)>/g, "");
      const currentDir: DirNode = {
        id: index + 1,
        title,
        domId,
        hLevel,
      };
      if (preDir) {
        if (currentDir.hLevel > preDir.hLevel) {
          currentDir.pid = preDir.id;
        } else if (currentDir.hLevel < preDir.hLevel) {
          currentDir.pid = 0;
        } else {
          currentDir.pid = preDir.pid;
        }
      } else {
        currentDir.pid = 0;
      }
      preDir = currentDir;
      dirTree.push(currentDir);
    }
  });
  dirTree.forEach((item) => {
    if (item.pid !== 0) {
      const findParent = dirTree.find((i) => i.id === item.pid);
      if (findParent) {
        if (!Array.isArray(findParent.children)) {
          findParent.children = [];
        }
        findParent.children.push(item);
      }
    }
  });
  return dirTree.filter((i) => i.pid === 0);
}

export default getContentDirTree;

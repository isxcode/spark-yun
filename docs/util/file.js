import MarkdownIt from "markdown-it";
import matter from "gray-matter";


let getPostRawList = function () {
  debugger;
  const context = require.context("./../docs", true, /\.md$/);
  console.log(context, "context");
  const keys = context.keys();
  if (postCount == 0) {
    postCount = keys.length;
  }
  let postRawList = [];
  if (keys == 0) {
    return postRawList;
  }
  keys.forEach((key) => {
    const raw = context(key).default;
    postRawList.push(raw);
  });
  return postRawList.sort(sortPostByDate);
};

export default getPostRawList;

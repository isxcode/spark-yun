export {
  cloneDeep,
  clone,
  concat,
  map,
  debounce,
  findIndex,
  find,
  some,
  forEach,
  reduce,
  random,
  assign,
  omit,
  attempt,
  uniq,
  filter,
  includes,
  orderBy,
  isEmpty,
  isEqual,
  isNaN,
  // isNull,
  isString,
  isUndefined,
  merge,
} from "lodash-es";

const toString = Object.prototype.toString;

// 判断值是否为某个类型
export function is(val: unknown, type: string) {
  return toString.call(val) === `[object ${type}]`;
}

// 是否已定义
export const isDef = <T = unknown>(val?: T): val is T => {
  return typeof val !== "undefined";
};

// 是否未定义
export const isUnDef = <T = unknown>(val?: T): val is T => {
  return !isDef(val);
};

// // 是否为null
export function isNull(val: unknown): val is null {
  return val === null;
}

// 是否为null并且未定义
export function isNullAndUnDef(val: unknown): val is null | undefined {
  return isUnDef(val) && isNull(val);
}

// 是否为null或者未定义
export function isNullOrUnDef(val: unknown): val is null | undefined {
  return isUnDef(val) || isNull(val);
}

// 是否为函数
export function isFunction<T = Function>(val: unknown): val is T {
  return is(val, "Function");
}

// 是否为对象
export const isObject = (val: any): val is Record<any, any> => {
  return val !== null && is(val, "Object");
};

// 是否为日期
export function isDate(val: unknown): val is Date {
  return is(val, "Date");
}

// 是否为数值
export function isNumber(val: unknown): val is number {
  return is(val, "Number");
}

// 是否为字符串
// export function isString(val: unknown): val is string {
//   return is(val, 'String');
// }

// 是否为布尔
export function isBoolean(val: unknown): val is boolean {
  return is(val, "Boolean");
}

// 是否为数组
export function isArray(val: any): val is Array<any> {
  return val && Array.isArray(val);
}

// 是否为AsyncFunction
export function isAsyncFunction<T = any>(val: unknown): val is Promise<T> {
  return is(val, "AsyncFunction");
}

// 是否为promise
export function isPromise<T = any>(val: unknown): val is Promise<T> {
  return is(val, "Promise") && isObject(val) && isFunction(val.then) && isFunction(val.catch);
}

// 是否为客户端
export const isClient = () => {
  return typeof window !== "undefined";
};

// 是否为服务端
export const isServer = typeof window === "undefined";

// 是否为window
export const isWindow = (val: any): val is Window => {
  return typeof window !== "undefined" && is(val, "Window");
};

// 是否为元素
export const isElement = (val: unknown): val is Element => {
  return isObject(val) && !!val.tagName;
};

// 是否为图片节点
export function isImageDom(o: Element) {
  return o && ["IMAGE", "IMG"].includes(o.tagName);
}

// 是否为完整的url
export function isUrl(url: string) {
  return /(^http|https:\/\/)/g.test(url);
}

export function setObjToUrlParams(baseUrl: string, obj: any): string {
  let parameters = "";
  let url = "";
  for (const key in obj) {
    parameters += key + "=" + encodeURIComponent(obj[key]) + "&";
  }
  parameters = parameters.replace(/&$/, "");
  if (/\?$/.test(baseUrl)) {
    url = baseUrl + parameters;
  } else {
    url = baseUrl.replace(/\/?$/, "?") + parameters;
  }
  return url;
}

// 返回URL中全部或者某个查询参数的值
export function getUrlParam(url: string, key?: string): string | null {
  const urlObj = new URL(url);
  return key ? urlObj.searchParams.get(key) : null;
  // const arr: string[] = url.substring(url.indexOf('?') + 1).split('&');
  // const obj: any = {};
  // for (let i = 0; i < arr.length; i++) {
  //   if (arr[i].indexOf('=') < 0) {
  //     obj[arr[i]] = 'undefined';
  //   } else {
  //     const arr2 = arr[i].split('=');
  //     obj[arr2[0]] = decodeURIComponent(arr2[1]);
  //   }
  // }
  // return !key ? obj : obj[key];
}

export function deepMerge<T = any>(src: any = {}, target: any = {}): T {
  let key: string;
  for (key in target) {
    src[key] = isObject(src[key]) ? deepMerge(src[key], target[key]) : target[key];
  }
  return src;
}

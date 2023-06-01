// import { isObject, isString } from '@devopsuse/shared';

const DATE_TIME_FORMAT = "YYYY-MM-DD HH:mm";

export function joinTimestamp<T extends boolean>(join: boolean, restful: T): T extends true ? string : object;

export function joinTimestamp(join: boolean, restful = false): string | object {
  if (!join) {
    return restful ? "" : {};
  }

  const now = new Date().getTime();
  if (restful) {
    return `?_t=${now}`;
  }

  return {
    _t: now,
  };
}

export function formatRequestDate(params: Recordable) {
  if (Object.prototype.toString.call(params) !== "[object Object]") {
    return;
  }

  for (const key in params) {
    if (params[key] && params[key]._isAMomentObject) {
      params[key] = params[key].format(DATE_TIME_FORMAT);
    }
    if (typeof key === "string") {
      const value = params[key];
      if (value) {
        try {
          params[key] = typeof value === "string" ? value.trim() : value;
        } catch (error) {
          throw new Error(error as any);
        }
      }
    }
    if (params[key] instanceof Object) {
      formatRequestDate(params[key]);
    }
  }
}

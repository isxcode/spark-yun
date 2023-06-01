import { AxiosRequestConfig } from "axios";
import { AxiosTransform } from "./axiosTransform";
import { ShowMessage, CheckStatus } from "./handler";

// axios实例化的配置项，在原本的基础之上添加了新的配置
export interface CreateAxiosOptions extends AxiosRequestConfig {
  transform?: AxiosTransform;
  requestOptions?: RequestOptions;
}

// 上传文件
export interface UploadFileParams {
  // 其他参数
  data?: Recordable;
  // 文件参数接口字段名
  name?: string;
  // 文件
  file: File | Blob;
  // 文件名称
  filename?: string;
  [key: string]: any;
}

// 请求的配置项
export interface RequestOptions {
  // 是否返回原生响应头
  isReturnNativeResponse?: boolean;
  // 是否需要对返回数据进行处理
  isTransformResponse?: boolean;
  // 是否将请求参数拼接到url
  joinParamsToUrl?: boolean;
  // 格式化请求参数时间
  formatDate?: boolean;
  // 接口地址
  apiUrl?: string;
  // 请求拼接路径
  urlPrefix?: string;
  // 是否将prefix添加到url
  joinPrefix?: boolean;
  // 是否添加时间戳
  joinTime?: boolean;
  // 忽略重复请求
  ignoreCancelToken?: boolean;
  // 是否携带token
  withToken?: boolean;
  // 是否解析成JSON
  isParseToJson?: boolean;
  // 是否显示提示信息
  isShowMessage?: boolean;
  // 是否显示成功信息
  isShowSuccessMessage?: boolean;
  // 成功的文本信息
  successMessageText?: string;
  // 显示成功信息的函数
  showSuccessMessage: ShowMessage;
  // 是否显示失败信息
  isShowErrorMessage?: boolean;
  // 错误的文本信息
  errorMessageText?: string;
  // 显示错误信息的函数
  showErrorMessage: ShowMessage;
  // 校验状态码
  checkStatus: CheckStatus;
}

// 接口返回结果
export interface Result<T = any> {
  code: number;
  type?: "success" | "error" | "warning";
  message: string;
  msg: string;
  data?: T;
}

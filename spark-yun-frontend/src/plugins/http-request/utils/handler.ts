import { ElMessage } from 'element-plus'

const message = ElMessage

export interface ShowMessage {
  (msg: string): void;
}

export interface CheckStatus {
  (status: number, msg: string, showMsg: ShowMessage): void;
}

// 显示消息
export const showMessage: ShowMessage = (msg) => {
  message.error(msg)
  console.log(msg)
}

// 校验状态
export const checkStatus: CheckStatus = (status, msg, showMsg) => {
  switch (status) {
  case 400:
    showMsg(msg)
    break
  case 401:
    showMsg('用户登录过期!')
    break
  case 403:
    showMsg('用户没有权限!')
    break
  case 404:
    showMsg('网络请求错误，未找到该资源!')
    break
  case 405:
    showMsg('网络请求错误，请求方法未允许!')
    break
  case 408:
    showMsg('网络请求超时!')
    break
  case 500:
    showMsg('服务器错误,请联系管理员!')
    break
  case 501:
    showMsg('网络未实现!')
    break
  case 502:
    showMsg('网络错误!')
    break
  case 503:
    showMsg('服务不可用，服务器暂时过载或维护!')
    break
  case 504:
    showMsg('网络超时!')
    break
  case 505:
    showMsg('http版本不支持该请求!')
    break
  default:
    showMsg(msg)
  }
}

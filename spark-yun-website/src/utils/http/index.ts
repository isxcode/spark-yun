/*
 * @Author: fanciNate
 * @Date: 2023-05-23 17:00:06
 * @LastEditTime: 2023-05-27 16:12:11
 * @LastEditors: fanciNate
 * @Description: In User Settings Edit
 * @FilePath: /zqy-web/src/utils/http/index.ts
 */
import { createAxios } from "@/plugins/http-request";
import router from "@/router";
import { merge } from "../checkType";
import store from "@/store";
import { ElMessage } from "element-plus";
import * as process from "process";

const message = ElMessage;
const storeData: any = store;

export const httpOption = {
  transform: {
    requestInterceptors: (config: any) => {
      config.headers["authorization"] = storeData.state?.authStoreModule?.token;
      config.headers["tenant"] = storeData.state?.authStoreModule?.tenantId;

      return config;
    },
    responseInterceptors: (config: any): any => {
      // getTokenFromResponse(config);

      return config;
    },
  },
  requestOptions: {
    // urlPrefix: 'http://isxcode.com:30211',
    urlPrefix: import.meta.env.VUE_APP_BASE_DOMAIN,
    showSuccessMessage: (msg: string): void => {
      message.success(msg);
    },
    showErrorMessage: (msg: string): void => {
      message.error(msg);
    },
    checkStatus: (status: number, msg: string, showMsg: any): void => {
      try {
        if (status == 401) {
          showMsg("用户登录过期");
          router.push({
            name: "login",
          });
        } else if (status == 403) {
          message.error("许可证无效，请联系管理员");
        } else {
          showMsg(msg);
        }
      } catch (error) {
        console.error("error", error);
        // console.log('err', error);
        // router.replace({
        //     name: LOGIN_NAME,
        //     query: {
        //         redirect: '/home'
        //     }
        // });
      }
    },
  },
  timeout: 30 * 1e3,
};

export const createHttp = (option = {}) => {
  return createAxios(
    merge(
      {},
      {
        ...httpOption,
      },
      {
        ...option,
      }
    )
  );
};

export const http = createHttp();

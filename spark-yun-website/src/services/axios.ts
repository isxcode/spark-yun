import axios, { AxiosRequestConfig, AxiosResponse } from 'axios';
import {message} from "antd";

const axiosInstance = axios.create({

  baseURL: process.env.API_PREFIX_URL,
  timeout: 5000,
  responseType: "json",
  maxContentLength: 2000,
  maxBodyLength: 2000,
  maxRedirects: 1
});

axiosInstance.interceptors.request.use(function (config) {
  return config
}, function (error) {
  return Promise.reject(error)
})

axiosInstance.interceptors.response.use(
  (response: AxiosResponse) => {
    if (response.status === 200) {
      if (response.data.code !== '200') {
        message.error(response.data.msg);
      } else {
        console.log(response.data);
        message.success(response.data.msg);
        return response.data;
      }
    }
  },
  (error: any) => {
    return Promise.reject(error);
  }
);

export default axiosInstance;

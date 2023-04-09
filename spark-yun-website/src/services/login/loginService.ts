import axios from 'axios';
import {LoginReq} from "./req/LoginReq";
import {LoginRes} from "./res/LoginRes";
import axiosInstance from "../axios";

export const loginApi = async (data: LoginReq): Promise<LoginRes> => {
  const response = await axiosInstance.post<LoginRes>('/usr/login', data);
  return response.data;
};

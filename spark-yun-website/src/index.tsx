import React from 'react'

import './global.less'
import ReactDOM from 'react-dom/client'
import MainRoute from "./routes/MainRoute";
import {ConfigProvider} from "antd";
import {THEME_COLOR} from "./global";

ReactDOM.createRoot(
  document.getElementById('root') as HTMLElement
).render(
  <React.StrictMode>
    <ConfigProvider
      theme={{
        token: {
          colorPrimary: THEME_COLOR
        },
      }}
    >
      <MainRoute/>
    </ConfigProvider>
  </React.StrictMode>
);


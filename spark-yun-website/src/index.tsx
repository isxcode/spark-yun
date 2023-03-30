import React from 'react'
import './global.scss'

import ReactDOM from 'react-dom/client'
import App from './layouts/App'
import { ConfigProvider } from 'antd'

ReactDOM.createRoot(document.getElementById('root') as HTMLElement).render(
  <React.StrictMode>
    <ConfigProvider
      theme={{
        token: { colorPrimary: '#fc5533' }
      }}>
      <App />
    </ConfigProvider>
  </React.StrictMode>
)

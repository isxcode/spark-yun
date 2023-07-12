import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { viteStaticCopy } from 'vite-plugin-static-copy'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [ vue(), viteStaticCopy({
    targets: [
      {
        src: 'public/*',
        dest: 'static'
      }
    ]
  }
  ) ],
  build: {
    outDir: 'dist',
    assetsDir: 'static',
    copyPublicDir: false,
    manifest: false
  },
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url)),
      '@antv/x6': '@antv/x6/lib',
      '@antv/x6-vue-shape': '@antv/x6-vue-shape/lib'
    }
  },
  css: {
    preprocessorOptions: {
      scss: {
        additionalData: '@import "src/assets/scss/variable/app-variable.scss";'
      }
    }
  }
})

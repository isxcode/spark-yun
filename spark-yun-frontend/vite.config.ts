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
  // optimizeDeps: {
  //   exclude: ['@antv/x6-vue-shape']
  // },
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url)),
      '@antv/x6': '@antv/x6/lib',
      '@antv/x6-vue-shape': '@antv/x6-vue-shape/lib'
    }
    // alias: [
    //   {
    //     find: '@',
    //     replacement: fileURLToPath(new URL('./src', import.meta.url))
    //   },
    //   {
    //     find: '@antv/x6',
    //     replacement: '@antv/x6/dist/x6.js',
    //   },
    //   {
    //     find: '@antv/x6-vue-shape',
    //     replacement: '@antv/x6-vue-shape/lib',
    //   },
    // ]
  },
  css: {
    preprocessorOptions: {
      scss: {
        additionalData: '@import "src/assets/scss/variable/app-variable.scss";'
      }
    }
  }
})

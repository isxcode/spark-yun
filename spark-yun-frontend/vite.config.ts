import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueJsx from '@vitejs/plugin-vue-jsx'
import { viteStaticCopy } from 'vite-plugin-static-copy'

import Components from 'unplugin-vue-components/vite'
import { ElementPlusResolver } from 'unplugin-vue-components/resolvers'
import { readFileSync } from 'node:fs'

// 读取VERSION文件
const version = readFileSync('./VERSION', 'utf-8').trim()

// https://vitejs.dev/config/
export default defineConfig({
  define: {
    __APP_VERSION__: JSON.stringify(version)
  },
  server: {
    host: '0.0.0.0'
  },
  plugins: [ 
    vue(), 
    vueJsx(), 
    viteStaticCopy({
      targets: [
        {
          src: 'public/*',
          dest: 'static'
        }
      ]
    }),
    Components({
      extensions: ['js', 'jsx', 'ts', 'tsx', 'vue'],
      include: [/\.vue$/, /\.vue\?vue/, /\.md$/, /\.tsx$/, /\.jsx$/],
      resolvers: [ElementPlusResolver({
        importStyle: 'sass'
      })]
    }),
    {
      name: 'singleHMR',
      handleHotUpdate({ modules }) {
        modules.map(m => {
          // m.importedModules = new Set()
          m.importers = new Set()
        })

        return modules
      }
    }
  ],
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
        additionalData: `@use "@/assets/styles/variable.scss" as *;`
      }
    }
  }
})

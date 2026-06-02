import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueJsx from '@vitejs/plugin-vue-jsx'
import { viteStaticCopy } from 'vite-plugin-static-copy'

import Components from 'unplugin-vue-components/vite'
import { ElementPlusResolver } from 'unplugin-vue-components/resolvers'
import { readFileSync } from 'node:fs'

// 读取根目录VERSION文件
const version = readFileSync('../VERSION', 'utf-8').trim()

const getPackageName = (id: string) => {
  const packagePath = id.split('node_modules/').pop()

  if (!packagePath) {
    return ''
  }

  const [scopeOrName, name] = packagePath.split('/')
  return scopeOrName.startsWith('@') ? `${scopeOrName}/${name}` : scopeOrName
}

const getChunkName = (name: string) => name.replace('@', '').replace('/', '-')

const packageChunks = new Set([
  '@babel/runtime',
  '@ctrl/tinycolor',
  '@lezer/common',
  '@lezer/lr',
  '@lezer/python',
  '@sxzz/popperjs-es',
  '@vue/reactivity',
  '@vue/runtime-core',
  '@vue/runtime-dom',
  '@vue/shared',
  '@vueuse/core',
  '@vueuse/shared',
  '@vxe-ui/core',
  'async-validator',
  'axios',
  'bubblesets-js',
  'dayjs',
  'gl-matrix',
  'graphlib',
  'jquery',
  'lodash',
  'lodash-es',
  'ml-matrix',
  'qs',
  'sortablejs'
])

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
      extensions: ['vue'],
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
    manifest: false,
    rollupOptions: {
      output: {
        manualChunks(id) {
          if (!id.includes('node_modules')) {
            return
          }

          const packageName = getPackageName(id)

          if (packageName === '@element-plus/icons-vue') {
            return 'vendor-element-plus-icons'
          }

          if (packageName === '@antv/x6') {
            return 'vendor-antv-x6'
          }

          if (packageName === 'element-plus' || packageName.startsWith('@element-plus/')) {
            return 'vendor-element-plus'
          }

          if (packageName === 'vxe-pc-ui') {
            const component = id.match(/\/es\/components\/([^/]+)/)?.[1]
            return component ? `vendor-vxe-pc-ui-${component}` : 'vendor-vxe-pc-ui'
          }

          if (packageName === 'vxe-table' || packageName === 'xe-utils') {
            return `vendor-${packageName}`
          }

          if (packageName.startsWith('@antv/')) {
            return `vendor-antv-${packageName.replace('@antv/', '')}`
          }

          if (packageName === 'g6-extension-vue' || packageName === 'dagre') {
            return `vendor-${packageName}`
          }

          if (packageName === 'echarts') {
            return 'vendor-echarts'
          }

          if (packageName === 'zrender') {
            return 'vendor-zrender'
          }

          if (packageName.startsWith('@codemirror/') || packageName === 'codemirror' || packageName === 'vue-codemirror6') {
            return `vendor-codemirror-${packageName.replace('@codemirror/', '')}`
          }

          if (packageName === 'jsplumb' || packageName === 'vue-grid-layout' || packageName === 'vuedraggable') {
            return `vendor-${packageName}`
          }

          if (packageName === 'vue' || packageName === 'vue-router' || packageName === 'pinia' || packageName === 'pinia-plugin-persistedstate') {
            return 'vendor-vue'
          }

          if (packageChunks.has(packageName)) {
            return `vendor-${getChunkName(packageName)}`
          }

          return 'vendor'
        }
      }
    },
    chunkSizeWarningLimit: 1100
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

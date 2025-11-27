import { createSvgIconsPlugin } from "vite-plugin-svg-icons";
import path from "path";

export default defineNuxtConfig({
  devtools: { enabled: false },
  build: {
    transpile: ['vueuc']
  },
  // 添加关键资源预加载
  app: {
    head: {
      link: [
        {
          rel: 'preload',
          href: 'https://zhiqingyun-demo.isxcode.com/tools/open/file/bg-0.jpg',
          as: 'image',
          type: 'image/jpeg'
        },
        {
          rel: 'preload',
          href: 'https://zhiqingyun-demo.isxcode.com/tools/open/file/logo.jpg',
          as: 'image',
          type: 'image/jpeg'
        },
        {
          rel: 'preload',
          href: 'https://zhiqingyun-demo.isxcode.com/tools/open/file/product.jpg',
          as: 'image',
          type: 'image/jpeg'
        }
      ]
    }
  },
  // 性能优化配置
  experimental: {
    payloadExtraction: false, // 禁用payload提取以提高首屏加载速度
    inlineSSRStyles: false, // 禁用内联SSR样式以减少HTML大小
  },
  // 渲染配置
  nitro: {
    compressPublicAssets: true, // 启用静态资源压缩
    routeRules: {
      // 首页启用强缓存
      '/': {
        headers: {
          'Cache-Control': 'public, max-age=3600, s-maxage=3600',
          'Vary': 'Accept-Language'
        }
      },
      // 静态资源长期缓存
      '/assets/**': { headers: { 'Cache-Control': 'public, max-age=31536000, immutable' } },
      '/images/**': { headers: { 'Cache-Control': 'public, max-age=31536000, immutable' } }
    }
  },
  modules: [
    "@nuxt/content",
    "@pinia/nuxt",
    "@vueuse/nuxt",
    "@element-plus/nuxt",
    "@nuxtjs/tailwindcss",
    "nuxt-lodash",
    "@nuxtjs/i18n",
    "nuxtjs-naive-ui"
  ],
  pinia: {
    autoImports: ["defineStore"],
  },
  plugins: [
    { src: "~/plugins/preload-loading.client.ts", mode: "client" },
    { src: "~/plugins/page-cache.client.ts", mode: "client" },
    { src: "~/plugins/resource-preloader.client.ts", mode: "client" },
    { src: "~/plugins/svgicon.client.ts" },
    { src: "~/plugins/loading.client.ts" }
  ],
  css: [
    "element-plus/dist/index.css",
    "element-plus/theme-chalk/dark/css-vars.css",
    "~/assets/css/index.scss",
  ],
  vite: {
    plugins: [
      createSvgIconsPlugin({
        iconDirs: [path.resolve(process.cwd(), "assets/svg")],
      }),
    ],
    build: {
      rollupOptions: {
        output: {
          // 确保关键组件优先加载
          manualChunks: {
            'loading': ['~/components/loading/TopLoadingBar.vue'],
            'critical': ['~/pages/index.vue', '~/layouts/home.vue']
          }
        }
      }
    },
    // 开发服务器配置
    server: {
      hmr: {
        overlay: false // 禁用错误覆盖层以提高开发体验
      }
    }
  },
  lodash: {
    prefix: "_",
  },
  content: {
    highlight: {
      theme: "dark-plus",
      langs: [
        "bash",
        "java",
        "json",
        "markdown",
        "typescript",
        "yaml",
        "yml",
        "xml",
        "javascript",
        "sql",
        "python",
        "html",
        "css",
        "shell",
        "vue",
        "go",
        "csharp",
        "cpp",
        "swift",
        "dockerfile",
        "ini",
        "toml",
        "powershell",
        "makefile",
        "graphql",
        "log",
        "wikitext",
        "groovy"
      ],
    },
  },
  i18n: {
    locales: [
      { name: "中文", code: "zh", iso: "zh-CN", dir: "ltr" },
      { name: "English", code: "en", iso: "en-US", dir: "ltr" },
      // { name: "日本語", code: "ja", iso: "ja-JP", dir: "ltr" },
      // { name: "한국어", code: "ko", iso: "ko-KR", dir: "ltr" },
      // { name: "Français", code: "fr", iso: "fr-FR", dir: "ltr" },
      // { name: "Deutsch", code: "de", iso: "de-DE", dir: "ltr" },
      // { name: "Español", code: "es", iso: "es-ES", dir: "ltr" },
      // { name: "Русский", code: "ru", iso: "ru-RU", dir: "ltr" },
    ],
    detectBrowserLanguage: false,
    vueI18n: "./locales/i18n.config.ts",
    defaultLocale: "zh",
    strategy: "prefix_and_default",
  },

  compatibilityDate: "2025-07-01",
});

export default defineNuxtConfig({
  devtools: { enabled: true },
  modules: [
    "@element-plus/nuxt",
    "@nuxtjs/tailwindcss",
    "@vueuse/nuxt",
    "@nuxt/content",
  ],
  css: [
    "element-plus/dist/index.css",
    "element-plus/theme-chalk/dark/css-vars.css",
    "~/assets/css/index.scss",
    "github-markdown-css/github-markdown.css",
  ],
});

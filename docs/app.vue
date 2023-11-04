<script setup lang="ts">
const route = useRoute()

useServerSeoMeta({
  ogSiteName: '至轻云'
})

useHead({
  htmlAttrs: {
    lang: 'cz'
  }
})

const colorMode = useColorMode()
colorMode.value = 'light'
const links = [{
  label: 'Documentation',
  icon: 'i-heroicons-book-open-solid',
  to: '/get-started/installation'
}, {
  label: 'Playground',
  icon: 'i-ph-play-duotone',
  to: '/playground'
}, {
  label: 'Releases',
  icon: 'i-heroicons-rocket-launch-solid',
  to: 'https://github.com/nuxt/content/releases',
  target: '_blank'
}]

const { data: files } = useLazyFetch('/api/search.json', {
  default: () => [],
  server: false
})

const { data: nav } = await useAsyncData('navigation', () => fetchContentNavigation())

const navigation = computed(() => {
  const main = nav.value?.filter(item => item._path !== '/v1')
  const v1 = nav.value?.find(item => item._path === '/v1')?.children

  return route.path.startsWith('/v1/') ? v1 : main
})

// Provide
provide('navigation', navigation)
</script>

<template>
  <LayoutHomeHeader />
  <NuxtLayout >
    <NuxtPage />
  </NuxtLayout>
  <!-- <LayoutHomeFooter/> -->
</template>

<template>
  <SpeedInsights />
  <DocsPageLayout>
    <slot />
  </DocsPageLayout>
</template>

<script setup lang="ts">
import { SpeedInsights } from "@vercel/speed-insights/vue";
definePageMeta({});
</script>

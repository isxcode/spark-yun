<template>
  <n-button quaternary v-model="locale" @click="changeLangs">
    {{ currentLocale.name }}
  </n-button>
</template>

<script setup lang="ts">
import type { LocaleObject } from "@nuxtjs/i18n/dist/runtime/composables";
import { NButton } from "naive-ui";

const { locale, locales } = useI18n();
const supportedLocales = locales.value as Array<LocaleObject>;

const router = useRouter();
const switchLocalePath = useSwitchLocalePath();

function changeLangs() {
  const newLocale = locale.value === "en" ? "zh" : "en";
  router.push({ path: switchLocalePath(newLocale) });
}

const currentLocale = computed(() => {
  return supportedLocales.find((l) => l.code === locale.value);
});
</script>

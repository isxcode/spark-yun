<template>
  <div class="lang-switcher">
    <div class="lang-dropdown" @click="toggleDropdown" ref="dropdownRef">
      <div class="lang-button">
        <SvgIcon name="globe" class="globe-icon" />
        <span class="lang-text">{{ currentLocale?.name }}</span>
      </div>
      <div class="lang-options" v-show="isDropdownOpen">
        <div
          v-for="lang in supportedLocales"
          :key="lang.code"
          class="lang-option"
          :class="{ 'active': lang.code === locale }"
          @click="handleLanguageSelect(lang.code)"
        >
          <span class="lang-name">{{ lang.name }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import type { LocaleObject } from "@nuxtjs/i18n/dist/runtime/composables";

const { locale, locales } = useI18n();
const supportedLocales = locales.value as Array<LocaleObject>;

const router = useRouter();
const switchLocalePath = useSwitchLocalePath();

const isDropdownOpen = ref(false);
const dropdownRef = ref<HTMLElement>();

const currentLocale = computed(() => {
  return supportedLocales.find((l) => l.code === locale.value);
});

// 移除国旗图标功能

function toggleDropdown() {
  isDropdownOpen.value = !isDropdownOpen.value;
}

function handleLanguageSelect(key: string) {
  if (key !== locale.value) {
    router.push({ path: switchLocalePath(key) });
  }
  isDropdownOpen.value = false;
}

// 点击外部关闭下拉菜单
function handleClickOutside(event: Event) {
  if (dropdownRef.value && !dropdownRef.value.contains(event.target as Node)) {
    isDropdownOpen.value = false;
  }
}

onMounted(() => {
  document.addEventListener('click', handleClickOutside);
});

onUnmounted(() => {
  document.removeEventListener('click', handleClickOutside);
});
</script>

<style scoped>
.lang-switcher {
  position: relative;
  display: inline-flex;
  align-items: center;
}

.lang-dropdown {
  position: relative;
  cursor: pointer;
}

.lang-button {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 0px 8px;
  height: 32px;
  line-height: 32px;
  text-align: center;
  border-radius: 20px;
  cursor: pointer;
  transition: all 0.3s;
  color: var(--sk-color-font-gray);
  background: transparent;
  border: 1px solid transparent;

  &:hover {
    color: var(--sk-color-font-gray-hover);
  }
}

.globe-icon {
  width: 18px;
  height: 18px;
  color: black;
  flex-shrink: 0;
}

.lang-text {
  white-space: nowrap;
  font-size: 14px;
  color: inherit;
}

.lang-options {
  position: absolute;
  top: 100%;
  right: 0;
  min-width: 160px;
  background: #ffffff;
  border: 1px solid #e1e5e9;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  z-index: 1000;
  overflow: hidden;
  margin-top: 4px;
}

.lang-option {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 10px 12px;
  cursor: pointer;
  transition: all 0.2s ease;
  color: #000;
  font-size: 14px;

  &:hover {
    background-color: #f5f5f5;
    color: #000;
  }

  &.active {
    background-color: rgba(226, 90, 27, 0.1);
    color: var(--sk-color-home-primary, #e25a1b);
    font-weight: 500;
  }
}

.lang-name {
  font-size: 14px;
  white-space: nowrap;
  color: inherit;
}

/* 深色模式适配 */
@media (prefers-color-scheme: dark) {
  .lang-button {
    color: var(--sk-color-font-gray);

    &:hover {
      color: var(--sk-color-font-gray-hover);
    }
  }

  .globe-icon {
    color: black;
  }

  .lang-options {
    background: #ffffff;
    border-color: #e1e5e9;
  }

  .lang-option {
    color: #000;

    &:hover {
      background-color: #f5f5f5;
      color: #000;
    }

    &.active {
      background-color: rgba(226, 90, 27, 0.1);
      color: var(--sk-color-home-primary, #e25a1b);
    }
  }
}
</style>

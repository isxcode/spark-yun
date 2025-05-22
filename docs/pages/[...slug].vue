<template>
  <div>
    <a style="color: #0969da; cursor: pointer;" @click="clickHome">点击刷新</a>
  </div>
</template>

<script setup lang="ts">
function clickHome() {
  const router = useRouter();
  router.push("/");
}
</script>
<template>
  <div class="main">
    <LayoutHomeHeader />
    <slot />
  </div>
</template>

<script lang="ts" setup>
defineComponent("Home");
</script>
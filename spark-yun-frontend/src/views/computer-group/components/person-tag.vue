<template>
  <div class="person-tag">{{ personName }}</div>
</template>

<script setup lang="ts">
const props = withDefaults(defineProps<{
  personName: string
  personUrl: string
}>(), {
  personName: '',
  personUrl: ''
});
</script>

<style scoped lang="scss">
.person-tag {

}
</style>
<template>
  <div class="ellipsis-tooltip">
    <el-tooltip :visible="visible" :content="labelStr" :placement="placement">
      <div
        class="ellipsis-tooltip__wrap"
        @mouseenter="handleMouseenter"
        @mouseleave="handleMouseleave"
      >
        <span class="ellipsis-tooltip__text">{{ labelStr }}</span>
      </div>
    </el-tooltip>
  </div>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { EllipsisTooltipProps, ellipsisTooltipPropsDefault } from './ellipsis-tooltip'

defineOptions({
  name: 'EllipsisTooltip'
})

const props = withDefaults(defineProps<EllipsisTooltipProps>(), ellipsisTooltipPropsDefault)

const labelStr = computed(() => String(props.label))

const visible = ref(false)

function handleMouseenter(event: MouseEvent) {
  visible.value =
    (event.target as HTMLElement).scrollWidth > (event.target as HTMLElement).offsetWidth
}

function handleMouseleave() {
  visible.value = false
}
</script>

<style lang="scss">
.ellipsis-tooltip {
  .ellipsis-tooltip__wrap {
    overflow: hidden;
    white-space: nowrap;
    text-overflow: ellipsis;
  }
}
</style>

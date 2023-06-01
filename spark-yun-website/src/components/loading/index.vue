<template>
  <div class="zqy-loading">
    <template v-if="displayVisable">
      <div class="zqy-loading-img__another" style="opacity: 0.7">
        <div class="loader" />
      </div>
      <span class="loading-text">
        正在加载中<span style="font-size: 18px">{{ points }}</span>
      </span>
    </template>
    <div v-else-if="networkError" class="zqy-loading-network-error">
      <img src="./error-page.png" class="network-error" alt="服务器不稳定，请稍后重试" />
      <span class="zqy-loading-error-text">
        服务器不稳定，请稍后
        <el-button class="zqy-loading-refresh-btn" type="text" @click="handleReflesh">重试</el-button>
      </span>
    </div>
    <slot />
  </div>
</template>

<script lang="ts" setup>
import { defineProps, withDefaults, ref, watch, defineEmits, onMounted, onBeforeUnmount } from "vue";

const props: any = withDefaults(
  defineProps<{
    visible: boolean;
    timer?: number;
    networkError: boolean;
  }>(),
  {
    visible: false,
    timer: 500,
  }
);

const displayVisable = ref(false);
const timestamp = ref(0);
const points = ref(".");
const timeCode = ref(0);

const emit = defineEmits(["loading-done", "loading-refresh"]);

watch(
  () => props.visible,
  (newVal) => {
    if (newVal) {
      timestamp.value = new Date().getTime();
      displayVisable.value = true;
    } else {
      const endTimestamp = new Date().getTime();
      const temp: number = endTimestamp - timestamp.value;
      if (temp >= props.timer) {
        displayVisable.value = false;
        if (props.networkError === false) {
          emit("loading-done");
        }
      } else {
        const timeout = setTimeout(() => {
          displayVisable.value = false;
          clearTimeout(timeout);
          if (props.networkError === false) {
            emit("loading-done");
          }
        }, props.timer - temp);
      }
    }
  }
);

onMounted(() => {
  timeCode.value = setInterval(() => {
    if (points.value === "...") {
      points.value = "";
    } else {
      points.value += ".";
    }
  }, 500);
});

onBeforeUnmount(() => {
  clearInterval(timeCode.value);
  timeCode.value = 0;
});

function handleReflesh() {
  emit("loading-refresh");
}
</script>

<style lang="scss">
.zqy-loading {
  width: 100%;
  height: 100%;
  position: relative;
  .zqy-loading-img {
    position: absolute;
    top: 0;
    bottom: 0;
    left: 0;
    right: 0;
    margin: auto;
    height: 16px;
  }
  .zqy-loading-img__another {
    position: absolute;
    top: 0;
    bottom: 0;
    left: 12px;
    right: 8px;
    background-color: $--app-header-bgColor;
    z-index: 100;

    .loader {
      left: 50%;
      top: 50%;
      position: absolute;
      transform: translate(-50%, -50%);

      color: $--app-primary-color;
      font-size: 10px;
      width: 1em;
      height: 1em;
      border-radius: 50%;
      z-index: 300;
      animation: load4 1.3s infinite linear;

      // font-size: 10px;
      // width: 1em;
      // height: 1em;
      // border-radius: 50%;
      // animation: load5 1.1s infinite ease;
    }
  }
  .loading-text {
    position: absolute;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    margin-top: 58px;
    font-size: 12px;
    color: $--app-primary-color;
  }
  .zqy-loading-network-error {
    position: absolute;
    top: 0;
    bottom: 0;
    left: 12px;
    right: 8px;
    background-color: $--app-header-bgColor;
    z-index: 100;
    display: flex;
    align-items: center;
    flex-direction: column;
    justify-content: center;
    height: 100%;
    font-size: 12px;
    .network-error {
      height: 200px;
      width: auto;
    }
    .zqy-loading-error-text {
      color: $--app-info-color;
      display: flex;
      align-items: center;
    }
    .zqy-loading-refresh-btn {
      font-size: 12px;
      padding: 12px 0 !important;
      color: $--app-primary-color;
    }
  }
}

@keyframes circle {
  from {
    transform: rotate(-180deg);
  }
  to {
    transform: rotate(180deg);
  }
}

@keyframes load4 {
  0%,
  100% {
    box-shadow: 0 -3em 0 0.2em, 2em -2em 0 0em, 3em 0 0 -1em, 2em 2em 0 -1em, 0 3em 0 -1em, -2em 2em 0 -1em, -3em 0 0 -1em, -2em -2em 0 0;
  }
  12.5% {
    box-shadow: 0 -3em 0 0, 2em -2em 0 0.2em, 3em 0 0 0, 2em 2em 0 -1em, 0 3em 0 -1em, -2em 2em 0 -1em, -3em 0 0 -1em, -2em -2em 0 -1em;
  }
  25% {
    box-shadow: 0 -3em 0 -0.5em, 2em -2em 0 0, 3em 0 0 0.2em, 2em 2em 0 0, 0 3em 0 -1em, -2em 2em 0 -1em, -3em 0 0 -1em, -2em -2em 0 -1em;
  }
  37.5% {
    box-shadow: 0 -3em 0 -1em, 2em -2em 0 -1em, 3em 0em 0 0, 2em 2em 0 0.2em, 0 3em 0 0em, -2em 2em 0 -1em, -3em 0em 0 -1em, -2em -2em 0 -1em;
  }
  50% {
    box-shadow: 0 -3em 0 -1em, 2em -2em 0 -1em, 3em 0 0 -1em, 2em 2em 0 0em, 0 3em 0 0.2em, -2em 2em 0 0, -3em 0em 0 -1em, -2em -2em 0 -1em;
  }
  62.5% {
    box-shadow: 0 -3em 0 -1em, 2em -2em 0 -1em, 3em 0 0 -1em, 2em 2em 0 -1em, 0 3em 0 0, -2em 2em 0 0.2em, -3em 0 0 0, -2em -2em 0 -1em;
  }
  75% {
    box-shadow: 0em -3em 0 -1em, 2em -2em 0 -1em, 3em 0em 0 -1em, 2em 2em 0 -1em, 0 3em 0 -1em, -2em 2em 0 0, -3em 0em 0 0.2em, -2em -2em 0 0;
  }
  87.5% {
    box-shadow: 0em -3em 0 0, 2em -2em 0 -1em, 3em 0 0 -1em, 2em 2em 0 -1em, 0 3em 0 -1em, -2em 2em 0 0, -3em 0em 0 0, -2em -2em 0 0.2em;
  }
}

@-webkit-keyframes load5 {
  0%,
  100% {
    box-shadow: 0em -2.6em 0em 0em #ffffff, 1.8em -1.8em 0 0em #efefef, 2.5em 0em 0 0em #efefef, 1.75em 1.75em 0 0em #efefef, 0em 2.5em 0 0em #efefef, -1.8em 1.8em 0 0em #efefef, -2.6em 0em 0 0em #97c1ff, -1.8em -1.8em 0 0em #5794f2;
  }
  12.5% {
    box-shadow: 0em -2.6em 0em 0em #5794f2, 1.8em -1.8em 0 0em #ffffff, 2.5em 0em 0 0em #efefef, 1.75em 1.75em 0 0em #efefef, 0em 2.5em 0 0em #efefef, -1.8em 1.8em 0 0em #efefef, -2.6em 0em 0 0em #efefef, -1.8em -1.8em 0 0em #97c1ff;
  }
  25% {
    box-shadow: 0em -2.6em 0em 0em #97c1ff, 1.8em -1.8em 0 0em #5794f2, 2.5em 0em 0 0em #ffffff, 1.75em 1.75em 0 0em #efefef, 0em 2.5em 0 0em #efefef, -1.8em 1.8em 0 0em #efefef, -2.6em 0em 0 0em #efefef, -1.8em -1.8em 0 0em #efefef;
  }
  37.5% {
    box-shadow: 0em -2.6em 0em 0em #efefef, 1.8em -1.8em 0 0em #97c1ff, 2.5em 0em 0 0em #5794f2, 1.75em 1.75em 0 0em #ffffff, 0em 2.5em 0 0em #efefef, -1.8em 1.8em 0 0em #efefef, -2.6em 0em 0 0em #efefef, -1.8em -1.8em 0 0em #efefef;
  }
  50% {
    box-shadow: 0em -2.6em 0em 0em #efefef, 1.8em -1.8em 0 0em #efefef, 2.5em 0em 0 0em #97c1ff, 1.75em 1.75em 0 0em #5794f2, 0em 2.5em 0 0em #ffffff, -1.8em 1.8em 0 0em #efefef, -2.6em 0em 0 0em #efefef, -1.8em -1.8em 0 0em #efefef;
  }
  62.5% {
    box-shadow: 0em -2.6em 0em 0em #efefef, 1.8em -1.8em 0 0em #efefef, 2.5em 0em 0 0em #efefef, 1.75em 1.75em 0 0em #97c1ff, 0em 2.5em 0 0em #5794f2, -1.8em 1.8em 0 0em #ffffff, -2.6em 0em 0 0em #efefef, -1.8em -1.8em 0 0em #efefef;
  }
  75% {
    box-shadow: 0em -2.6em 0em 0em #efefef, 1.8em -1.8em 0 0em #efefef, 2.5em 0em 0 0em #efefef, 1.75em 1.75em 0 0em #efefef, 0em 2.5em 0 0em #97c1ff, -1.8em 1.8em 0 0em #5794f2, -2.6em 0em 0 0em #ffffff, -1.8em -1.8em 0 0em #efefef;
  }
  87.5% {
    box-shadow: 0em -2.6em 0em 0em #efefef, 1.8em -1.8em 0 0em #efefef, 2.5em 0em 0 0em #efefef, 1.75em 1.75em 0 0em #efefef, 0em 2.5em 0 0em #efefef, -1.8em 1.8em 0 0em #97c1ff, -2.6em 0em 0 0em #5794f2, -1.8em -1.8em 0 0em #ffffff;
  }
}
@keyframes load5 {
  0%,
  100% {
    box-shadow: 0em -2.6em 0em 0em #ffffff, 1.8em -1.8em 0 0em #efefef, 2.5em 0em 0 0em #efefef, 1.75em 1.75em 0 0em #efefef, 0em 2.5em 0 0em #efefef, -1.8em 1.8em 0 0em #efefef, -2.6em 0em 0 0em #97c1ff, -1.8em -1.8em 0 0em #5794f2;
  }
  12.5% {
    box-shadow: 0em -2.6em 0em 0em #5794f2, 1.8em -1.8em 0 0em #ffffff, 2.5em 0em 0 0em #efefef, 1.75em 1.75em 0 0em #efefef, 0em 2.5em 0 0em #efefef, -1.8em 1.8em 0 0em #efefef, -2.6em 0em 0 0em #efefef, -1.8em -1.8em 0 0em #97c1ff;
  }
  25% {
    box-shadow: 0em -2.6em 0em 0em #97c1ff, 1.8em -1.8em 0 0em #7094c9, 2.5em 0em 0 0em #ffffff, 1.75em 1.75em 0 0em #efefef, 0em 2.5em 0 0em #efefef, -1.8em 1.8em 0 0em #efefef, -2.6em 0em 0 0em #efefef, -1.8em -1.8em 0 0em #efefef;
  }
  37.5% {
    box-shadow: 0em -2.6em 0em 0em #efefef, 1.8em -1.8em 0 0em #97c1ff, 2.5em 0em 0 0em #5794f2, 1.75em 1.75em 0 0em #ffffff, 0em 2.5em 0 0em #efefef, -1.8em 1.8em 0 0em #efefef, -2.6em 0em 0 0em #efefef, -1.8em -1.8em 0 0em #efefef;
  }
  50% {
    box-shadow: 0em -2.6em 0em 0em #efefef, 1.8em -1.8em 0 0em #efefef, 2.5em 0em 0 0em #97c1ff, 1.75em 1.75em 0 0em #5794f2, 0em 2.5em 0 0em #ffffff, -1.8em 1.8em 0 0em #efefef, -2.6em 0em 0 0em #efefef, -1.8em -1.8em 0 0em #efefef;
  }
  62.5% {
    box-shadow: 0em -2.6em 0em 0em #efefef, 1.8em -1.8em 0 0em #efefef, 2.5em 0em 0 0em #efefef, 1.75em 1.75em 0 0em #97c1ff, 0em 2.5em 0 0em #5794f2, -1.8em 1.8em 0 0em #ffffff, -2.6em 0em 0 0em #efefef, -1.8em -1.8em 0 0em #efefef;
  }
  75% {
    box-shadow: 0em -2.6em 0em 0em #efefef, 1.8em -1.8em 0 0em #efefef, 2.5em 0em 0 0em #efefef, 1.75em 1.75em 0 0em #efefef, 0em 2.5em 0 0em #97c1ff, -1.8em 1.8em 0 0em #5794f2, -2.6em 0em 0 0em #ffffff, -1.8em -1.8em 0 0em #efefef;
  }
  87.5% {
    box-shadow: 0em -2.6em 0em 0em #efefef, 1.8em -1.8em 0 0em #efefef, 2.5em 0em 0 0em #efefef, 1.75em 1.75em 0 0em #efefef, 0em 2.5em 0 0em #efefef, -1.8em 1.8em 0 0em #97c1ff, -2.6em 0em 0 0em #5794f2, -1.8em -1.8em 0 0em #ffffff;
  }
}
</style>

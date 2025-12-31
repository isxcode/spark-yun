<template>
  <div class="main">
    <div class="module-intro">
      <div class="content">
        <div class="left">
          <div class="name">
            {{ $t("zhi_qing_yun") }}
          </div>
          <div class="slogan">
            {{ $t("enterprise_data_platform") }}
          </div>
          <div class="command">
            <p id="command-shell">docker run -p 8080:8080 isxcode/zhiqingyun</p>
            <SvgIcon
                class="copy-icon"
                name="copy"
                @click="handleCommandCopyClick"
            ></SvgIcon>
          </div>
          <div class="btn-div">
            <div class="guide-btn" @click="handleGuideClick">
              <p>{{ $t("quick_start") }}</p>
            </div>
            <div class="quick-btn" @click="handleQuickClick">
              <p>{{ $t("experience_now") }}</p>
              <SvgIcon name="arrow-right" class="link-right-svg"></SvgIcon>
            </div>
            <div class="movie-btn" @click="toggleVideo">
              <p>{{ $t("video_introduction") }}</p>
            </div>
          </div>
        </div>
        <div class="right">
          <div v-show="!isMobile || showVideo" class="artplayer-app"></div>
        </div>
      </div>
    </div>
    <div class="module-about">
      <div class="content">
        <div class="why-title">
          {{ $t("choose_light_cloud") }}
        </div>
        <div class="why-content">
          {{ $t("light_cloud_description") }}
        </div>
        <div class="why-content-mobile">
          {{ $t("light_cloud_description_mobile") }}
        </div>
        <div class="tech-title">
          {{ $t("related_technologies") }}
        </div>
        <div class="tech-img-div">
          <img class="tech-img" src="https://zhiqingyun-demo.isxcode.com/tools/open/file/t-0.png" alt=""/>
          <img class="tech-img" src="https://zhiqingyun-demo.isxcode.com/tools/open/file/t-1.png" alt=""/>
          <img class="tech-img" src="https://zhiqingyun-demo.isxcode.com/tools/open/file/t-2.png" alt=""/>
          <img class="tech-img" src="https://zhiqingyun-demo.isxcode.com/tools/open/file/t-3.png" alt=""/>
          <img class="tech-img" style="height: 41px;margin-top: 6px" src="https://zhiqingyun-demo.isxcode.com/tools/open/file/t-4.png" alt=""/>
          <img class="tech-img" src="https://zhiqingyun-demo.isxcode.com/tools/open/file/t-5.png" alt=""/>
        </div>
      </div>
    </div>
    <div class="module-feat-left">
      <div class="content">
        <div class="left">
          <img id="zoom" src="https://zhiqingyun-demo.isxcode.com/tools/open/file/p-0.jpg" alt="">
        </div>
        <div class="left-phone">
          <img src="https://zhiqingyun-demo.isxcode.com/tools/open/file/p-0.jpg" alt="">
        </div>
        <div class="right">
          <div class="line-1">{{ $t("coding_capability") }}</div>
          <div class="line-2">{{ $t("job_types_supported") }}</div>
          <div class="line-3" @click="handleQuickClick">
            {{ $t("experience_now") }}
          </div>
        </div>
      </div>
    </div>
    <div class="module-feat-right">
      <div class="content">
        <div class="right-phone">
          <img src="https://zhiqingyun-demo.isxcode.com/tools/open/file/p-1.jpg" alt="">
        </div>
        <div class="left">
          <div class="line-1">{{ $t("job_orchestration") }}</div>
          <div class="line-2">{{ $t("job_support") }}</div>
          <div class="line-3" @click="handleQuickClick">
            {{ $t("experience_now") }}
          </div>
        </div>
        <div class="right">
          <img id="zoom" src="https://zhiqingyun-demo.isxcode.com/tools/open/file/p-1.jpg" alt="">
        </div>
      </div>
    </div>
    <div class="module-feat-left">
      <div class="content">
        <div class="left">
          <img id="zoom" src="https://zhiqingyun-demo.isxcode.com/tools/open/file/p-2.jpg" alt="">
        </div>
        <div class="left-phone">
          <img src="https://zhiqingyun-demo.isxcode.com/tools/open/file/p-2.jpg" alt="">
        </div>
        <div class="right">
          <div class="line-1">{{ $t("real_work") }}</div>
          <div class="line-2">
            {{ $t("real_work_description") }}
          </div>
          <div class="line-3" @click="handleQuickClick"> {{ $t("experience_now") }}</div>
        </div>
      </div>
    </div>
    <div class="module-business">
      <div class="content">
        <div class="line-1">{{ $t("data_drives_value") }}</div>
        <div class="line-2">{{ $t("data_drives_value_description") }}</div>
        <div class="business-img">
          <img src="https://zhiqingyun-demo.isxcode.com/tools/open/file/b-0.png" alt=""/>
          <img class="bus-img" src="https://zhiqingyun-demo.isxcode.com/tools/open/file/b-1.png" alt=""/>
          <img class="bus-img" src="https://zhiqingyun-demo.isxcode.com/tools/open/file/b-2.png" alt=""/>
          <img class="bus-img" src="https://zhiqingyun-demo.isxcode.com/tools/open/file/b-3.png" alt=""/>
          <img class="bus-img" src="https://zhiqingyun-demo.isxcode.com/tools/open/file/b-4.png" alt=""/>
          <img class="bus-img" src="https://zhiqingyun-demo.isxcode.com/tools/open/file/b-5.png" alt=""/>
          <img class="bus-img" src="https://zhiqingyun-demo.isxcode.com/tools/open/file/b-6.png" alt=""/>
          <img class="bus-img" src="https://zhiqingyun-demo.isxcode.com/tools/open/file/b-7.png" alt=""/>
          <img class="bus-img" src="https://zhiqingyun-demo.isxcode.com/tools/open/file/b-8.png" alt=""/>
          <img class="bus-img" src="https://zhiqingyun-demo.isxcode.com/tools/open/file/b-9.png" alt=""/>
        </div>
      </div>
    </div>
    <div class="module-feat-right">
      <div class="content">
        <div class="right-phone">
          <img class="feat-img" src="https://zhiqingyun-demo.isxcode.com/tools/open/file/p-3.jpg" alt=""/>
        </div>
        <div class="left">
          <div class="line-1">{{ $t("multi_platform_deployment") }}</div>
          <div class="line-2">{{ $t("multi_platform_description") }}</div>
          <div class="line-3" @click="handleQuickClick">
            {{ $t("experience_now") }}
          </div>
        </div>
        <div class="right">
          <img id="zoom" class="feat-img" src="https://zhiqingyun-demo.isxcode.com/tools/open/file/p-3.jpg" alt="">
        </div>
      </div>
    </div>
    <div class="module-feat-left">
      <div class="content">
        <div class="left">
          <img id="zoom" class="feat-img" src="https://zhiqingyun-demo.isxcode.com/tools/open/file/p-4.jpg" alt="">
        </div>
        <div class="left-phone">
          <img class="feat-img" src="https://zhiqingyun-demo.isxcode.com/tools/open/file/p-4.jpg" alt="">
        </div>
        <div class="right">
          <div class="line-1">{{ $t("data_view") }}</div>
          <div class="line-2">{{ $t("data_view_description") }}</div>
          <div class="line-3" @click="handleQuickClick">
            {{ $t("experience_now") }}
          </div>
        </div>
      </div>
    </div>
    <div class="module-end">
      <div class="content">
        <div class="line-1">{{ $t("opensource_value") }}</div>
        <div class="end-btn" @click="handleQuickClick">
          {{ $t("free_trial") }}
        </div>
      </div>
    </div>
  </div>
  <LayoutHomeFooter/>
</template>

<script lang="ts" setup>
import  {ElMessage} from "element-plus";
import {defineProps} from "vue";
import {useI18n} from "vue-i18n";
import mediumZoom from "medium-zoom";

onMounted(async () => {
  await nextTick()

  // 检查是否从其他页面返回，如果是则立即显示背景图
  const moduleIntro = document.querySelector('.module-intro') as HTMLElement;
  if (moduleIntro && document.referrer) {
    // 检查背景图是否已在缓存中
    const img = new Image();
    img.src = 'https://zhiqingyun-demo.isxcode.com/tools/open/file/bg-0.jpg';

    if (img.complete && img.naturalWidth > 0) {
      // 如果图片已缓存，立即显示
      moduleIntro.style.backgroundImage = `url("https://zhiqingyun-demo.isxcode.com/tools/open/file/bg-0.jpg")`;
      moduleIntro.style.backgroundSize = 'cover';
      moduleIntro.style.backgroundPosition = 'center';
      moduleIntro.style.backgroundRepeat = 'no-repeat';
      moduleIntro.style.opacity = '1';
      moduleIntro.style.transition = 'opacity 0.3s ease-in-out';
    }
  }

  mediumZoom(document.querySelectorAll('#zoom'), {
    margin: 100,
    scrollOffset: 1,
    background: '#fffaf8',
  })
})

const $t = useI18n().t;

definePageMeta({
  title: $t("home"),
  layout: "home",
  // 启用页面缓存，提高返回首页的速度
  keepalive: true,
});

useSeoMeta({
  title: $t("zhi_qing_yun"),
  ogTitle: $t("zhi_yao_shu_ju"),
  description: $t("enterprise_data_platform"),
  ogDescription: $t("build_enterprise_open_source_software"),
});

// 优先预加载关键背景图片
useHead({
  link: [
    {
      rel: 'preload',
      href: 'https://zhiqingyun-demo.isxcode.com/tools/open/file/bg-0.jpg',
      as: 'image',
      type: 'image/jpeg'
    }
  ]
});

const isMobile = useMediaQuery("(max-width: 767px)");
const {locale} = useI18n();

const props = defineProps({
  showVideo: {
    type: Boolean,
    default: true,
  },
});

const showVideo = ref(props.showVideo);

const toggleVideo = () => {
  showVideo.value = !showVideo.value;
};

onMounted(() => {
  // 使用 Artplayer 容器嵌入 Bilibili 播放器
  const container = document.querySelector('.artplayer-app');
  if (container) {
    // 创建封面容器
    const posterWrapper = document.createElement('div');
    posterWrapper.style.width = '100%';
    posterWrapper.style.height = '100%';
    posterWrapper.style.position = 'absolute';
    posterWrapper.style.top = '0';
    posterWrapper.style.left = '0';
    posterWrapper.style.cursor = 'pointer';
    posterWrapper.style.zIndex = '1';

    // 创建封面图片
    const poster = document.createElement('img');
    poster.src = 'https://zhiqingyun-demo.isxcode.com/tools/open/file/product.jpg';
    poster.style.width = '100%';
    poster.style.height = '100%';
    poster.style.objectFit = 'cover';

    // 创建播放按钮
    const playBtn = document.createElement('div');
    playBtn.style.position = 'absolute';
    playBtn.style.top = '50%';
    playBtn.style.left = '50%';
    playBtn.style.transform = 'translate(-50%, -50%)';
    playBtn.style.width = '60px';
    playBtn.style.height = '60px';
    playBtn.style.backgroundColor = '#e25a1b';
    playBtn.style.borderRadius = '50%';
    playBtn.style.display = 'flex';
    playBtn.style.alignItems = 'center';
    playBtn.style.justifyContent = 'center';
    playBtn.style.transition = 'transform 0.2s ease';

    // 创建播放三角形图标
    const playIcon = document.createElement('div');
    playIcon.style.width = '0';
    playIcon.style.height = '0';
    playIcon.style.borderTop = '12px solid transparent';
    playIcon.style.borderBottom = '12px solid transparent';
    playIcon.style.borderLeft = '18px solid white';
    playIcon.style.marginLeft = '4px';

    playBtn.appendChild(playIcon);
    posterWrapper.appendChild(poster);
    posterWrapper.appendChild(playBtn);

    // 鼠标悬停效果
    posterWrapper.addEventListener('mouseenter', () => {
      playBtn.style.transform = 'translate(-50%, -50%) scale(1.1)';
    });
    posterWrapper.addEventListener('mouseleave', () => {
      playBtn.style.transform = 'translate(-50%, -50%) scale(1)';
    });

    // 点击封面后才创建 iframe，这样可以绕过浏览器的自动播放静音限制
    posterWrapper.addEventListener('click', () => {
      const iframe = document.createElement('iframe');
      iframe.src = '//player.bilibili.com/player.html?isOutside=true&aid=115813424109548&bvid=BV1LniMBBEU2&cid=35098134728&p=1&autoplay=1&muted=0&high_quality=1&danmaku=0';
      iframe.allowFullscreen = true;
      iframe.style.width = '100%';
      iframe.style.height = '100%';
      iframe.style.position = 'absolute';
      iframe.style.top = '0';
      iframe.style.left = '0';
      iframe.style.border = 'none';
      container.appendChild(iframe);
      posterWrapper.style.display = 'none';
    });

    // 只将封面添加到容器中，iframe 在点击后才创建
    container.appendChild(posterWrapper);
  }
  window.addEventListener("scroll", handleScroll);
  definePageMeta({
    title: "home_title",
    layout: "home",
  });
});

function handleScroll() {
  if (window.scrollY > 50) {
    showVideo.value = false;
  }
}

function handleGuideClick() {
  const router = useRouter();
  const langPrefix = locale.value;
  router.push(`/${langPrefix}/docs/${langPrefix}/1/0`);
}

function handleQuickClick() {
  window.open("https://zhiqingyun-demo.isxcode.com");
}

function handleCommandCopyClick() {
  const codeBlock = document.getElementById("command-shell");
  const codeBlockText = codeBlock?.innerText;
  if (codeBlockText) {
    copyContent(codeBlockText);
  }
}

const copyContent = async (text: string) => {
  try {
    await navigator.clipboard.writeText(text);
    ElMessage({
      duration: 800,
      message: $t("copy_success"),
      type: "success",
    });
  } catch (err) {
    console.error("Failed to copy: ", err);
  }
};
</script>

<style lang="scss" scoped>

$font-size: 14px;
// 介绍的高度
$module-intro-height: 850px;
// 相关技术板块的高度
$module-about-height: 460px;
// 商业板块的高度
$module-business-height: 550px;
// 主题色
$primary-color: #e25a1b;
// 特点1背景色
$module-feat-left-bg-color: white;
// 特点2背景色
$module-feat-right-bg-color: rgba(255, 156, 110, 5%);
// 特点高度
$module-feat-height: 440px;
// 结束板块高度
$module-end-height: 300px;
// 结束板块高度
$primary-width: 1200px;
// 产品介绍图片大小
$module-intro-img-width: 600px;

.main {
  font-family: "阿里巴巴普惠体 2.0 45 Light", sans-serif;
  .module-intro {
    width: 100%;
    height: $module-intro-height;
    background-size: cover;
    background-position: center;
    background-repeat: no-repeat;
    /* 移除默认背景图，由 JavaScript 控制加载和显示 */
    background-color: rgba(255, 156, 110, 5%); /* 添加默认背景色 */
    padding-top: 200px;
    position: relative;
    overflow: hidden;
    opacity: 0; /* 初始隐藏，等待背景图加载完成 */
    transition: opacity 0.5s ease-in-out;

    /* 支持缓存快速恢复的样式 */
    &.cache-restored {
      opacity: 1;
      transition: none;
    }

    .bg-video {
      position: absolute;
      top: 0;
      left: 0;
      width: 100%;
      height: 105%;
      object-fit: cover;
      z-index: -1;
      pointer-events: none;
    }

    .content {
      display: flex;

      .left {
        width: 600px;

        .name {
          margin-top: 120px;
          font-family: "阿里妈妈数黑体 Bold", sans-serif;
          font-size: 60px;
        }

        .slogan {
          margin-top: 20px;
          font-family: "阿里妈妈数黑体 Bold", sans-serif;
          font-size: 40px;
        }

        .command {
          display: flex;
          margin-top: 20px;
          padding-left: 15px;
          border: #e25a1b solid 1px;
          color: #e25a1b;
          width: 435px;
          border-radius: 3px;
          font-size: 17px;
          height: 38px;
          line-height: 38px;

          .copy-icon {
            cursor: pointer;
            margin-left: 20px;
            margin-top: 6px;
            width: 24px;
            height: 24px;
          }
        }

        .btn-div {
          margin-top: 25px;
          display: flex;

          .guide-btn {
            cursor: pointer;
            width: 120px;
            border-radius: 3px;
            background: #e25a1b;
            color: white;
            text-align: center;
            font-size: 18px;
            height: 40px;
            line-height: 40px;
          }

          .movie-btn {
            display: none;
          }

          .quick-btn {
            cursor: pointer;
            margin-left: 20px;
            width: 150px;
            color: #e25a1b;
            height: 40px;
            line-height: 40px;
            text-align: center;
            display: flex;

            .link-right-svg {
              margin-top: 10px;
              width: 20px;
              height: 20px;
            }
          }
        }
      }

      .right {
        .artplayer-app {
          height: 341px;
          margin-top: 70px;
          width: 630px;
          position: relative;
        }
      }
    }
  }

  .module-about {
    width: 100%;
    background: white;
    height: $module-about-height;
    display: flex;
    align-items: center;
    justify-content: center;
    padding-top: 40px;

    .content {
      .why-title {
        margin: auto;
        text-align: center;
        width: 400px;
        font-family: "阿里妈妈数黑体 Bold", sans-serif;
        font-size: 30px;
      }

      .why-content {
        margin: 25px auto auto;
        text-indent: 2em;
        width: 860px;
        line-height: 26px;
        font-size: 18px;
      }

      .why-content-mobile {
        display: none;
      }

      .tech-title {
        text-align: center;
        margin: 40px auto auto;
        width: 400px;
        font-size: 12px;
        color: lightgray;
      }

      .tech-img-div {
        .tech-img {
          cursor: pointer;
        }

        display: flex;
        justify-content: space-between;
        margin: 20px auto auto;
        width: 1200px;

        img {
          width: 150px;
          height: 55px;
        }
      }
    }
  }

  .module-business {
    width: 100%;
    height: $module-business-height;
    background-size: cover;
    background-position: center;
    background-repeat: no-repeat;
    background-image: url("https://zhiqingyun-demo.isxcode.com/tools/open/file/bg-2.jpg");
    display: flex;
    align-items: center;
    justify-content: center;

    .content {
      .line-1 {
        margin-top: 50px;
        color: white;
        text-align: center;
        font-family: "阿里妈妈数黑体 Bold", sans-serif;
        font-size: 35px;
      }

      .line-2 {
        margin-top: 16px;
        color: white;
        text-align: center;
      }

      .business-img {
        margin: 40px auto auto;
        width: 1200px;
        display: flex;
        flex-wrap: wrap;
        justify-content: center;

        img {
          width: 220px;
          height: 150px;
          margin: 6px;
        }
      }
    }
  }
}

.module-feat-left {
  width: 100%;
  height: $module-feat-height;
  background: $module-feat-right-bg-color;
  display: flex;
  align-items: center;
  justify-content: center;

  .content {
    display: flex;

    .left-phone {
      display: none;
    }

    .left {
      width: 600px;

      img {
        width: 450px;
        box-shadow: 0 2px 10px rgba(0, 0, 0, 0.2);
      }
    }

    .right {
      margin-left: 100px;
      margin-top: 50px;
      width: 600px;

      .line-1 {
        font-family: "阿里妈妈数黑体 Bold", sans-serif;
        font-size: 30px;
      }

      .line-2 {
        width: 560px;
        height: 55px;
        font-size: 17px;
        margin-top: 30px;
        line-height: 24px;
      }

      .line-3 {
        cursor: pointer;
        margin-top: 35px;
        color: #e25a1b;
        font-family: "阿里妈妈数黑体 Bold", sans-serif;
      }
    }
  }
}

.module-feat-right {
  width: 100%;
  height: $module-feat-height;
  display: flex;
  align-items: center;
  justify-content: center;
  background: $module-feat-left-bg-color;

  .content {
    display: flex;

    .right-phone {
      display: none;
    }

    .right {
      width: 600px;
      margin-left: 100px;

      img {
        width: 450px;
        box-shadow: 0 2px 10px rgba(0, 0, 0, 0.2);
      }
    }

    .left {
      width: 600px;
      margin-top: 50px;

      .line-1 {
        font-family: "阿里妈妈数黑体 Bold", sans-serif;
        font-size: 30px;
      }

      .line-2 {
        width: 560px;
        height: 55px;
        font-size: 17px;
        margin-top: 30px;
        line-height: 24px;
      }

      .line-3 {
        cursor: pointer;
        margin-top: 35px;
        color: #e25a1b;
        font-family: "阿里妈妈数黑体 Bold", sans-serif;
      }
    }
  }
}

.module-end {
  height: $module-end-height;
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
  background-image: url("https://zhiqingyun-demo.isxcode.com/tools/open/file/bg-1.jpg");
  display: flex;
  align-items: center;
  justify-content: center;

  .content {
    .line-1 {
      width: 600px;
      text-align: center;
      color: white;
      margin: auto;
      font-family: "阿里妈妈数黑体 Bold", sans-serif;
      font-size: 40px;
    }

    .end-btn {
      cursor: pointer;
      margin: 40px auto auto;
      background: white;
      color: #e25a1b;
      width: 200px;
      font-size: 20px;
      border-radius: 3px;
      height: 40px;
      line-height: 40px;
      text-align: center;
    }
  }
}

.content {
  margin: auto;
  width: $primary-width;
}

// ------------------------------------------------------- 移动端 ----------------------------------------------------------------------

@media (max-width: 768px) {
  .main {
    .module-intro {
      width: 100%;
      height: 850px;
      padding-top: 400px;

      .content {
        display: flex;

        .right {
          position: fixed;
          top: 30%;
          left: 50%;
          transform: translate(-50%, -50%);

          .artplayer-app {
            height: 195px;
            margin-top: 70px;
            width: 360px;
            position: relative;
          }
        }

        .left {
          width: 400px;

          .name {
            text-align: center;
            margin-top: 10px;
            font-family: "阿里妈妈数黑体 Bold", sans-serif;
            font-size: 66px;
          }

          .slogan {
            margin-top: 20px;
            text-align: center;
            font-family: "阿里妈妈数黑体 Bold", sans-serif;
            font-size: 27px;
          }

          .command {
            display: none;
          }

          .btn-div {
            display: flex;
            width: 300px;
            margin: 25px auto auto;

            .guide-btn {
              display: none;
              cursor: pointer;
              width: 170px;
              border-radius: 3px;
              background: #e25a1b;
              color: white;
              text-align: center;
              font-size: 18px;
              height: 40px;
              line-height: 40px;
              margin-left: 40px;
            }

            .quick-btn {
              display: none;
            }

            .movie-btn {
              cursor: pointer;
              width: 150px;
              margin: auto;
              background: #e25a1b;
              color: white;
              height: 40px;
              line-height: 40px;
              text-align: center;
              font-size: 20px;
              display: block;
              border-radius: 3px;
            }

          }
        }
      }
    }

    .module-about {
      width: 100%;
      background: white;
      height: 350px;
      display: flex;
      align-items: center;
      justify-content: center;
      padding-top: 40px;

      .content {
        .why-title {
          margin: auto;
          text-align: center;
          width: 300px;
          font-family: "阿里妈妈数黑体 Bold", sans-serif;
          font-size: 30px;
        }

        .why-content-mobile {
          display: block;
          margin: 25px auto auto;
          width: 300px;
          line-height: 25px;
          font-size: 19px;
          text-indent: 2em;
          text-align: justify;
          text-justify: distribute-all-lines;
          word-break: break-all;
        }

        .why-content {
          display: none;
        }

        .tech-title {
          display: none;
        }

        .tech-img-div {
          display: none;
        }
      }
    }

    .module-business {
      width: 100%;
      height: 320px;
      background-size: cover;
      background-position: center;
      background-repeat: no-repeat;
      background-image: url("https://zhiqingyun-demo.isxcode.com/tools/open/file/bg-2.jpg");
      align-items: center;

      .content {
        height: 350px;

        .line-1 {
          color: white;
          text-align: center;
          font-family: "阿里妈妈数黑体 Bold", sans-serif;
          font-size: 20px;
        }

        .line-2 {
          margin-top: 16px;
          color: white;
          text-align: center;
          font-size: 13px;
        }

        .business-img {
          margin: 20px auto auto;
          width: 300px;

          .bus-img {
            display: none;
          }

          img {
            width: 250px;
          }
        }
      }
    }
  }

  .module-feat-left {
    width: 100%;
    height: 480px;
    display: flex;
    align-items: center;
    justify-content: center;

    .content {
      height: 380px;
      flex-direction: column;

      .left {
        display: none;
      }

      .left-phone {
        display: block;
        width: 300px;
        box-shadow: 0 2px 10px rgba(0, 0, 0, 0.2);

        img {
          width: 300px;
        }
      }

      .right {
        margin-left: 0;
        margin-top: 20px;
        width: 300px;

        .line-1 {
          margin-top: 15px;
          font-family: "阿里妈妈数黑体 Bold", sans-serif;
          font-size: 25px;
          text-align: center;
        }

        .line-2 {
          height: 100px;
          width: 300px;
          margin-top: 30px;
          line-height: 25px;
          font-size: 18px;
          text-align: justify;
          text-justify: distribute-all-lines;
          word-break: break-all;
        }

        .line-3 {
          display: none;
        }
      }
    }
  }

  .module-feat-right {
    width: 100%;
    height: 480px;
    display: flex;
    align-items: center;
    justify-content: center;

    .content {
      height: 380px;
      flex-direction: column;

      .right {
        display: none;
      }

      .right-phone {
        display: block;
        box-shadow: 0 2px 10px rgba(0, 0, 0, 0.2);

        img {
          width: 300px;
        }
      }

      .left {
        margin-left: 0;
        margin-top: 20px;
        width: 300px;

        .line-1 {
          margin-top: 15px;
          font-family: "阿里妈妈数黑体 Bold", sans-serif;
          font-size: 25px;
          text-align: center;
        }

        .line-2 {
          height: 100px;
          width: 300px;
          margin-top: 30px;
          line-height: 25px;
          font-size: 18px;
          text-align: justify;
          text-justify: distribute-all-lines;
          word-break: break-all;
        }

        .line-3 {
          display: none;
        }
      }
    }
  }

  .module-end {
    height: 300px;
    background-size: cover;
    background-position: center;
    background-repeat: no-repeat;
    background-image: url("https://zhiqingyun-demo.isxcode.com/tools/open/file/bg-1.jpg");
    display: flex;
    align-items: center;
    justify-content: center;

    .content {
      .line-1 {
        width: 300px;
        text-align: center;
        color: white;
        margin: auto;
        font-family: "阿里妈妈数黑体 Bold", sans-serif;
        font-size: 40px;
      }

      .end-btn {
        display: none;
      }
    }
  }

  .content {
    margin: auto;
    width: 300px;
  }
}
</style>

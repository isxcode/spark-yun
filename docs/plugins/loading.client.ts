export default defineNuxtPlugin(() => {
  const { start, finish, error, setProgress } = useTopLoading();
  const router = useRouter();
  let isLoading = false;
  let loadingTimeout: NodeJS.Timeout | null = null;
  let progressUpdateTimer: NodeJS.Timeout | null = null;
  let currentProgress = 0;

  // 资源加载阶段权重
  const PROGRESS_WEIGHTS = {
    DOM_READY: 20,        // DOM准备完成 20%
    STYLESHEETS: 30,      // 样式表加载 30%
    SCRIPTS: 25,          // 脚本加载 25%
    IMAGES: 20,           // 图片加载 20%
    FONTS: 5              // 字体加载 5%
  };

  // 清理函数
  const cleanup = () => {
    if (loadingTimeout) {
      clearTimeout(loadingTimeout);
      loadingTimeout = null;
    }
    if (progressUpdateTimer) {
      clearTimeout(progressUpdateTimer);
      progressUpdateTimer = null;
    }
  };

  // 更新进度条
  const updateProgress = (progress: number) => {
    const newProgress = Math.min(Math.max(progress, currentProgress), 100);

    // 只有进度真正增加时才更新
    if (newProgress > currentProgress) {
      currentProgress = newProgress;
      if (setProgress) {
        setProgress(currentProgress);
      }

      // 如果进度达到100%，自动完成
      if (currentProgress >= 100 && isLoading) {
        setTimeout(() => {
          completeLoading();
        }, 100);
      }
    }
  };

  // 完成loading的统一方法
  const completeLoading = () => {
    if (isLoading) {
      isLoading = false;
      cleanup();
      updateProgress(100);
      setTimeout(() => {
        finish();
      }, 200); // 减少延迟时间，让完成更快
    }
  };

  // 页面切换时立即显示loading
  router.beforeEach((to, from) => {
    if (to.path !== from.path && !isLoading) {
      cleanup(); // 清理之前的状态
      isLoading = true;
      currentProgress = 0;

      // 如果有预加载loading条，先显示它
      if (process.client && (window as any).__preloadLoading) {
        (window as any).__preloadLoading.show();
        (window as any).__preloadLoading.setProgress(5);
      }

      // 立即显示进度条，不等待资源检测
      start();
      updateProgress(5); // 开始时显示5%

      // 设置超时保护，防止loading卡住
      loadingTimeout = setTimeout(() => {
        console.warn('Loading超时，强制完成');
        completeLoading();
      }, 6000); // 6秒后强制完成，减少等待时间
    }
  });

  router.afterEach(() => {
    // 立即开始检测资源加载进度
    startProgressTracking();
  });

  // 开始进度跟踪
  const startProgressTracking = () => {
    if (!isLoading) return;

    // 启动智能进度模拟
    startSmartProgressSimulation();

    // 预加载关键视频资源
    preloadCriticalVideo();

    // 阶段1: DOM准备 (立即检查)
    checkDOMReady();
  };

  // 智能进度模拟，确保进度条不会卡住
  const startSmartProgressSimulation = () => {
    let simulationProgress = 5; // 从5%开始

    const progressSimulation = setInterval(() => {
      if (!isLoading || currentProgress >= 95) {
        clearInterval(progressSimulation);
        return;
      }

      // 如果真实进度落后于模拟进度，使用模拟进度
      if (currentProgress < simulationProgress) {
        updateProgress(simulationProgress);
      }

      // 模拟进度递增，但不超过95%
      simulationProgress = Math.min(simulationProgress + Math.random() * 3 + 1, 95);
    }, 200); // 每200ms更新一次

    // 将清理函数添加到现有的清理逻辑中
    if (progressUpdateTimer) {
      clearTimeout(progressUpdateTimer);
    }
    progressUpdateTimer = progressSimulation;
  };

  // 预加载关键视频资源
  const preloadCriticalVideo = () => {
    const videoUrl = 'https://zhiqingyun-demo.isxcode.com/tools/open/file/product.mp4';

    // 创建视频元素进行预加载
    const video = document.createElement('video');
    video.preload = 'metadata'; // 只预加载元数据，不预加载整个视频
    video.src = videoUrl;
    video.style.display = 'none';

    video.addEventListener('loadedmetadata', () => {
      updateProgress(currentProgress + 5); // 视频元数据加载完成，增加5%进度
    });

    video.addEventListener('canplaythrough', () => {
      updateProgress(currentProgress + 10); // 视频可以播放，增加10%进度
      document.body.removeChild(video); // 清理预加载的视频元素
    });

    video.addEventListener('error', () => {
      updateProgress(currentProgress + 5); // 即使出错也增加一些进度
      if (video.parentNode) {
        document.body.removeChild(video);
      }
    });

    document.body.appendChild(video);
  };

  // 检查DOM准备状态
  const checkDOMReady = () => {
    if (document.readyState === 'loading') {
      document.addEventListener('DOMContentLoaded', () => {
        updateProgress(PROGRESS_WEIGHTS.DOM_READY);
        checkStylesheets();
      });
    } else {
      updateProgress(PROGRESS_WEIGHTS.DOM_READY);
      checkStylesheets();
    }
  };

  // 检查样式表加载
  const checkStylesheets = () => {
    const stylesheets = document.querySelectorAll('link[rel="stylesheet"]:not([data-loaded])');
    const totalStylesheets = stylesheets.length;
    let loadedStylesheets = 0;

    if (totalStylesheets === 0) {
      updateProgress(PROGRESS_WEIGHTS.DOM_READY + PROGRESS_WEIGHTS.STYLESHEETS);
      checkScripts();
      return;
    }

    const onStylesheetLoad = () => {
      loadedStylesheets++;
      const stylesheetProgress = (loadedStylesheets / totalStylesheets) * PROGRESS_WEIGHTS.STYLESHEETS;
      updateProgress(PROGRESS_WEIGHTS.DOM_READY + stylesheetProgress);

      if (loadedStylesheets >= totalStylesheets) {
        setTimeout(() => checkScripts(), 50); // 小延迟确保状态更新
      }
    };

    stylesheets.forEach((link) => {
      const linkElement = link as HTMLLinkElement;
      if (linkElement.sheet || linkElement.readyState === 'complete') {
        linkElement.setAttribute('data-loaded', 'true');
        onStylesheetLoad();
      } else {
        const onLoad = () => {
          linkElement.setAttribute('data-loaded', 'true');
          onStylesheetLoad();
          linkElement.removeEventListener('load', onLoad);
          linkElement.removeEventListener('error', onError);
        };
        const onError = () => {
          linkElement.setAttribute('data-loaded', 'true');
          onStylesheetLoad();
          linkElement.removeEventListener('load', onLoad);
          linkElement.removeEventListener('error', onError);
        };
        linkElement.addEventListener('load', onLoad);
        linkElement.addEventListener('error', onError);

        // 单个样式表超时
        setTimeout(() => {
          if (!linkElement.hasAttribute('data-loaded')) {
            linkElement.setAttribute('data-loaded', 'true');
            onStylesheetLoad();
            linkElement.removeEventListener('load', onLoad);
            linkElement.removeEventListener('error', onError);
          }
        }, 3000);
      }
    });
  };

  // 检查脚本加载
  const checkScripts = () => {
    const scripts = document.querySelectorAll('script[src]:not([data-loaded])');
    const totalScripts = scripts.length;
    let loadedScripts = 0;

    const baseProgress = PROGRESS_WEIGHTS.DOM_READY + PROGRESS_WEIGHTS.STYLESHEETS;

    if (totalScripts === 0) {
      updateProgress(baseProgress + PROGRESS_WEIGHTS.SCRIPTS);
      checkImages();
      return;
    }

    const onScriptLoad = () => {
      loadedScripts++;
      const scriptProgress = (loadedScripts / totalScripts) * PROGRESS_WEIGHTS.SCRIPTS;
      updateProgress(baseProgress + scriptProgress);

      if (loadedScripts >= totalScripts) {
        checkImages();
      }
    };

    scripts.forEach((script) => {
      const scriptElement = script as HTMLScriptElement;
      if (scriptElement.readyState === 'complete' || scriptElement.readyState === 'loaded') {
        scriptElement.setAttribute('data-loaded', 'true');
        onScriptLoad();
      } else {
        const onLoad = () => {
          scriptElement.setAttribute('data-loaded', 'true');
          onScriptLoad();
          scriptElement.removeEventListener('load', onLoad);
          scriptElement.removeEventListener('error', onError);
        };
        const onError = () => {
          scriptElement.setAttribute('data-loaded', 'true');
          onScriptLoad();
          scriptElement.removeEventListener('load', onLoad);
          scriptElement.removeEventListener('error', onError);
        };
        scriptElement.addEventListener('load', onLoad);
        scriptElement.addEventListener('error', onError);

        // 单个脚本超时
        setTimeout(() => {
          if (!scriptElement.hasAttribute('data-loaded')) {
            scriptElement.setAttribute('data-loaded', 'true');
            onScriptLoad();
            scriptElement.removeEventListener('load', onLoad);
            scriptElement.removeEventListener('error', onError);
          }
        }, 4000);
      }
    });
  };

  // 检查图片加载
  const checkImages = () => {
    const images = document.querySelectorAll('img:not([data-loaded])');
    const totalImages = images.length;
    let loadedImages = 0;

    const baseProgress = PROGRESS_WEIGHTS.DOM_READY + PROGRESS_WEIGHTS.STYLESHEETS + PROGRESS_WEIGHTS.SCRIPTS;

    if (totalImages === 0) {
      updateProgress(baseProgress + PROGRESS_WEIGHTS.IMAGES);
      checkFonts();
      return;
    }

    const onImageLoad = () => {
      loadedImages++;
      const imageProgress = (loadedImages / totalImages) * PROGRESS_WEIGHTS.IMAGES;
      updateProgress(baseProgress + imageProgress);

      if (loadedImages >= totalImages) {
        checkFonts();
      }
    };

    images.forEach((img) => {
      const imgElement = img as HTMLImageElement;
      // 检查图片是否已经加载完成
      if (imgElement.complete && (imgElement.naturalHeight !== 0 || imgElement.src === '')) {
        imgElement.setAttribute('data-loaded', 'true');
        onImageLoad();
      } else {
        const onLoad = () => {
          imgElement.setAttribute('data-loaded', 'true');
          onImageLoad();
          imgElement.removeEventListener('load', onLoad);
          imgElement.removeEventListener('error', onError);
        };
        const onError = () => {
          imgElement.setAttribute('data-loaded', 'true');
          onImageLoad();
          imgElement.removeEventListener('load', onLoad);
          imgElement.removeEventListener('error', onError);
        };
        imgElement.addEventListener('load', onLoad);
        imgElement.addEventListener('error', onError);

        // 单个图片超时
        setTimeout(() => {
          if (!imgElement.hasAttribute('data-loaded')) {
            imgElement.setAttribute('data-loaded', 'true');
            onImageLoad();
            imgElement.removeEventListener('load', onLoad);
            imgElement.removeEventListener('error', onError);
          }
        }, 5000);
      }
    });
  };

  // 检查字体加载
  const checkFonts = () => {
    const baseProgress = PROGRESS_WEIGHTS.DOM_READY + PROGRESS_WEIGHTS.STYLESHEETS +
                        PROGRESS_WEIGHTS.SCRIPTS + PROGRESS_WEIGHTS.IMAGES;

    // 检查字体是否加载完成
    if (document.fonts && document.fonts.ready) {
      // 设置字体检查超时，防止卡住
      const fontTimeout = setTimeout(() => {
        console.warn('字体加载超时，强制完成');
        updateProgress(baseProgress + PROGRESS_WEIGHTS.FONTS);
        completeLoading();
      }, 2000); // 2秒超时

      document.fonts.ready.then(() => {
        clearTimeout(fontTimeout);
        updateProgress(baseProgress + PROGRESS_WEIGHTS.FONTS);
        completeLoading();
      }).catch(() => {
        clearTimeout(fontTimeout);
        updateProgress(baseProgress + PROGRESS_WEIGHTS.FONTS);
        completeLoading();
      });
    } else {
      // 如果不支持字体API，直接完成
      updateProgress(baseProgress + PROGRESS_WEIGHTS.FONTS);
      completeLoading();
    }
  };

  // 监听DOM变化，处理动态加载的资源
  const observeNewResources = () => {
    const observer = new MutationObserver((mutations) => {
      if (isLoading) return; // 如果正在loading，不处理新资源

      let hasNewResources = false;

      mutations.forEach((mutation) => {
        mutation.addedNodes.forEach((node) => {
          if (node.nodeType === Node.ELEMENT_NODE) {
            const element = node as Element;

            // 检查是否是图片或包含图片
            if (element.tagName === 'IMG' || element.querySelectorAll('img:not([data-loaded])').length > 0) {
              hasNewResources = true;
            }

            // 检查是否是样式表或脚本
            if ((element.tagName === 'LINK' && element.getAttribute('rel') === 'stylesheet') ||
                (element.tagName === 'SCRIPT' && element.getAttribute('src'))) {
              hasNewResources = true;
            }
          }
        });
      });

      // 动态资源不触发新的loading，只在当前loading过程中处理
      // 这里暂时不做处理，保持原有逻辑
    });

    observer.observe(document.body, {
      childList: true,
      subtree: true
    });

    return observer;
  };

  // 初始页面加载处理
  if (process.client) {
    // 页面完全加载完成后确保loading隐藏
    window.addEventListener('load', () => {
      if (isLoading) {
        completeLoading();
      }
    });

    // 页面可见性变化时的处理
    document.addEventListener('visibilitychange', () => {
      if (!document.hidden && isLoading) {
        // 页面重新可见时，检查是否需要完成loading
        setTimeout(() => {
          if (isLoading && document.readyState === 'complete') {
            completeLoading();
          }
        }, 500);
      }
    });

    // 添加额外的完成检测机制
    const checkLoadingComplete = () => {
      if (!isLoading) return;

      // 检查文档状态
      const isDocumentComplete = document.readyState === 'complete';

      // 检查是否所有关键资源都已加载
      const images = document.querySelectorAll('img');
      const scripts = document.querySelectorAll('script[src]');
      const stylesheets = document.querySelectorAll('link[rel="stylesheet"]');

      let allResourcesLoaded = true;
      let loadedCount = 0;
      let totalCount = 0;

      // 检查图片
      images.forEach(img => {
        totalCount++;
        if (img.complete && (img.naturalHeight !== 0 || img.src === '')) {
          loadedCount++;
        } else if (img.src) {
          allResourcesLoaded = false;
        }
      });

      // 检查脚本
      scripts.forEach(script => {
        totalCount++;
        if (script.readyState === 'complete' || script.readyState === 'loaded') {
          loadedCount++;
        } else {
          allResourcesLoaded = false;
        }
      });

      // 检查样式表
      stylesheets.forEach(link => {
        totalCount++;
        if (link.sheet || link.readyState === 'complete') {
          loadedCount++;
        } else {
          allResourcesLoaded = false;
        }
      });

      // 计算资源加载百分比
      const resourceProgress = totalCount > 0 ? (loadedCount / totalCount) * 100 : 100;

      // 如果满足完成条件，完成loading
      if ((isDocumentComplete && allResourcesLoaded) ||
          currentProgress >= 98 ||
          resourceProgress >= 95) {
        // console.log('资源检查完成，完成loading', {
        //   isDocumentComplete,
        //   allResourcesLoaded,
        //   currentProgress,
        //   resourceProgress
        // });
        completeLoading();
      }
    };

    // 定期检查loading是否应该完成
    const loadingCheckInterval = setInterval(() => {
      if (!isLoading) {
        clearInterval(loadingCheckInterval);
        return;
      }
      checkLoadingComplete();
    }, 500); // 每500ms检查一次，更频繁的检查

    // 开始监听新资源
    const observer = observeNewResources();

    // 页面卸载时清理观察器和状态
    window.addEventListener('beforeunload', () => {
      observer.disconnect();
      cleanup();
    });

    // 监听路由错误
    router.onError(() => {
      completeLoading();
    });
  }
});
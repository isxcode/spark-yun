export default defineNuxtPlugin(() => {
  const { start, finish, error } = useTopLoading();
  const router = useRouter();
  let isLoading = false;

  // 页面切换时显示loading
  router.beforeEach((to, from) => {
    if (to.path !== from.path && !isLoading) {
      isLoading = true;
      start();
    }
  });

  router.afterEach(() => {
    // 延迟一点时间，让页面资源开始加载
    setTimeout(() => {
      handleResourceLoading();
    }, 100);
  });

  // 监听页面资源加载
  const handleResourceLoading = () => {
    // 获取所有需要加载的资源
    const images = document.querySelectorAll('img:not([data-loaded])');
    const stylesheets = document.querySelectorAll('link[rel="stylesheet"]:not([data-loaded])');
    const scripts = document.querySelectorAll('script[src]:not([data-loaded])');

    const totalResources = images.length + stylesheets.length + scripts.length;
    let resourcesLoaded = 0;

    // 如果没有资源需要加载，直接完成
    if (totalResources === 0) {
      isLoading = false;
      setTimeout(() => {
        finish();
      }, 200);
      return;
    }

    // 有资源需要加载，确保loading已开始
    if (!isLoading) {
      isLoading = true;
      start();
    }

    const checkAllLoaded = () => {
      resourcesLoaded++;
      if (resourcesLoaded >= totalResources) {
        isLoading = false;
        setTimeout(() => {
          finish();
        }, 200);
      }
    };

    // 监听图片加载
    images.forEach((img) => {
      const imgElement = img as HTMLImageElement;
      if (imgElement.complete && imgElement.naturalHeight !== 0) {
        imgElement.setAttribute('data-loaded', 'true');
        checkAllLoaded();
      } else {
        const onLoad = () => {
          imgElement.setAttribute('data-loaded', 'true');
          checkAllLoaded();
          imgElement.removeEventListener('load', onLoad);
          imgElement.removeEventListener('error', onError);
        };
        const onError = () => {
          imgElement.setAttribute('data-loaded', 'true');
          checkAllLoaded();
          imgElement.removeEventListener('load', onLoad);
          imgElement.removeEventListener('error', onError);
        };
        imgElement.addEventListener('load', onLoad);
        imgElement.addEventListener('error', onError);
      }
    });

    // 监听样式表加载
    stylesheets.forEach((link) => {
      const linkElement = link as HTMLLinkElement;
      if (linkElement.sheet) {
        linkElement.setAttribute('data-loaded', 'true');
        checkAllLoaded();
      } else {
        const onLoad = () => {
          linkElement.setAttribute('data-loaded', 'true');
          checkAllLoaded();
          linkElement.removeEventListener('load', onLoad);
          linkElement.removeEventListener('error', onError);
        };
        const onError = () => {
          linkElement.setAttribute('data-loaded', 'true');
          checkAllLoaded();
          linkElement.removeEventListener('load', onLoad);
          linkElement.removeEventListener('error', onError);
        };
        linkElement.addEventListener('load', onLoad);
        linkElement.addEventListener('error', onError);
      }
    });

    // 监听脚本加载
    scripts.forEach((script) => {
      const scriptElement = script as HTMLScriptElement;
      const onLoad = () => {
        scriptElement.setAttribute('data-loaded', 'true');
        checkAllLoaded();
        scriptElement.removeEventListener('load', onLoad);
        scriptElement.removeEventListener('error', onError);
      };
      const onError = () => {
        scriptElement.setAttribute('data-loaded', 'true');
        checkAllLoaded();
        scriptElement.removeEventListener('load', onLoad);
        scriptElement.removeEventListener('error', onError);
      };
      scriptElement.addEventListener('load', onLoad);
      scriptElement.addEventListener('error', onError);
    });
  };

  // 监听DOM变化，处理动态加载的资源
  const observeNewResources = () => {
    const observer = new MutationObserver((mutations) => {
      if (isLoading) return; // 如果已经在loading，不重复处理

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

      if (hasNewResources) {
        // 延迟一点时间让新资源开始加载
        setTimeout(() => {
          handleResourceLoading();
        }, 100);
      }
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
      isLoading = false;
      setTimeout(() => {
        finish();
      }, 300);
    });

    // DOM加载完成后检查资源
    if (document.readyState === 'loading') {
      document.addEventListener('DOMContentLoaded', () => {
        setTimeout(() => {
          handleResourceLoading();
        }, 100);
      });
    } else if (document.readyState === 'interactive') {
      // DOM交互就绪，检查资源
      setTimeout(() => {
        handleResourceLoading();
      }, 100);
    } else if (document.readyState === 'complete') {
      // 页面已完全加载，直接完成
      isLoading = false;
      setTimeout(() => {
        finish();
      }, 100);
    }

    // 开始监听新资源
    const observer = observeNewResources();

    // 页面卸载时清理观察器
    window.addEventListener('beforeunload', () => {
      observer.disconnect();
    });

    // 设置最大loading时间，防止卡住
    setTimeout(() => {
      if (isLoading) {
        isLoading = false;
        finish();
      }
    }, 8000); // 8秒后强制完成
  }
});
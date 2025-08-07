export default defineNuxtPlugin(() => {
  const { start, finish, error } = useTopLoading();

  // 监听页面资源加载
  const handleResourceLoading = () => {
    let loadingStarted = false;
    let resourcesLoaded = 0;
    let totalResources = 0;

    // 获取所有需要加载的资源
    const images = document.querySelectorAll('img');
    const stylesheets = document.querySelectorAll('link[rel="stylesheet"]');
    const scripts = document.querySelectorAll('script[src]');

    totalResources = images.length + stylesheets.length + scripts.length;

    // 如果有资源需要加载，显示loading
    if (totalResources > 0 && !loadingStarted) {
      start();
      loadingStarted = true;
    }

    const checkAllLoaded = () => {
      resourcesLoaded++;
      if (resourcesLoaded >= totalResources && loadingStarted) {
        setTimeout(() => {
          finish();
        }, 200);
      }
    };

    // 监听图片加载
    images.forEach((img) => {
      if (img.complete) {
        checkAllLoaded();
      } else {
        img.addEventListener('load', checkAllLoaded);
        img.addEventListener('error', () => {
          checkAllLoaded();
        });
      }
    });

    // 监听样式表加载
    stylesheets.forEach((link) => {
      if (link.sheet) {
        checkAllLoaded();
      } else {
        link.addEventListener('load', checkAllLoaded);
        link.addEventListener('error', checkAllLoaded);
      }
    });

    // 监听脚本加载
    scripts.forEach((script) => {
      script.addEventListener('load', checkAllLoaded);
      script.addEventListener('error', checkAllLoaded);
    });

    // 如果没有资源需要加载，直接完成
    if (totalResources === 0 && loadingStarted) {
      setTimeout(() => {
        finish();
      }, 100);
    }
  };

  // 监听DOM变化，处理动态加载的资源
  const observeNewResources = () => {
    const observer = new MutationObserver((mutations) => {
      let hasNewResources = false;

      mutations.forEach((mutation) => {
        mutation.addedNodes.forEach((node) => {
          if (node.nodeType === Node.ELEMENT_NODE) {
            const element = node as Element;

            // 检查是否是图片或包含图片
            if (element.tagName === 'IMG' || element.querySelectorAll('img').length > 0) {
              hasNewResources = true;
            }

            // 检查是否是样式表或脚本
            if (element.tagName === 'LINK' || element.tagName === 'SCRIPT') {
              hasNewResources = true;
            }
          }
        });
      });

      if (hasNewResources) {
        // 延迟一点时间让新资源开始加载
        setTimeout(() => {
          handleResourceLoading();
        }, 50);
      }
    });

    observer.observe(document.body, {
      childList: true,
      subtree: true
    });

    return observer;
  };

  // 页面加载完成后开始监听
  onMounted(() => {
    // 初始资源加载检查
    handleResourceLoading();

    // 开始监听新资源
    const observer = observeNewResources();

    // 清理观察器
    onBeforeUnmount(() => {
      observer.disconnect();
    });
  });
});
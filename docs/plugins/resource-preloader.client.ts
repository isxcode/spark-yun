export default defineNuxtPlugin({
  name: 'resource-preloader',
  parallel: false,
  setup() {
    if (process.client) {
      // 关键背景图（优先加载并显示）
      const criticalBackgroundUrl = 'https://zhiqingyun-demo.isxcode.com/tools/open/file/bg-0.jpg';

      // 其他资源配置（bg-0.jpg 加载完成后再加载）
      const orderedResources = [
        { url: 'https://zhiqingyun-demo.isxcode.com/tools/open/file/logo.jpg', type: 'image' },
        { url: 'https://zhiqingyun-demo.isxcode.com/tools/open/file/product.jpg', type: 'image' },
        { url: 'https://zhiqingyun-demo.isxcode.com/tools/open/file/t-0.png', type: 'image' },
        { url: 'https://zhiqingyun-demo.isxcode.com/tools/open/file/t-1.png', type: 'image' },
        { url: 'https://zhiqingyun-demo.isxcode.com/tools/open/file/t-2.png', type: 'image' },
        { url: 'https://zhiqingyun-demo.isxcode.com/tools/open/file/t-3.png', type: 'image' },
        { url: 'https://zhiqingyun-demo.isxcode.com/tools/open/file/t-4.png', type: 'image' },
        { url: 'https://zhiqingyun-demo.isxcode.com/tools/open/file/t-5.png', type: 'image' },
        { url: 'https://zhiqingyun-demo.isxcode.com/tools/open/file/p-0.jpg', type: 'image' },
        { url: 'https://zhiqingyun-demo.isxcode.com/tools/open/file/p-1.jpg', type: 'image' },
        { url: 'https://zhiqingyun-demo.isxcode.com/tools/open/file/p-2.jpg', type: 'image' },
        { url: 'https://zhiqingyun-demo.isxcode.com/tools/open/file/p-3.jpg', type: 'image' },
        { url: 'https://zhiqingyun-demo.isxcode.com/tools/open/file/p-4.jpg', type: 'image' },
        { url: 'https://zhiqingyun-demo.isxcode.com/tools/open/file/bg-1.jpg', type: 'image' },
        { url: 'https://zhiqingyun-demo.isxcode.com/tools/open/file/bg-2.jpg', type: 'image' },
        { url: 'https://zhiqingyun-demo.isxcode.com/tools/open/file/qrcode.jpg', type: 'image' },
        { url: 'https://zhiqingyun-demo.isxcode.com/tools/open/file/AlimamaShuHeiTi-Bold.woff2', type: 'font' },
        { url: 'https://zhiqingyun-demo.isxcode.com/tools/open/file/AlibabaPuHuiTi-2-45-Light.woff2', type: 'font' },
        { url: 'https://zhiqingyun-demo.isxcode.com/tools/open/file/product.mp4', type: 'video' }
      ];

      let loadedCount = 0;
      const totalResources = orderedResources.length + 1; // +1 for bg-0.jpg
      let backgroundLoaded = false;

      // 资源加载完成回调
      const onResourceLoaded = () => {
        loadedCount++;
        const progress = Math.floor((loadedCount / totalResources) * 100);
        
        // 更新预加载loading条进度
        if ((window as any).__preloadLoading) {
          (window as any).__preloadLoading.setProgress(Math.min(progress, 90));
        }

        // 所有关键资源加载完成
        if (loadedCount >= totalResources) {
          console.log('所有关键资源加载完成');
          setTimeout(() => {
            if ((window as any).__preloadLoading) {
              (window as any).__preloadLoading.complete();
            }
          }, 200);
        }
      };

      // 单进程预加载单个资源
      const preloadResource = (resource: { url: string; type: string }, index: number) => {
        return new Promise<void>((resolve, reject) => {
          const { url, type } = resource;
          const startTime = Date.now();

          // 设置单个资源的超时时间（5秒）
          const timeout = setTimeout(() => {
            console.warn(`资源加载超时: ${url}`);
            onResourceLoaded();
            resolve();
          }, 5000);

          const handleSuccess = () => {
            clearTimeout(timeout);
            const loadTime = Date.now() - startTime;
            console.log(`资源加载成功 (${loadTime}ms): ${url}`);
            onResourceLoaded();
            resolve();
          };

          const handleError = (error?: any) => {
            clearTimeout(timeout);
            console.warn(`资源加载失败: ${url}`, error);
            onResourceLoaded(); // 即使失败也要计数
            resolve(); // 不要 reject，继续加载下一个
          };

          if (type === 'image') {
            const img = new Image();
            img.onload = handleSuccess;
            img.onerror = handleError;
            img.src = url;
          } else if (type === 'font') {
            const link = document.createElement('link');
            link.rel = 'preload';
            link.as = 'font';
            link.type = 'font/woff2';
            link.crossOrigin = 'anonymous';
            link.href = url;
            link.onload = handleSuccess;
            link.onerror = handleError;
            document.head.appendChild(link);
          } else if (type === 'video') {
            const video = document.createElement('video');
            video.preload = 'metadata';
            video.muted = true;
            video.style.display = 'none';
            video.src = url;

            video.addEventListener('loadedmetadata', handleSuccess);
            video.addEventListener('error', handleError);

            document.body.appendChild(video);

            // 清理视频元素
            setTimeout(() => {
              if (video.parentNode) {
                document.body.removeChild(video);
              }
            }, 8000);
          }
        });
      };

      // 优先加载并显示背景图
      const loadCriticalBackground = () => {
        return new Promise<void>((resolve) => {
          console.log('开始预加载关键背景图: bg-0.jpg');

          // 先隐藏背景图区域，避免渐进式显示
          const moduleIntro = document.querySelector('.module-intro') as HTMLElement;
          if (moduleIntro) {
            moduleIntro.style.backgroundImage = 'none';
            moduleIntro.style.opacity = '0';
            moduleIntro.style.transition = 'opacity 0.5s ease-in-out';
          }

          const img = new Image();

          const timeout = setTimeout(() => {
            console.warn('背景图加载超时，显示默认状态');
            if (moduleIntro) {
              moduleIntro.style.opacity = '1';
            }
            onResourceLoaded();
            backgroundLoaded = true;
            resolve();
          }, 10000);

          img.onload = () => {
            clearTimeout(timeout);
            console.log('✓ 背景图完全加载到内存，准备显示');

            // 确保图片完全加载后再显示
            setTimeout(() => {
              if (moduleIntro) {
                // 一次性设置背景图并显示
                moduleIntro.style.backgroundImage = `url("${criticalBackgroundUrl}")`;
                moduleIntro.style.backgroundSize = 'cover';
                moduleIntro.style.backgroundPosition = 'center';
                moduleIntro.style.backgroundRepeat = 'no-repeat';

                // 淡入显示
                setTimeout(() => {
                  moduleIntro.style.opacity = '1';
                  console.log('✓ 背景图已完整显示');
                }, 50);
              }

              onResourceLoaded();
              backgroundLoaded = true;

              // 等待淡入动画完成
              setTimeout(() => {
                console.log('背景图显示动画完成，准备加载其他资源');
                resolve();
              }, 600);
            }, 100);
          };

          img.onerror = () => {
            clearTimeout(timeout);
            console.warn('背景图加载失败');
            if (moduleIntro) {
              moduleIntro.style.opacity = '1';
            }
            onResourceLoaded();
            backgroundLoaded = true;
            resolve();
          };

          // 开始加载图片到内存
          img.src = criticalBackgroundUrl;
        });
      };

      // 单进程顺序预加载其他资源
      const preloadResourcesInOrder = async () => {
        console.log('开始单进程顺序预加载其他资源...');

        for (let i = 0; i < orderedResources.length; i++) {
          const resource = orderedResources[i];
          console.log(`正在加载第 ${i + 1}/${orderedResources.length} 个资源: ${resource.url}`);

          try {
            await preloadResource(resource, i);
            console.log(`✓ 第 ${i + 1} 个资源加载完成`);
          } catch (error) {
            console.warn(`✗ 第 ${i + 1} 个资源加载失败:`, error);
          }

          // 每个资源加载完成后稍作停顿，确保单进程执行
          if (i < orderedResources.length - 1) {
            await new Promise(resolve => setTimeout(resolve, 100));
          }
        }

        console.log('所有资源预加载完成！');
      };

      // 智能预加载策略
      const startPreloading = async () => {
        // 检查网络连接类型
        const connection = (navigator as any).connection || (navigator as any).mozConnection || (navigator as any).webkitConnection;
        const isSlowConnection = connection && (connection.effectiveType === 'slow-2g' || connection.effectiveType === '2g');

        try {
          // 第一步：优先加载并显示背景图
          await loadCriticalBackground();

          if (isSlowConnection) {
            console.log('检测到慢速网络，背景图加载完成后只加载关键资源');
            // 慢速网络只预加载最关键的资源（前5个）
            const criticalResources = orderedResources.slice(0, 5);
            for (let i = 0; i < criticalResources.length; i++) {
              await preloadResource(criticalResources[i], i);
              if (i < criticalResources.length - 1) {
                await new Promise(resolve => setTimeout(resolve, 200));
              }
            }
          } else {
            // 第二步：背景图显示完成后，单进程顺序预加载其他资源
            console.log('背景图已显示，开始加载其他资源');
            await preloadResourcesInOrder();
          }
        } catch (error) {
          console.error('预加载过程中出现错误:', error);
          // 确保即使出错也完成loading
          if (loadedCount < totalResources) {
            loadedCount = totalResources;
            onResourceLoaded();
          }
        }
      };

      // 确保 DOM 准备就绪后再开始预加载
      const initPreloading = () => {
        // 等待页面基本渲染完成
        setTimeout(() => {
          console.log('开始初始化资源预加载...');
          startPreloading();
        }, 200);
      };

      if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', initPreloading);
      } else {
        initPreloading();
      }

      // 设置超时保护
      setTimeout(() => {
        if (loadedCount < totalResources) {
          console.log('资源预加载超时，强制完成loading');
          loadedCount = totalResources;
          onResourceLoaded();
        }
      }, 8000); // 8秒超时
    }
  }
});

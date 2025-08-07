export default defineNuxtPlugin({
  name: 'resource-preloader',
  parallel: false,
  setup() {
    if (process.client) {
      // 关键资源配置
      const criticalResources = {
        images: [
          'https://zhiqingyun-demo.isxcode.com/tools/open/file/bg-0.jpg',
          'https://zhiqingyun-demo.isxcode.com/tools/open/file/product.jpg',
          'https://zhiqingyun-demo.isxcode.com/tools/open/file/bg-2.jpg',
          'https://zhiqingyun-demo.isxcode.com/tools/open/file/logo.jpg'
        ],
        fonts: [
          'https://zhiqingyun-demo.isxcode.com/tools/open/file/AlimamaShuHeiTi-Bold.woff2',
          'https://zhiqingyun-demo.isxcode.com/tools/open/file/AlibabaPuHuiTi-2-45-Light.woff2'
        ],
        videos: [
          'https://zhiqingyun-demo.isxcode.com/tools/open/file/product.mp4'
        ]
      };

      let loadedCount = 0;
      const totalResources = criticalResources.images.length + 
                           criticalResources.fonts.length + 
                           criticalResources.videos.length;

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

      // 预加载图片
      const preloadImages = () => {
        criticalResources.images.forEach(url => {
          const img = new Image();
          img.onload = onResourceLoaded;
          img.onerror = onResourceLoaded; // 即使失败也要计数
          img.src = url;
        });
      };

      // 预加载字体
      const preloadFonts = () => {
        criticalResources.fonts.forEach(url => {
          const link = document.createElement('link');
          link.rel = 'preload';
          link.as = 'font';
          link.type = 'font/woff2';
          link.crossOrigin = 'anonymous';
          link.href = url;
          link.onload = onResourceLoaded;
          link.onerror = onResourceLoaded;
          document.head.appendChild(link);
        });
      };

      // 预加载视频元数据
      const preloadVideos = () => {
        criticalResources.videos.forEach(url => {
          const video = document.createElement('video');
          video.preload = 'metadata';
          video.muted = true;
          video.style.display = 'none';
          video.src = url;
          
          video.addEventListener('loadedmetadata', onResourceLoaded);
          video.addEventListener('error', onResourceLoaded);
          
          document.body.appendChild(video);
          
          // 清理视频元素
          setTimeout(() => {
            if (video.parentNode) {
              document.body.removeChild(video);
            }
          }, 5000);
        });
      };

      // 智能预加载策略
      const startPreloading = () => {
        // 检查网络连接类型
        const connection = (navigator as any).connection || (navigator as any).mozConnection || (navigator as any).webkitConnection;
        const isSlowConnection = connection && (connection.effectiveType === 'slow-2g' || connection.effectiveType === '2g');
        
        if (isSlowConnection) {
          console.log('检测到慢速网络，只预加载关键图片');
          // 慢速网络只预加载最关键的资源
          const criticalImg = new Image();
          criticalImg.onload = () => {
            loadedCount = totalResources; // 直接完成
            onResourceLoaded();
          };
          criticalImg.src = criticalResources.images[0]; // 只加载背景图
        } else {
          // 正常网络预加载所有资源
          preloadImages();
          preloadFonts();
          preloadVideos();
        }
      };

      // 延迟启动预加载，避免阻塞关键渲染路径
      if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', () => {
          setTimeout(startPreloading, 100);
        });
      } else {
        setTimeout(startPreloading, 100);
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

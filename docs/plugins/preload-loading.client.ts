export default defineNuxtPlugin({
  name: 'preload-loading',
  parallel: false, // 确保串行执行，优先级高
  setup() {
    // 在客户端处理预加载loading条
    if (process.client) {
      // 检查是否已经有内联的预加载loading条（来自app.html）
      const existingPreloadLoading = (window as any).__preloadLoading;

      if (existingPreloadLoading) {
        // console.log('发现内联预加载loading条，使用现有的loading条');

        // 如果已经有内联loading条，确保它正在显示
        existingPreloadLoading.show();

        // 监听DOM加载完成
        if (document.readyState === 'loading') {
          document.addEventListener('DOMContentLoaded', () => {
            // DOM加载完成后，等待Vue组件接管
            setTimeout(() => {
              if ((window as any).__preloadLoading) {
                (window as any).__preloadLoading.hide();
              }
            }, 100);
          });
        } else {
          // 如果DOM已经加载完成，稍后隐藏预加载loading条
          setTimeout(() => {
            if ((window as any).__preloadLoading) {
              (window as any).__preloadLoading.hide();
            }
          }, 100);
        }

        // 页面完全加载后清理预加载loading条
        window.addEventListener('load', () => {
          setTimeout(() => {
            if ((window as any).__preloadLoading) {
              (window as any).__preloadLoading.remove();
              delete (window as any).__preloadLoading;
            }
          }, 1000);
        });
      } else {
        // console.warn('预加载loading条未找到，创建备用loading条');

        // 如果没有内联loading条，创建备用的loading条
        // 创建内联样式，确保loading条样式立即可用
        const inlineStyle = document.createElement('style');
        inlineStyle.id = 'preload-loading-styles';
        inlineStyle.innerHTML = `
          .top-loading-bar {
            position: fixed !important;
            top: 0 !important;
            left: 0 !important;
            right: 0 !important;
            z-index: 9999 !important;
            height: 4px !important;
            background-color: transparent !important;
            transition: opacity 0.3s ease !important;
          }

          .top-loading-bar__progress {
            height: 100% !important;
            background: linear-gradient(90deg, #e25a1b 0%, #d4461a 50%, #c73e1d 100%) !important;
            transition: width 0.3s ease !important;
            border-radius: 0 3px 3px 0 !important;
            box-shadow: 0 0 12px rgba(226, 90, 27, 0.6) !important;
          }

          .top-loading-bar--loading .top-loading-bar__progress {
            background: linear-gradient(90deg, #e25a1b 0%, #d4461a 100%) !important;
            animation: loading-shimmer 2s infinite !important;
          }

          @keyframes loading-shimmer {
            0% { background-position: -200px 0; }
            100% { background-position: calc(200px + 100%) 0; }
          }
        `;

        // 插入到head的最前面，确保优先级
        document.head.insertBefore(inlineStyle, document.head.firstChild);

        // 预创建loading条DOM结构，确保立即可用
        const preloadLoadingBar = document.createElement('div');
        preloadLoadingBar.id = 'preload-loading-bar';
        preloadLoadingBar.className = 'top-loading-bar';
        preloadLoadingBar.style.display = 'none'; // 初始隐藏

        const progressBar = document.createElement('div');
        progressBar.className = 'top-loading-bar__progress';
        progressBar.style.width = '0%';

        preloadLoadingBar.appendChild(progressBar);
        document.body.appendChild(preloadLoadingBar);

        // 提供全局方法来控制预加载loading条
        (window as any).__preloadLoading = {
          show: () => {
            preloadLoadingBar.style.display = 'block';
            preloadLoadingBar.classList.add('top-loading-bar--loading');
            progressBar.style.width = '5%';
          },
          hide: () => {
            preloadLoadingBar.style.display = 'none';
            preloadLoadingBar.classList.remove('top-loading-bar--loading');
            progressBar.style.width = '0%';
          },
          setProgress: (progress: number) => {
            progressBar.style.width = `${Math.min(Math.max(progress, 0), 100)}%`;
          },
          remove: () => {
            if (preloadLoadingBar.parentNode) {
              preloadLoadingBar.parentNode.removeChild(preloadLoadingBar);
            }
            if (inlineStyle.parentNode) {
              inlineStyle.parentNode.removeChild(inlineStyle);
            }
          }
        };

        // 立即显示loading条
        (window as any).__preloadLoading.show();

        // 监听页面加载状态
        if (document.readyState === 'loading') {
          // 监听DOM加载完成
          document.addEventListener('DOMContentLoaded', () => {
            // DOM加载完成后，等待Vue组件接管
            setTimeout(() => {
              if ((window as any).__preloadLoading) {
                (window as any).__preloadLoading.hide();
              }
            }, 100);
          });
        }

        // 页面完全加载后清理预加载loading条
        window.addEventListener('load', () => {
          setTimeout(() => {
            if ((window as any).__preloadLoading) {
              (window as any).__preloadLoading.remove();
              delete (window as any).__preloadLoading;
            }
          }, 1000);
        });
      }
    }
  }
});

export default defineNuxtPlugin({
  name: 'page-cache',
  parallel: false,
  setup() {
    if (process.client) {
      const router = useRouter();
      
      // 存储页面状态的缓存
      const pageCache = new Map();
      
      // 首页背景图URL
      const homeBackgroundUrl = 'https://zhiqingyun-demo.isxcode.com/tools/open/file/bg-0.jpg';
      
      // 预加载首页关键资源
      const preloadHomeResources = () => {
        // 预加载背景图
        const img = new Image();
        img.src = homeBackgroundUrl;
        
        // 预加载其他关键资源
        const criticalResources = [
          'https://zhiqingyun-demo.isxcode.com/tools/open/file/logo.jpg',
          'https://zhiqingyun-demo.isxcode.com/tools/open/file/product.jpg'
        ];
        
        criticalResources.forEach(url => {
          const resource = new Image();
          resource.src = url;
        });
      };
      
      // 快速恢复首页状态
      const restoreHomePage = () => {
        const moduleIntro = document.querySelector('.module-intro') as HTMLElement;
        if (!moduleIntro) return;
        
        // 检查背景图是否已缓存
        const img = new Image();
        img.src = homeBackgroundUrl;
        
        if (img.complete && img.naturalWidth > 0) {
          // 立即显示缓存的背景图
          moduleIntro.style.backgroundImage = `url("${homeBackgroundUrl}")`;
          moduleIntro.style.backgroundSize = 'cover';
          moduleIntro.style.backgroundPosition = 'center';
          moduleIntro.style.backgroundRepeat = 'no-repeat';
          moduleIntro.style.opacity = '1';
          moduleIntro.style.transition = 'none'; // 移除过渡效果，立即显示
          
          // console.log('✓ 首页背景图从缓存快速恢复');
          
          // 短暂延迟后恢复过渡效果
          setTimeout(() => {
            moduleIntro.style.transition = 'opacity 0.3s ease-in-out';
          }, 100);
        }
      };
      
      // 路由守卫：离开页面时保存状态
      router.beforeEach((to, from) => {
        if (from.path === '/'||from.path === '/en') {
          // 离开首页时，保存页面状态
          const moduleIntro = document.querySelector('.module-intro') as HTMLElement;
          if (moduleIntro) {
            pageCache.set('home-background-loaded', {
              backgroundImage: moduleIntro.style.backgroundImage,
              opacity: moduleIntro.style.opacity,
              timestamp: Date.now()
            });
          }
        }
      });
      
      // 路由守卫：进入页面时恢复状态
      router.afterEach((to, from) => {
        if ((to.path === '/'||to.path === '/en') && (from.path !== '/'|| from.path !== '/en')) {
          // 返回首页时，尝试快速恢复
          nextTick(() => {
            const cachedState = pageCache.get('home-background-loaded');
            const now = Date.now();
            
            // 如果缓存状态存在且未过期（5分钟内）
            if (cachedState && (now - cachedState.timestamp) < 300000) {
              restoreHomePage();
            } else {
              // 缓存过期，清理并重新加载
              pageCache.delete('home-background-loaded');
            }
          });
        }
      });
      
      // 页面加载完成后预加载资源
      window.addEventListener('load', () => {
        preloadHomeResources();
      });
      
      // 监听页面可见性变化，当页面重新可见时检查缓存
      document.addEventListener('visibilitychange', () => {
        if (!document.hidden && (router.currentRoute.value.path === '/'||router.currentRoute.value.path === '/en')) {
          setTimeout(() => {
            restoreHomePage();
          }, 100);
        }
      });
      
      // 监听浏览器前进后退
      window.addEventListener('popstate', () => {
        if (router.currentRoute.value.path === '/'||router.currentRoute.value.path === '/en') {
          setTimeout(() => {
            restoreHomePage();
          }, 50);
        }
      });
    }
  }
});

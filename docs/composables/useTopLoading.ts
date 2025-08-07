interface TopLoadingInstance {
  start: () => void;
  finish: () => void;
  error: () => void;
  hide: () => void;
  reset: () => void;
}

let loadingInstance: TopLoadingInstance | null = null;

export const useTopLoading = () => {
  // 设置loading实例
  const setInstance = (instance: TopLoadingInstance) => {
    loadingInstance = instance;
  };

  // 开始loading
  const start = () => {
    if (loadingInstance) {
      loadingInstance.start();
    }
  };

  // 完成loading
  const finish = () => {
    if (loadingInstance) {
      loadingInstance.finish();
    }
  };

  // 错误状态
  const error = () => {
    if (loadingInstance) {
      loadingInstance.error();
    }
  };

  // 隐藏loading
  const hide = () => {
    if (loadingInstance) {
      loadingInstance.hide();
    }
  };

  // 重置loading
  const reset = () => {
    if (loadingInstance) {
      loadingInstance.reset();
    }
  };

  return {
    setInstance,
    start,
    finish,
    error,
    hide,
    reset
  };
};
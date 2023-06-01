import { createStore } from "vuex";
import createPersistedstate from "vuex-persistedstate";

import { authStore } from "./auth.store";

export default createStore({
  modules: {
    authStoreModule: authStore,
  },
  plugins: [
    // 默认是存储在localStorage中
    createPersistedstate({
      // key: 存储数据的键名
      key: "__vuex__",
      // paths:存储state中的那些数据
      paths: ["authStoreModule"],
    }),
  ],
});

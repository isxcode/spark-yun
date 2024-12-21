export const useCounterStore = defineStore("counter", () => {
  const height = ref(0);
  function setHeightState(val) {
    height.value = val;
  }

  return { height, setHeightState };
});

// 存储菜单数据
export const useMenuStore = defineStore("menu", () => {
  const menuList = ref([]);
  function setMenuList(val) {
    menuList.value = val;
  }

  return { menuList, setMenuList };
});

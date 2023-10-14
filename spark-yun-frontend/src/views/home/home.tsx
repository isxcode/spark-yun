import { computed, defineComponent, type CSSProperties } from "vue";
import { menuListData } from "./menu.config";
import { useRouterMenu } from "./hooks/use-router-menu";

import logoURL from '@/assets/imgs/logo.png';
import './home.scss';

export default defineComponent({
  setup() {
    const { renderHomeMenu, isCollapse } = useRouterMenu(menuListData)

    const homeClass = computed<Record<string, boolean>>(() => ({
      'zqy-home': true,
      'is-collapse': isCollapse.value
    }))


    return () => (
      <div class={homeClass.value}>
        <div class="zqy-home__sidebar">
          <div class="zqy-home__nav">
            <img class="zqy-home__logo" src={logoURL} alt="logo" />
            <span class="zqy-home__title">至轻云</span>
          </div>
          { renderHomeMenu() }
        </div>
        <div class="zqy-home__main">
          <router-view></router-view>
        </div>
      </div>
    )
  }
})
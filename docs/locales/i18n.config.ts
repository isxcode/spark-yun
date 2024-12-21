import HomeEn from "./home/en.json";
import HomeZh from "./home/zh.json";
import layoutEn from "./layout/en.json";
import layoutZh from "./layout/zh.json";

const en = {
  ...HomeEn,
  ...layoutEn,
};

const zh = {
  ...HomeZh,
  ...layoutZh,
};

export default defineI18nConfig(() => ({
  legacy: false,
  locale: "zh",
  messages: {
    en,
    zh,
  },
}));

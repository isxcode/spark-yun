import HomeEn from "./home/en.json";
import HomeZh from "./home/zh.json";
import HomeJa from "./home/ja.json";
import HomeKo from "./home/ko.json";
import HomeFr from "./home/fr.json";
import HomeDe from "./home/de.json";
import HomeEs from "./home/es.json";
import HomeRu from "./home/ru.json";
import layoutEn from "./layout/en.json";
import layoutZh from "./layout/zh.json";
import layoutJa from "./layout/ja.json";
import layoutKo from "./layout/ko.json";
import layoutFr from "./layout/fr.json";
import layoutDe from "./layout/de.json";
import layoutEs from "./layout/es.json";
import layoutRu from "./layout/ru.json";

const en = {
  ...HomeEn,
  ...layoutEn,
};

const zh = {
  ...HomeZh,
  ...layoutZh,
};

const ja = {
  ...HomeJa,
  ...layoutJa,
};

const ko = {
  ...HomeKo,
  ...layoutKo,
};

const fr = {
  ...HomeFr,
  ...layoutFr,
};

const de = {
  ...HomeDe,
  ...layoutDe,
};

const es = {
  ...HomeEs,
  ...layoutEs,
};

const ru = {
  ...HomeRu,
  ...layoutRu,
};

export default defineI18nConfig(() => ({
  legacy: false,
  locale: "zh",
  messages: {
    en,
    zh,
    ja,
    ko,
    fr,
    de,
    es,
    ru,
  },
}));

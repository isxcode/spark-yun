export default defineNuxtPlugin(() => {
  if (process.client) {
    // // 屏幕适应
    // (function (win, doc) {
    //   if (!win.addEventListener) return;
    //   function setFont() {
    //     let screenWidth = document.querySelector("html")!.offsetWidth;
    //     const baseSize = 37.5;
    //     const pageWidth = 750;
    //     let fontSize = (baseSize * screenWidth) / pageWidth;
    //     document.querySelector("html")!.style.fontSize = `${fontSize}px`;
    //   }
    //   setFont();
    //   setTimeout(() => {
    //     setFont();
    //   }, 300);
    //   doc.addEventListener("DOMContentLoaded", setFont, false);
    //   win.addEventListener("resize", setFont, false);
    //   win.addEventListener("load", setFont, false);
    // })(window, document);
    // (function flexible(window, document) {
    //   var docEl = document.documentElement;
    //   var dpr = window.devicePixelRatio || 1;

    //   // adjust body font size
    //   function setBodyFontSize() {
    //     if (document.body) {
    //       document.body.style.fontSize = 12 * dpr + "px";
    //     } else {
    //       document.addEventListener("DOMContentLoaded", setBodyFontSize);
    //     }
    //   }
    //   setBodyFontSize();

    //   // set 1rem = viewWidth / 10
    //   function setRemUnit() {
    //     var rem = docEl.clientWidth / 10;
    //     docEl.style.fontSize = rem + "px";
    //   }

    //   setRemUnit();

    //   // reset rem unit on page resize
    //   window.addEventListener("resize", setRemUnit);
    //   window.addEventListener("pageshow", function (e) {
    //     if (e.persisted) {
    //       setRemUnit();
    //     }
    //   });

    //   // detect 0.5px supports
    //   if (dpr >= 2) {
    //     var fakeBody = document.createElement("body");
    //     var testElement = document.createElement("div");
    //     testElement.style.border = ".5px solid transparent";
    //     fakeBody.appendChild(testElement);
    //     docEl.appendChild(fakeBody);
    //     if (testElement.offsetHeight === 1) {
    //       docEl.classList.add("hairlines");
    //     }
    //     docEl.removeChild(fakeBody);
    //   }
    // })(window, document);
  }
});

document.documentElement.classList.add("motion-enabled");
window.__marketplaceMotionFallback = window.setTimeout(function () {
    document.documentElement.classList.add("motion-ready");
}, 2500);

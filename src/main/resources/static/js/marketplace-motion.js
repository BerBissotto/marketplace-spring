(function () {
    "use strict";

    const root = document.documentElement;
    const reducedMotionQuery = window.matchMedia("(prefers-reduced-motion: reduce)");
    const loadingButtons = new Map();
    let progressElement;
    let progressBar;
    let liveRegion;
    let progressValue = 0;
    let progressTimer;

    function prefersReducedMotion() {
        return reducedMotionQuery.matches;
    }

    function createFeedbackElements() {
        progressElement = document.createElement("div");
        progressElement.className = "app-progress";
        progressElement.setAttribute("role", "progressbar");
        progressElement.setAttribute("aria-label", "Carregamento da página");
        progressElement.setAttribute("aria-valuemin", "0");
        progressElement.setAttribute("aria-valuemax", "100");

        progressBar = document.createElement("div");
        progressBar.className = "app-progress__bar";
        progressElement.appendChild(progressBar);

        liveRegion = document.createElement("div");
        liveRegion.className = "visually-hidden";
        liveRegion.setAttribute("role", "status");
        liveRegion.setAttribute("aria-live", "polite");
        liveRegion.setAttribute("aria-atomic", "true");

        document.body.prepend(liveRegion);
        document.body.prepend(progressElement);
    }

    function announce(message) {
        if (!liveRegion) {
            return;
        }

        liveRegion.textContent = "";
        window.requestAnimationFrame(function () {
            liveRegion.textContent = message;
        });
    }

    function setProgress(value) {
        progressValue = Math.max(0, Math.min(100, value));
        progressElement.setAttribute("aria-valuenow", String(Math.round(progressValue)));
        progressBar.style.transform = "scaleX(" + progressValue / 100 + ")";
    }

    function startProgress(message) {
        window.clearInterval(progressTimer);
        progressElement.classList.add("is-active");
        setProgress(Math.max(progressValue, 12));
        announce(message || "Carregando conteúdo");

        progressTimer = window.setInterval(function () {
            if (progressValue >= 82) {
                window.clearInterval(progressTimer);
                return;
            }

            setProgress(progressValue + Math.max(2, (82 - progressValue) * 0.16));
        }, 240);
    }

    function completeProgress(message) {
        window.clearInterval(progressTimer);

        if (!progressElement) {
            return;
        }

        setProgress(100);
        announce(message || "Conteúdo carregado");

        window.setTimeout(function () {
            progressElement.classList.remove("is-active");
            setProgress(0);
        }, prefersReducedMotion() ? 0 : 180);
    }

    function revealMotionTargets() {
        const targets = [];
        const header = document.querySelector("body > header");
        const footer = document.querySelector("body > footer");

        if (header) {
            targets.push(header);
        }

        document.querySelectorAll("main > *").forEach(function (element) {
            if (!element.matches("script, style, input[type='hidden']")) {
                targets.push(element);
            }
        });

        if (footer) {
            targets.push(footer);
        }

        targets.forEach(function (element) {
            element.classList.add("motion-target");
        });

        if (prefersReducedMotion() || !("IntersectionObserver" in window)) {
            targets.forEach(function (element) {
                element.classList.add("is-motion-visible");
            });
            return;
        }

        const observer = new IntersectionObserver(function (entries) {
            entries.forEach(function (entry) {
                if (!entry.isIntersecting) {
                    return;
                }

                entry.target.classList.add("is-motion-visible");
                observer.unobserve(entry.target);
            });
        }, {
            threshold: 0.06,
            rootMargin: "0px 0px -6%"
        });

        targets.forEach(function (element) {
            observer.observe(element);
        });
    }

    function markImageLoaded(image) {
        image.classList.remove("is-image-error");
        image.classList.add("is-image-loaded");
    }

    function markImageError(image) {
        image.classList.add("is-image-loaded", "is-image-error");
    }

    function prepareImage(image) {
        const isPriorityImage = image.matches(
            "[data-image-priority='high'], .navbar-brand img, #imagemPrincipal, .carousel-item.active img"
        );

        image.setAttribute("data-motion-image", "");
        image.decoding = "async";

        if (isPriorityImage) {
            image.loading = "eager";
            image.fetchPriority = "high";
        } else {
            image.loading = "lazy";
        }

        if (image.complete) {
            if (image.naturalWidth > 0) {
                markImageLoaded(image);
            } else {
                markImageError(image);
            }
            return;
        }

        image.addEventListener("load", function () {
            markImageLoaded(image);
        }, {once: true});

        image.addEventListener("error", function () {
            markImageError(image);
        }, {once: true});
    }

    function setupImages(scope) {
        (scope || document).querySelectorAll("img").forEach(prepareImage);
    }

    function setupProductGallery() {
        const mainImage = document.getElementById("imagemPrincipal");
        const thumbnailButtons = document.querySelectorAll("[data-gallery-thumbnail]");

        if (!mainImage || thumbnailButtons.length === 0) {
            return;
        }

        thumbnailButtons.forEach(function (button, index) {
            button.setAttribute("aria-pressed", index === 0 ? "true" : "false");

            button.addEventListener("click", function () {
                const thumbnail = button.querySelector("img");
                if (!thumbnail) {
                    return;
                }

                const nextSource = thumbnail.currentSrc || thumbnail.src;
                const nextAlt = thumbnail.alt || mainImage.alt;
                if (mainImage.currentSrc === nextSource || mainImage.src === nextSource) {
                    return;
                }

                thumbnailButtons.forEach(function (item) {
                    item.setAttribute("aria-pressed", "false");
                });
                button.setAttribute("aria-pressed", "true");

                const preload = new Image();
                preload.decoding = "async";
                preload.src = nextSource;
                mainImage.classList.add("is-image-switching");
                announce("Carregando imagem selecionada");

                const performSwap = function () {
                    window.setTimeout(function () {
                        mainImage.src = nextSource;
                        mainImage.alt = nextAlt;
                        mainImage.classList.remove("is-image-switching");
                        markImageLoaded(mainImage);
                        announce("Imagem selecionada carregada");
                    }, prefersReducedMotion() ? 0 : 90);
                };

                if (preload.complete) {
                    performSwap();
                } else {
                    preload.addEventListener("load", performSwap, {once: true});
                    preload.addEventListener("error", function () {
                        mainImage.classList.remove("is-image-switching");
                        announce("Não foi possível carregar a imagem selecionada");
                    }, {once: true});
                }
            });
        });
    }

    function createLoadingIndicator() {
        const indicator = document.createElement("span");
        indicator.className = "button-loading-indicator";
        indicator.setAttribute("aria-hidden", "true");
        return indicator;
    }

    function setButtonLoading(button) {
        if (!button || loadingButtons.has(button)) {
            return;
        }

        loadingButtons.set(button, button.innerHTML);
        button.textContent = "";
        button.appendChild(createLoadingIndicator());
        button.appendChild(document.createTextNode(" "));
        button.classList.add("is-loading");
        button.setAttribute("aria-disabled", "true");
    }

    function restoreLoadingButtons() {
        loadingButtons.forEach(function (content, button) {
            button.innerHTML = content;
            button.classList.remove("is-loading");
            button.removeAttribute("aria-disabled");
        });
        loadingButtons.clear();

        document.querySelectorAll("form[aria-busy='true']").forEach(function (form) {
            form.removeAttribute("aria-busy");
            delete form.dataset.motionSubmitting;
        });
    }

    function setupForms() {
        document.querySelectorAll("form").forEach(function (form) {
            form.addEventListener("submit", function (event) {
                if (event.defaultPrevented || !form.checkValidity()) {
                    return;
                }

                if (form.dataset.motionSubmitting === "true") {
                    event.preventDefault();
                    return;
                }

                form.dataset.motionSubmitting = "true";
                form.setAttribute("aria-busy", "true");
                setButtonLoading(event.submitter || form.querySelector("button[type='submit'], input[type='submit']"));
                startProgress("Processando solicitação");

                if (!prefersReducedMotion()) {
                    document.body.classList.add("is-page-leaving");
                }
            });
        });
    }

    function isNavigableLink(event, link) {
        if (!link || event.defaultPrevented || event.button !== 0) {
            return false;
        }

        if (event.metaKey || event.ctrlKey || event.shiftKey || event.altKey) {
            return false;
        }

        if (link.hasAttribute("download") || link.target === "_blank" || link.hasAttribute("data-bs-toggle")) {
            return false;
        }

        const rawHref = link.getAttribute("href");
        if (!rawHref || rawHref.startsWith("#") || rawHref.startsWith("javascript:") || rawHref.startsWith("mailto:") || rawHref.startsWith("tel:")) {
            return false;
        }

        const destination = new URL(link.href, window.location.href);
        return destination.origin === window.location.origin;
    }

    function setupPageExit() {
        document.addEventListener("click", function (event) {
            const link = event.target.closest("a[href]");
            if (!isNavigableLink(event, link)) {
                return;
            }

            event.preventDefault();
            startProgress("Carregando página");

            const keyboardInitiated = event.detail === 0;
            if (!prefersReducedMotion() && !keyboardInitiated) {
                document.body.classList.add("is-page-leaving");
            }

            window.setTimeout(function () {
                window.location.assign(link.href);
            }, prefersReducedMotion() || keyboardInitiated ? 0 : 140);
        });
    }

    function formatFileSize(size) {
        if (size < 1024 * 1024) {
            return Math.max(1, Math.round(size / 1024)) + " KB";
        }
        return (size / (1024 * 1024)).toFixed(1).replace(".", ",") + " MB";
    }

    function setupImagePreviews() {
        document.querySelectorAll("input[type='file'][accept*='image']").forEach(function (input) {
            let objectUrls = [];

            input.addEventListener("change", function () {
                objectUrls.forEach(URL.revokeObjectURL);
                objectUrls = [];

                const oldRegion = input.parentElement.querySelector(".file-preview-region");
                if (oldRegion) {
                    oldRegion.remove();
                }

                const files = Array.from(input.files || []);
                if (files.length === 0) {
                    return;
                }

                const region = document.createElement("div");
                region.className = "file-preview-region";

                const status = document.createElement("p");
                status.className = "small text-muted mb-2";
                status.setAttribute("role", "status");
                const totalSize = files.reduce(function (sum, file) {
                    return sum + file.size;
                }, 0);
                status.textContent = files.length + (files.length === 1 ? " imagem selecionada" : " imagens selecionadas") +
                    " — " + formatFileSize(totalSize);
                region.appendChild(status);

                const grid = document.createElement("div");
                grid.className = "file-preview-grid";

                files.forEach(function (file) {
                    const objectUrl = URL.createObjectURL(file);
                    objectUrls.push(objectUrl);

                    const preview = document.createElement("img");
                    preview.src = objectUrl;
                    preview.alt = "Prévia de " + file.name;
                    preview.loading = "lazy";
                    grid.appendChild(preview);
                });

                region.appendChild(grid);
                input.insertAdjacentElement("afterend", region);
                setupImages(region);
                announce(status.textContent);
            });
        });
    }

    function resetPageState() {
        document.body.classList.remove("is-page-leaving");
        restoreLoadingButtons();
        completeProgress("Página pronta");
    }

    function initialize() {
        window.clearTimeout(window.__marketplaceMotionFallback);
        createFeedbackElements();
        startProgress("Carregando conteúdo");
        revealMotionTargets();
        setupImages(document);
        setupProductGallery();
        setupForms();
        setupPageExit();
        setupImagePreviews();

        root.classList.add("motion-ready");
        window.requestAnimationFrame(function () {
            window.requestAnimationFrame(function () {
                document.querySelectorAll(".motion-target").forEach(function (element) {
                    const bounds = element.getBoundingClientRect();
                    if (bounds.top < window.innerHeight && bounds.bottom > 0) {
                        element.classList.add("is-motion-visible");
                    }
                });
                completeProgress("Conteúdo carregado");
            });
        });
    }

    if (document.readyState === "loading") {
        document.addEventListener("DOMContentLoaded", initialize, {once: true});
    } else {
        initialize();
    }

    window.addEventListener("pageshow", resetPageState);
}());

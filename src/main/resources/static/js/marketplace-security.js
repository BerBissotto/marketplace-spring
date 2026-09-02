document.addEventListener("DOMContentLoaded", function () {
    document.querySelectorAll("[data-confirm]").forEach(function (element) {
        element.addEventListener("click", function (event) {
            if (!window.confirm(element.dataset.confirm)) {
                event.preventDefault();
            }
        });
    });

    const cadastro = document.getElementById("formCadastro");
    if (cadastro) {
        cadastro.addEventListener("submit", function (event) {
            const senha = document.getElementById("senha");
            const confirmacao = document.getElementById("confirmarSenha");
            const senhasIguais = senha.value === confirmacao.value;

            confirmacao.classList.toggle("is-invalid", !senhasIguais);
            if (!senhasIguais) {
                event.preventDefault();
                confirmacao.focus();
            }
        });
    }
});

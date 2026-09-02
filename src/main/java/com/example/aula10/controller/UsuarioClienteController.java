package com.example.aula10.controller;

import com.example.aula10.model.Usuario;
import com.example.aula10.model.Endereco;
import com.example.aula10.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.WebDataBinder;

@Controller
public class UsuarioClienteController {

    @Autowired
    private UsuarioService usuarioService;

    @InitBinder("usuario")
    public void restringirCamposCadastro(WebDataBinder binder) {
        binder.setAllowedFields(
                "nome", "email", "senha", "telefone", "cpf",
                "endereco.logradouro", "endereco.numero", "endereco.bairro",
                "endereco.cidade", "endereco.cep", "endereco.complemento"
        );
    }

    // Exibir formulário
    @GetMapping("/formcliente")
    public String mostrarFormCadastro(Model model) {
        Usuario usuario = new Usuario();
        usuario.setEndereco(new Endereco());
        model.addAttribute("usuario", usuario);
        return "usuarios/formcliente";
    }

    // Receber submissão do formulário
    @PostMapping("/usuarioscliente/salvar")
    public String salvarUsuarioCliente(@Valid Usuario usuario,
                                       BindingResult result,
                                       @RequestParam String confirmarSenha,
                                       Model model) {

        // Confirmação da senha
        if (usuario.getSenha() == null || !usuario.getSenha().equals(confirmarSenha)) {
            result.rejectValue("senha", "error.usuario", "As senhas não coincidem.");
        }

        // Forçar o role como "user" - cliente não escolhe isso
        usuario.setRole("user");

        // Validação
        if (result.hasErrors()) {
            model.addAttribute("erro", "Por favor, corrija os erros abaixo.");
            return "usuarios/formcliente";
        }

        // Salvar no banco
        try {
            usuarioService.salvar(usuario);
        } catch (IllegalArgumentException exception) {
            result.reject("cadastro.invalido", exception.getMessage());
            model.addAttribute("erro", exception.getMessage());
            return "usuarios/formcliente";
        }

        // Redireciona para login ou outra página
        return "redirect:/login?cadastroSucesso";
    }
}

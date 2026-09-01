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
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class UsuarioClienteController {

    @Autowired
    private UsuarioService usuarioService;

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
                                       String confirmarSenha,
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
        usuarioService.salvar(usuario);

        // Redireciona para login ou outra página
        return "redirect:/login?cadastroSucesso";
    }
}

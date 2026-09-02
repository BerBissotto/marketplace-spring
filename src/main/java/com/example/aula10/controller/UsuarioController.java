package com.example.aula10.controller;

import com.example.aula10.model.Endereco;
import com.example.aula10.model.Usuario;
import com.example.aula10.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/usuariosadmin")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @InitBinder("usuario")
    public void restringirCamposUsuario(WebDataBinder binder) {
        binder.setAllowedFields(
                "id", "nome", "email", "senha", "telefone", "cpf", "role",
                "endereco.id", "endereco.logradouro", "endereco.numero", "endereco.bairro",
                "endereco.cidade", "endereco.cep", "endereco.complemento"
        );
    }

    @GetMapping
    public String listarUsuarios(Model model) {
        model.addAttribute("usuarios", usuarioService.listarUsuarios());
        return "usuarios/lista";
    }

    @GetMapping("/novo")
    public String novoUsuario(Model model) {
        Usuario usuario = new Usuario();
        usuario.setEndereco(new Endereco()); // inicializa o endereço para o formulário
        model.addAttribute("usuario", usuario);
        return "usuarios/form"; // ou onde estiver seu form
    }

    @PostMapping("/salvar")
    public String salvarUsuario(@Valid @ModelAttribute Usuario usuario, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "usuarios/form";
        }
        try {
            if (usuario.getEndereco() != null) {
                usuario.getEndereco().setUsuario(usuario);
            }
            usuarioService.salvar(usuario);
            return "redirect:/usuariosadmin";
        } catch (IllegalArgumentException e) {
            model.addAttribute("usuario", usuario);
            model.addAttribute("erro", e.getMessage());
            return "usuarios/form";
        }
    }

    @GetMapping("/editar/{id}")
    public String editarUsuario(@PathVariable Long id, Model model) {
        Usuario usuario = usuarioService.buscarPorId(id);

        if (usuario.getEndereco() == null) {
            usuario.setEndereco(new Endereco());
        }

        model.addAttribute("usuario", usuario);
        return "usuarios/form";
    }
    @PostMapping("/deletar/{id}")
    public String deletarUsuario(@PathVariable Long id) {
        usuarioService.deletar(id);
        return "redirect:/usuariosadmin";
    }
}

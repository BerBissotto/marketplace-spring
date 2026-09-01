package com.example.aula10.service;

import com.example.aula10.model.Usuario;
import com.example.aula10.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<Usuario> listarUsuarios() {
        return usuarioRepository.findAll();
    }

    public Usuario buscarPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    }

    public Usuario salvar(Usuario usuario) {
        if (usuario.getEndereco() != null) {
            usuario.getEndereco().setUsuario(usuario);
        }

        Optional<Usuario> existente = usuarioRepository.findByEmail(usuario.getEmail());

        List<String> rolesValidas = List.of("admin", "user");
        if (!rolesValidas.contains(usuario.getRole().toLowerCase())) {
            throw new IllegalArgumentException("Perfil inválido.");
        }

        if (existente.isPresent()) {
            if (usuario.getId() == null || !existente.get().getId().equals(usuario.getId())) {
                throw new IllegalArgumentException("E-mail já está em uso.");
            }
        }
        if (usuario.getId() != null && (usuario.getSenha() == null || usuario.getSenha().isBlank())) {
            Usuario atual = usuarioRepository.findById(usuario.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado."));
            usuario.setSenha(atual.getSenha());
        } else if (usuario.getSenha() == null || usuario.getSenha().isBlank()) {
            throw new IllegalArgumentException("A senha é obrigatória para novos usuários.");
        } else if (!usuario.getSenha().startsWith("$2a$")) {
            usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        }

        return usuarioRepository.save(usuario);
    }

    public void deletar(Long id) {
        usuarioRepository.deleteById(id);
    }

    public Optional<Usuario> buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

}

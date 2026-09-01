package com.example.aula10.controller;

import com.example.aula10.model.Imagem;
import com.example.aula10.repository.ImagemRepository;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;

@RestController
public class ImagemController {

    private final ImagemRepository imagemRepository;

    public ImagemController(ImagemRepository imagemRepository) {
        this.imagemRepository = imagemRepository;
    }

    @GetMapping("/imagem/{id}")
    public ResponseEntity<byte[]> exibirImagem(@PathVariable Long id) {
        Imagem imagem = imagemRepository.findById(id).orElse(null);
        if (imagem == null || imagem.getDados() == null) {
            return ResponseEntity.notFound().build();
        }

        MediaType tipo = MediaType.APPLICATION_OCTET_STREAM;
        if (imagem.getTipo() != null) {
            try {
                tipo = MediaType.parseMediaType(imagem.getTipo());
            } catch (IllegalArgumentException ignored) {
                // Mantém application/octet-stream para registros antigos sem MIME válido.
            }
        }

        return ResponseEntity.ok()
                .contentType(tipo)
                .cacheControl(CacheControl.maxAge(Duration.ofDays(30)).cachePublic())
                .body(imagem.getDados());
    }
}

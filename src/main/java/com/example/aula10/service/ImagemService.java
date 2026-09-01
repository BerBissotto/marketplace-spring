package com.example.aula10.service;

import com.example.aula10.model.Imagem;
import com.example.aula10.repository.ImagemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ImagemService {

    @Autowired
    private ImagemRepository imagemRepository;

    public void salvar(Imagem imagem) {
        imagemRepository.save(imagem);
    }
}
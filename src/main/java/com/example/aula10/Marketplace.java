package com.example.aula10;

import com.example.aula10.repository.ImagemRepository;
import com.example.aula10.repository.ProdutoRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Marketplace {

	public static void main(String[] args) {
		SpringApplication.run(Marketplace.class, args);
	}

	@Bean
	public CommandLineRunner testInsert(ProdutoRepository produtoRepository, ImagemRepository imagemRepository) {
		return args -> {
		};
	}
}

/*
INSERT INTO usuario (
    nome,
    email,
    role,
    senha,
    telefone,
    cpf
) VALUES (
    'admin',
    'admin@gmail.com',
    'ADMIN',
    '$2a$10$gCSQEkHvEL524cuIIQ8zsevsfihPUwRont0J9wV9bCF0FUC/doRZy', -- senha: 123456 (bcrypt)
    '(11) 91234-5678',
    '123.456.789-00'
);

*/
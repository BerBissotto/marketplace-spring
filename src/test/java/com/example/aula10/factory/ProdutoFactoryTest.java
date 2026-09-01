package com.example.aula10.factory;

import com.example.aula10.model.Imagem;
import com.example.aula10.model.Produto;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class ProdutoFactoryTest {

    @Test
    void devePreservarDadosNomeTipoEVinculoDaImagem() throws Exception {
        byte[] conteudo = new byte[]{1, 2, 3};
        MockMultipartFile arquivo = new MockMultipartFile(
                "imagens", "produto.png", "image/png", conteudo);

        Produto produto = ProdutoFactory.criarProduto(
                "Produto", 1, "Descrição", "Categoria", 10.0, List.of(arquivo));

        Imagem imagem = produto.getImagens().get(0);
        assertArrayEquals(conteudo, imagem.getDados());
        assertEquals("produto.png", imagem.getNome());
        assertEquals("image/png", imagem.getTipo());
        assertSame(produto, imagem.getProduto());
    }
}

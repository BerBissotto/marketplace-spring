package com.example.aula10.dto;

import com.example.aula10.model.Produto;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public class ProdutoForm {

    private Long id;

    @NotBlank(message = "Informe o nome do produto.")
    @Size(max = 120, message = "O nome deve ter no máximo 120 caracteres.")
    private String nome;

    @NotNull(message = "Informe a quantidade.")
    @PositiveOrZero(message = "A quantidade não pode ser negativa.")
    private Integer quantidade;

    @Size(max = 1000, message = "A descrição deve ter no máximo 1000 caracteres.")
    private String descricao;

    @NotBlank(message = "Informe a categoria.")
    @Size(max = 80, message = "A categoria deve ter no máximo 80 caracteres.")
    private String categoria;

    @NotNull(message = "Informe o preço.")
    @DecimalMin(value = "0.01", message = "O preço deve ser maior que zero.")
    private Double preco;

    public static ProdutoForm from(Produto produto) {
        ProdutoForm form = new ProdutoForm();
        form.setId(produto.getId());
        form.setNome(produto.getNome());
        form.setQuantidade(produto.getQuantidade());
        form.setDescricao(produto.getDescricao());
        form.setCategoria(produto.getCategoria());
        form.setPreco(produto.getPreco());
        return form;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public Integer getQuantidade() { return quantidade; }
    public void setQuantidade(Integer quantidade) { this.quantidade = quantidade; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    public Double getPreco() { return preco; }
    public void setPreco(Double preco) { this.preco = preco; }
}

package com.example.aula10.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Entity
public class Endereco {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank(message = "Informe o logradouro.")
    @Size(max = 160, message = "O logradouro deve ter no máximo 160 caracteres.")
    private String logradouro;

    @NotBlank(message = "Informe o número.")
    @Size(max = 20, message = "O número deve ter no máximo 20 caracteres.")
    private String numero;

    @NotBlank(message = "Informe o bairro.")
    @Size(max = 100, message = "O bairro deve ter no máximo 100 caracteres.")
    private String bairro;

    @NotBlank(message = "Informe a cidade.")
    @Size(max = 100, message = "A cidade deve ter no máximo 100 caracteres.")
    private String cidade;

    @NotBlank(message = "Informe o CEP.")
    @Pattern(regexp = "^\\d{5}-?\\d{3}$", message = "Informe um CEP válido.")
    private String cep;

    @Size(max = 120, message = "O complemento deve ter no máximo 120 caracteres.")
    private String complemento;

    @OneToOne
    @JoinColumn(name = "usuario_id", referencedColumnName = "id")
    @JsonIgnoreProperties("endereco")
    private Usuario usuario;

    @Override
    public String toString() {
        return "Endereco{id=" + id + ", usuarioId="
                + (usuario != null ? usuario.getId() : null) + '}';
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLogradouro() {
        return logradouro;
    }

    public void setLogradouro(String logradouro) {
        this.logradouro = logradouro;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public String getComplemento() {
        return complemento;
    }

    public void setComplemento(String complemento) {
        this.complemento = complemento;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }



}

package com.example.aula10.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pagamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String metodoPagamento;
    private double valor;
    private LocalDateTime dataPagamento = LocalDateTime.now();

    @OneToOne
    @JoinColumn(name = "pedido_id", unique = true)
    @JsonIgnoreProperties("pagamento")
    private Pedido pedido;

    @Override
    public String toString() {
        return "Pagamento{" +
                "id=" + id +
                ", metodoPagamento='" + metodoPagamento + '\'' +
                ", valor=" + valor +
                ", dataPagamento=" + dataPagamento +
                ", pedidoId=" + (pedido != null ? pedido.getId() : null) +
                '}';
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMetodoPagamento() {
        return metodoPagamento;
    }

    public void setMetodoPagamento(String metodoPagamento) {
        this.metodoPagamento = metodoPagamento;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public LocalDateTime getDataPagamento() {
        return dataPagamento;
    }

    public void setDataPagamento(LocalDateTime dataPagamento) {
        this.dataPagamento = dataPagamento;
    }

    public Pedido getPedido() {
        return pedido;
    }

    public void setPedido(Pedido pedido) {
        this.pedido = pedido;
    }



}
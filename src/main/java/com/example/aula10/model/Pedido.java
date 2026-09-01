package com.example.aula10.model;

import com.example.aula10.decorator.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDate dataPedido = LocalDate.now();
    private double valorTotal;
    @ManyToMany
    @JoinTable(
            name = "pedido_produto",
            joinColumns = @JoinColumn(name = "pedido_id"),
            inverseJoinColumns = @JoinColumn(name = "produto_id")
    )
    @JsonIgnoreProperties("pedidos")
    private List<Produto> produtos;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    @JsonIgnoreProperties("pedidos")
    private Usuario usuario;

    @OneToOne(mappedBy = "pedido", cascade = CascadeType.ALL)
    @JsonIgnoreProperties("pedido")
    private Pagamento pagamento;

    public double calcularValorProdutos() {
        if (produtos == null) {
            return 0.0;
        }
        return produtos.stream().mapToDouble(Produto::getPreco).sum();
    }

    private String tipoFrete;
    private String tipoEmbalagem;
    @Column(nullable = true)
    private Double percentualDesconto = 0.0;
    @Enumerated(EnumType.STRING)
    private StatusPedido status = StatusPedido.PENDENTE;

    public enum StatusPedido {
        PENDENTE,
        FINALIZADO
    }

    @Override
    public String toString() {
        return "Pedido{" +
                "id=" + id +
                ", dataPedido=" + dataPedido +
                ", valorTotal=" + valorTotal +
                ", usuarioId=" + (usuario != null ? usuario.getId() : null) +
                '}';
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDataPedido() {
        return dataPedido;
    }

    public void setDataPedido(LocalDate dataPedido) {
        this.dataPedido = dataPedido;
    }

    public double getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(double valorTotal) {
        this.valorTotal = valorTotal;
    }

    public List<Produto> getProdutos() {
        return produtos;
    }

    public void setProdutos(List<Produto> produtos) {
        this.produtos = produtos;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Pagamento getPagamento() {
        return pagamento;
    }

    public void setPagamento(Pagamento pagamento) {
        this.pagamento = pagamento;
    }

    public String getTipoFrete() {
        return tipoFrete;
    }

    public void setTipoFrete(String tipoFrete) {
        this.tipoFrete = tipoFrete;
    }

    public String getTipoEmbalagem() {
        return tipoEmbalagem;
    }

    public void setTipoEmbalagem(String tipoEmbalagem) {
        this.tipoEmbalagem = tipoEmbalagem;
    }

    public Double getPercentualDesconto() {
        return percentualDesconto;
    }

    public StatusPedido getStatus() {
        return status;
    }

    public void setStatus(StatusPedido status) {
        this.status = status;
    }

    public void setPercentualDesconto(Double percentualDesconto) {
        this.percentualDesconto = percentualDesconto;
    }

    public double calcularValorTotalDecorado() {
        PedidoDecorator decorado = new PedidoBase(this);
        double desconto = getPercentualDescontoNormalizado();

        // Aplica frete
        if (tipoFrete != null) {
            switch (tipoFrete.toLowerCase()) {
                case "expresso":
                    decorado = new FreteExpressoDecorator(decorado);
                    break;
                case "economico":
                    decorado = new FreteEconomicoDecorator(decorado);
                    break;
                case "padrao":
                    decorado = new FretePadraoDecorator(decorado);
                    break;
            }
        }

        // Aplica embalagem
        if (tipoEmbalagem != null) {
            switch (tipoEmbalagem.toLowerCase()) {
                case "basica":
                    decorado = new EmbalagemBasicaDecorator(decorado);
                    break;
                case "presente":
                    decorado = new EmbalagemPresenteDecorator(decorado);
                    break;
                case "comemorativa":
                    decorado = new EmbalagemComemorativaDecorator(decorado);
                    break;
                case "semembalagem":
                    decorado = new SemEmbalagemDecorator(decorado);
                    break;
            }
        }

        // Aplica desconto
        if (desconto > 0) {
            decorado = new DescontoProdutoDecorator(decorado, desconto);
        }

        return arredondarMoeda(Math.max(0.0, decorado.calcularCusto()));
    }

    public double calcularValorFrete() {
        if (tipoFrete == null) {
            return 0.0;
        }

        return switch (tipoFrete.toLowerCase()) {
            case "economico" -> 10.0;
            case "padrao" -> 20.0;
            case "expresso" -> 25.0;
            default -> 0.0;
        };
    }

    public double calcularValorEmbalagem() {
        if (tipoEmbalagem == null) {
            return 0.0;
        }

        return switch (tipoEmbalagem.toLowerCase()) {
            case "basica" -> 1.5;
            case "presente" -> 10.0;
            case "comemorativa" -> 12.0;
            default -> 0.0;
        };
    }

    public double calcularValorDesconto() {
        double valorAntesDesconto = calcularValorProdutos()
                + calcularValorFrete()
                + calcularValorEmbalagem();
        return arredondarMoeda(valorAntesDesconto * getPercentualDescontoNormalizado() / 100.0);
    }

    private double getPercentualDescontoNormalizado() {
        double desconto = percentualDesconto != null ? percentualDesconto : 0.0;
        return Math.max(0.0, Math.min(100.0, desconto));
    }

    private double arredondarMoeda(double valor) {
        return Math.round(valor * 100.0) / 100.0;
    }





}

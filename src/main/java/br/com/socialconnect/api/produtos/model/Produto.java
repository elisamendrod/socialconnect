package br.com.socialconnect.api.produtos.model;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
@Entity
@Table(name = "produtos")
@Getter @Setter @NoArgsConstructor
public class Produto {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_produto")
    private Long idProduto;
    @Column(nullable = false, unique = true, length = 150)
    private String nome;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 20)
    private CategoriaProduto categoria;
    @Column(nullable = false)
    private Integer estoqueAtual;
    @Column(nullable = false)
    private Integer estoqueMinimo;
    @Column(nullable = false, length = 20)
    private String unidadeMedida;
    @Column(nullable = false)
    private LocalDate dataCadastro;
}

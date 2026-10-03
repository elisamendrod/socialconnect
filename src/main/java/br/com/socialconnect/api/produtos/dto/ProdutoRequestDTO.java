package br.com.socialconnect.api.produtos.dto;
import br.com.socialconnect.api.produtos.model.CategoriaProduto;
import br.com.socialconnect.api.validation.EstoqueNaoNegativo;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;
public record ProdutoRequestDTO(
    @NotBlank(message = "{produto.nome.obrigatorio}") @Size(max = 150)
    @Schema(example = "Arroz 5kg") String nome,
    @NotNull(message = "{produto.categoria.obrigatoria}")
    @Schema(example = "ALIMENTO") CategoriaProduto categoria,
    @NotNull(message = "{produto.estoque.obrigatorio}") @EstoqueNaoNegativo
    @Schema(example = "3") Integer estoqueAtual,
    @NotNull(message = "{produto.estoque.obrigatorio}") @EstoqueNaoNegativo
    @Schema(example = "10") Integer estoqueMinimo,
    @NotBlank(message = "{produto.unidade.obrigatoria}") @Size(max = 20)
    @Schema(example = "unidade") String unidadeMedida
) {}

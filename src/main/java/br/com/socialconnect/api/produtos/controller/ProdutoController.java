package br.com.socialconnect.api.produtos.controller;
import br.com.socialconnect.api.produtos.dto.*;
import br.com.socialconnect.api.produtos.model.CategoriaProduto;
import br.com.socialconnect.api.produtos.service.ProdutoService;
import br.com.socialconnect.api.exception.ProblemDetail;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.*;
import org.springframework.data.web.PageableDefault;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import io.swagger.v3.oas.annotations.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.*;
import io.swagger.v3.oas.annotations.media.*;
@RestController
@RequestMapping("/api/v1/produtos")
@Tag(name = "Produtos", description = "Cadastro e controle do estoque de produtos")
public class ProdutoController {
    private final ProdutoService service;
    public ProdutoController(ProdutoService service) { this.service = service; }
    @GetMapping
    @Operation(summary = "Listar produtos", description = "Filtros opcionais por nome parcial e categoria, com paginacao e ordenacao.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista paginada"),
        @ApiResponse(responseCode = "400", description = "Parametro invalido", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public Page<ProdutoResponseDTO> listar(@RequestParam(required = false) String nome,
        @RequestParam(required = false) CategoriaProduto categoria,
        @ParameterObject @PageableDefault(size = 10, sort = "nome") Pageable pageable) {
        return service.listar(nome, categoria, pageable);
    }
    @GetMapping("/{id_produto}")
    @Operation(summary = "Buscar produto pelo ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Produto encontrado"),
        @ApiResponse(responseCode = "404", description = "Produto nao encontrado", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ProdutoResponseDTO buscar(@PathVariable("id_produto") Long id) { return service.buscarPorId(id); }
    @PostMapping
    @Operation(summary = "Cadastrar produto")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Produto criado com cabecalho Location"),
        @ApiResponse(responseCode = "400", description = "Dados invalidos", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
        @ApiResponse(responseCode = "409", description = "Nome duplicado", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
        @ApiResponse(responseCode = "422", description = "Estoque negativo", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ResponseEntity<ProdutoResponseDTO> criar(@Valid @RequestBody ProdutoRequestDTO dto) {
        ProdutoResponseDTO produto = service.criar(dto);
        return ResponseEntity.created(ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
            .buildAndExpand(produto.idProduto()).toUri()).body(produto);
    }
    @PutMapping("/{id_produto}")
    @Operation(summary = "Atualizar todos os dados do produto")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Produto atualizado"),
        @ApiResponse(responseCode = "400", description = "Dados invalidos", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
        @ApiResponse(responseCode = "404", description = "Produto nao encontrado", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
        @ApiResponse(responseCode = "409", description = "Nome duplicado", content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
        @ApiResponse(responseCode = "422", description = "Estoque negativo", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ProdutoResponseDTO atualizar(@PathVariable("id_produto") Long id, @Valid @RequestBody ProdutoRequestDTO dto) {
        return service.atualizar(id, dto);
    }
    @DeleteMapping("/{id_produto}")
    @Operation(summary = "Remover produto")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Produto removido", content = @Content),
        @ApiResponse(responseCode = "404", description = "Produto nao encontrado", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ResponseEntity<Void> deletar(@PathVariable("id_produto") Long id) {
        service.deletar(id); return ResponseEntity.noContent().build();
    }
}

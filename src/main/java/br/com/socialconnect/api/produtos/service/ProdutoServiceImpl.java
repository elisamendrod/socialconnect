package br.com.socialconnect.api.produtos.service;
import br.com.socialconnect.api.produtos.dto.*;
import br.com.socialconnect.api.produtos.model.*;
import br.com.socialconnect.api.produtos.repository.ProdutoRepository;
import br.com.socialconnect.api.exception.*;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.dao.DataIntegrityViolationException;
import java.time.LocalDate;
@Service
@Transactional
public class ProdutoServiceImpl implements ProdutoService {
    private final ProdutoRepository repository;
    public ProdutoServiceImpl(ProdutoRepository repository) { this.repository = repository; }
    @Transactional(readOnly = true)
    public Page<ProdutoResponseDTO> listar(String nome, CategoriaProduto categoria, Pageable pageable) {
        java.util.Set<String> camposPermitidos = java.util.Set.of(
            "idProduto", "nome", "categoria", "estoqueAtual", "estoqueMinimo", "unidadeMedida", "dataCadastro");
        for (Sort.Order ordem : pageable.getSort()) {
            if (!camposPermitidos.contains(ordem.getProperty())) {
                throw new OrdenacaoInvalidaException();
            }
        }
        return repository.filtrar(nome == null || nome.isBlank() ? null : nome.trim(), categoria, pageable).map(this::resposta);
    }
    @Transactional(readOnly = true)
    public ProdutoResponseDTO buscarPorId(Long id) { return resposta(buscar(id)); }
    public ProdutoResponseDTO criar(ProdutoRequestDTO dto) {
        validarEstoque(dto);
        String nome = dto.nome().trim();
        if (repository.existsByNomeIgnoreCase(nome)) throw new ProdutoDuplicadoException();
        Produto produto = new Produto();
        produto.setDataCadastro(LocalDate.now());
        preencher(produto, dto);
        return salvar(produto);
    }
    public ProdutoResponseDTO atualizar(Long id, ProdutoRequestDTO dto) {
        Produto produto = buscar(id);
        validarEstoque(dto);
        if (repository.existsByNomeIgnoreCaseAndIdProdutoNot(dto.nome().trim(), id)) throw new ProdutoDuplicadoException();
        preencher(produto, dto);
        return salvar(produto);
    }
    public void deletar(Long id) { repository.delete(buscar(id)); }
    private Produto buscar(Long id) {
        return repository.findById(id).orElseThrow(() -> new RecursoNaoEncontradoException("Produto nao encontrado com o ID: " + id));
    }
    private void validarEstoque(ProdutoRequestDTO dto) {
        if ((dto.estoqueAtual() != null && dto.estoqueAtual() < 0) || (dto.estoqueMinimo() != null && dto.estoqueMinimo() < 0))
            throw new EstoqueNegativoException();
    }
    private void preencher(Produto p, ProdutoRequestDTO dto) {
        p.setNome(dto.nome().trim()); p.setCategoria(dto.categoria());
        p.setEstoqueAtual(dto.estoqueAtual()); p.setEstoqueMinimo(dto.estoqueMinimo());
        p.setUnidadeMedida(dto.unidadeMedida().trim());
    }
    private ProdutoResponseDTO salvar(Produto p) {
        try { return resposta(repository.saveAndFlush(p)); }
        catch (DataIntegrityViolationException ex) {
            if (ex.getMostSpecificCause().getMessage() != null && ex.getMostSpecificCause().getMessage().contains("uk_produtos_nome"))
                throw new ProdutoDuplicadoException();
            throw ex;
        }
    }
    private ProdutoResponseDTO resposta(Produto p) {
        return new ProdutoResponseDTO(p.getIdProduto(), p.getNome(), p.getCategoria(), p.getEstoqueAtual(),
            p.getEstoqueMinimo(), p.getUnidadeMedida(), p.getDataCadastro(), p.getEstoqueAtual() < p.getEstoqueMinimo());
    }
}

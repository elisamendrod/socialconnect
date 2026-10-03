package br.com.socialconnect.api.produtos;
import br.com.socialconnect.api.produtos.dto.*;
import br.com.socialconnect.api.produtos.model.*;
import br.com.socialconnect.api.produtos.repository.ProdutoRepository;
import br.com.socialconnect.api.produtos.service.ProdutoServiceImpl;
import br.com.socialconnect.api.exception.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class ProdutoServiceTest {
    @Mock ProdutoRepository repository;
    @InjectMocks ProdutoServiceImpl service;
    private ProdutoRequestDTO dados(int estoque) {
        return new ProdutoRequestDTO("Arroz", CategoriaProduto.ALIMENTO, estoque, 10, "kg");
    }
    @Test void deveCriarProdutoQuandoDadosValidos() {
        // ARRANGE
        when(repository.saveAndFlush(any())).thenAnswer(invocation -> {
            Produto p = invocation.getArgument(0); p.setIdProduto(1L); return p;
        });
        // ACT
        ProdutoResponseDTO resultado = service.criar(dados(3));
        // ASSERT
        assertEquals(1L, resultado.idProduto());
        assertTrue(resultado.estoqueBaixo());
        assertNotNull(resultado.dataCadastro());
    }
    @Test void deveLancarExcecaoQuandoEstoqueNegativo() {
        // ARRANGE
        ProdutoRequestDTO dto = dados(-1);
        // ACT e ASSERT
        assertThrows(EstoqueNegativoException.class, () -> service.criar(dto));
        verifyNoInteractions(repository);
    }
    @Test void deveLancarExcecaoQuandoNomeDuplicado() {
        // ARRANGE
        when(repository.existsByNomeIgnoreCase("Arroz")).thenReturn(true);
        // ACT e ASSERT
        assertThrows(ProdutoDuplicadoException.class, () -> service.criar(dados(1)));
        verify(repository, never()).saveAndFlush(any());
    }
}
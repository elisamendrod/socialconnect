package br.com.socialconnect.api.produtos.repository;
import br.com.socialconnect.api.produtos.model.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.domain.*;
import org.springframework.data.repository.query.Param;
public interface ProdutoRepository extends JpaRepository<Produto, Long> {
    boolean existsByNomeIgnoreCase(String nome);
    boolean existsByNomeIgnoreCaseAndIdProdutoNot(String nome, Long id);
    @Query("select p from Produto p where (:nome is null or lower(p.nome) like lower(concat('%', :nome, '%'))) and (:categoria is null or p.categoria = :categoria)")
    Page<Produto> filtrar(@Param("nome") String nome, @Param("categoria") CategoriaProduto categoria, Pageable pageable);
}

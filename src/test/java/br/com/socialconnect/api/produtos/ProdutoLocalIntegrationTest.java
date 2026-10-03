package br.com.socialconnect.api.produtos;
import br.com.socialconnect.api.produtos.repository.ProdutoRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ActiveProfiles;
import java.net.URI;
import java.net.http.*;
import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("local")
class ProdutoLocalIntegrationTest {
    @Value("${local.server.port}") int port;
    @Autowired ProdutoRepository repository;
    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();
    @BeforeEach void limpar() { repository.deleteAll(); }
    private String dados(String nome, int atual, int minimo) throws Exception {
        return mapper.writeValueAsString(java.util.Map.of("nome", nome, "categoria", "ALIMENTO",
            "estoqueAtual", atual, "estoqueMinimo", minimo, "unidadeMedida", "kg"));
    }
    private HttpResponse<String> enviar(String metodo, String path, String body) throws Exception {
        HttpRequest.Builder request = HttpRequest.newBuilder(URI.create("http://localhost:" + port + path));
        request.header("Content-Type", "application/json");
        request.method(metodo, body == null ? HttpRequest.BodyPublishers.noBody() : HttpRequest.BodyPublishers.ofString(body));
        return client.send(request.build(), HttpResponse.BodyHandlers.ofString());
    }
    @Test void deveExecutarCrudFiltrosEAlerta() throws Exception {
        // ARRANGE
        var criado = enviar("POST", "/api/v1/produtos", dados("Arroz", 3, 10));
        assertEquals(201, criado.statusCode(), criado.body());
        JsonNode produto = mapper.readTree(criado.body());
        long id = produto.get("idProduto").asLong();
        assertTrue(produto.get("estoqueBaixo").asBoolean());
        assertTrue(criado.headers().firstValue("Location").orElseThrow().endsWith("/" + id));
        // ACT e ASSERT
        assertEquals(200, enviar("GET", "/api/v1/produtos/" + id, null).statusCode());
        var lista = enviar("GET", "/api/v1/produtos?nome=rr&categoria=ALIMENTO&page=0&size=10&sort=nome,asc", null);
        assertEquals(200, lista.statusCode(), lista.body());
        assertEquals(1, mapper.readTree(lista.body()).get("content").size());
        var atualizado = enviar("PUT", "/api/v1/produtos/" + id, dados("Arroz", 10, 10));
        assertEquals(200, atualizado.statusCode(), atualizado.body());
        assertFalse(mapper.readTree(atualizado.body()).get("estoqueBaixo").asBoolean());
        assertEquals(produto.get("dataCadastro"), mapper.readTree(atualizado.body()).get("dataCadastro"));
        assertEquals(204, enviar("DELETE", "/api/v1/produtos/" + id, null).statusCode());
        assertEquals(404, enviar("GET", "/api/v1/produtos/" + id, null).statusCode());
        assertEquals(404, enviar("DELETE", "/api/v1/produtos/" + id, null).statusCode());
    }
    @Test void deveRejeitarEntradasInvalidas() throws Exception {
        // ARRANGE
        assertEquals(201, enviar("POST", "/api/v1/produtos", dados("Arroz", 3, 10)).statusCode());
        // ACT e ASSERT
        assertEquals(409, enviar("POST", "/api/v1/produtos", dados("arroz", 3, 10)).statusCode());
        var negativo = enviar("POST", "/api/v1/produtos", dados("Feijao", -1, 10));
        assertEquals(422, negativo.statusCode(), negativo.body());
        assertEquals(422, mapper.readTree(negativo.body()).get("status").asInt());
        assertTrue(negativo.headers().firstValue("Content-Type").orElseThrow().contains("application/problem+json"));
        assertEquals(422, enviar("POST", "/api/v1/produtos", dados("Feijao", 1, -1)).statusCode());
        assertEquals(400, enviar("POST", "/api/v1/produtos", "{}").statusCode());
        assertEquals(400, enviar("POST", "/api/v1/produtos", "{").statusCode());
        assertEquals(400, enviar("GET", "/api/v1/produtos?categoria=INVALIDA", null).statusCode());
        assertEquals(400, enviar("GET", "/api/v1/produtos/abc", null).statusCode());
        assertEquals(0, repository.count() - 1);
    }
    @Test void deveRetornar400QuandoOrdenacaoInvalida() throws Exception {
        // ARRANGE e ACT
        var resposta = enviar("GET", "/api/v1/produtos?sort=campoInexistente,asc", null);
        // ASSERT
        assertEquals(400, resposta.statusCode(), resposta.body());
        assertEquals(400, mapper.readTree(resposta.body()).get("status").asInt());
        assertTrue(resposta.headers().firstValue("Content-Type").orElseThrow().contains("application/problem+json"));
        assertTrue(mapper.readTree(resposta.body()).get("detail").asText().contains("Campo de ordenacao invalido"));
    }
    @Test void deveDocumentarSwagger() throws Exception {
        var docs = enviar("GET", "/api-docs", null);
        assertEquals(200, docs.statusCode(), docs.body());
        JsonNode paths = mapper.readTree(docs.body()).get("paths");
        assertTrue(paths.has("/api/v1/produtos"));
        assertTrue(paths.has("/api/v1/produtos/{id_produto}"));
        assertTrue(paths.get("/api/v1/produtos").get("post").get("responses").has("422"));
        assertEquals(200, enviar("GET", "/swagger-ui/index.html", null).statusCode());
        assertEquals(302, enviar("GET", "/swagger-ui.html", null).statusCode());
    }
    @Test void deveValidarAtualizacaoDuplicadaENegativa() throws Exception {
        var primeiro = enviar("POST", "/api/v1/produtos", dados("Arroz", 3, 10));
        long id = mapper.readTree(primeiro.body()).get("idProduto").asLong();
        enviar("POST", "/api/v1/produtos", dados("Feijao", 3, 10));
        assertEquals(409, enviar("PUT", "/api/v1/produtos/" + id, dados("Feijao", 3, 10)).statusCode());
        assertEquals(422, enviar("PUT", "/api/v1/produtos/" + id, dados("Arroz", -1, 10)).statusCode());
        assertEquals(404, enviar("PUT", "/api/v1/produtos/99999", dados("Outro", 1, 1)).statusCode());
        assertEquals("Arroz", mapper.readTree(enviar("GET", "/api/v1/produtos/" + id, null).body()).get("nome").asText());
    }
}
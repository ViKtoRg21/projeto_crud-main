package br.com.anm.produtos.crud_produtos.controle;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

import java.util.Arrays;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.anm.produtos.crud_produtos.modelo.ProdutoModelo;
import br.com.anm.produtos.crud_produtos.servico.ProdutoServico;

// @WebMvcTest foca apenas na camada web (o controlador) sem carregar todo o contexto
// É mais rápido e focado para testes de controller
@WebMvcTest(ProdutoControle.class)
public class ProdutoControleTest {

    // MockMvc é a principal ferramenta para testar controllers. Ele simula as chamadas HTTP.
    @Autowired
    private MockMvc mockMvc;
    // @MockBean cria um mock do serviço e o adiciona ao contexto do Spring para o teste.
    // Assim, o ProdutoControle usará nosso mock em vez do serviço real.
    @MockBean
    private ProdutoServico ps;

    // Utilitário para converter objetos Java para JSON e vice-versa.
    @Autowired
    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void deveListarTodosOsProdutos() throws Exception {
        // --- ARRANGE --- 
        ProdutoModelo p1 = new ProdutoModelo();
        p1.setCodigo(1L);
        p1.setNome("Celular");
        p1.setMarca("Marca X");

        ProdutoModelo p2 = new ProdutoModelo();
        p2.setCodigo(2L);
        p2.setNome("Televisão");
        p2.setMarca("Marca Y");
        List<ProdutoModelo> produtos = Arrays.asList(p1, p2);

        // Configuramos o mock do serviço para retornar nossa lista de produtos quando o método listar for chamado. 
        when(ps.listar()).thenReturn(produtos);
        // --- ACT & ASSERT --- 
        mockMvc.perform(get("/listar"))
                // Simula uma requisição GET para "/listar" .andExpect(status().isOk()) 
                // Esperamos que o status da resposta seja 200 OK 
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                // O conteúdo deve ser JSON 
                .andExpect(jsonPath("$[0].nome").value("Celular"))
                // Verifica o nome do primeiro produto na lista 
                .andExpect(jsonPath("$[1].nome").value("Televisão"));
        // Verifica o nome do segundo }
    }

    @Test
    void deveCadastrarUmNovoProduto() throws Exception {
        // --- ARRANGE ---
        ProdutoModelo produto = new ProdutoModelo();
        produto.setNome("Novo Produto");
        produto.setMarca("Nova Marca");

        // 1. Cria a ResponseEntity com o tipo específico (ProdutoModelo ou Object)
        // Usar Object ou o tipo específico (ProdutoModelo) ajuda a resolver o problema do generic wildcard.
        ResponseEntity<Object> respostaEsperada = ResponseEntity
                .status(HttpStatus.CREATED)
                .body(produto);

        // 2. Usa doReturn().when() para o stubbing:
        // Isso é mais robusto com ResponseEntity<?> e evita o cast que você usou.
        doReturn(respostaEsperada)
                .when(ps)
                .cadastrarAlterar(any(ProdutoModelo.class), eq("cadastrar"));

        // --- ACT & ASSERT ---
        mockMvc.perform(post("/cadastrar")
                // Simula uma requisição POST
                .contentType(MediaType.APPLICATION_JSON)
                // Define o tipo de conteúdo do corpo da requisição
                .content(objectMapper.writeValueAsString(produto)))
                // Converte o objeto produto para uma string JSON
                .andExpect(status().isCreated())
                // Esperamos que o status seja 201 CREATED
                .andExpect(jsonPath("$.nome").value("Novo Produto"));
        // Verificamos o corpo da resposta
    }

    @Test
    void deveRemoverUmProdutoComSucesso() throws Exception {
        // --- ARRANGE ---
        long codigoParaRemover = 10L;

        // Cria a resposta de sucesso que o Serviço deve retornar
        ResponseEntity<Object> respostaOK = ResponseEntity
                .status(HttpStatus.OK)
                .body("Produto removido com sucesso!");

        // Configura o mock do serviço:
        // Quando o ps.remover for chamado com o código 10L, ele deve retornar a resposta OK.
        doReturn(respostaOK)
                .when(ps)
                .remover(codigoParaRemover);

        // --- ACT & ASSERT ---
        // Simula uma requisição DELETE para /remover/10
        mockMvc.perform(delete("/remover/{codigo}", codigoParaRemover))
                // Esperamos que o status da resposta seja 200 OK
                .andExpect(status().isOk())
                // O conteúdo da resposta (o corpo) deve ser o JSON esperado
                .andExpect(content().string("Produto removido com sucesso!"));

        // Verifica se o método 'remover' do serviço foi chamado exatamente uma vez com o código correto.
        verify(ps, times(1)).remover(codigoParaRemover);
    }
}

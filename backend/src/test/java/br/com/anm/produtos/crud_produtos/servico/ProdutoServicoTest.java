package br.com.anm.produtos.crud_produtos.servico;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import br.com.anm.produtos.crud_produtos.modelo.ProdutoModelo;
import br.com.anm.produtos.crud_produtos.modelo.RespostaModelo;
import br.com.anm.produtos.crud_produtos.repositorio.ProdutoRepositorio;

@ExtendWith(MockitoExtension.class)
public class ProdutoServicoTest {

    @Mock
    private ProdutoRepositorio pr;

    // Cria um mock para o modelo de resposta.     @Mock     private RespostaModelo rm; 
    @Mock
    private RespostaModelo rm;

    // Injeta os mocks (@Mock) na instância do serviço que será testada.     
    @InjectMocks
    private ProdutoServico ps;

    @Test
    void deveCadastrarProdutoComSucesso() {
        // --- ARRANGE (Preparação) --- 
        ProdutoModelo produto = new ProdutoModelo();
        produto.setNome("Notebook Gamer");
        produto.setMarca("Marca Famosa");

        // Quando o método `save` do repositório for chamado com qualquer objeto ProdutoModelo,         
        // ele deve retornar o mesmo objeto que foi passado.        
        when(pr.save(any(ProdutoModelo.class))).thenReturn(produto);

        // --- ACT (Ação) ---
        // Executamos o método que queremos testar.
        ResponseEntity<?> resposta = ps.cadastrarAlterar(produto, "cadastrar");

        // --- ASSERT (Verificação) ---
        // Verificamos se a resposta está correta.
        assertEquals(HttpStatus.CREATED, resposta.getStatusCode());
        assertEquals(produto, resposta.getBody());
        verify(pr, times(1)).save(produto);
        // Verifica se o método save foi chamado exatamente 1 vez. 
    }

    @Test
    void deveRetornarErroQuandoNomeProdutoEstiverVazio() {

        // --- ARRANGE ---         
        ProdutoModelo produto = new ProdutoModelo();
        produto.setNome("");
        produto.setMarca("Marca Válida");

        // --- ACT ---       
        ResponseEntity<?> resposta = ps.cadastrarAlterar(produto, "cadastrar");

        // --- ASSERT ---      
        assertEquals(HttpStatus.BAD_REQUEST, resposta.getStatusCode());

        // Verificamos que o método save NUNCA foi chamado, pois a validação deve falhar antes.      
        verify(pr, never()).save(any(ProdutoModelo.class));
    }

    @Test
    void deveRetornarErroQuandoMarcaProdutoEstiverVazia() {
        
        // --- ARRANGE ---       
        ProdutoModelo produto = new ProdutoModelo();
        produto.setNome("Nome Válido");
        produto.setMarca("");
       
        // --- ACT ---     
        ResponseEntity<?> resposta = ps.cadastrarAlterar(produto, "cadastrar");
       
        // --- ASSERT ---    
        assertEquals(HttpStatus.BAD_REQUEST, resposta.getStatusCode());
        verify(pr, never()).save(any(ProdutoModelo.class));

        
    }
    @Test
    void deveAlterarProdutoComSucesso() {
        // --- ARRANGE (Preparação) --- 
        ProdutoModelo produtoOriginal = new ProdutoModelo();
        produtoOriginal.setCodigo(1L);
        produtoOriginal.setNome("Notebook Gamer");
        produtoOriginal.setMarca("Marca Famosa");

        ProdutoModelo produtoAlterado = new ProdutoModelo();
        produtoAlterado.setCodigo(1L);
        produtoAlterado.setNome("Notebook Gamer PRO"); // Nova alteração
        produtoAlterado.setMarca("Marca Famosa");
        // Simula o comportamento do save para alterar
        when(pr.save(any(ProdutoModelo.class))).thenReturn(produtoAlterado);

        // --- ACT (Ação) ---
        // Chamamos o método para alterar
        ResponseEntity<?> resposta = ps.cadastrarAlterar(produtoAlterado, "alterar");

        // --- ASSERT (Verificação) ---
        // Verificamos se a resposta está correta (Status 200 OK)
        assertEquals(HttpStatus.OK, resposta.getStatusCode());
        // Verificamos se o corpo da resposta é o produto alterado
        assertEquals(produtoAlterado, resposta.getBody());
        // Verifica se o método save foi chamado exatamente 1 vez com o produto alterado. 
        verify(pr, times(1)).save(produtoAlterado);
    }
    @Test
    void deveRemoverProdutoComSucesso() {
        // --- ARRANGE (Preparação) --- 
        long codigoParaRemover = 5L;
        
        // --- ACT (Ação) ---
        // Executamos o método que queremos testar.
        ResponseEntity<RespostaModelo> resposta = ps.remover(codigoParaRemover);

        // --- ASSERT (Verificação) ---
        // Verificamos se a resposta tem o status 200 OK
        assertEquals(HttpStatus.OK, resposta.getStatusCode());
        
        // Verificamos se o método deleteById foi chamado exatamente 1 vez com o código correto.
        verify(pr, times(1)).deleteById(codigoParaRemover);
        // Verificamos se o método save NUNCA foi chamado.
        verify(pr, never()).save(any(ProdutoModelo.class)); 
    }
}

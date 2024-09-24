package services;

import java.time.LocalDateTime;

public class Cliente {

    private TabelaHash tabela;

    public Cliente(TabelaHash tabela) {
        this.tabela = tabela;
    }

    // Método para acessar a quantidade de registros através da TabelaHash
    public void acessarQuantidadeRegistros() {
        tabela.acessarQuantidadeRegistros();
    }

    // Método para cadastrar uma ordem de serviço
    public void cadastrarOS(int codigo, String nome, String descricao) {
        LocalDateTime horaSolicitacao = LocalDateTime.now();
        tabela.inserir(codigo, nome, descricao, horaSolicitacao);
        System.out.println("Ordem de serviço cadastrada com sucesso.");
    }

    // Método para listar todas as ordens de serviço
    public void listarOS() {
        System.out.println("Listando todas as ordens de serviço:");
        tabela.imprimirTabelaHash();
    }

    // Método para alterar uma ordem de serviço
    public void alterarOS(int codigo, String novoNome, String novaDescricao) {
        LocalDateTime novaHoraSolicitacao = LocalDateTime.now();
        tabela.atualizar(codigo, novoNome, novaDescricao, novaHoraSolicitacao);
        System.out.println("Ordem de serviço alterada com sucesso.");
    }

    // Método para remover uma ordem de serviço
    public void removerOS(int codigo) {
        tabela.remover(codigo);
        System.out.println("Ordem de serviço removida com sucesso.");
    }

    // Método para buscar uma ordem de serviço
    public void buscarOS(int codigo) {
        tabela.buscar(codigo);
    }
}

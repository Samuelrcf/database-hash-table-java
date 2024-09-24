package application;

import services.Cache;
import services.Cliente;
import services.TabelaHash;

public class Simulacao {

    public static void main(String[] args) {

        Cache cache = new Cache();
        TabelaHash tabelaHash = new TabelaHash(16, cache);
        cache.setTabela(tabelaHash); 
        Cliente cliente = new Cliente(tabelaHash);

        for (int i = 1; i <= 70; i++) {
            cliente.cadastrarOS(i, "Nome " + i, "Descrição da OS " + i);
        }
        
        cache.preencherCacheComElementosAleatorios();
        
        cache.imprimirCache();
        
        cliente.buscarOS(34);
        
        cache.imprimirCache();
        
        cliente.buscarOS(1);
        
        cache.imprimirCache();
        
        cliente.buscarOS(17);
        
        cache.imprimirCache();
        
        System.out.println("\nListagem completa após 70 inserções:");
        cliente.listarOS();
        
        cliente.cadastrarOS(71, "Nome 71", "Descrição da OS 71");
        System.out.println("\nListagem completa após inserção:");
        cliente.listarOS();
        
        cliente.cadastrarOS(72, "Nome 72", "Descrição da OS 72");
        System.out.println("\nListagem completa após inserção:");
        cliente.listarOS();
        
        cliente.alterarOS(1, "Nome 1 Atualizado", "Descrição da OS 1 Atualizada");
        System.out.println("\nListagem completa após atualização:");
        cliente.listarOS();
        
        cache.imprimirCache();
        
        cliente.alterarOS(17, "Nome 17 Atualizado", "Descrição da OS 17 Atualizada");
        System.out.println("\nListagem completa após atualização:");
        cliente.listarOS();
        
        cache.imprimirCache();

        cliente.removerOS(1);
        System.out.println("\nListagem completa após remoção:");
        cliente.listarOS();
        
        cache.imprimirCache();
        
        cliente.removerOS(17);
        System.out.println("\nListagem completa após remoção:");
        cliente.listarOS();
        
        cache.imprimirCache();
        
        System.out.println("\nConsultando a quantidade de registros:");
        cliente.acessarQuantidadeRegistros();
        
        //cliente.cadastrarOS(70, "Exceção ResourceAlreadyExistsException", "Exemplo de exceção"); //Exceção ResourceAlreadyExistsException
        //cliente.alterarOS(150, "Exceção ResourceNotFoundException", "Exemplo de exceção"); //Exceção ResourceNotFoundException
        //cliente.removerOS(150); //Exceção ResourceNotFoundException
    }
}

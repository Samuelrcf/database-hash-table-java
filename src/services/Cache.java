package services;

import java.io.BufferedWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import entities.DuploNo;
import entities.No;

public class Cache {

    private static final int M = 20; // Tamanho da cache
    private No[] tabelaCache;
    private TabelaHash tabela;

    public Cache() {
        this.tabelaCache = new No[M];
    }

    public void setTabela(TabelaHash tabela) {
        this.tabela = tabela;
    }

    // Método público que exibe as mensagens de HIT ou MISS
    public No buscarNaCache(int v) {
        No no = buscarNaCacheInterno(v);
        if (no != null) {
            System.out.println("HIT!");
        } else {
            System.out.println("MISS!");
        }
        return no;
    }

    // Método interno que faz a busca sem exibir mensagens
    private No buscarNaCacheInterno(int v) {
        int h0 = hash(v);
        int h = h0;
        int k = 0;

        System.out.println("Buscando o código " + v + " na cache...");
        
        while (tabelaCache[h] != null && k < M) {
            if (tabelaCache[h].getCodigo() == v) {
                System.out.println("Código " + v + " encontrado na posição " + h + " da cache.");
                return tabelaCache[h];
            }
            k++;
            h = (h0 + k) % M;
        }

        System.out.println("Código " + v + " não encontrado na cache...");
        
        return null;
    }

    public DuploNo removerDaCache(int v) {
        int h0 = hash(v);
        int h = h0;
        int k = 0;

        DuploNo nos = new DuploNo();
        
        System.out.println("Buscando o código " + v + " na cache...");

        // Buscar o elemento na cache e capturar o valor de h
        while (tabelaCache[h] != null && k < M) {
            if (tabelaCache[h].getCodigo() == v) {
                System.out.println("Código " + v + " encontrado na posição " + h + " da cache.");
                No no = tabelaCache[h];  // Captura o elemento a ser removido

                // Remover o elemento da cache usando a posição h
                System.out.println("Removendo o código " + v + " da cache.");
                tabelaCache[h] = null;

                // Reinserir um novo elemento aleatório da TabelaHash, se possível
                if (tabela != null) {  // Verifica se a tabela não é nula
                    System.out.println("Buscando um novo elemento aleatório da TabelaHash para inserir na cache.");
                    No noParaInserir = tabela.buscarAleatoriamente();
                    while (buscarNaCacheInterno(noParaInserir.getCodigo()) != null) {
                        noParaInserir = tabela.buscarAleatoriamente();
                    }
                    inserirNaCache(noParaInserir);
                    nos.setAdicionado(noParaInserir);
                } else {
                    System.out.println("TabelaHash não inicializada.");
                }
                nos.setRemovido(no);
                return nos;  // Retorna o elemento removido
            }
            k++;
            h = (h0 + k) % M;  // Atualiza h para a próxima posição
        }

        System.out.println("Código " + v + " não encontrado na cache.");
        return null;  // Retorna null se o elemento não for encontrado
    }
    
    public No atualizarNaCache(int v, String nome, String descricao, LocalDateTime horaSolicitacao) {
        No no = buscarNaCacheInterno(v);
        if (no != null) {
            System.out.println("Atualizando o código " + v + " na cache.");
            no.setDescricao(descricao);
            no.setHoraSolicitacao(horaSolicitacao);
            no.setNome(nome);
            return no; // Retorna o elemento atualizado
        } else {
            return null; // Retorna null se o elemento não foi encontrado
        }
    }

    public No inserirNaCache(No elemento) {
        int h = hash(elemento.getCodigo());
        System.out.println("Inserindo o código " + elemento.getCodigo() + " na cache.");

        No elementoSubstituido = null;

        if (isCacheCheia()) {
            System.out.println("Cache cheia. Substituindo o elemento na posição " + h + ".");
            elementoSubstituido = tabelaCache[h]; // Armazena o elemento que será substituído
            tabelaCache[h] = elemento;
        } else {
            while (tabelaCache[h] != null) {
                System.out.println("A posição " + h + " já está ocupada. Tentando a próxima posição.");
                h = (h + 1) % M; // Tentativa linear
            }
            System.out.println("Elemento inserido na posição " + h + " da cache.");
            tabelaCache[h] = elemento;
        }
        return elementoSubstituido; // Retorna o elemento substituído ou null
    }

    private int hash(int v) {
	    double A = 0.6180339887; 
	    double temp = v * A;
	    temp = temp - (int) temp;
	    return (int) (M * temp); 
    }

    private boolean isCacheCheia() {
        System.out.println("Verificando se a cache está cheia...");
        for (No no : tabelaCache) {
            if (no == null) {
                System.out.println("Cache não está cheia.");
                return false; // Se encontrar uma posição vazia, a cache não está cheia
            }
        }
        System.out.println("Cache está cheia.");
        return true;
    }

    public void imprimirCache() {
        System.out.println("Estado atual da Cache:");
        for (int i = 0; i < M; i++) {
            if (tabelaCache[i] != null)
                System.out.println(i + " --> " + tabelaCache[i].toString());
            else
                System.out.println(i + " --> [Vazio]");
        }
    }

    public void imprimirCache(BufferedWriter writer) throws IOException {
        writer.write("Estado atual da Cache:\n");
        for (int i = 0; i < M; i++) {
            if (tabelaCache[i] != null) {
                writer.write(i + " --> " + tabelaCache[i].toString() + "\n");
            } else {
                writer.write(i + " --> [Vazio]\n");
            }
        }
    }

    public void preencherCacheComElementosAleatorios() {
        if (tabela == null) {
            System.out.println("TabelaHash não está inicializada.");
            return;
        }

        int totalElementosTabela = tabela.acessarQuantidadeRegistros();  // Método que retorna o total de elementos
        if (totalElementosTabela < M) {
            System.out.println("A TabelaHash não possui 20 elementos suficientes para preencher a cache.");
            return;
        }

        System.out.println("Preenchendo a cache com elementos aleatórios da TabelaHash...");

        List<No> elementosCache = new ArrayList<>(); //set para não guardar valores duplicados

        // Preencher a cache com 20 elementos aleatórios
        while (elementosCache.size() < M) {
            No elementoAleatorio = tabela.buscarAleatoriamente();  // Método para buscar um elemento aleatório da tabela
            if (elementoAleatorio != null && !elementosCache.contains(elementoAleatorio)) { //verificando se a cache já possui esse elemento
                inserirNaCache(elementoAleatorio);
                elementosCache.add(elementoAleatorio);
            } else {
            	System.out.println("Elemento com código " + elementoAleatorio.getCodigo() + " já existe na cache. Buscando próximo elemento aleatório...");
            }
        }

        System.out.println("Cache preenchida com 20 elementos aleatórios da TabelaHash.");
    }

}

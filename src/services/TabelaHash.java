package services;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import entities.DuploNo;
import entities.No;
import exceptions.ResourceAlreadyExistsException;
import exceptions.ResourceNotFoundException;

public class TabelaHash {

	int M;
	No tabela[];
	final double FC_CONSTANTE = 0.7;
	Cache cache;
	final String CAMINHO_ARQUIVO =  "tabela_hash_log.txt";

	public TabelaHash(int tam, Cache cache) {
		this.M = tam;
		this.tabela = new No[this.M];
		this.cache = cache;
		System.out.println("Tabela hash criada com tamanho: " + this.M);
	}

	public int hash(int ch) { 
	    double A = 0.6180339887; 
	    double temp = ch * A;
	    temp = temp - (int) temp;
	    return (int) (this.M * temp); 
	}

	public void registrarLogOperacao(String operacao, No elemento, No elementoCacheAdicionado,
	        No elementoCacheRemovido, No elementoCacheAlterado) {
	    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
	    String dataFormatada = LocalDateTime.now().format(formatter);

	    try (BufferedWriter writer = new BufferedWriter(new FileWriter(CAMINHO_ARQUIVO, true))) {
	        writer.write("Operação: " + operacao + "\n" + "Data: " + dataFormatada + "\n");

	        // Informando o elemento que sofreu a operação
	        if (elemento != null) {
	            writer.write("Elemento afetado: " + elemento.toString() + "\n");
	        } else {
	            writer.write("Elemento afetado: -\n");
	        }

	        // Informando a quantidade de elementos presentes na tabela hash
	        int quantidadeElementos = 0;
	        for (No no : this.tabela) {
	            if (no != null) {
	                quantidadeElementos++;
	            }
	        }
	        writer.write("Quantidade de elementos na tabela: " + quantidadeElementos + "\n");

	        // Informando o fator de carga atual
	        double fatorCarga = (double) quantidadeElementos / this.M;
	        writer.write("Fator de carga atual: " + fatorCarga + "\n");

	        // Adicionando o estado da cache
	        cache.imprimirCache(writer);
	        
	        if (elementoCacheRemovido != null) {
	            writer.write("Elemento de código " + elementoCacheRemovido.getCodigo() + " removido da cache.\n");
	        }
	        
	        if (elementoCacheAdicionado != null) {
	            writer.write("Elemento de código " + elementoCacheAdicionado.getCodigo() + " adicionado à cache.\n");
	        }
	        
	        if (elementoCacheAlterado != null) {
	            writer.write("Elemento de código " + elementoCacheAlterado.getCodigo() + " alterado na cache.\n");
	        }
	        
	        writer.write("----------------------------------------------------\n");
	    } catch (IOException e) {
	        System.err.println("Erro ao registrar log: " + e.getMessage());
	    }
	}

	public void inserir(int codigo, String nome, String descricao, LocalDateTime horaSolicitacao) {
	    if (verificarFatorCarga()) {
	        System.out.println("Fator de carga atingido. Redimensionando tamanho da base de dados...");
	        redimensionarTabela();
	    }

	    int h0 = this.hash(codigo);
	    int h = h0;
	    int k = 0;

	    // Verifica colisão e tenta novas posições
	    while (this.tabela[h] != null && this.tabela[h].getCodigo() != codigo) {
	        System.out.println("Colisão detectada na posição " + h + " para o código " + codigo + ". Tentando nova posição...");
	        k++;
	        h = (h0 + k) % this.M;
	    }

	    if (this.tabela[h] == null) {
	        No novoNo = new No(codigo, nome, descricao, horaSolicitacao);
	        this.tabela[h] = novoNo;
	        System.out.println("Inserido na posição " + h + ": " + this.tabela[h]);
	        registrarLogOperacao("Inserir OS " + codigo, novoNo, null, null, null);
	    } else {
	        throw new ResourceAlreadyExistsException("Elemento com código " + codigo + " já existe.");
	    }
	}

	public No buscar(int codigo) {
		System.out.println("Buscando elemento com código: " + codigo);

		// Primeiro verifica na cache e na base de dados
		No noCache = cache.buscarNaCache(codigo);
		if (noCache != null) {
			System.out.println("Elemento encontrado na cache, retornando...");
			registrarLogOperacao("Buscar OS " + codigo, noCache, null, null, null);
			return noCache; // Retorna se encontrado na cache
		}

		// Se não estiver na cache, busca na tabela hash
		No resultado = buscarNaBase(codigo);
		// Inserir na cache apenas se o elemento foi encontrado
		if (resultado != null) {
			No removidoDaCache = cache.inserirNaCache(resultado);
			registrarLogOperacao("Buscar OS " + codigo, resultado, resultado, removidoDaCache, null);
		}
		return resultado;
	}

	private No buscarNaBase(int codigo) {
		int h0 = this.hash(codigo);
		int h = h0;
		int k = 0;

		System.out.println("Buscando elemento na base de dados com código: " + codigo);

		while (this.tabela[h] != null && k < this.M) {
			if (this.tabela[h].getCodigo() == codigo) {
				System.out.println("Elemento encontrado na base de dados na posição: " + h);
				return this.tabela[h];
			}
			k++;
			h = (h0 + k) % this.M;
		}

		throw new ResourceNotFoundException("Elemento com código " + codigo + " não encontrado na base de dados.");
	}

	public void remover(int codigo) {
	    No elemento = buscarNaBase(codigo); // Não inserimos na cache aqui
	    if (elemento != null) {
	        DuploNo nos = cache.removerDaCache(codigo);
	        int h0 = this.hash(codigo);
	        int h = h0;
	        int k = 0;

	        while (this.tabela[h] != null && k < this.M) {
	            if (this.tabela[h].getCodigo() == codigo) {
	                this.tabela[h] = null;
	                System.out.println("Removido da posição " + h + ": " + elemento);
	                if(nos == null) {
	                    registrarLogOperacao("Remover OS " + codigo, elemento, null, null, null);
	                }else {
	                    registrarLogOperacao("Remover OS " + codigo, elemento, nos.getAdicionado(), nos.getRemovido(), null);
	                }
	                return;
	            }
	            k++;
	            h = (h0 + k) % this.M;
	        }
	    }
	}

	public void atualizar(int codigo, String nome, String descricao, LocalDateTime horaSolicitacao) {
	    No elemento = buscarNaBase(codigo); // Não inserimos na cache aqui
	    if (elemento != null) {
	        No elementoCacheAlterado = cache.atualizarNaCache(codigo, nome, descricao, horaSolicitacao);
	        int h0 = this.hash(codigo);
	        int h = h0;
	        int k = 0;

	        while (this.tabela[h] != null && k < this.M) {
	            if (this.tabela[h].getCodigo() == codigo) {
	                No no = this.tabela[h];
	                no.setNome(nome);
	                no.setDescricao(descricao);
	                no.setHoraSolicitacao(horaSolicitacao);
	                System.out.println("Atualizado na posição " + h + ": " + no);
	                registrarLogOperacao("Atualizar OS " + codigo, no, null, null, elementoCacheAlterado);
	                return;
	            }
	            k++;
	            h = (h0 + k) % this.M;
	        }
	    } else {
	        throw new ResourceNotFoundException("Elemento com código " + codigo + " não encontrado para atualização.");
	    }
	}


	public boolean verificarFatorCarga() {
		int elementosOcupados = 0;

		for (No no : this.tabela) {
			if (no != null) {
				elementosOcupados++;
			}
		}

		double fc = (double) elementosOcupados / this.M;
		return fc > FC_CONSTANTE;
	}

	private void redimensionarTabela() {
		int novoTam = proximoPrimoMaiorQue(this.M * 2);
		No[] tabelaAntiga = this.tabela;
		this.M = novoTam;
		this.tabela = new No[this.M];
		System.out.println("Tabela redimensionada para o novo tamanho: " + novoTam);
		registrarLogOperacao("Redimensionamento da tabela", null, null, null, null);
		reinserirElementos(tabelaAntiga);
	}

	private int proximoPrimoMaiorQue(int n) {
		while (true) {
			if (isPrimo(n))
				return n;
			n++;
		}
	}

	private boolean isPrimo(int num) {
		if (num <= 1)
			return false;
		if (num <= 3)
			return true;
		if (num % 2 == 0 || num % 3 == 0)
			return false;
		for (int i = 5; i * i <= num; i += 6) {
			if (num % i == 0 || num % (i + 2) == 0)
				return false;
		}
		return true;
	}

	private void reinserirElementos(No[] tabelaAntiga) {
		System.out.println("Reinserindo elementos da tabela antiga após redimensionamento...");
		for (No no : tabelaAntiga) {
			if (no != null) {
				inserir(no.getCodigo(), no.getNome(), no.getDescricao(), no.getHoraSolicitacao());
				System.out.println("Reinserido elemento com código: " + no.getCodigo());
			}
		}
		System.out.println("Elementos reinseridos com sucesso.");
	}

	public void imprimirTabelaHash() {
		System.out.println("Estado atual da tabela hash:");
		for (int i = 0; i < this.M; i++) {
			if (this.tabela[i] != null)
				System.out.println(i + " --> " + this.tabela[i]);
			else
				System.out.println(i);
		}
	}

	public No buscarAleatoriamente() { //adiciono todos os elementos em um array list e obtenho um aleatório usando a lib random
		List<No> elementos = new ArrayList<>();
		for (No no : this.tabela) {
			if (no != null) {
				elementos.add(no);
			}
		}

		if (elementos.isEmpty()) {
			throw new ResourceNotFoundException("Não existe nenhum elemento na base de dados.");
		}

		Random random = new Random();
		int index = random.nextInt(elementos.size());
		No noSelecionado = elementos.get(index);
		System.out.println("Elemento selecionado aleatoriamente com código: " + noSelecionado.getCodigo());
		return noSelecionado;
	}

	public int acessarQuantidadeRegistros() {
		int registros = 0;
		for (No no : this.tabela) {
			if (no != null) {
				registros++;
			}
		}
		registrarLogOperacao("Acessar quantidade de registros", null, null, null, null);
		System.out.println("Quantidade de registros: " + registros);
		return registros;
	}
	
}

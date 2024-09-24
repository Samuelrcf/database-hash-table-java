package entities;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class No implements Serializable {
	private static final long serialVersionUID = 1L;

    private int codigo;          
    private String nome;        
    private String descricao;    
    private LocalDateTime horaSolicitacao; 
    
    public No() {
    }

    public No(int codigo, String nome, String descricao, LocalDateTime horaSolicitacao) {
        this.codigo = codigo;
        this.nome = nome;
        this.descricao = descricao;
        this.horaSolicitacao = horaSolicitacao;
    }

    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public LocalDateTime getHoraSolicitacao() {
        return horaSolicitacao;
    }

    public void setHoraSolicitacao(LocalDateTime horaSolicitacao) {
        this.horaSolicitacao = horaSolicitacao;
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        String horaFormatada = (horaSolicitacao != null) ? horaSolicitacao.format(formatter) : "Não disponível";
        
        return "OS {" +
                "CÓDIGO = " + codigo +
                ", NOME = '" + nome + '\'' +
                ", DESCRIÇÃO = '" + descricao + '\'' +
                ", HORA DA SOLICITAÇÃO = " + horaFormatada +
                '}';
    }
}

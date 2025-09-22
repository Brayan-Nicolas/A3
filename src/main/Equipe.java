package main;

import java.util.ArrayList;
import java.util.List;

import usuário.Usuario;

public class Equipe {

    private long id; // Adicionado para identificação única
    private String nome;
    private String descricao;
    private List<Usuario> membros;

    public Equipe(long id, String nome, String descricao) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.membros = new ArrayList<>();
    }
    
    public Equipe(long id, String nome) {
    	this.id = id;
    	this.nome = nome;
    }

    // Métodos de Gerenciamento de Membros
    public void adicionarMembro(Usuario novoMembro) {
        if (novoMembro != null && !this.membros.contains(novoMembro)) {
            this.membros.add(novoMembro);
            System.out.println(novoMembro.getNome() + " foi adicionado(a) à equipe " + this.nome);
        }
    }

    public void removerMembro(Usuario membro) {
        if (membro != null && this.membros.contains(membro)) {
            this.membros.remove(membro);
            System.out.println(membro.getNome() + " foi removido(a) da equipe " + this.nome);
        }
    }

    // Getters
    public String getNome() {
        return nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public List<Usuario> getMembros() {
        return membros;
    }

    // Método crucial para a GUI
    @Override
    public String toString() {
        return this.nome;
    }

	public long getId() {
		return this.id;
	}
}
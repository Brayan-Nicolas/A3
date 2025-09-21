package main;

import java.util.ArrayList;
import java.util.List;

import usuário.Usuario;

public class Equipe {

    private String nome;
    private String descricao;
    private List<Usuario> membros;

    /**
     * Construtor para criar uma nova equipe.
     *
     * @param nome      O nome da equipe.
     * @param descricao A descrição da equipe.
     */
    public Equipe(String nome, String descricao) {
        this.nome = nome;
        this.descricao = descricao;
        this.membros = new ArrayList<>(); // Inicializa a lista de membros vazia
    }

    // --- Métodos de Gerenciamento de Membros ---
    /**
     * Adiciona um novo usuário à equipe.
     *
     * @param novoMembro O usuário a ser adicionado.
     */
    public void adicionarMembro(Usuario novoMembro) {
        if (novoMembro != null && !this.membros.contains(novoMembro)) {
            this.membros.add(novoMembro);
            System.out.println(novoMembro.getNome() + " foi adicionado(a) à equipe " + this.nome);
        }
    }

    /**
     * Remove um membro da equipe.
     *
     * @param membro O usuário a ser removido.
     */
    public void removerMembro(Usuario membro) {
        if (membro != null && this.membros.contains(membro)) {
            this.membros.remove(membro);
            System.out.println(membro.getNome() + " foi removido(a) da equipe " + this.nome);
        }
    }

    // --- Métodos de Acesso (Getters) ---
    // Note que não há setters para a lista de membros, pois a gestão
    // é feita pelos métodos adicionar/remover, garantindo segurança.
    public String getNome() {
        return nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public List<Usuario> getMembros() {
        return membros;
    }

}

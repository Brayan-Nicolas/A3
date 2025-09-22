package main.Projeto;

import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

import usuario.Usuario;
import main.Equipe;

public class Projeto {

    private String nome;
    private String descricao;
    private LocalDate dataDeInicio;
    private LocalDate dataDeTerminoPrevisto;
    private StatusProjeto status;
    private Usuario gerente;
    private List<Equipe> equipes;

    // Construtor para inicializar um novo projeto
    public Projeto(String nome, String descricao, LocalDate dataDeInicio,
            LocalDate dataDeTerminoPrevisto, Usuario gerente, List<Equipe> equipes) {
        this.nome = nome;
        this.descricao = descricao;
        this.dataDeInicio = dataDeInicio;
        this.dataDeTerminoPrevisto = dataDeTerminoPrevisto;
        this.gerente = gerente;
        this.equipes = equipes; // CORRIGIDO: Ponto e vírgula
        this.status = StatusProjeto.PLANEJAMENTO;
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

    public LocalDate getDataDeInicio() {
        return dataDeInicio;
    }

    public LocalDate getDataDeTerminoPrevisto() {
        return dataDeTerminoPrevisto;
    }

    public void setDataDeTerminoPrevisto(LocalDate dataDeTerminoPrevisto) {
        this.dataDeTerminoPrevisto = dataDeTerminoPrevisto;
    }

    public StatusProjeto getStatus() {
        return status;
    }

    public void setStatus(StatusProjeto status) {
        this.status = status;
    }

    public Usuario getGerente() {
        return gerente;
    }

    public List<Equipe> getEquipes() {
        return equipes;
    }

    public void setEquipes(List<Equipe> equipes) {
        this.equipes = equipes; // CORRIGIDO: Ponto e vírgula
    }

    public void adicionarEquipe(Equipe novaEquipe) {
        if (this.equipes == null) {
            this.equipes = new ArrayList<>();
        }
        this.equipes.add(novaEquipe);
    }
}
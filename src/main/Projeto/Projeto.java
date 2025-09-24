package main.Projeto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import main.DatabaseDAO;
import main.Equipe;
import usuário.Usuario;

public class Projeto {

	private long id;
    private String nome;
    private String descricao;
    private LocalDate dataDeInicio;
    private LocalDate dataDeTerminoPrevisto;
    private StatusProjeto status;
    private long gerenteId;
    private List<Equipe> equipes;

    // Construtor para inicializar um novo projeto
    public Projeto(long id, String nome, String descricao, LocalDate dataDeInicio,
            LocalDate dataDeTerminoPrevisto, long gerenteId) {
    	this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.dataDeInicio = dataDeInicio;
        this.dataDeTerminoPrevisto = dataDeTerminoPrevisto;
        this.gerenteId = gerenteId;
        this.equipes = DatabaseDAO.getEquipesProjeto(this.id);
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
        return DatabaseDAO.getUsuario(this.gerenteId);
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
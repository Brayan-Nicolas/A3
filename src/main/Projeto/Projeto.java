package main.Projeto;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.Date;

import main.DatabaseDAO;
import usuário.Usuario;

public class Projeto {

    private String nome;
    private String descricao;
    private LocalDate dataDeInicio;
    private LocalDate dataDeTerminoPrevisto;
    private StatusProjeto status;
    private Usuario gerente;

    // Construtor para inicializar um novo projeto
    public Projeto(String nome, String descricao, LocalDate dataDeInicio,
            LocalDate dataDeTerminoPrevisto, Usuario gerente) {
        this.nome = nome;
        this.descricao = descricao;
        this.dataDeInicio = dataDeInicio;
        this.dataDeTerminoPrevisto = dataDeTerminoPrevisto;
        this.gerente = gerente;
        this.status = StatusProjeto.PLANEJAMENTO; // O status inicial de todo projeto é Planejamento
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

}

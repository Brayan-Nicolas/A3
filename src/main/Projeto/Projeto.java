package main.Projeto;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

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

    // O gerente não pode ser alterado diretamente por um "setter" comum
    // pois a troca de gerente é uma operação de negócio mais complexa.
    // Assim, removemos o setter para esse campo.
    class ProjetoDAO {

        private Connection conectar() {
            try {
                // Configuração do banco (MySQL neste exemplo)
                String url = "jdbc:mysql://localhost:3306/sistema";
                String user = "root";
                String password = "1234"; // coloque a senha do seu MySQL
                return DriverManager.getConnection(url, user, password);
            } catch (SQLException e) {
                e.printStackTrace();
                return null;
            }
        }

        public boolean inserir(Projeto projeto) {
            Connection conn = ConexaoDB.conectar();
            if (conn == null) {
                return false;
            }

            // A query SQL agora inclui a chave estrangeira do gerente (gerente_id)
            String sql = "INSERT INTO projetos (nome, descricao, data_inicio, data_termino_previsto, status, gerente_id) VALUES (?, ?, ?, ?, ?, ?)";

            try {
                // Usando PreparedStatement para evitar SQL Injection
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

                // 1. Mapeando atributos simples
                stmt.setString(1, projeto.getNome());
                stmt.setString(2, projeto.getDescricao());

                // 2. Mapeando LocalDate para java.sql.Date
                stmt.setDate(3, Date.valueOf(projeto.getDataDeInicio()));
                stmt.setDate(4, Date.valueOf(projeto.getDataDeTerminoPrevisto()));

                // 3. Mapeando o Enum para String
                stmt.setString(5, projeto.getStatus().name());

                // 4. Mapeando o objeto Gerente para a sua chave estrangeira (id)
                stmt.setLong(6, projeto.getGerente().getId());

                stmt.executeUpdate();

                // Extraindo o ID gerado pelo banco para o novo projeto
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    long novoId = rs.getLong(1);
                    // Atualizamos o objeto Java com o ID gerado
                    // projeto.setId(novoId);
                    System.out.println("Projeto inserido com ID: " + novoId);
                }

                // Fecha os recursos
                stmt.close();
                conn.close();
                return true;
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        }

    }
}

package main;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Usuário {
    private String nome, cpf, email, cargo, login, senha;

    public Usuário(String nome, String cpf, String email, String cargo, String login, String senha) {
        this.nome = nome;
        this.cpf = cpf;
        this.email = email;
        this.cargo = cargo;
        this.login = login;
        this.senha = senha;
    }

    // Getters
    public String getNome() { return nome; }
    public String getCpf() { return cpf; }
    public String getEmail() { return email; }
    public String getCargo() { return cargo; }
    public String getLogin() { return login; }
    public String getSenha() { return senha; }
}

//Classe DAO responsável pela conexão e inserção no banco
class UsuarioDAO {
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

 public boolean inserir(Usuário usuario) {
     Connection conn = conectar();
     if (conn == null) return false;

     String sql = "INSERT INTO usuarios (nome, cpf, email, cargo, login, senha) VALUES (?, ?, ?, ?, ?, ?)";

     try {
         PreparedStatement stmt = conn.prepareStatement(sql);
         stmt.setString(1, usuario.getNome());
         stmt.setString(2, usuario.getCpf());
         stmt.setString(3, usuario.getEmail());
         stmt.setString(4, usuario.getCargo());
         stmt.setString(5, usuario.getLogin());
         stmt.setString(6, usuario.getSenha());

         stmt.executeUpdate();
         stmt.close();
         conn.close();
         return true;
     } catch (SQLException e) {
         e.printStackTrace();
         return false;
     }
 }
}
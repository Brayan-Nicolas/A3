package usuário;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

//Classe DAO responsável pela conexão e inserção no banco
public class UsuarioDAO {
	private Connection conectar() {
	     try {
	         // Configuração do banco (MySQL neste exemplo)
	         String url = "jdbc:mysql://localhost:3306/sistema";
	         String user = "root";
	         String password = "root"; // coloque a senha do seu MySQL
	         return DriverManager.getConnection(url, user, password);
	     } catch (SQLException e) {
	         e.printStackTrace();
	         return null;
	     }
	}

	public boolean inserir(Usuário usuario) {
	     Connection conn = conectar();
	     if (conn == null) return false;
	     String sql = "INSERT INTO usuarios (nome, cpf, email, cargo, login, senha, nivel) VALUES (?, ?, ?, ?, ?, ?, ?)";
	
	     try {
	         PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
	         stmt.setString(1, usuario.getNome());
	         stmt.setString(2, usuario.getCpf());
	         stmt.setString(3, usuario.getEmail());
	         stmt.setString(4, usuario.getCargo());
	         stmt.setString(5, usuario.getLogin());
	         stmt.setString(6, usuario.getSenha());
	         stmt.setString(7, usuario.getNivel().toString());
	
	         stmt.executeUpdate();
	         stmt.close();
	         conn.close();
	         return true;
	     } catch (SQLException e) {
	         e.printStackTrace();
	         return false;
	     }
	 }
	
	public boolean alterar(Usuário usuario, String campo, String valor) {
		Connection conn = conectar();
	    if (conn == null) return false;
	    String sql = "UPDATE usuarios SET ? = ? WHERE id = ?";
	    
	    try {
	    	PreparedStatement stmt = conn.prepareStatement(sql);
	    	stmt.setString(1, campo);
	    	stmt.setString(2, valor);
	    	stmt.setInt(3, usuario.getId());
	    	
	    	stmt.executeUpdate();
	    	stmt.close();
	    	conn.close();
	    	return true;
	    } catch(SQLException e) {
	    	e.printStackTrace();
	    	return false;
	    }
	}
}

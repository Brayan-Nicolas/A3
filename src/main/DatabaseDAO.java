package main;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JOptionPane;

import main.Projeto.Projeto;
import usuário.Nivel;
import usuário.Usuario;

//Classe DAO responsável pela conexão e inserção no banco
public class DatabaseDAO {
	private static Connection conectar() {
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
	// Cria um usuário novo no Banco de Dados baseado no objeto de usuário Java
	public static boolean criarUsuário(Usuario usuario) {
	     Connection conn = conectar();
	     if (conn == null) return false;
	     try {
	     // Prepara o query para verificar se o CPF inserido já existe no banco de dados
	     PreparedStatement stmt = conn.prepareStatement("SELECT cpf FROM usuarios WHERE cpf = ?");
	     stmt.setString(1, usuario.getCpf());
	     
	     // Verifica se o CPF já existe no banco de dados e retorna
	     if (stmt.executeQuery().next()) {
	    	 JOptionPane.showMessageDialog(ProgramaGestão.currentWindow, "CPF já cadastrado!");
	    	 return false;
	     }
	     // Prepara o query para verificar se o login inserido já existe no banco de dados
	     stmt = conn.prepareStatement("SELECT login FROM usuarios WHERE login = ?");
	     stmt.setString(1, usuario.getLogin());
	     
	     // Verifica se o login já existe no banco de dados e retorna
	     if (stmt.executeQuery().next()) {
	    	 JOptionPane.showMessageDialog(ProgramaGestão.currentWindow, "Login já cadastrado!");
	    	 return false;
	     }
	     
	     String sql = "INSERT INTO usuarios (nome, cpf, email, cargo, login, senha, nivel) VALUES (?, ?, ?, ?, ?, ?, ?)";
	
	         stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
	         stmt.setString(1, usuario.getNome());
	         stmt.setString(2, usuario.getCpf());
	         stmt.setString(3, usuario.getEmail());
	         stmt.setString(4, usuario.getCargo());
	         stmt.setString(5, usuario.getLogin());
	         stmt.setString(6, usuario.getSenha());
	         stmt.setString(7, usuario.getNivel().toString());
	
	         stmt.executeUpdate();
	         stmt.getGeneratedKeys().first();
	         usuario.setId(stmt.getGeneratedKeys().getInt(1));
	         stmt.close();
	         conn.close();
	         return true;
	     } catch (SQLException e) {
	         e.printStackTrace();
	         return false;
	     }
	 }
	// Altera um campo no banco de dados para um usuário
	public static boolean alterarUsuario(Usuario usuario, String campo, String valor) {
		Connection conn = conectar();
	    if (conn == null) return false;
	    String sql = "UPDATE usuarios SET ? = ? WHERE id = ?";
	    
	    try {
	    	PreparedStatement stmt = conn.prepareStatement(sql);
	    	stmt.setString(1, campo);
	    	stmt.setString(2, valor);
	    	stmt.setInt(3, (int) usuario.getId());
	    	
	    	stmt.executeUpdate();
	    	stmt.close();
	    	conn.close();
	    	return true;
	    } catch(SQLException e) {
	    	e.printStackTrace();
	    	JOptionPane.showMessageDialog(ProgramaGestão.currentWindow, "Não foi possível estabelecer conexão com o banco de dados.", "Erro", JOptionPane.ERROR_MESSAGE);
	    	return false;
	    }
	}

	// Cria um novo projeto no banco de dados
    public static boolean criarProjeto(Projeto projeto) {
        Connection conn = conectar();
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
	/**
    * Método para retornar valores de um usuário específico do banco de dados
    * 
    * @param userId		Id único do usuário a ser retornado
    * @param campo 		Coluna a ser retornada
    */
    public static String getValorUsuario(int userId, String campo) {
    	Connection conn = conectar();
        if (conn == null) { return null; }
        
        String sql = "SELECT ? FROM usuarios WHERE id = ?";
        try {
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, campo);
        stmt.setInt(2, userId);
        
        if (stmt.executeQuery().next()) {
        	String retorno = stmt.getResultSet().getString(1);
			stmt.close();
			conn.close();
			return retorno;
        }
        return null;
        } catch (SQLException e) {
        	e.printStackTrace();
        	return null;
        }
    }
    /**
    * Método para recuperar valores do banco de dados
    * 
    * @param coluna		  Especifica a coluna que deve ser retornada
    * @param filtroColuna Especifica a coluna a ser filtrada para obtenção do valor
    * @param filtroValor  Especifica o valor o qual procurar na coluna selecionada no filtroColuna
    */
    public static String getValor(String coluna, String filtroColuna, String filtroValor) {
    	Connection conn = conectar();
        if (conn == null) { return null; }
        
        String sql = "SELECT " + coluna + " FROM usuarios WHERE " + filtroColuna + " = ?";
        try {
        	PreparedStatement stmt = conn.prepareStatement(sql);
        	
        	stmt.setString(1, filtroValor);
        	
        	if (stmt.executeQuery().next()) {
            	String retorno = stmt.getResultSet().getString(1);
    			stmt.close();
    			conn.close();
    			return retorno;
            }
            return null;
        	
        } catch (SQLException e) {
        	e.printStackTrace();
        	return null;
        }
        
    }
    /*
     * Método usado para recuperar os dados de um usuário e atualizar o usuário do cliente com os dados recuperados
     * 
     * @param id 	Id do usuário a ser recuperado
     */
    public static boolean recuperarUsuário(int id) {
    	Connection conn = conectar();
        if (conn == null) { return false; }
        
        String sql = "SELECT * FROM usuarios WHERE id = ?";
        
        try {
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setInt(1, id);
			stmt.executeQuery();
			if (!stmt.getResultSet().next()) return false;
			ResultSet set = stmt.getResultSet();
			
			String nome = set.getString("nome");
			String cpf = set.getString("cpf");
			String email = set.getString("email");
			String cargo = set.getString("cargo");
			String login = set.getString("login");
			String senha = set.getString("senha");
			Nivel nivel = Nivel.valueOf(set.getString("nivel"));
			
			ProgramaGestão.usuarioAtual = new Usuario(nome, cpf, email, cargo, login, senha, nivel);
			
			stmt.close();
			conn.close();
			return true;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
        
    }
}

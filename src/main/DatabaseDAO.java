package main;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import main.Projeto.Projeto;
import usuário.Nivel;
import usuário.Usuario;
import main.Equipe;

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
		if (conn == null)
			return false;
		try {
			// Prepara o query para verificar se o CPF inserido já existe no banco de dados
			PreparedStatement stmt = conn.prepareStatement("SELECT cpf FROM usuarios WHERE cpf = ?");
			stmt.setString(1, usuario.getCpf());

			// Verifica se o CPF já existe no banco de dados e retorna
			if (stmt.executeQuery().next()) {
				JOptionPane.showMessageDialog(ProgramaGestão.currentWindow, "CPF já cadastrado!");
				return false;
			}
			// Prepara o query para verificar se o login inserido já existe no banco de
			// dados
			stmt = conn.prepareStatement("SELECT login FROM usuarios WHERE login = ?");
			stmt.setString(1, usuario.getLogin());

			// Verifica se o login já existe no banco de dados e retorna
			if (stmt.executeQuery().next()) {
				JOptionPane.showMessageDialog(ProgramaGestão.currentWindow, "Login já cadastrado!");
				return false;
			}

			String sql = "INSERT INTO usuarios (nome, cpf, email, cargo, login, senha, nivel) VALUES (?, ?, ?, ?, ?, ?, ?)";

			stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			stmt.setString(1, Utilidades.primeiraMaiuscula(usuario.getNome()));
			stmt.setString(2, usuario.getCpf());
			stmt.setString(3, usuario.getEmail());
			stmt.setString(4, Utilidades.primeiraMaiuscula(usuario.getCargo()));
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
		if (conn == null)
			return false;
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
		} catch (SQLException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(ProgramaGestão.currentWindow,
					"Não foi possível estabelecer conexão com o banco de dados.", "Erro", JOptionPane.ERROR_MESSAGE);
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
	 * @param idUsuario Id único do usuário a ser retornado
	 * @param campo     Coluna a ser retornada
	 */
	public static String getValorUsuario(int idUsuario, String campo) {
		Connection conn = conectar();
		if (conn == null) {
			return null;
		}

		String sql = "SELECT ? FROM usuarios WHERE id = ?";
		try {
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setString(1, campo);
			stmt.setInt(2, idUsuario);

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
     * Retorna uma lista de todos os usuários cadastrados no banco de dados.
     * @return Uma lista de objetos Usuario, ou uma lista vazia em caso de erro.
     */
    public static List<Usuario> getTodosUsuarios() {
        List<Usuario> usuarios = new ArrayList<>();
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = conectar();
            if (conn == null) {
                return usuarios;
            }

            // A query SQL para selecionar todos os usuários
            String sql = "SELECT * FROM usuarios";
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);

            // Itera sobre o conjunto de resultados
            while (rs.next()) {
            	long idUsuario = rs.getInt("id");
				String nome = rs.getString("nome");
				String cpf = rs.getString("cpf");
				String email = rs.getString("email");
				String cargo = rs.getString("cargo");
				Nivel nivel = Nivel.valueOf(rs.getString("nivel"));

				usuarios.add(new Usuario(idUsuario, nome, cpf, email, cargo, nivel));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Fecha os recursos
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        return usuarios;
    }

	/**
	 * Método para recuperar valores do banco de dados
	 * 
	 * @param coluna       Especifica a coluna que deve ser retornada
	 * @param filtroColuna Especifica a coluna a ser filtrada para obtenção do valor
	 * @param filtroValor  Especifica o valor o qual procurar na coluna selecionada
	 *                     no filtroColuna
	 */
	public static String getValor(String coluna, String filtroColuna, String filtroValor) {
		Connection conn = conectar();
		if (conn == null) {
			return null;
		}

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
	 * Método usado para recuperar os dados de um usuário e atualizar o usuário do
	 * cliente com os dados recuperados
	 * 
	 * @param id Id do usuário a ser recuperado
	 */
	public static boolean recuperarUsuarioLogado(long idUsuario) {
		Connection conn = conectar();
		if (conn == null) {
			return false;
		}

		String sql = "SELECT * FROM usuarios WHERE id = ?";

		try {
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setInt(1, (int) idUsuario);
			stmt.executeQuery();
			if (!stmt.getResultSet().next())
				return false;
			ResultSet set = stmt.getResultSet();

			String nome = set.getString("nome");
			String cpf = set.getString("cpf");
			String email = set.getString("email");
			String cargo = set.getString("cargo");
			String login = set.getString("login");
			String senha = set.getString("senha");
			Nivel nivel = Nivel.valueOf(set.getString("nivel"));

			ProgramaGestão.usuarioAtual = new Usuario(nome, cpf, email, cargo, login, senha, nivel);
			ProgramaGestão.usuarioAtual.setId(set.getInt("id"));

			stmt.close();
			conn.close();
			return true;

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}

	}

	public static List<Equipe> getEquipes() {
		Connection conn = conectar();
		if (conn == null) {
			return null;
		}

		String sql = "SELECT * FROM equipes";

		try {
			PreparedStatement stmt = conn.prepareStatement(sql);

			ResultSet retorno = stmt.executeQuery();
			List<Equipe> equipes = new ArrayList<Equipe>();
			while (retorno.next()) {
				long idEquipe = retorno.getInt("id");
				String nome = retorno.getString("nome");
				String descricao = retorno.getString("descricao");

				equipes.add(new Equipe(idEquipe, nome, descricao));

			}

			stmt.close();
			conn.close();
			if (!equipes.isEmpty())
				return equipes;

			return null;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static List<Equipe> getEquipesUsuario(long idUsuario) {
		Connection conn = conectar();
		if (conn == null) {
			return null;
		}

		String sql = "SELECT equipes.* FROM equipes "
				+ "INNER JOIN usuarios_equipes AS u_e "
				+ "ON u_e.id_equipe = equipes.id WHERE u_e.id_usuario = ?";

		try {
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setInt(1, (int) idUsuario);

			ResultSet retorno = stmt.executeQuery();
			List<Equipe> equipes = new ArrayList<Equipe>();
			while (retorno.next()) {
				long idEquipe = retorno.getInt("id");
				String nome = retorno.getString("nome");
				String descricao = retorno.getString("descricao");

				equipes.add(new Equipe(idEquipe, nome, descricao));

			}

			stmt.close();
			conn.close();
			if (!equipes.isEmpty())
				return equipes;
			return null;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static List<Usuario> getUsuariosEquipe(long idEquipe) {
		Connection conn = conectar();
		if (conn == null) {
			return null;
		}

		String sql = "SELECT usuarios.* FROM usuarios "
				+ "INNER JOIN usuarios_equipes AS u_e "
				+ "ON u_e.id_usuario = usuarios.id WHERE id_equipe = ?";

		try {
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setInt(1, (int) idEquipe);

			ResultSet retorno = stmt.executeQuery();
			List<Usuario> usuarios = new ArrayList<Usuario>();
			while (retorno.next()) {
				long idUsuario = retorno.getInt("id");
				String nome = retorno.getString("nome");
				String cpf = retorno.getString("cpf");
				String email = retorno.getString("email");
				String cargo = retorno.getString("cargo");
				Nivel nivel = Nivel.valueOf(retorno.getString("nivel"));

				usuarios.add(new Usuario(idUsuario, nome, cpf, email, cargo, nivel));

			}

			stmt.close();
			conn.close();
			if (!usuarios.isEmpty())
				return usuarios;
			return null;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static List<Projeto> getProjetos() {
		Connection conn = conectar();
		if (conn == null) {
			return null;
		}

		String sql = "SELECT * FROM projetos";

		try {
			PreparedStatement stmt = conn.prepareStatement(sql);

			ResultSet retorno = stmt.executeQuery();

			List<Projeto> projetos = new ArrayList<Projeto>();

			while (retorno.next()) {
				long idProjeto = retorno.getInt("id");
				String nome = retorno.getString("nome");
				String descricao = retorno.getString("descricao");
				LocalDate dataInicio = retorno.getDate("data_inicio").toLocalDate();
				LocalDate dataTerminoPrevisto = retorno.getDate("data_termino_previsto").toLocalDate();
				long gerenteId = retorno.getInt("gerente_id");

				projetos.add(new Projeto(idProjeto, nome, descricao, dataInicio, dataTerminoPrevisto, gerenteId));
			}

			stmt.close();
			conn.close();
			if (!projetos.isEmpty())
				return projetos;
			return null;

		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}

	}

	public static List<Equipe> getEquipesProjeto(long idProjeto) {
		Connection conn = conectar();
		if (conn == null) {
			return null;
		}

		String sql = "SELECT equipes.* FROM equipes "
				+ "INNER JOIN equipes_projetos AS e_p ON e_p.id_equipe = equipes.id "
				+ "WHERE e_p.id_projeto = ?";

		try {
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setInt(1, (int) idProjeto);

			ResultSet retorno = stmt.executeQuery();

			List<Equipe> equipes = new ArrayList<Equipe>();

			while (retorno.next()) {
				long equipeId = retorno.getInt("id");
				String nome = retorno.getString("nome");
				String descricao = retorno.getString("descricao");

				equipes.add(new Equipe(equipeId, nome, descricao));
			}

			stmt.close();
			conn.close();
			if (!equipes.isEmpty())
				return equipes;
			return null;

		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}

	}

	public static List<Projeto> getProjetosEquipe(long idEquipe) {
		Connection conn = conectar();
		if (conn == null) {
			return null;
		}

		String sql = "SELECT projetos.* FROM projetos "
				+ "INNER JOIN equipes_projetos AS e_p ON e_p.id_projeto = projetos.id "
				+ "WHERE e_p.id_equipe = ?";

		try {
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setInt(1, (int) idEquipe);

			ResultSet retorno = stmt.executeQuery();

			List<Projeto> projetos = new ArrayList<Projeto>();

			while (retorno.next()) {
				long idProjeto = retorno.getInt("id");
				String nome = retorno.getString("nome");
				String descricao = retorno.getString("descricao");
				LocalDate dataInicio = retorno.getDate("data_inicio").toLocalDate();
				LocalDate dataTerminoPrevisto = retorno.getDate("data_termino_previsto").toLocalDate();
				long gerenteId = retorno.getInt("gerente_id");

				projetos.add(new Projeto(idProjeto, nome, descricao, dataInicio, dataTerminoPrevisto, gerenteId));
			}

			stmt.close();
			conn.close();
			if (!projetos.isEmpty())
				return projetos;
			return null;

		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Usuario getUsuario(long id) {
		Connection conn = conectar();
		if (conn == null) {
			return null;
		}

		String sql = "SELECT * FROM usuarios WHERE id = ?";

		try {
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setInt(1, (int) id);

			ResultSet retorno = stmt.executeQuery();
			if (retorno.next()) {
				long idUsuario = retorno.getInt("id");
				String nome = retorno.getString("nome");
				String cpf = retorno.getString("cpf");
				String email = retorno.getString("email");
				String cargo = retorno.getString("cargo");
				Nivel nivel = Nivel.valueOf(retorno.getString("nivel"));

				return new Usuario(idUsuario, nome, cpf, email, cargo, nivel);
			}
			return null;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}

	}

	/*
	 * public static Equipe getEquipeDB(long id) {
	 * Connection conn = conectar();
	 * if (conn == null) { return null; }
	 * 
	 * String sql = "SELECT * FROM equipes WHERE id = ?";
	 * try {
	 * PreparedStatement stmt = conn.prepareStatement(sql);
	 * stmt.setInt(1, (int) id);
	 * 
	 * ResultSet retorno = stmt.executeQuery();
	 * if (retorno.next()) {
	 * 
	 * return retorno;
	 * }
	 * 
	 * } catch (SQLException e) {
	 * e.printStackTrace();
	 * return null;
	 * }
	 * 
	 * return null;
	 * }
	 */

	public static List<Projeto> getProjetosUsuario(long idUsuario) {
		Connection conn = conectar();
		if (conn == null) {
			return null;
		}

		List<Equipe> equipes = DatabaseDAO.getEquipesUsuario(idUsuario);
		List<Projeto> projetos = new ArrayList<Projeto>();

		for (Equipe e : equipes) {
			List<Projeto> adicionar = DatabaseDAO.getProjetosEquipe(e.getId());
			if (adicionar != null)
				projetos.addAll(adicionar);
		}

		if (!projetos.isEmpty())
			return projetos;
		return null;
	}

	/**
	 * Cria uma nova equipe na tabela 'equipes' do banco de dados.
	 * 
	 * @param equipe O objeto Equipe a ser inserido.
	 * @return true se a operação for bem-sucedida, false caso contrário.
	 */
	public static boolean criarEquipe(Equipe equipe) {
		Connection conn = conectar();
		if (conn == null) {
			return false;
		}

		// A query SQL para inserir nome e descricao na tabela 'equipes'
		String sql = "INSERT INTO equipes (nome, descricao) VALUES (?, ?)";

		try {
			// Usando PreparedStatement para evitar SQL Injection e para retornar o ID
			// gerado
			PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

			// Mapeando os atributos do objeto para os parâmetros do query
			stmt.setString(1, equipe.getNome());
			stmt.setString(2, equipe.getDescricao());

			// Executa a inserção
			stmt.executeUpdate();

			// Extrai o ID gerado pelo banco de dados para a nova equipe
			ResultSet rs = stmt.getGeneratedKeys();
			if (rs.next()) {
				long novoId = rs.getLong(1);
				equipe.setId(novoId); // Atualiza o objeto Java com o ID gerado
				System.out.println("Equipe inserida com sucesso! ID: " + novoId);
			}

			// Fecha os recursos para evitar vazamentos
			stmt.close();
			conn.close();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Altera o nome e/ou a descrição de uma equipe no banco de dados.
	 * 
	 * @param equipe O objeto Equipe contendo o ID e as novas informações.
	 * @return true se a operação for bem-sucedida, false caso contrário.
	 */
	public static boolean alterarEquipe(Equipe equipe) {
		Connection conn = null;
		PreparedStatement stmt = null;

		try {
			conn = conectar();
			if (conn == null) {
				return false;
			}

			// A query SQL para atualizar nome e descricao
			String sql = "UPDATE equipes SET nome = ?, descricao = ? WHERE id = ?";

			stmt = conn.prepareStatement(sql);
			stmt.setString(1, equipe.getNome());
			stmt.setString(2, equipe.getDescricao());
			stmt.setLong(3, equipe.getId());

			int rowsAffected = stmt.executeUpdate();

			return rowsAffected > 0;

		} catch (SQLException e) {
			e.printStackTrace();
			return false;

		} finally {
			// Fecha os recursos
			try {
				if (stmt != null)
					stmt.close();
				if (conn != null)
					conn.close();
			} catch (SQLException closeEx) {
				closeEx.printStackTrace();
			}
		}
	}

	public static boolean adicionarUsuarioEquipe(long equipeId, long usuarioId) {
		Connection conn = null;
		PreparedStatement stmt = null;

		try {
			conn = conectar();
			if (conn == null) {
				return false;
			}

			// A query SQL para inserir uma nova associação
			String sql = "INSERT INTO usuarios_equipes (equipe_id, usuario_id) VALUES (?, ?)";

			stmt = conn.prepareStatement(sql);
			stmt.setLong(1, equipeId);
			stmt.setLong(2, usuarioId);

			int rowsAffected = stmt.executeUpdate();

			return rowsAffected > 0;

		} catch (SQLException e) {
			e.printStackTrace();
			return false;

		} finally {
			// Fecha os recursos
			try {
				if (stmt != null)
					stmt.close();
				if (conn != null)
					conn.close();
			} catch (SQLException closeEx) {
				closeEx.printStackTrace();
			}
		}
	}

	/**
	 * Remove um usuário de uma equipe na tabela de relacionamento.
	 * 
	 * @param equipeId  O ID da equipe.
	 * @param usuarioId O ID do usuário a ser removido.
	 * @return true se a operação for bem-sucedida, false caso contrário.
	 */
	public static boolean removerUsuarioEquipe(long equipeId, long usuarioId) {
		Connection conn = null;
		PreparedStatement stmt = null;

		try {
			conn = conectar();
			if (conn == null) {
				return false;
			}

			// A query SQL para remover uma associação específica
			String sql = "DELETE FROM usuarios_equipes WHERE equipe_id = ? AND usuario_id = ?";

			stmt = conn.prepareStatement(sql);
			stmt.setLong(1, equipeId);
			stmt.setLong(2, usuarioId);

			int rowsAffected = stmt.executeUpdate();

			// Retorna true se a remoção foi bem-sucedida (uma linha foi afetada)
			return rowsAffected > 0;

		} catch (SQLException e) {
			e.printStackTrace();
			return false;

		} finally {
			// Fecha os recursos
			try {
				if (stmt != null)
					stmt.close();
				if (conn != null)
					conn.close();
			} catch (SQLException closeEx) {
				closeEx.printStackTrace();
			}
		}
	}


    /**
     * Busca uma equipe no banco de dados pelo seu ID.
     * @param id O ID da equipe.
     * @return O objeto Equipe, ou null se não for encontrado.
     */
    public static Equipe getEquipePorId(long id) {
        String sql = "SELECT * FROM equipes WHERE id = ?";
        try (Connection conn = conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    long equipeId = rs.getLong("id");
                    String nome = rs.getString("nome");
                    String descricao = rs.getString("descricao");
                    
                    return new Equipe(equipeId, nome, descricao);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
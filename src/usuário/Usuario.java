package usuário;

public class Usuario {
	
    private String nome, cpf, email, cargo, login, senha;
    private long id;
    private Nivel nivel;

    public Usuario(String nome, String cpf, String email, String cargo, String login, String senha, Nivel nivel) {
        this.nome = nome;
        this.cpf = cpf;
        this.email = email;
        this.cargo = cargo;
        this.login = login;
        this.senha = senha;
        this.nivel = nivel;
    }

    public Usuario(long id, String nome, String cpf, String email, String cargo, Nivel nivel) {
    	this.id = id;
    	this.nome = nome;
        this.cpf = cpf;
        this.email = email;
        this.cargo = cargo;
        this.nivel = nivel;
	}

	// Getters
    public String getNome() { return nome; }
    public String getCpf() { return cpf; }
    public String getEmail() { return email; }
    public String getCargo() { return cargo; }
    public String getLogin() { return login; }
    public String getSenha() { return senha; }
    public Nivel getNivel() { return nivel; }
    public int getId() { return id; }
    
    public void setId(int id) {
    	this.id = id;
    }
}

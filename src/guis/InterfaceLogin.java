package guis;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import main.DatabaseDAO;
import main.ProgramaGestão;

public class InterfaceLogin extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTextField txtUsuario;
	private JPasswordField txtSenha;
	private JButton entrar, registrar;
	
	public InterfaceLogin() {
		super("Entrar em uma conta");
		
		GridBagLayout gbl = new GridBagLayout();
		GridBagConstraints gbc = new GridBagConstraints();
		setLayout(gbl);
		
		gbc.weightx = 1;
		gbc.weighty = 1;
		
		gbc.insets = new Insets(0, 5, 0, 5);
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.fill = GridBagConstraints.NONE;
		add(new JLabel("Login: "), gbc);
		txtUsuario = new JTextField();
		gbc.gridx = 1;
		gbc.gridwidth = 2;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		add(txtUsuario, gbc);
		
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.fill = GridBagConstraints.NONE;
		gbc.gridwidth = 1;
		add(new JLabel("Senha: "), gbc);
		txtSenha = new JPasswordField();
		gbc.gridx = 1;
		gbc.gridwidth = 2;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		add(txtSenha, gbc);
		
		entrar = new JButton("Entrar");
		gbc.gridy = 3;
		gbc.gridx = 0;
		gbc.gridwidth = 1;
		add(entrar, gbc);
		
		registrar = new JButton("Criar nova conta");
		gbc.gridx = 2;
		add(registrar, gbc);
		
		entrar.addActionListener(e -> {
			if (verificarSenha(txtUsuario.getText(), new String(txtSenha.getPassword()))) {
				DatabaseDAO.recuperarUsuário(Integer.parseInt(DatabaseDAO.getValor("id", "login", txtUsuario.getText())));
				JOptionPane.showMessageDialog(ProgramaGestão.currentWindow, "Login feito com sucesso!", "Sucesso!", JOptionPane.INFORMATION_MESSAGE);
				ProgramaGestão.currentWindow = new PainelPrincipal();
				this.dispose();
			} else {
				JOptionPane.showMessageDialog(ProgramaGestão.currentWindow, "Usuário ou senha incorretos.", "Falha no login.", JOptionPane.ERROR_MESSAGE);
			}
			
		});
		
		registrar.addActionListener(e -> {
			ProgramaGestão.currentWindow = new RegistroUsuário();
			this.dispose();
		});
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 200);
        setLocationRelativeTo(null); // Centralizar
        setVisible(true);
	}

	// Valida a senha inserida
	private boolean verificarSenha(String login, String pass) {
		return DatabaseDAO.getValor("senha", "login", login).equals(pass);
	}
}

package guis;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class InterfaceLogin extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTextField txtUsuário;
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
		txtUsuário = new JTextField();
		gbc.gridx = 1;
		gbc.gridwidth = 2;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		add(txtUsuário, gbc);
		
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
			/* TODO painel principal
			* this.dispose();
			*/
		});
		
		registrar.addActionListener(e -> {
			new RegistroUsuário();
			this.dispose();
		});
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 200);
        setLocationRelativeTo(null); // Centralizar
        setVisible(true);
	}
}

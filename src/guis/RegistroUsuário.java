package guis;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import main.ProgramaGestão;
import main.DatabaseDAO;
import usuário.Nivel;
import usuário.Usuario;

public class RegistroUsuário extends JFrame {

    private JTextField txtNome, txtCpf, txtEmail, txtCargo, txtLogin;
    private JPasswordField txtSenha;
    private JButton btnSalvar;

    public RegistroUsuário() {
        super("Cadastro de Usuário");

        // Configuração do layout
        setLayout(new GridLayout(7, 2, 5, 5));

        // Campos de entrada
        add(new JLabel("Nome Completo:"));
        txtNome = new JTextField();
        add(txtNome);

        add(new JLabel("CPF:"));
        txtCpf = new JTextField();
        add(txtCpf);

        add(new JLabel("E-mail:"));
        txtEmail = new JTextField();
        add(txtEmail);

        add(new JLabel("Cargo:"));
        txtCargo = new JTextField();
        add(txtCargo);

        add(new JLabel("Login:"));
        txtLogin = new JTextField();
        add(txtLogin);

        add(new JLabel("Senha:"));
        txtSenha = new JPasswordField();
        add(txtSenha);

        // Botão salvar
        btnSalvar = new JButton("Salvar");
        add(btnSalvar);

        // Espaço vazio para alinhar
        add(new JLabel(""));

        // Ação do botão
        btnSalvar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                salvarUsuario();
            }
        });

        // Configurações da janela
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null); // Centralizar
        setVisible(true);
    }

    // Método que coleta os dados e chama o DAO
    private void salvarUsuario() {
        String nome = txtNome.getText();
        String cpf = txtCpf.getText();
        String email = txtEmail.getText();
        String cargo = txtCargo.getText();
        String login = txtLogin.getText();
        String senha = new String(txtSenha.getPassword());

        ProgramaGestão.usuarioAtual = new Usuario(nome, cpf, email, cargo, login, senha, Nivel.COLABORADOR);

        if (DatabaseDAO.criarUsuário(ProgramaGestão.usuarioAtual)) {
            JOptionPane.showMessageDialog(this, "Usuário cadastrado com sucesso!");
            ProgramaGestão.currentWindow = new PainelPrincipal();
            this.dispose();
        }
    }

    public static void main(String[] args) {
        new ProgramaGestão();
    }
}

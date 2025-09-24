package guis;

import main.Equipe;
import main.DatabaseDAO;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RegistroEquipe extends JDialog {

    private JTextField txtNome;
    private JTextArea txtDescricao;
    private JButton btnSalvar;
    private JButton btnCancelar;

    public RegistroEquipe(JFrame parent) {
        super(parent, "Registrar Nova Equipe", true);
        setSize(400, 300);
        setLayout(new BorderLayout(10, 10));
        setLocationRelativeTo(parent);

        // --- 1. Painel de Formulário ---
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Campo Nome da Equipe
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Nome da Equipe:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        txtNome = new JTextField(20);
        formPanel.add(txtNome, gbc);

        // Campo Descrição
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Descrição:"), gbc);
        gbc.gridx = 1;
        txtDescricao = new JTextArea(5, 20);
        JScrollPane scrollPane = new JScrollPane(txtDescricao);
        formPanel.add(scrollPane, gbc);

        add(formPanel, BorderLayout.CENTER);

        // --- 2. Painel de Botões ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        btnSalvar = new JButton("Salvar");
        btnCancelar = new JButton("Cancelar");

        buttonPanel.add(btnSalvar);
        buttonPanel.add(btnCancelar);

        add(buttonPanel, BorderLayout.SOUTH);

        // --- 3. Ações dos Botões ---
        btnSalvar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                salvarEquipe();
            }
        });

        btnCancelar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose(); // Fecha a janela
            }
        });
    }

    private void salvarEquipe() {
        String nome = txtNome.getText().trim();
        String descricao = txtDescricao.getText().trim();

        if (nome.isEmpty()) {
            JOptionPane.showMessageDialog(this, "O nome da equipe não pode ser vazio.", "Erro",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Cria o objeto Equipe com as informações do formulário
        Equipe novaEquipe = new Equipe(nome, descricao);

        // Chama o método DAO para salvar no banco de dados
        boolean sucesso = DatabaseDAO.criarEquipe(novaEquipe);

        if (sucesso) {
            JOptionPane.showMessageDialog(this, "Equipe registrada com sucesso!", "Sucesso",
                    JOptionPane.INFORMATION_MESSAGE);
            dispose(); // Fecha a janela após o sucesso
        } else {
            JOptionPane.showMessageDialog(this, "Falha ao registrar a equipe. Verifique o console.", "Erro",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
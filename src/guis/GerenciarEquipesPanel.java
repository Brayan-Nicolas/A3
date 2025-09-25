package guis;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import main.DatabaseDAO;
import main.Equipe;
import usuário.Nivel;
import usuário.Usuario;

public class GerenciarEquipesPanel extends JPanel {

    private Equipe equipe;
    private Usuario usuarioLogado;
    private PainelPrincipal painelPrincipal; // Adicionando uma referência ao painel principal

    private JTextField txtNome;
    private JTextArea txtDescricao;
    private JList<Usuario> listaMembros;
    private DefaultListModel<Usuario> modeloMembros;

    private JButton btnSalvar, btnAdicionarMembro, btnRemoverMembro;
    private JButton btnVoltar; // O novo botão

    // Construtor agora recebe o usuário logado e a referência ao painel principal
    public GerenciarEquipesPanel(Equipe equipe, Usuario usuarioLogado, PainelPrincipal painelPrincipal) {
        this.equipe = equipe;
        this.usuarioLogado = usuarioLogado;
        this.painelPrincipal = painelPrincipal; 
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // --- 1. Formulário de Detalhes da Equipe ---
        JPanel painelForm = new JPanel(new GridLayout(2, 2, 5, 5));
        painelForm.setBorder(BorderFactory.createTitledBorder("Detalhes da Equipe"));

        txtNome = new JTextField(equipe.getNome());
        txtDescricao = new JTextArea(equipe.getDescricao());

        painelForm.add(new JLabel("Nome:"));
        painelForm.add(txtNome);
        painelForm.add(new JLabel("Descrição:"));
        painelForm.add(new JScrollPane(txtDescricao));

        add(painelForm, BorderLayout.NORTH);

        // --- 2. Lista de Membros da Equipe ---
        JPanel painelMembros = new JPanel(new BorderLayout());
        painelMembros.setBorder(BorderFactory.createTitledBorder("Membros da Equipe"));

        modeloMembros = new DefaultListModel<>();
        listaMembros = new JList<>(modeloMembros);
        JScrollPane scrollMembros = new JScrollPane(listaMembros);
        painelMembros.add(scrollMembros, BorderLayout.CENTER);

        adicionarBotoesDeMembro(painelMembros);
        add(painelMembros, BorderLayout.CENTER);

        // --- 3. Botões de ação, incluindo o novo botão Voltar ---
        JPanel painelBotoesAcao = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnSalvar = new JButton("Salvar Alterações");
        btnVoltar = new JButton("Voltar"); // Instanciando o botão Voltar

        painelBotoesAcao.add(btnSalvar);
        painelBotoesAcao.add(btnVoltar);

        add(painelBotoesAcao, BorderLayout.SOUTH);

        // --- 4. Carregar os dados iniciais ---
        carregarMembros();
        adicionarAcoes();

        // --- 5. Aplicar o controle de acesso ---
        aplicarRestricoesDeAcesso();
    }

    private void adicionarBotoesDeMembro(JPanel parentPanel) {
        JPanel painelBotoes = new JPanel();
        btnAdicionarMembro = new JButton("Adicionar Membro");
        btnRemoverMembro = new JButton("Remover Membro");
        painelBotoes.add(btnAdicionarMembro);
        painelBotoes.add(btnRemoverMembro);
        parentPanel.add(painelBotoes, BorderLayout.SOUTH);
    }

    private void carregarMembros() {
        modeloMembros.clear();
        if (equipe.getMembros() != null) {
            for (Usuario membro : equipe.getMembros()) {
                modeloMembros.addElement(membro);
            }
        }
    }

    private void adicionarAcoes() {
        btnSalvar.addActionListener(e -> salvarAlteracoes());
        btnAdicionarMembro.addActionListener(e -> adicionarMembro());
        btnRemoverMembro.addActionListener(e -> removerMembro());
        btnVoltar.addActionListener(e -> voltarAoPainelPrincipal()); // Adicionando a ação do botão Voltar
    }

    private void aplicarRestricoesDeAcesso() {
        boolean podeModificar = usuarioLogado.getNivel() == Nivel.ADMINISTRADOR
                || usuarioLogado.getNivel() == Nivel.GERENTE;

        txtNome.setEditable(podeModificar);
        txtDescricao.setEditable(podeModificar);

        btnSalvar.setEnabled(podeModificar);
        btnAdicionarMembro.setEnabled(podeModificar);
        btnRemoverMembro.setEnabled(podeModificar);
    }

    // Método que implementa a navegação
    private void voltarAoPainelPrincipal() {
        // Pega o contêiner pai, que deve ser o seu JFrame ou um JPanel intermediário
        this.getParent().add(painelPrincipal);
        this.setVisible(false); // Oculta o painel atual
        painelPrincipal.setVisible(true); // Exibe o painel principal
        this.getParent().remove(this); // Remove o painel atual para liberar memória
        this.getParent().revalidate();
        this.getParent().repaint();
    }

    // --- Ações dos Botões ---
    private void salvarAlteracoes() {
        String novoNome = txtNome.getText().trim();
        String novaDescricao = txtDescricao.getText().trim();

        if (novoNome.isEmpty()) {
            JOptionPane.showMessageDialog(this, "O nome da equipe não pode ser vazio.", "Erro",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        equipe.setNome(novoNome);
        equipe.setDescricao(novaDescricao);

        boolean sucesso = DatabaseDAO.alterarEquipe(equipe);

        if (sucesso) {
            JOptionPane.showMessageDialog(this, "Alterações salvas com sucesso!", "Sucesso",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Falha ao salvar as alterações.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void adicionarMembro() {
        List<Usuario> todosOsUsuarios = DatabaseDAO.getTodosUsuarios();

        if (todosOsUsuarios.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Não há novos usuários para adicionar.", "Aviso",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JComboBox<Usuario> comboUsuarios = new JComboBox<>(todosOsUsuarios.toArray(new Usuario[0]));

        int resultado = JOptionPane.showConfirmDialog(this, comboUsuarios, "Selecione um usuário para adicionar:",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (resultado == JOptionPane.OK_OPTION) {
            Usuario usuarioSelecionado = (Usuario) comboUsuarios.getSelectedItem();
            if (usuarioSelecionado != null) {
                if (equipe.getMembros().contains(usuarioSelecionado)) {
                    JOptionPane.showMessageDialog(this, "Este usuário já é membro da equipe.", "Aviso",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }
                boolean sucesso = DatabaseDAO.adicionarUsuarioEquipe(equipe.getId(), usuarioSelecionado.getId());
                if (sucesso) {
                    JOptionPane.showMessageDialog(this, "Membro adicionado com sucesso!");
                    equipe.getMembros().add(usuarioSelecionado);
                    carregarMembros();
                } else {
                    JOptionPane.showMessageDialog(this, "Falha ao adicionar o membro.", "Erro",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private void removerMembro() {
        Usuario membroSelecionado = listaMembros.getSelectedValue();
        if (membroSelecionado != null) {
            int confirmacao = JOptionPane.showConfirmDialog(this, "Tem certeza que deseja remover o membro " +
                    membroSelecionado.getNome() + "?", "Confirmar Remoção", JOptionPane.YES_NO_OPTION);

            if (confirmacao == JOptionPane.YES_OPTION) {
                boolean sucesso = DatabaseDAO.removerUsuarioEquipe(equipe.getId(), membroSelecionado.getId());
                if (sucesso) {
                    JOptionPane.showMessageDialog(this, "Membro removido com sucesso!");
                    equipe.getMembros().remove(membroSelecionado);
                    carregarMembros();
                } else {
                    JOptionPane.showMessageDialog(this, "Falha ao remover o membro.", "Erro",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Selecione um membro para remover.");
        }
    }
}
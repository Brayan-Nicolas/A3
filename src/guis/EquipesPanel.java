package guis;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import main.DatabaseDAO;
import main.Equipe;
import usuário.Nivel;
import usuário.Usuario;

public class EquipesPanel extends JPanel {

    private Usuario usuarioLogado;
    private JTable tabelaEquipes;
    private DefaultTableModel modeloTabela;

    public EquipesPanel(Usuario usuarioLogado) {
        this.usuarioLogado = usuarioLogado;
        setLayout(new BorderLayout(10, 10));

        // --- 1. Tabela de Equipes ---
        String[] colunas = { "ID", "Nome da Equipe", "Membros", "Ações" };
        modeloTabela = new DefaultTableModel(colunas, 0);
        tabelaEquipes = new JTable(modeloTabela);
        // Desabilita a edição de células para evitar que o usuário altere o conteúdo
        tabelaEquipes.setDefaultEditor(Object.class, null);
        JScrollPane scrollPane = new JScrollPane(tabelaEquipes);

        add(scrollPane, BorderLayout.CENTER);

        // --- 2. Painel de Botões de Criação ---
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER));
        if (usuarioLogado.getNivel() == Nivel.ADMINISTRADOR || usuarioLogado.getNivel() == Nivel.GERENTE) {
            JButton btnNovaEquipe = new JButton("Nova Equipe");
            painelBotoes.add(btnNovaEquipe);
            btnNovaEquipe.addActionListener(e -> {
                JDialog dialog = new RegistroEquipe((JFrame) SwingUtilities.getWindowAncestor(this));
                dialog.setVisible(true);
                carregarEquipes();
            });
        }
        add(painelBotoes, BorderLayout.SOUTH);

        // --- 3. Carregar os Dados e popular a tabela ---
        carregarEquipes();
    }

    private void carregarEquipes() {
        modeloTabela.setRowCount(0);
        List<Equipe> equipesDisponiveis = new ArrayList<>();

        if (this.usuarioLogado.getNivel() == Nivel.ADMINISTRADOR) {
            equipesDisponiveis = DatabaseDAO.getEquipes();
        } else {
            equipesDisponiveis = DatabaseDAO.getEquipesUsuario(this.usuarioLogado.getId());
        }

        int row = 0;
        for (Equipe equipe : equipesDisponiveis) {
            // Cria uma nova instância do botão para cada linha.
            // A classe interna tem acesso direto ao método 'gerenciarEquipe'.
            JButton gerenciarButton = new GerenciarEquipeButton(row);

            modeloTabela.addRow(new Object[] {
                    equipe.getId(),
                    equipe.getNome(),
                    formatarMembros(equipe.getMembros()),
                    gerenciarButton
            });
            row++;
        }
    }

    private String formatarMembros(List<Usuario> membros) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < membros.size(); i++) {
            sb.append(membros.get(i).getNome());
            if (i < membros.size() - 1) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }

    // Este método é público para ser acessado pela classe interna do botão.
    public void gerenciarEquipe(int row) {
        long equipeId = (long) modeloTabela.getValueAt(row, 0);
        Equipe equipeSelecionada = DatabaseDAO.getEquipePorId(equipeId);

        if (equipeSelecionada != null) {
            SwingUtilities.invokeLater(() -> {
                JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
                if (parentFrame != null) {
                    GerenciarEquipesPanel gerenciarPanel = new GerenciarEquipesPanel(equipeSelecionada, usuarioLogado,
                            parentFrame);
                    parentFrame.setContentPane(gerenciarPanel);
                    parentFrame.revalidate();
                    parentFrame.repaint();
                } else {
                    JOptionPane.showMessageDialog(this, "Janela principal não encontrada.", "Erro",
                            JOptionPane.ERROR_MESSAGE);
                }
            });
        } else {
            JOptionPane.showMessageDialog(this, "Equipe não encontrada.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Classe interna para o botão "Gerenciar".
     * Ela tem acesso direto aos membros da classe EquipesPanel e encapsula a lógica
     * de clique.
     */
    private class GerenciarEquipeButton extends JButton {

        private final int row;

        public GerenciarEquipeButton(int row) {
            super("Gerenciar");
            this.row = row;

            // Adiciona a ação do botão. O método gerenciarEquipe() é acessado diretamente.
            addActionListener(e -> {
                gerenciarEquipe(this.row);
            });
        }
    }
}
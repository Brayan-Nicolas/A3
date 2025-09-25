package guis;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import main.DatabaseDAO;
import main.Equipe;
import main.ProgramaGestão;
import usuário.Nivel;
import usuário.Usuario;

public class EquipesPanel extends JPanel {

    private Usuario usuarioLogado;
    private JTable tabelaEquipes;
    private DefaultTableModel modeloTabela;

    /**
     * Interface funcional para o ouvinte do botão.
     */
    @FunctionalInterface
    private interface ButtonClickListener {
        void onButtonClick(int row);
    }

    public EquipesPanel(Usuario usuarioLogado) {
        // CORREÇÃO: Usa o parâmetro passado, não a variável global
        this.usuarioLogado = ProgramaGestão.usuarioAtual;
        setLayout(new BorderLayout(10, 10));

        // --- 1. Tabela de Equipes ---
        String[] colunas = { "ID", "Nome da Equipe", "Membros", "Ações" };
        modeloTabela = new DefaultTableModel(colunas, 0);
        tabelaEquipes = new JTable(modeloTabela);
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
                // revalidate() e repaint() são redundantes após carregarEquipes()
            });
        }
        add(painelBotoes, BorderLayout.SOUTH);

        // --- 3. Carregar os Dados ---
        carregarEquipes();

        // --- 4. Configurar o Botão na Tabela (Sempre) ---
        // CORREÇÃO: Remove a verificação desnecessária
        tabelaEquipes.getColumn("Ações").setCellRenderer(new ButtonRenderer());
        tabelaEquipes.getColumn("Ações").setCellEditor(new ButtonEditor(this::gerenciarEquipe));
    }

    private void carregarEquipes() {
        modeloTabela.setRowCount(0);
        List<Equipe> equipesDisponiveis = new ArrayList<>();

        // CORREÇÃO: Usa a referência correta do usuário logado
        if (this.usuarioLogado.getNivel() == Nivel.ADMINISTRADOR) {
            equipesDisponiveis = DatabaseDAO.getEquipes();
        } else {
            equipesDisponiveis = DatabaseDAO.getEquipesUsuario(this.usuarioLogado.getId());
        }

        for (Equipe equipe : equipesDisponiveis) {
            modeloTabela.addRow(new Object[] {
                    equipe.getId(),
                    equipe.getNome(),
                    formatarMembros(equipe.getMembros()),
                    new JButton("Gerenciar")
            });
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

    private void gerenciarEquipe(int row) {
        long equipeId = (long) modeloTabela.getValueAt(row, 0);
        Equipe equipeSelecionada = DatabaseDAO.getEquipePorId(equipeId);

        if (equipeSelecionada != null) {
            SwingUtilities.invokeLater(() -> {
                JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);

                if (parentFrame != null) {
                    // CORREÇÃO: A lógica de passagem de referência para o GerenciarEquipesPanel
                    // está correta.
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
     * Renderizador para exibir o JButton na célula da tabela.
     */
    private class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
                int row, int column) {
            if (value instanceof JButton) {
                return (JButton) value;
            }
            setText((value == null) ? "" : value.toString());
            return this;
        }
    }

    /**
     * Editor para lidar com o clique no botão da célula da tabela.
     */
    private class ButtonEditor extends DefaultCellEditor {
        protected JButton button;
        private final ButtonClickListener listener;
        private int row;
        private boolean isPushed;

        public ButtonEditor(ButtonClickListener listener) {
            super(new JTextField());
            this.listener = listener;
            setClickCountToStart(1);

            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(e -> {
                isPushed = true;
                fireEditingStopped();
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row,
                int column) {
            this.row = row;
            isPushed = false;

            if (value instanceof JButton) {
                button = (JButton) value;
            } else {
                button.setText((value == null) ? "" : value.toString());
            }
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            if (isPushed) {
                SwingUtilities.invokeLater(() -> {
                    if (row != -1) {
                        listener.onButtonClick(row);
                    }
                });
            }
            isPushed = false;
            return button;
        }

        @Override
        public boolean stopCellEditing() {
            isPushed = false;
            return super.stopCellEditing();
        }
    }
}
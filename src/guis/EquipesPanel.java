package guis;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import javax.swing.JButton;
import javax.swing.JDialog; // Importação necessária
import javax.swing.JFrame; // Importação necessária
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities; // Importação necessária
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.AbstractCellEditor;
import javax.swing.table.TableCellEditor;

import main.DatabaseDAO;
import main.Equipe;
import main.ProgramaGestao;
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
        tabelaEquipes.setDefaultEditor(Object.class, null);
        JScrollPane scrollPane = new JScrollPane(tabelaEquipes);

        add(scrollPane, BorderLayout.CENTER);

        // --- 2. Painel de Botões de Criação ---
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER));
        if (usuarioLogado.getNivel() == Nivel.ADMINISTRADOR || usuarioLogado.getNivel() == Nivel.GERENTE) {
            JButton btnNovaEquipe = new JButton("Nova Equipe");
            painelBotoes.add(btnNovaEquipe);

            // Ação do botão "Nova Equipe"
            btnNovaEquipe.addActionListener(e -> {
                // Obtém a janela pai para que o JDialog seja exibido corretamente
                JDialog dialog = new RegistroEquipe((JFrame) SwingUtilities.getWindowAncestor(this));
                dialog.setVisible(true);

                // Recarrega a lista de equipes para mostrar a nova equipe
                carregarEquipes();
            });
        }
        add(painelBotoes, BorderLayout.SOUTH);

        // --- 3. Carregar os Dados ---
        carregarEquipes();

        // --- 4. Configurar o Botão na Tabela ---
        if (modeloTabela.getRowCount() > 0) {
            tabelaEquipes.getColumn("Ações").setCellRenderer(new ButtonRenderer());
            tabelaEquipes.getColumn("Ações").setCellEditor(new ButtonEditor(this::gerenciarEquipe));
        }
    }

    private void carregarEquipes() {
        modeloTabela.setRowCount(0);
        List<Equipe> equipesDisponiveis = new ArrayList<>();

        if (usuarioLogado.getNivel() == Nivel.ADMINISTRADOR) {
            equipesDisponiveis = DatabaseDAO.getEquipes();
        } else {
            equipesDisponiveis = DatabaseDAO.getEquipesUsuario(ProgramaGestao.usuarioAtual.getId());
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
        
        // Obter o objeto Equipe completo do banco de dados
        Equipe equipeSelecionada = DatabaseDAO.getEquipePorId(equipeId);
        
        if (equipeSelecionada != null) {
            // Cria a nova tela de gerenciamento, passando a referência da tela atual para o botão "Voltar"
            GerenciarEquipePanel gerenciarPanel = new GerenciarEquipePanel(equipeSelecionada, usuarioLogado, this);
            
            // Troca o painel na janela principal
            parentFrame.setContentPane(gerenciarPanel);
            parentFrame.revalidate();
            parentFrame.repaint();
        } else {
            JOptionPane.showMessageDialog(this, "Equipe não encontrada.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}
package guis;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.ArrayList; // Para simular o serviço
import java.util.List;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import main.DatabaseDAO;
import main.Equipe;
import main.ProgramaGestão;
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
        // A coluna agora se chama "Membros"
        String[] colunas = { "ID", "Nome da Equipe", "Membros" }; 
        modeloTabela = new DefaultTableModel(colunas, 0);
        tabelaEquipes = new JTable(modeloTabela);
        tabelaEquipes.setDefaultEditor(Object.class, null);
        JScrollPane scrollPane = new JScrollPane(tabelaEquipes);
        
        add(scrollPane, BorderLayout.CENTER);

        // --- 2. Lógica de Permissões para os Botões ---
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER));

        if (usuarioLogado.getNivel() == Nivel.ADMINISTRADOR || usuarioLogado.getNivel() == Nivel.GERENTE) {
            JButton btnNovaEquipe = new JButton("Nova Equipe");
            JButton btnGerenciarMembros = new JButton("Gerenciar Membros");

            painelBotoes.add(btnNovaEquipe);
            painelBotoes.add(btnGerenciarMembros);

            btnNovaEquipe.addActionListener(
                    e -> JOptionPane.showMessageDialog(this, "Abrir formulário para criar nova equipe."));
            btnGerenciarMembros.addActionListener(
                    e -> JOptionPane.showMessageDialog(this, "Abrir formulário para gerenciar membros."));
        }

        add(painelBotoes, BorderLayout.SOUTH);

        // --- 3. Carregar os Dados ---
        carregarEquipes();
    }

    private void carregarEquipes() {
        modeloTabela.setRowCount(0);

        List<Equipe> equipesDisponiveis = new ArrayList<>();

        // Lógica de filtro movida do serviço
        if (usuarioLogado.getNivel() == Nivel.ADMINISTRADOR) {
            equipesDisponiveis = DatabaseDAO.getEquipes();
        } else {
            equipesDisponiveis = DatabaseDAO.getEquipesUsuario(ProgramaGestão.usuarioAtual.getId());
        }

        for (Equipe equipe : equipesDisponiveis) {
            modeloTabela.addRow(new Object[] {
                equipe.getId(),
                equipe.getNome(),
                formatarMembros(equipe.getMembros()) // Usando o novo método
            });
        }
    }

    /**
     * Converte uma lista de usuários em uma string de nomes separados por vírgula.
     * @param membros A lista de usuários.
     * @return Uma string com os nomes dos membros.
     */
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
}
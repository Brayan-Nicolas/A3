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
        // A coluna agora se chama "Membros"
        String[] colunas = { "ID", "Nome da Equipe", "Membros" }; 
        modeloTabela = new DefaultTableModel(colunas, 0);
        tabelaEquipes = new JTable(modeloTabela);
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

        // Dados fictícios, que na vida real viriam do banco de dados
        Usuario admin = new Usuario(1L, "Admin Geral", "123", "admin@a.com", "Administrador", Nivel.ADMINISTRADOR);
        Usuario gerente = new Usuario(2L, "Gerente Equipe A", "456", "gerente@a.com", "Gerente", Nivel.GERENTE);
        Usuario colaborador = new Usuario(3L, "Colaborador B", "789", "colab@b.com", "Colaborador", Nivel.COLABORADOR);

        Equipe equipeA = new Equipe(10L, "Equipe A");
        equipeA.adicionarMembro(admin);
        equipeA.adicionarMembro(gerente);

        Equipe equipeB = new Equipe(11L, "Equipe B");
        equipeB.adicionarMembro(admin);
        equipeB.adicionarMembro(colaborador);

        // Lógica de filtro movida do serviço
        if (usuarioLogado.getNivel() == Nivel.ADMINISTRADOR) {
            equipesDisponiveis.add(equipeA);
            equipesDisponiveis.add(equipeB);
        } else {
            if (equipeA.getMembros().contains(usuarioLogado)) {
                equipesDisponiveis.add(equipeA);
            }
            if (equipeB.getMembros().contains(usuarioLogado)) {
                equipesDisponiveis.add(equipeB);
            }
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
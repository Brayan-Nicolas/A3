package guis;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import main.Equipe;
import main.Projeto.Projeto;
import usuário.Nivel;
import usuário.Usuario;

public class DetalhesProjetoPanel extends JPanel {

    private Usuario usuarioLogado;
    private Projeto projeto;

    private JLabel lblNome, lblDescricao, lblStatus, lblGerente;
    private JList<Equipe> listaEquipes;
    private JButton btnAdicionarEquipe, btnRemoverEquipe;
    private JList<String> listaTarefas;

    public DetalhesProjetoPanel(Projeto projeto, Usuario usuarioLogado) {
        this.projeto = projeto;
        this.usuarioLogado = usuarioLogado;
        
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // --- Seção 1: Detalhes do Projeto ---
        JPanel painelDetalhes = new JPanel(new GridLayout(4, 1, 5, 5));

        lblNome = new JLabel("Nome: " + projeto.getNome());
        lblDescricao = new JLabel("Descrição: " + projeto.getDescricao());
        lblStatus = new JLabel("Status: " + projeto.getStatus().toString());
        lblGerente = new JLabel("Gerente: " + projeto.getGerente().getNome());

        painelDetalhes.add(lblNome);
        painelDetalhes.add(lblDescricao);
        painelDetalhes.add(lblStatus);
        painelDetalhes.add(lblGerente);
        painelDetalhes.add(lblGerente);

        add(painelDetalhes, BorderLayout.NORTH);

        // --- Seção 2: Equipes do Projeto ---
        JPanel painelEquipes = new JPanel(new BorderLayout());
        painelEquipes.setBorder(BorderFactory.createTitledBorder("Equipes do Projeto"));

        DefaultListModel<Equipe> modeloEquipes = new DefaultListModel<>();
        if (projeto.getEquipes() != null) {
            for (Equipe equipe : projeto.getEquipes()) {
                modeloEquipes.addElement(equipe);
            }
        }

        listaEquipes = new JList<>(modeloEquipes);
        JScrollPane scrollEquipes = new JScrollPane(listaEquipes);
        painelEquipes.add(scrollEquipes, BorderLayout.CENTER);

        // --- Lógica de permissão corrigida ---
        boolean usuarioNaEquipeDoProjeto = false;
        if (projeto.getEquipes() != null) {
            for (Equipe equipe : projeto.getEquipes()) {
                if (equipe.getMembros().contains(usuarioLogado)) {
                    usuarioNaEquipeDoProjeto = true;
                    break;
                }
            }
        }

        boolean podeGerenciar = (usuarioLogado.getNivel() == Nivel.ADMINISTRADOR) ||
                (usuarioLogado.getNivel() == Nivel.GERENTE && usuarioNaEquipeDoProjeto) || 
                (usuarioLogado.getNivel() == Nivel.GERENTE && usuarioLogado.getId() == projeto.getGerente().getId());

        if (podeGerenciar) {
            JPanel painelBotoesEquipes = new JPanel();
            btnAdicionarEquipe = new JButton("Adicionar Equipe");
            btnRemoverEquipe = new JButton("Remover Equipe");
            painelBotoesEquipes.add(btnAdicionarEquipe);
            painelBotoesEquipes.add(btnRemoverEquipe);
            painelEquipes.add(painelBotoesEquipes, BorderLayout.SOUTH);

            // --- Implementando a nova ação do botão Adicionar ---
            btnAdicionarEquipe.addActionListener(e -> {
                // Simulação da lista de todas as equipes existentes no sistema
                // Em uma aplicação real, isso viria do seu EquipeService ou DAO
                List<Equipe> todasAsEquipes = new ArrayList<>();
                todasAsEquipes.add(new Equipe(1, "Equipe Alpha", "Equipe de desenvolvimento A"));
                todasAsEquipes.add(new Equipe(2, "Equipe Beta", "Equipe de desenvolvimento B"));
                todasAsEquipes.add(new Equipe(3, "Equipe de Teste", "Equipe de QA"));

                // Cria um ComboBox com as equipes disponíveis
                JComboBox<Equipe> comboEquipes = new JComboBox<>(todasAsEquipes.toArray(new Equipe[0]));

                // Exibe a caixa de diálogo para o usuário escolher a equipe
                int resultado = JOptionPane.showConfirmDialog(this, comboEquipes,
                        "Selecione uma equipe para adicionar ao projeto:",
                        JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

                if (resultado == JOptionPane.OK_OPTION) {
                    Equipe equipeSelecionada = (Equipe) comboEquipes.getSelectedItem();
                    if (equipeSelecionada != null) {
                        // Verifica se a equipe já não está no projeto
                        if (!projeto.getEquipes().contains(equipeSelecionada)) {
                            projeto.adicionarEquipe(equipeSelecionada);
                            modeloEquipes.addElement(equipeSelecionada);
                            JOptionPane.showMessageDialog(this,
                                    "Equipe " + equipeSelecionada.getNome() + " adicionada com sucesso!");
                        } else {
                            JOptionPane.showMessageDialog(this, "Esta equipe já faz parte do projeto.", "Aviso",
                                    JOptionPane.WARNING_MESSAGE);
                        }
                    }
                }
            });

            btnRemoverEquipe.addActionListener(e -> {
                Equipe equipeSelecionada = listaEquipes.getSelectedValue();
                if (equipeSelecionada != null) {
                    projeto.getEquipes().remove(equipeSelecionada); // Remove a equipe do objeto Projeto
                    modeloEquipes.removeElement(equipeSelecionada); // Atualiza a GUI
                } else {
                    JOptionPane.showMessageDialog(this, "Por favor, selecione uma equipe para remover.");
                }
            });
        }

    add(painelEquipes, BorderLayout.WEST);

        // --- Seção 3: Tarefas Relacionadas ---
        JPanel painelTarefas = new JPanel(new BorderLayout());
        painelTarefas.setBorder(BorderFactory.createTitledBorder("Tarefas"));

        String[] tarefasExemplo = {"Tarefa 1: Desenvolver login", "Tarefa 2: Criar UI do painel", "Tarefa 3: Conectar API"};
        listaTarefas = new JList<>(tarefasExemplo);
        JScrollPane scrollTarefas = new JScrollPane(listaTarefas);
        
        painelTarefas.add(scrollTarefas, BorderLayout.CENTER);

        add(painelTarefas, BorderLayout.CENTER);
    }
}
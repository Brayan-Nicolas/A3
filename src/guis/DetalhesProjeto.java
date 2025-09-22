package guis;

import com.meuprojeto.gestao.projeto.Projeto;
import com.meuprojeto.gestao.usuario.Papel;
import com.meuprojeto.gestao.usuario.Usuario;

import javax.swing.*;
import java.awt.*;

public class DetalhesProjetoPanel extends JPanel {

    private Usuario usuarioLogado;
    private Projeto projeto;

    private JLabel lblNome, lblDescricao, lblStatus, lblGerente;
    private JList<Usuario> listaMembros;
    private JButton btnAdicionarMembro, btnRemoverMembro;
    private JList<String> listaTarefas; // Usaremos String por enquanto, mas seria uma classe 'Tarefa'

    public DetalhesProjetoPanel(Projeto projeto, Usuario usuarioLogado) {
        this.projeto = projeto;
        this.usuarioLogado = usuarioLogado;
        
        setLayout(new BorderLayout(10, 10)); // Layout principal
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

        add(painelDetalhes, BorderLayout.NORTH);

        // --- Seção 2: Membros da Equipe ---
        JPanel painelMembros = new JPanel(new BorderLayout());
        painelMembros.setBorder(BorderFactory.createTitledBorder("Membros da Equipe"));

        DefaultListModel<Usuario> modeloMembros = new DefaultListModel<>();
        for (Usuario membro : projeto.getEquipe()) {
            modeloMembros.addElement(membro);
        }
        
        listaMembros = new JList<>(modeloMembros);
        JScrollPane scrollMembros = new JScrollPane(listaMembros);
        painelMembros.add(scrollMembros, BorderLayout.CENTER);

        // Lógica de permissão: apenas gestores podem gerenciar a equipe
        if (usuarioLogado.getPapel() == Papel.GESTOR || usuarioLogado.getPapel() == Papel.ADMIN) {
            JPanel painelBotoesMembros = new JPanel();
            btnAdicionarMembro = new JButton("Adicionar Membro");
            btnRemoverMembro = new JButton("Remover Membro");
            painelBotoesMembros.add(btnAdicionarMembro);
            painelBotoesMembros.add(btnRemoverMembro);
            painelMembros.add(painelBotoesMembros, BorderLayout.SOUTH);
            
            // Adicionar as ações dos botões (por exemplo, abrir um diálogo para adicionar)
        }

        add(painelMembros, BorderLayout.WEST);

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
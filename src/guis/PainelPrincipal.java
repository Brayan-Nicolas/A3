package guis;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import main.DatabaseDAO;
import main.ProgramaGestão;
import main.Projeto.Projeto;
import usuário.Nivel;

public class PainelPrincipal extends JFrame {
	JPanel painelDetalhes;

	public PainelPrincipal() {
		// Define o Layout do frame do painel principal
		setLayout(new BorderLayout());
		// Lógica botão de deslogar
		JButton deslogar = new JButton("Deslogar");
		deslogar.addActionListener(e -> {
			ProgramaGestão.usuarioAtual = null;
			int opção = JOptionPane.showOptionDialog(this, "Deseja deslogar?", "Deslogar", JOptionPane.YES_NO_OPTION,
					JOptionPane.QUESTION_MESSAGE, null, new String[] { "Sim", "Não" }, "Não");
			if (opção == JOptionPane.YES_OPTION) {
				JOptionPane.showMessageDialog(this, "Deslogado com sucesso!", "Deslogado",
						JOptionPane.INFORMATION_MESSAGE);
				dispose();
				ProgramaGestão.currentWindow = new InterfaceLogin();
			}

		});

		// Painel para adicionar os botões
		JPanel botões = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		botões.setBorder(BorderFactory.createMatteBorder(0, 0, 3, 0, Color.BLACK));
		botões.add(deslogar);

		// Painel de abas para as abas de equipe e projetos
		JTabbedPane painelAbas = new JTabbedPane();

		// Painel de equipes
		JPanel equipesPainel = new EquipesPanel(ProgramaGestão.usuarioAtual);

		// Painel de projetos
		JPanel projetosPainel = new JPanel(new BorderLayout());

		// Menu de seleção de projetos
		List<Projeto> projetosLista = new ArrayList<Projeto>();
		// Se o usuário é Administrador, ele pode ver todos os projetos
		if (ProgramaGestão.usuarioAtual.getNivel() == Nivel.ADMINISTRADOR) {
			// Retorna os projetos e verifica se existe pelo menos um
			List<Projeto> projetosDB = DatabaseDAO.getProjetos();
			if (!(projetosDB == null))
				projetosLista = projetosDB;
		} else {
			// Retorna os projetos e verifica se existe pelo menos um
			List<Projeto> projetosDB = DatabaseDAO.getProjetosUsuario(ProgramaGestão.usuarioAtual.getId());
			if (!(projetosDB == null))
				projetosLista = projetosDB;
		}
		// Cria a seleção, com N/A caso não tenha nenhum projeto
		JComboBox<String> projetosCombo;
		if (projetosLista == null) {
			projetosCombo = new JComboBox<String>();
		} else {
			projetosCombo = new JComboBox<String>(projetosLista.stream().map(Projeto::getNome).toArray(String[]::new));
		}
		projetosPainel.add(projetosCombo, BorderLayout.NORTH);

		painelDetalhes = new JPanel();
		// Define o Projeto padrão como o primeiro da lista
		if (projetosCombo.getSelectedIndex() >= 0) {
			painelDetalhes = new DetalhesProjetoPanel(projetosLista.get(projetosCombo.getSelectedIndex()),
					ProgramaGestão.usuarioAtual);
			projetosPainel.add(painelDetalhes, BorderLayout.CENTER);
		}

		// Lógica para atualizar o painel a partir da seleção no menu
		List<Projeto> projetosListaFinal = projetosLista;
		projetosCombo.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.DESELECTED) {
					projetosPainel.remove(painelDetalhes);
					painelDetalhes = null;
				}

				if (e.getStateChange() == ItemEvent.SELECTED) {
					painelDetalhes = new DetalhesProjetoPanel(projetosListaFinal.get(projetosCombo.getSelectedIndex()),
							ProgramaGestão.usuarioAtual);
					projetosPainel.add(painelDetalhes, BorderLayout.CENTER);
				}
				projetosPainel.revalidate();
			}
		});

		painelAbas.addTab("Equipes", equipesPainel);
		painelAbas.addTab("Projetos", projetosPainel);
		add(botões, BorderLayout.NORTH);
		add(painelAbas, BorderLayout.CENTER);

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("Painel Principal");
		setSize(500, 400);
		setLocationRelativeTo(null); // Centralizar
		setVisible(true);
	}

}

package guis;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
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
	//Projeto ultimoProjeto;
	ComboBoxModel<String> comboModel;
	Projeto projetoSelecionado;
	
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
		JComboBox<String> projetosCombo;
		List<Projeto> projetosLista = getProjetos();
		
		comboModel = new DefaultComboBoxModel<String>(projetosLista.stream().map(Projeto::getNome).toArray(String[]::new));
		projetosCombo = new JComboBox<String>(comboModel);
		
		projetosPainel.add(projetosCombo, BorderLayout.NORTH);

		painelDetalhes = new JPanel();
		

		// Botões para criar e excluir projetos
		JPanel painelBotões = new JPanel(new GridBagLayout());
        if (ProgramaGestão.usuarioAtual.getNivel() == Nivel.GERENTE || ProgramaGestão.usuarioAtual.getNivel() == Nivel.ADMINISTRADOR) {
        	GridBagConstraints gbc = new GridBagConstraints();
        	gbc.fill = GridBagConstraints.BOTH;
        	gbc.weightx = 1;
        	gbc.weighty = 1;
        	gbc.insets = new Insets(2, 2, 2, 2);
        	painelBotões.setBorder(BorderFactory.createTitledBorder("Editar Projetos"));
        	
        	JButton btnCriarProjeto = new JButton("Criar novo Projeto");
        	JButton btnExcluirProjeto = new JButton("Excluir Projeto");
	        gbc.gridx = 0;
	        gbc.gridy = 0;
	        painelBotões.add(btnCriarProjeto, gbc);
	        gbc.gridy = 1;
	        painelBotões.add(btnExcluirProjeto, gbc);

	        btnCriarProjeto.addActionListener(e -> {
	        	new GerenciarProjetosPanel.painelCriar(this);
	        	comboModel = new DefaultComboBoxModel<String>(getProjetos().stream().map(Projeto::getNome).toArray(String[]::new));
	        });
	        
	        btnExcluirProjeto.addActionListener(e -> {
	        	if (GerenciarProjetosPanel.painelExcluir(this) == JOptionPane.YES_OPTION) {
	        		comboModel = new DefaultComboBoxModel<String>(getProjetos().stream().map(Projeto::getNome).toArray(String[]::new));
		        	projetosCombo.setSelectedIndex(0);
	        	}
	        });
        }
	        
		
		// Define o Projeto padrão como o primeiro da lista
		if (projetosCombo.getSelectedIndex() >= 0) {
			painelDetalhes = new DetalhesProjetoPanel(projetosLista.get(projetosCombo.getSelectedIndex()),
					ProgramaGestão.usuarioAtual);
			projetoSelecionado = projetosLista.get(projetosCombo.getSelectedIndex());
			painelDetalhes.add(painelBotões, BorderLayout.EAST);
			projetosPainel.add(painelDetalhes, BorderLayout.CENTER);
		}

		// Lógica para atualizar o painel a partir da seleção no menu
		projetosCombo.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.DESELECTED) {
					projetosPainel.remove(painelDetalhes);
					painelDetalhes = null;
				}

				if (e.getStateChange() == ItemEvent.SELECTED) {
					painelDetalhes = new DetalhesProjetoPanel(projetosLista.get(projetosCombo.getSelectedIndex()),
							ProgramaGestão.usuarioAtual);
					projetoSelecionado = projetosLista.get(projetosCombo.getSelectedIndex());
					painelDetalhes.add(painelBotões, BorderLayout.EAST);
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

	private List<Projeto> getProjetos() {
		// Se o usuário é Administrador, ele pode ver todos os projetos
		if (ProgramaGestão.usuarioAtual.getNivel() == Nivel.ADMINISTRADOR) {
			// Retorna os projetos e verifica se existe pelo menos um
			List<Projeto> projetosDB = DatabaseDAO.getProjetos();
			if (!(projetosDB == null)) return projetosDB;
			
		} else {
			
			// Retorna os projetos e verifica se existe pelo menos um
			List<Projeto> projetosDB = DatabaseDAO.getProjetosUsuario(ProgramaGestão.usuarioAtual.getId());
			if (!(projetosDB == null)) return projetosDB;
		}
		return new ArrayList<Projeto>();		
	}
}

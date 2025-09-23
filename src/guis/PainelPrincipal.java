package guis;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import main.Equipe;
import main.ProgramaGestão;
import main.Projeto.Projeto;
import usuário.Nivel;
import usuário.Usuario;

public class PainelPrincipal extends JFrame {
	JPanel painelDetalhes;
	
	
	public PainelPrincipal() {
		// Define o Layout do frame do painel principal
		setLayout(new BorderLayout());
		// Lógica botão de deslogar
		JButton deslogar = new JButton("Deslogar");
		deslogar.addActionListener(e -> {
			ProgramaGestão.usuarioAtual = null;
			int opção = JOptionPane.showOptionDialog(this, "Deseja deslogar?", "Deslogar", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, new String[] {"Sim", "Não"}, "Não");
			if (opção == JOptionPane.YES_OPTION) {
				JOptionPane.showMessageDialog(this, "Deslogado com sucesso!", "Deslogado", JOptionPane.INFORMATION_MESSAGE);
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
		JPanel equipes = new EquipesPanel(ProgramaGestão.usuarioAtual);
		
		// Painel de projetos
		JPanel projetos = new JPanel(new BorderLayout());

		// Menu de seleção de projetos
		Projeto[] projetosLista = getProjetos();
		JComboBox<String> projetosCombo = new JComboBox<String>(Arrays.stream(projetosLista).map(Projeto::getNome).toArray(String[]::new));
		projetos.add(projetosCombo, BorderLayout.NORTH);
		
		painelDetalhes = new JPanel();
		// Define o Projeto padrão como o primeiro da lista
		if (projetosCombo.getSelectedIndex() >= 0) {
			painelDetalhes = new DetalhesProjetoPanel(projetosLista[projetosCombo.getSelectedIndex()], ProgramaGestão.usuarioAtual);
			projetos.add(painelDetalhes, BorderLayout.CENTER);
		}
		
		// Lógica para atualizar o painel a partir da seleção no menu
		projetosCombo.addItemListener( new ItemListener(){
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.DESELECTED) {
					projetos.remove(painelDetalhes);
					painelDetalhes = null;
				}
				
				if (e.getStateChange() == ItemEvent.SELECTED) {
					painelDetalhes = new DetalhesProjetoPanel(projetosLista[projetosCombo.getSelectedIndex()], ProgramaGestão.usuarioAtual);
					projetos.add(painelDetalhes, BorderLayout.CENTER);
				}
				projetos.revalidate();
			}
		});
		
		
		painelAbas.addTab("Equipes", equipes);
		painelAbas.addTab("Projetos", projetos);
		add(botões, BorderLayout.NORTH);
		add(painelAbas, BorderLayout.CENTER);
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("Painel Principal");
        setSize(500, 400);
        setLocationRelativeTo(null); // Centralizar
        setVisible(true);
	}
	// Método temporário, deve ser substituído por uma integração com o banco de dados
	private Projeto[] getProjetos() {
		Usuario gerente = new Usuario(35, "aaa", "123", "email@email.com", "Gerente", Nivel.GERENTE);
		List<Equipe> equipes = Arrays.asList(new Equipe(35, "equipe boa"), new Equipe(36, "equipe ruim"));
		Projeto projeto1 = new Projeto("A1", "Fazer a A1", LocalDate.now(), LocalDate.now(), gerente, equipes);
		Projeto projeto2 = new Projeto("A2", "Fazer a A2", LocalDate.now(), LocalDate.now(), gerente, equipes);
		Projeto projeto3 = new Projeto("A3", "Fazer a A3", LocalDate.now(), LocalDate.now(), gerente, equipes);
		return new Projeto[] {projeto1, projeto2, projeto3};
	}

}

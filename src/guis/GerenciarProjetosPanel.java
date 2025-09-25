package guis;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import main.DatabaseDAO;
import main.Projeto.Projeto;
import usuário.Nivel;
import usuário.Usuario;

public class GerenciarProjetosPanel {
	
	public static class painelCriar extends JDialog {
		
		public painelCriar(JFrame parent) {
			super(parent, "Criar novo Projeto", true);
			setSize(400, 280);
			setLayout(new GridBagLayout());
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.insets = new Insets(5, 5, 5, 5);
			gbc.weightx = 1;
			gbc.gridheight = 1;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			
			List<Usuario> gerentes = DatabaseDAO.getTodosUsuarios().stream().filter(u -> u.getNivel() == Nivel.GERENTE).toList();
			
			JLabel nomeLabel = new JLabel("Nome do Projeto: ");
			JLabel descricaoLabel = new JLabel("Descrição do Projeto: ");
			JLabel dataInicioLabel = new JLabel("Data de início: ");
			JLabel dataTerminoLabel = new JLabel("Data de término prevista: ");
			JLabel gerenteLabel = new JLabel("Gerente: ");
			// List<JLabel> labels = List.of(new JLabel[] {nomeLabel, descricaoLabel, dataInicioLabel, dataTerminoLabel, gerenteLabel});
			
			JTextField nome = new JTextField();
			JTextField descricao = new JTextField();
			JTextField dataInicio = new JTextField();
			dataInicio.setToolTipText("Formato 'DD MM AAAA'");
			JTextField dataTermino = new JTextField();
			dataTermino.setToolTipText("Formato 'DD MM AAAA'");
			JComboBox<String> gerente = new JComboBox<String>(gerentes.stream().map(Usuario::getNome).toArray(String[]::new));
			List<JTextField> fields = List.of(new JTextField[] {nome, descricao, dataInicio, dataTermino});
			
			JButton salvar = new JButton("Salvar");
			JButton cancelar= new JButton("Cancelar");
			
			gbc.gridx = 0;
			gbc.gridy = 0;
			gbc.gridwidth = 1;
			add(nomeLabel, gbc);
			gbc.gridy = 1;
			add(descricaoLabel, gbc);
			gbc.gridy = 2;
			add(dataInicioLabel, gbc);
			gbc.gridy = 3;
			add(dataTerminoLabel, gbc);
			gbc.gridy = 4;
			add(gerenteLabel, gbc);
			
			gbc.gridx = 1;
			gbc.gridy = 0;
			gbc.gridwidth = 2;
			add(nome, gbc);
			gbc.gridy = 1;
			add(descricao, gbc);
			gbc.gridy = 2;
			add(dataInicio, gbc);
			gbc.gridy = 3;
			add(dataTermino, gbc);
			gbc.gridy = 4;
			add(gerente, gbc);
			
			gbc.gridx = 0;
			gbc.gridy = 6;
			gbc.gridwidth = 1;
			add(salvar, gbc);
			
			gbc.gridx = 2;
			add(cancelar, gbc);
			
			salvar.addActionListener(m -> {
				LocalDate valorDataInicio = null;
				LocalDate valorDataTermino = null;
				if (fields.stream().map(JTextField::getText).anyMatch(s -> s.equals(""))) {
					JOptionPane.showMessageDialog(this, "Favor preencher todos os campos!");
				} else {
					try {
						valorDataInicio = LocalDate.parse(dataInicio.getText(), DateTimeFormatter.ofPattern("dd mm aaaa"));
					} catch (DateTimeParseException e) {
						JOptionPane.showMessageDialog(this, "Favor inserir uma data de início válida no formato DD MM AAAA!");
					}
					try {
						valorDataTermino = LocalDate.parse(dataTermino.getText());
					} catch (DateTimeParseException e) {
						JOptionPane.showMessageDialog(this, "Favor inserir uma data de término prevista válida no formato DD MM AAAA!");
					}
				}
				if ((valorDataInicio != null) && (valorDataTermino != null)) {
					if (DatabaseDAO.criarProjeto(new Projeto(nome.getText(), descricao.getText(), valorDataInicio
							, valorDataTermino, gerentes.get(gerente.getSelectedIndex()).getId()))) { 
						
						JOptionPane.showMessageDialog(this, "Projeto criado com sucesso!"); 
						
					}
					this.dispose();
				}
			});
			
			cancelar.addActionListener(e -> {
				this.dispose();
			});
			

			setVisible(true);
		}
		
		
	}
	
	public static int painelExcluir(PainelPrincipal parent) {
		int resposta = JOptionPane.showConfirmDialog(parent, "Deseja excluir o Projeto selecionado?", "Excluir Projeto", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
		
		if (resposta == JOptionPane.YES_OPTION) {
			DatabaseDAO.excluirProjeto(parent.projetoSelecionado.getId());
			JOptionPane.showMessageDialog(parent, "Projeto excluído com sucesso!");
		}
		return resposta;
	}
}

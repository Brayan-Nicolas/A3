package guis;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

public class PainelPrincipal extends JFrame {
	
	public PainelPrincipal() {
		JTabbedPane panel = new JTabbedPane();
		
		JComponent equipes = makeTextPanel("Equipes");
		JComponent projetos = makeTextPanel("Projetos");
		
		panel.addTab("equipes", equipes);
		panel.addTab("projetos", projetos);
		
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 400);
        setLocationRelativeTo(null); // Centralizar
        setVisible(true);
	}
	
	private JComponent makeTextPanel(String text) {
		JPanel panel = new JPanel(false);
		JLabel texto = new JLabel(text);
		texto.setHorizontalAlignment(JLabel.CENTER);
		panel.add(texto);
		return panel;
	}
}

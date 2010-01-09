package practica.agentes.ventalibros.gui;

import java.awt.Dimension;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import practica.agentes.ventalibros.entidades.LibroSubasta;

@SuppressWarnings("serial")
public class VendedorEntrada extends JPanel {

	VendedorGUI gui;
	private JLabel jLabel = null;
	private JTextField jTextPrecioMínimo = null;
	private JLabel jLabel1 = null;
	private JTextField jTextTitulo = null;
	private JLabel jLabel2 = null;
	private JTextField jTextPrecioInicial = null;
	private JButton jButtonAñadir = null;

	/**
	 * This method initializes 
	 * 
	 */
	public VendedorEntrada() {
		super();
		initialize();
	}

	protected boolean comprobarentrada() {
		boolean error = 
		  jTextTitulo.getText()==null || "".equals(jTextTitulo.getText()) ||
		  jTextPrecioInicial.getText()==null || "".equals(jTextPrecioInicial.getText()) ||
		  jTextPrecioMínimo.getText()==null || "".equals(jTextPrecioMínimo.getText());
		
		if(!error) {
		for(int i = 0; i < gui.vendedorInfo.defaultTableModel.getRowCount(); i++ )
		{
			if(jTextTitulo.getText().equals(gui.vendedorInfo.defaultTableModel.getValueAt(i,0))) {
				error =true;
				break;
			}
		}
		}
		
		return !error;
	}

	/**
	 * This method initializes jButtonAñadir	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButtonAñadir() {
		if (jButtonAñadir == null) {
			jButtonAñadir = new JButton();
			jButtonAñadir.setText("Añadir a la Subasta");
			jButtonAñadir.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					if(comprobarentrada()) {
					LibroSubasta l = new LibroSubasta();
					l.setTitulo(jTextTitulo.getText());
					l.setPrecioInicial(Float.parseFloat(jTextPrecioInicial.getText()));
					l.setPrecioMinimo(Float.parseFloat(jTextPrecioMínimo.getText()));
					gui.AñadirLibro(l);
					System.out.println("Libro añadido");
					}
					else
						JOptionPane.showMessageDialog(jButtonAñadir,"Compruebe los datos a añadir");
				}
			});
		}
		return jButtonAñadir;
	}

	/**
	 * This method initializes jTextPrecioInicial	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextPrecioInicial() {
		if (jTextPrecioInicial == null) {
			jTextPrecioInicial = new JTextField();
		}
		return jTextPrecioInicial;
	}

	/**
	 * This method initializes jTextPrecioMínimo	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextPrecioMínimo() {
		if (jTextPrecioMínimo == null) {
			jTextPrecioMínimo = new JTextField();
		}
		return jTextPrecioMínimo;
	}

	/**
	 * This method initializes jTextTitulo	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextTitulo() {
		if (jTextTitulo == null) {
			jTextTitulo = new JTextField();
		}
		return jTextTitulo;
	}

	/**
	 * This method initializes this
	 * 
	 */
	private void initialize() {
        jLabel2 = new JLabel();
        jLabel2.setText("Precio Mínimo de Venta:");
        jLabel1 = new JLabel();
        jLabel1.setText("Precio Inicial Subasta:");
        jLabel = new JLabel();
        jLabel.setText("Libro:");
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setSize(new Dimension(551, 190));
        this.add(jLabel, null);
        this.add(getJTextTitulo(), null);
        this.add(jLabel1, null);
        this.add(getJTextPrecioInicial(), null);
        this.add(jLabel2, null);
        this.add(getJTextPrecioMínimo(), null);
        this.add(getJButtonAñadir(), null);
			
	}

}  //  @jve:decl-index=0:visual-constraint="10,10"

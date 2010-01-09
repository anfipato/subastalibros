package practica.agentes.ventalibros.gui;

import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.event.KeyEvent;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

import practica.agentes.ventalibros.entidades.LibroSubasta;

@SuppressWarnings("serial")
public class VendedorInfo extends JPanel {

	VendedorGUI gui;
	DefaultTableModel defaultTableModel;
	private JScrollPane jScrollPane = null;
	private JTable jTableSubasta = null;
	JButton jButtonIniciar = null;
	private JPanel jPanel = null;
	JButton jButtonTerminar = null;

	/**
	 * This method initializes 
	 * 
	 */
	public VendedorInfo() {
		super();
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 */
	private void initialize() {
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setSize(new Dimension(611, 268));
        this.add(getJPanel(), null);
        this.add(getJScrollPane(), null);
			
	}

	/**
	 * This method initializes jScrollPane	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getJScrollPane() {
		if (jScrollPane == null) {
			jScrollPane = new JScrollPane();
			jScrollPane.setViewportView(getJTableSubasta());
		}
		return jScrollPane;
	}

	/**
	 * This method initializes jTableSubasta	
	 * 	
	 * @return javax.swing.JTable	
	 */
	private JTable getJTableSubasta() {
		if (jTableSubasta == null) {
			defaultTableModel = new DefaultTableModel();
			defaultTableModel.setColumnCount(0);
			defaultTableModel.setNumRows(0);
			defaultTableModel.setRowCount(0);
			jTableSubasta = new JTable();
			jTableSubasta.setComponentOrientation(ComponentOrientation.UNKNOWN);
			jTableSubasta.setShowGrid(true);
			jTableSubasta.setShowVerticalLines(true);
			jTableSubasta.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			jTableSubasta.setRowSelectionAllowed(true);
			jTableSubasta.setVisible(true);
			jTableSubasta.setModel(defaultTableModel);
			
			defaultTableModel.addColumn("Libro");
			defaultTableModel.addColumn("Precio Mínimo");
			defaultTableModel.addColumn("Precio Inicial");
			defaultTableModel.addColumn("Precio Actual");
			defaultTableModel.addColumn("Comprador");
			
		}
		return jTableSubasta;
	}

	/**
	 * This method initializes jButtonIniciar	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButtonIniciar() {
		if (jButtonIniciar == null) {
			jButtonIniciar = new JButton();
			jButtonIniciar.setMnemonic(KeyEvent.VK_UNDEFINED);
			jButtonIniciar.setText("Iniciar Subasta");
			jButtonIniciar.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					jButtonIniciar.setEnabled(false);
					jButtonTerminar.setEnabled(true);
					gui.IniciarSubasta();
				}
			});
		}
		return jButtonIniciar;
	}

	public void AñadirLibro(LibroSubasta l) {
		defaultTableModel.addRow(new Object[] {l.getTitulo(), l.getPrecioMinimo(), l.getPrecioInicial(), l.getPrecioPuja(), ""});
	}

	public void ActualizarLibro(LibroSubasta l) {
		for(int i=0; i<defaultTableModel.getRowCount();i++) {
			if(l.getTitulo().equals(defaultTableModel.getValueAt(i,0).toString()))
			{
				defaultTableModel.setValueAt(l.getPrecioPuja(), i, 3);
				defaultTableModel.setValueAt(l.getComprador(), i, 4);
				break;
			}
		}
		
	}

	/**
	 * This method initializes jPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanel() {
		if (jPanel == null) {
			jPanel = new JPanel();
			jPanel.setLayout(new BoxLayout(getJPanel(), BoxLayout.X_AXIS));
			jPanel.add(getJButtonIniciar(), null);
			jPanel.add(getJButtonTerminar(), null);
		}
		return jPanel;
	}

	/**
	 * This method initializes jButtonTerminar	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButtonTerminar() {
		if (jButtonTerminar == null) {
			jButtonTerminar = new JButton();
			jButtonTerminar.setText("Terminar Subasta");
			jButtonTerminar.setEnabled(false);
			jButtonTerminar.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					jButtonIniciar.setEnabled(true);
					jButtonTerminar.setEnabled(false);
					gui.TerminarSubasta();
				}
			});
		}
		return jButtonTerminar;
	}
	
}  //  @jve:decl-index=0:visual-constraint="10,10"

package practica.agentes.ventalibros.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.util.Hashtable;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

import practica.agentes.ventalibros.Comprador;
import practica.agentes.ventalibros.entidades.LibroSubasta;

@SuppressWarnings("serial")
public class CompradorGUI extends JFrame {

	private DefaultTableModel defaultTableModel = null;
	private JButton jButtonPujar = null;
	private JScrollPane jScrollPane = null;
	private JPanel jPanel = null;
	private JScrollPane jScrollPane1 = null;
	private JList jListComprados = null;
	private JPanel jPanel1 = null;
	private JTextField jTextPrecioMáximo = null;
	private JLabel jLabel = null;
	private JLabel jLabel1 = null;
	private JLabel jLabel2 = null;
	private JTable jTableSubasta = null;
	private Comprador agentecomprador;
	
	private DefaultListModel defaultListModelComprado = null;  //  @jve:decl-index=0:visual-constraint="387,100"

	private DefaultListModel defaultListModelLog = null;  //  @jve:decl-index=0:visual-constraint="575,52"
	private JScrollPane jScrollPane2 = null;
	private JList jListLog = null;
	/**
	 * This method initializes 
	 * 
	 */
	public CompradorGUI() {
		super();
		initialize();
	}
	public void ActualizarLibrosSubasta(Hashtable<String, LibroSubasta> listado) {
		int i = 0;
		boolean nuevo;
		for(LibroSubasta l : listado.values()) {
			nuevo=true;
			
			//Comprobación si ya fue comprado
			for(int j = 0; j< defaultListModelComprado.getSize(); j++)
			{
				if(l.getTitulo().equals(defaultListModelComprado.elementAt(j))) {
					nuevo=false;
					break;
				}
			}

			//Comprobación si del nuevo estado de las pujas
			if(nuevo) {
				for(i = 0; i < defaultTableModel.getRowCount(); i++) {
					if(l.getTitulo().equals(defaultTableModel.getValueAt(i,0)))
					{
						nuevo=false;
						if(!l.Anulado() && !l.Comprado()) {
							//Se actualiza el precio para pujar
							defaultTableModel.setValueAt(l.getPrecioPuja(), i,1);
						} else if (l.Comprado() && l.getComprador().equals(agentecomprador.getName())) {
							//Se mueve el libro a la lista de comprados
							defaultTableModel.removeRow(i);
							defaultListModelComprado.addElement(l.getTitulo());
							Log(l.getTitulo() + " comprado");
						} else {
							//Si el libro se anuló o lo compró otro agente de la subasta se elimina
							defaultTableModel.removeRow(i);
							//Se actualiza la puja a 0
							agentecomprador.actualizarPuja(l.getTitulo(), 0.0f);
							if(l.Anulado())
								Log(l.getTitulo() + " anulada su subasta");
							else if(l.Comprado())
								Log(l.getTitulo() + " comprado por " + l.getComprador());

						}
						break;
					}
				}
			}
			
			if(nuevo && !l.Anulado() && !l.Comprado()) {
				//Nuevo libro en subasta
				defaultTableModel.addRow(new Object[] {l.getTitulo(),l.getPrecioPuja(),""});
			}
		}
	}

	/**
	 * This method initializes jButtonPujar	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButtonPujar() {
		if (jButtonPujar == null) {
			jButtonPujar = new JButton();
			jButtonPujar.setText("Pujar");
			jButtonPujar.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					int fila = jTableSubasta.getSelectedRow();
					
					if(fila!=-1 && Float.parseFloat(jTextPrecioMáximo.getText())>0 ) {
						defaultTableModel.setValueAt(jTextPrecioMáximo.getText(), fila,2);
						agentecomprador.actualizarPuja((String)defaultTableModel.getValueAt(fila,0),Float.parseFloat(jTextPrecioMáximo.getText()));
						
					}
					else
						JOptionPane.showMessageDialog(jButtonPujar, "Seleccione un libro de la subasta y establezca un precio máximo");
				}
			});
		}
		return jButtonPujar;
	}

	/**
	 * This method initializes jLabel	
	 * 	
	 * @return javax.swing.JLabel	
	 */
	private JLabel getJLabel() {
		if (jLabel == null) {
			jLabel = new JLabel();
			jLabel.setText("Precio Máximo de Puja:");
		}
		return jLabel;
	}

	/**
	 * This method initializes jLabel1	
	 * 	
	 * @return javax.swing.JLabel	
	 */
	private JLabel getJLabel1() {
		if (jLabel1 == null) {
			jLabel1 = new JLabel();
			jLabel1.setText("Libros en Subasta");
		}
		return jLabel1;
	}

	/**
	 * This method initializes jLabel2	
	 * 	
	 * @return javax.swing.JLabel	
	 */
	private JLabel getJLabel2() {
		if (jLabel2 == null) {
			jLabel2 = new JLabel();
			jLabel2.setText("Libros comprados");
		}
		return jLabel2;
	}

	/**
	 * This method initializes jListComprados	
	 * 	
	 * @return javax.swing.JList	
	 */
	private JList getJListComprados() {
		if (jListComprados == null) {
			defaultListModelComprado = new DefaultListModel();
			jListComprados = new JList();
			jListComprados.setModel(defaultListModelComprado);
			
		}
		return jListComprados;
	}

	/**
	 * This method initializes jListLog	
	 * 	
	 * @return javax.swing.JList	
	 */
	private JList getJListLog() {
		if (jListLog == null) {
			jListLog = new JList();
			jListLog.setBackground(Color.lightGray);
			jListLog.setVisibleRowCount(30);
			defaultListModelLog = new DefaultListModel();
			jListLog.setModel(defaultListModelLog);
			jListLog.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		}
		return jListLog;
	}

	/**
	 * This method initializes jPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanel() {
		if (jPanel == null) {
			jPanel = new JPanel();
			jPanel.setLayout(new BoxLayout(getJPanel(), BoxLayout.Y_AXIS));
			jPanel.add(getJLabel1(), null);
			jPanel.add(getJScrollPane(), null);
			jPanel.add(getJPanel1(), null);
			jPanel.add(getJLabel2(), null);
			jPanel.add(getJScrollPane1(), null);
			jPanel.add(getJScrollPane2(), null);
		}
		return jPanel;
	}

	/**
	 * This method initializes jPanel1	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanel1() {
		if (jPanel1 == null) {
			jPanel1 = new JPanel();
			jPanel1.setLayout(new BoxLayout(getJPanel1(), BoxLayout.X_AXIS));
			jPanel1.add(getJLabel(), null);
			jPanel1.add(getJTextPrecioMáximo(), null);
			jPanel1.add(getJButtonPujar(), null);
		}
		return jPanel1;
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
	 * This method initializes jScrollPane1	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getJScrollPane1() {
		if (jScrollPane1 == null) {
			jScrollPane1 = new JScrollPane();
			jScrollPane1.setViewportView(getJListComprados());
		}
		return jScrollPane1;
	}

	/**
	 * This method initializes jScrollPane2	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getJScrollPane2() {
		if (jScrollPane2 == null) {
			jScrollPane2 = new JScrollPane();
			jScrollPane2.setViewportView(getJListLog());
		}
		return jScrollPane2;
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
			jTableSubasta = new JTable();
			jTableSubasta.setShowGrid(true);
			jTableSubasta.setShowVerticalLines(true);
			jTableSubasta.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			jTableSubasta.setRowSelectionAllowed(true);
			jTableSubasta.setVisible(true);
			jTableSubasta.setColumnSelectionAllowed(false);
			jTableSubasta.setModel(defaultTableModel);
			defaultTableModel.addColumn("Libro");
			defaultTableModel.addColumn("Precio Actual");
			defaultTableModel.addColumn("Puja");
		}
		return jTableSubasta;
	}
	
	/**
	 * This method initializes jTextPrecioMáximo	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextPrecioMáximo() {
		if (jTextPrecioMáximo == null) {
			jTextPrecioMáximo = new JTextField();
		}
		return jTextPrecioMáximo;
	}

	/**
	 * This method initializes this
	 * 
	 */
	private void initialize() {
        this.setSize(new Dimension(500, 385));
        this.setPreferredSize(new Dimension(500, 385));
        this.setContentPane(getJPanel());
        this.addWindowListener(new java.awt.event.WindowAdapter() {
        	@Override
			public void windowClosing(java.awt.event.WindowEvent e) {
        		agentecomprador.doDelete();
        	}
        });
			
	}

	public void Log(String texto) {
		defaultListModelLog.insertElementAt(texto,0);
		
	}

	public void setAgentecomprador(Comprador agentecomprador) {
		this.agentecomprador = agentecomprador;
		this.setTitle("Agente de Compra [" + agentecomprador.getName() + "]");
	}

}  //  @jve:decl-index=0:visual-constraint="10,10"

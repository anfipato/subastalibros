package practica.agentes.ventalibros.gui;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import practica.agentes.ventalibros.Vendedor;
import practica.agentes.ventalibros.entidades.LibroSubasta;

@SuppressWarnings("serial")
public class VendedorGUI extends JFrame {

	private Vendedor agentevendedor;  //  @jve:decl-index=0:
	
	private DefaultListModel defaultListModel;

	private JPanel jPanel = null;
	VendedorInfo vendedorInfo = null;
	private VendedorEntrada vendedorEntrada = null;
	private JScrollPane jScrollPane = null;

	private JList jList = null;

	/**
	 * This method initializes 
	 * 
	 */
	public VendedorGUI() {
		super();
		initialize();
	}

	
	public void ActualizarLibro(LibroSubasta l) {
		vendedorInfo.ActualizarLibro(l);
	}

	public void AñadirLibro(LibroSubasta l) {
		vendedorInfo.AñadirLibro(l);
		agentevendedor.actualizarListado(l.getTitulo(), l);
	}

	/**
	 * This method initializes jList	
	 * 	
	 * @return javax.swing.JList	
	 */
	private JList getJList() {
		if (jList == null) {
			jList = new JList();
			jList.setBackground(Color.gray);
			jList.setVisibleRowCount(25);
			defaultListModel = new DefaultListModel();
			jList.setModel(defaultListModel);
		}
		return jList;
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
			jPanel.add(getVendedorInfo(), null);
		}
		return jPanel;
	}

	/**
	 * This method initializes jScrollPane	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getJScrollPane() {
		if (jScrollPane == null) {
			jScrollPane = new JScrollPane();
			jScrollPane.setViewportView(getJList());
		}
		return jScrollPane;
	}
	
	/**
	 * This method initializes vendedorEntrada	
	 * 	
	 * @return practica.agentes.ventalibros.gui.VendedorEntrada	
	 */
	private VendedorEntrada getVendedorEntrada() {
		if (vendedorEntrada == null) {
			vendedorEntrada = new VendedorEntrada();
			vendedorEntrada.gui = this;
		}
		return vendedorEntrada;
	}
	
	/**
	 * This method initializes vendedorInfo	
	 * 	
	 * @return practica.agentes.ventalibros.gui.VendedorInfo	
	 */
	private VendedorInfo getVendedorInfo() {
		if (vendedorInfo == null) {
			vendedorInfo = new VendedorInfo();
			vendedorInfo.gui = this;
			vendedorInfo.add(getVendedorEntrada(), null);
			vendedorInfo.add(getJScrollPane(), null);
		}
		return vendedorInfo;
	}
	
	public void IniciarSubasta() {
		agentevendedor.addBehaviour( agentevendedor.new IniciarSubasta());
		Log("Subasta Iniciada");
	}
	
	/**
	 * This method initializes this
	 * 
	 */
	private void initialize() {
        this.setSize(new Dimension(737, 504));
        this.setPreferredSize(new Dimension(737, 504));
        this.setContentPane(getJPanel());
        this.addWindowListener(new java.awt.event.WindowAdapter() {
        	@Override
			public void windowClosing(java.awt.event.WindowEvent e) {
        		agentevendedor.addBehaviour(agentevendedor.new TerminarSubasta(true));
        	}
        });
			
	}

	public void Log(String texto) {
		defaultListModel.insertElementAt(texto,0);
	}

	public void setAgentevendedor(Vendedor agentevendedor) {
		this.agentevendedor = agentevendedor;
		this.setTitle("Agente Vendedor [" + agentevendedor.getName() + "]");
	}

	public void TerminarSubasta() {
		agentevendedor.addBehaviour( agentevendedor.new TerminarSubasta());
		vendedorInfo.jButtonTerminar.setEnabled(false);
		vendedorInfo.jButtonIniciar.setEnabled(true);
		Log("Subasta Terminada");
	}

}  //  @jve:decl-index=0:visual-constraint="10,10"

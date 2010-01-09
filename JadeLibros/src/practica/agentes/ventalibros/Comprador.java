package practica.agentes.ventalibros;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPANames;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Hashtable;

import javax.swing.JOptionPane;

import practica.agentes.ventalibros.entidades.LibroSubasta;
import practica.agentes.ventalibros.gui.CompradorGUI;

@SuppressWarnings("serial")
public class Comprador extends Agent {

	// Se envia la aceptación de la compra al vendedor de la subasta
	private class Comprar extends OneShotBehaviour {

		private String titulo;

		public Comprar(String titulo) {
			this.titulo = titulo;
		}

		@Override
		public void action() {
			ACLMessage msgcomprar = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
			msgcomprar.setProtocol(FIPANames.InteractionProtocol.FIPA_DUTCH_AUCTION);
			msgcomprar.setContent(titulo);
			msgcomprar.addReceiver(vendedor);
			myAgent.send(msgcomprar);
		}

	}
	
	private class InformacionSubasta extends CyclicBehaviour {

		@Override
		public void action() {
			//Recogida de mensajes de información de inicio y fin de subasta
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
			mt = MessageTemplate.and(mt,MessageTemplate.MatchProtocol(FIPANames.InteractionProtocol.FIPA_DUTCH_AUCTION));
			mt = MessageTemplate.and(mt, MessageTemplate.MatchSender(vendedor));
			ACLMessage msg = myAgent.receive(mt);
			if (msg != null) {
				String contenido = msg.getContent();
				if (contenido.equals("inicio-subasta")) {
					guiPujas.Log("Inicio de la subasta");
				} else if (contenido.equals("fin-subasta")) {
					String titulo = msg.getUserDefinedParameter("Libro");
					if(titulo!=null && !"".equals(titulo))
						guiPujas.Log("Fin de la subasta para el libro " + titulo);
					else {
						guiPujas.Log("Fin de la subasta");
						this.done();
					}
				}
			} else
				block();

			//Recogida de mensajes de cancelación relativos a una puja desde este agente
			mt = MessageTemplate.MatchPerformative(ACLMessage.REFUSE);
			mt = MessageTemplate.and(mt,MessageTemplate.MatchProtocol(FIPANames.InteractionProtocol.FIPA_DUTCH_AUCTION));
			mt = MessageTemplate.and(mt, MessageTemplate.MatchSender(vendedor));
			mt = MessageTemplate.and(mt, MessageTemplate.MatchReceiver(new AID[] {myAgent.getAID()}));
			msg = myAgent.receive(mt);
			if (msg != null) {
				String contenido = msg.getReplyWith();
				String titulo = msg.getContent();
				guiPujas.ActualizarLibrosSubasta(listado);
				guiPujas.Log(contenido + " " + titulo);
			} else
				block();
			
			//Recogida de mensajes de confirmación relativos a una puja desde este agente
			mt = MessageTemplate.MatchPerformative(ACLMessage.CONFIRM);
			mt = MessageTemplate.and(mt,MessageTemplate.MatchProtocol(FIPANames.InteractionProtocol.FIPA_DUTCH_AUCTION));
			mt = MessageTemplate.and(mt, MessageTemplate.MatchSender(vendedor));
			mt = MessageTemplate.and(mt, MessageTemplate.MatchReceiver(new AID[] {myAgent.getAID()}));
			msg = myAgent.receive(mt);
			if (msg != null) {
				String titulo = msg.getContent();
				
				//Envio de lectura de confirmación de compra al vendedor
				ACLMessage informar = new ACLMessage(ACLMessage.INFORM);
				informar.setContent(titulo);
				informar.setReplyWith("subasta-libro" + titulo + myAgent.getName());
				informar.addReceiver(vendedor);
				myAgent.send(informar);
				
				guiPujas.ActualizarLibrosSubasta(listado);
			} else
				block();
			
		}
	}

	//Se extrae el listado de libros de la subasta del vendedor 
	//Si es necesario se lanza la puja
	private class ListadoLibros extends CyclicBehaviour {
		@SuppressWarnings("unchecked")
		@Override
		public void action() {
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
			mt = MessageTemplate.and(mt, MessageTemplate.MatchProtocol("listado-libros"));
			mt = MessageTemplate.and(mt, MessageTemplate.MatchSender(vendedor));
			ACLMessage msg = myAgent.receive(mt);
			if (msg != null) {
				// Deserializado del mensaje con el listado de libros en subasta
				byte[] bytes = null;
				ObjectInputStream in = null;
				try {
					bytes = msg.getByteSequenceContent();
					if (bytes != null) {
						in = new ObjectInputStream(new ByteArrayInputStream(
								bytes));
						listado = (Hashtable<String, LibroSubasta>) in
								.readObject();
						in.close();
					}
					
					//Lanza las pujas al comprador
					if(pujas!=null) {
						for(LibroSubasta l : listado.values()) {
							if(!l.Anulado() && !l.Comprado() && pujas.containsKey(l.getTitulo())) {
								if (pujas.get(l.getTitulo()) >= l.getPrecioPuja()) {
									myAgent.addBehaviour(new Comprar(l.getTitulo()));
								}
							}
						
						}
					}
					guiPujas.ActualizarLibrosSubasta(listado);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			} else
				block();
		}

	}
	
	

	// Listado de libros del vendedor
	// Se pasa mediante un mensaje
	Hashtable<String, LibroSubasta> listado;
	
	//Relación de pujas del comprador
	Hashtable<String, Float> pujas;

	// Comportamiento para la consulta del listado de libros
	private ListadoLibros behaviourListadoLibros;

	//Agente vendedor para este agente comprador
	private AID vendedor;

	// GUI para controla las pujas en la subasta
	private CompradorGUI guiPujas;

	public void actualizarPuja(final String titulo, final Float preciopuja) {
		addBehaviour(new OneShotBehaviour() {
			@Override
			public void action() {
				if(pujas!=null) {
					if(pujas.contains(titulo)) pujas.remove(titulo);
					pujas.put(titulo, preciopuja);
				}
	
			}
		});
	}

	protected AID buscarAgenteVendedor(Agent agente) {
		AID vendedor = null;
		try {
			// Búsqueda en las páginas amarillas el agente vendedor
			DFAgentDescription plantilla = new DFAgentDescription();
			ServiceDescription sd = new ServiceDescription();
			sd.setType("vendedor-libros-subasta");
			sd.setName("JADE-vendedor-subasta");
			plantilla.addServices(sd);

			DFAgentDescription[] res = DFService.search(agente, plantilla);
			for (int i = 0; i < res.length; ++i) {
				if (agente.getAID().equals(res[i].getName())) {
					continue;
				}
				vendedor = res[i].getName();
			}
		} catch (FIPAException fe) {
			throw new RuntimeException("Error al buscar el agente vendedor...",
					fe);
		}
		return vendedor;
	}

	// Inicialización del agente de compra
	@Override
	protected void setup() {
		pujas = new Hashtable<String, Float>();
		
		// Crea y muestra el GUI
		guiPujas = new CompradorGUI();
		guiPujas.pack();
		guiPujas.setVisible(true);
		
		guiPujas.setAgentecomprador(this);
		

		// Registro en las páginas amarillas del agente de compra
		//Y búsqueda del agente vendedor
		addBehaviour(new OneShotBehaviour() {

			@Override
			public void action() {
				
				vendedor = buscarAgenteVendedor(myAgent);
				if(vendedor!=null) {
					DFAgentDescription template = new DFAgentDescription();
					ServiceDescription sd = new ServiceDescription();
					sd.setType("comprador-libros-subasta");
					sd.setName("JADE-comprador-subasta" + System.currentTimeMillis());
					template.addServices(sd);
					try {
						DFService.register(myAgent, template);
					} catch (FIPAException e) {
						e.printStackTrace();
						doDelete();
					}
				}
				else
				{
					JOptionPane.showMessageDialog(guiPujas, "No se encontró ningún agente vendedor");
					doDelete();
				}
					
			}
		});

		// Consulta cíclica del listado de libros del agente vendedor
		// Saltará en el momento que el vendedor añada un nuevo libro al listado
		// o se actualice el precio de puja de un libro en la subasta
		behaviourListadoLibros = new ListadoLibros();
		addBehaviour(behaviourListadoLibros);

		addBehaviour(new InformacionSubasta());

		guiPujas.Log("Agente comprador preparado: " + getAID().getName());
	}

	// Limpieza y cierra del agente de venta
	@Override
	protected void takeDown() {
		// Cierra el GUI
		guiPujas.dispose();
		try {
			DFService.deregister(this);
		} catch (FIPAException fe) {
			//fe.printStackTrace();
		}
		System.out.println("Agente comprador cerrado " + getAID().getName());
		System.exit(0);
	}
}

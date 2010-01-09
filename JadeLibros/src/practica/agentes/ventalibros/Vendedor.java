package practica.agentes.ventalibros;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPANames;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import practica.agentes.ventalibros.entidades.LibroSubasta;
import practica.agentes.ventalibros.gui.VendedorGUI;

@SuppressWarnings("serial")
public class Vendedor extends Agent {

	/*
	 * Desde este comportamiento se envia una notificación a todos los agentes compradores
	 * con el listado actualizado de libros
	 */
	public class ActualizarPujaAgentes extends OneShotBehaviour {

		@Override
		public void action() {
			List<AID> compradores = buscarAgentesCompradores(myAgent);
			if (compradores != null) {
				ACLMessage informar = new ACLMessage(ACLMessage.INFORM);
				informar.setProtocol("listado-libros");
				for (AID aid : compradores) {
					informar.addReceiver(aid);
				}
				try {
					//Serializado de los valores del listado a un array de bytes
					ByteArrayOutputStream bos = new ByteArrayOutputStream() ;
					ObjectOutput out = new ObjectOutputStream(bos) ;
					out.writeObject(listado);
					out.close();
					informar.setByteSequenceContent(bos.toByteArray());
				} catch (IOException e) {
					e.printStackTrace();
				}
				informar.setSender(myAgent.getAID());
				myAgent.send(informar);
			}
		}
		
	}

	/* 
	 * Comportamiento que controla la primer comprador que acepta la puja
	 * 
	 */
	private class ControlCompra extends CyclicBehaviour {
		@Override
		public void action() {
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.ACCEPT_PROPOSAL);
			mt = MessageTemplate.and(mt, MessageTemplate.MatchProtocol(FIPANames.InteractionProtocol.FIPA_DUTCH_AUCTION));
			
			ACLMessage msg = myAgent.receive(mt);
			if (msg != null) {
				// Procesar el mensaje
				String titulo = msg.getContent();
				
				//Creación del mensaje de respuesta al posible comprador
				ACLMessage respuesta = msg.createReply();
				respuesta.setContent(titulo);
				
				//Comprobar que el libro no se compró
				LibroSubasta libro = listado.get(titulo);
				if(!libro.Anulado() && !libro.Comprado())
				{
					respuesta.setPerformative(ACLMessage.CONFIRM);
					respuesta.setReplyWith("vendido");
				}
				else if(!libro.Comprado())
				{
					respuesta.setPerformative(ACLMessage.REFUSE);
					respuesta.setReplyWith("ya vendido");
				}
				else
				{
					respuesta.setPerformative(ACLMessage.REFUSE);
					respuesta.setReplyWith("venta de libro anulada");
				}
				myAgent.send(respuesta);
			}
			else
			{
				block();
			}
					
			//Comprobación de compra confirmada
			mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
			mt = MessageTemplate.and(mt, MessageTemplate.MatchReceiver(new AID[] {myAgent.getAID()}));
			msg = myAgent.receive(mt);
			if(msg!=null) {
				String titulo = msg.getContent();
				if(msg.getReplyWith() == "subasta-libro" + titulo + msg.getSender().getName());
				{
					//Actualizar el estado del libro
					LibroSubasta libro = listado.get(titulo);
					libro.setComprador(msg.getSender().getName());
					//Cerrar las pujas para ese libro
					List<AID> compradores = buscarAgentesCompradores(myAgent);
					if (compradores != null) {
						ACLMessage informar = new ACLMessage(ACLMessage.INFORM);
						for (AID aid : compradores) {
							if(msg.getSender()!=aid)
								informar.addReceiver(aid);
						}
						informar.setContent("fin-subasta");
						informar.addUserDefinedParameter("Libro", titulo);
						myAgent.send(informar);
					}
					guiSubasta.ActualizarLibro(libro);
				}
				
			}
			else
			{
				block();
			}
		}
	}
	
	//Disparador del inicio de subasta
	public class IniciarSubasta extends OneShotBehaviour {
		@Override
		public void action() {
			if(listado.size()!=0) {
				//Se informa a todos los agentes compradores activos que comienza la subasta
				List<AID> compradores = buscarAgentesCompradores(myAgent);
				if (compradores != null) {
					ACLMessage informar = new ACLMessage(ACLMessage.INFORM);
					for (AID aid : compradores) {
						informar.addReceiver(aid);
					}
	
					informar.setProtocol(FIPANames.InteractionProtocol.FIPA_DUTCH_AUCTION);
					informar.setContent("inicio-subasta");
					myAgent.send(informar);
	
					//Arranque del comportamiento cíclico ante un solicitud de compra
					if(behaviourControlCompra!=null) myAgent.removeBehaviour(behaviourControlCompra);
					behaviourControlCompra = new ControlCompra();
					
					//Arranque del comportamiento cíclico con el control de subasta
					if(behaviourSubastaLibros!=null) {
						behaviourSubastaLibros.stop();
						myAgent.removeBehaviour(behaviourSubastaLibros);
					}
					behaviourSubastaLibros = new Subastar(myAgent);
					
					myAgent.addBehaviour(behaviourControlCompra);
					myAgent.addBehaviour(behaviourSubastaLibros);
				}
				else {
					guiSubasta.Log("No hay agentes compradores");
					guiSubasta.TerminarSubasta();
				}
			}
			else {
				guiSubasta.Log("No hay libros a subastar");
				guiSubasta.TerminarSubasta();
			}
		}
	}
	/*
	 * Clase interna para el control de la subasta de libros Cada 3 segundos se
	 * actualiza baja el precio de los libros en 1 unidad También se
	 * controla si se llegó al precio mínimo o si algún agente comprador hace
	 * una puja
	 */
	private class Subastar extends TickerBehaviour {
		public Subastar(Agent a) {
			super(a,3000);
		}

		@Override
		protected void onTick() {
			boolean fin = true;
			for (LibroSubasta l : listado.values()) {
				if (!l.Comprado() && !l.Anulado()) {
					fin = false;
					if (!l.ActualizarPuja(-1.0f)) {
						l.setComprador("- ANULADA -");
					}
					guiSubasta.ActualizarLibro(l);
				}
			}
			// Si no hay más libros para pujar, se termina la subasta
			if (fin) {
				this.stop();
				guiSubasta.TerminarSubasta();
			}
			//Lanza el comportamiento de actualización del listado de los agentes
			myAgent.addBehaviour(new ActualizarPujaAgentes());
		}

	}


	//Terminar la subasta
	//Esta clase será disparada desde el gui
	//También se puede indicar el cierre del agente vendedor
	public class TerminarSubasta extends OneShotBehaviour {
		private boolean cerraragente;
		public TerminarSubasta() {this.cerraragente=false;}
		public TerminarSubasta(boolean cerraragente) { this.cerraragente = cerraragente;}
		
		@Override
		public void action() {
			//Borra comportamientos de control de compra y de subasta de libros
			if(behaviourControlCompra!=null)
				myAgent.removeBehaviour(behaviourControlCompra);
			if(behaviourSubastaLibros!=null)
				myAgent.removeBehaviour(behaviourSubastaLibros);
			
			//Informar a los compradores que se ha terminado la subasta
			List<AID> compradores = buscarAgentesCompradores(myAgent);
			if (compradores != null) {
				ACLMessage informar = new ACLMessage(ACLMessage.INFORM);
				for (AID aid : compradores) {
					informar.addReceiver(aid);
				}
				informar.setContent("fin-subasta");
				myAgent.send(informar);
			}
			if(cerraragente)
			{
				myAgent.doDelete();
			}
		}

	}

	// Listado de libros a subastar
	private Hashtable<String, LibroSubasta> listado;

	// GUI para añadir libros a la subasta
	private VendedorGUI guiSubasta;

	//
	private Subastar behaviourSubastaLibros;


	private ControlCompra behaviourControlCompra;

	/**
	 * Función invocada por el GUI en el momento que el usuario añade un nuevo
	 * libro para la subasta
	 */
	public void actualizarListado(final String titulo, final LibroSubasta libro) {
		addBehaviour(new OneShotBehaviour() {
			@Override
			public void action() {
				listado.put(titulo, libro);
				guiSubasta.Log(titulo
						+ " añadido a la subasta. Precio Inicial = "
						+ String.valueOf(libro.getPrecioInicial())
						+ " Precio Mínimo = "
						+ String.valueOf(libro.getPrecioMinimo()));
				
				myAgent.addBehaviour(new ActualizarPujaAgentes());
			}
		});
	}
	
	/*
	 * Función para la búsqueda de posibles compradores en un momento dado
	 */
	protected List<AID> buscarAgentesCompradores(Agent agente) {
		List<AID> lista = null;
		try {
			//Búsqueda en las páginas amarillas de los agentes compradores
			DFAgentDescription plantilla = new DFAgentDescription();
			ServiceDescription sd = new ServiceDescription();
		    sd.setType("comprador-libros-subasta");
		    plantilla.addServices(sd);
			
			DFAgentDescription[] res = DFService.search(agente, plantilla);
			lista = new ArrayList<AID>();
			for (int i = 0; i < res.length; ++i) {
				AID comprador = res[i].getName();
				if (agente.getAID().equals(comprador)
						|| lista.contains(comprador)) {
					continue;
				}
				lista.add(comprador);
			}
		} catch (FIPAException fe) {
			throw new RuntimeException(
					"Error al buscar agentes compradores...", fe);
		}
		return (lista == null || lista.isEmpty()) ? null : lista;
	}
	

	// Inicialización del agente de venta
	@Override
	protected void setup() {
		// Crea el listado
		listado = new Hashtable<String, LibroSubasta>();

		// Crea y muestra el GUI
		guiSubasta = new VendedorGUI();
		guiSubasta.pack();
		guiSubasta.setVisible(true);

		guiSubasta.setAgentevendedor(this);
		

		// Registro del servicio de subasta de libros en las página amarillas
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType("vendedor-libros-subasta");
	    sd.setName("JADE-vendedor-subasta");
		dfd.addServices(sd);
		try {
			DFService.register(this, dfd);
		} catch (FIPAException fe) {
			fe.printStackTrace();
		}
	
	}

		
	// Limpia el agente
	@Override
	protected void takeDown() {
		
		// Deregistro de las páginas amarillas
		try {
			DFService.deregister(this);
		} catch (FIPAException fe) {
			fe.printStackTrace();
		}

		// Cierra el GUI
		guiSubasta.dispose();
		
		System.exit(0);
	} 
}

/*
 * Copyright NEC Europe Ltd. 2006-2007
 *
 * This file is part of the context simulator called Siafu.
 *
 * Siafu is free software; you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 *
 * Siafu is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package de.nec.nle.siafu.safepet;

import static de.nec.nle.siafu.safepet.Constants.POPULATION_OF_POINTS;
import static de.nec.nle.siafu.safepet.Constants.Fields.ACTIVITY;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.*;

import de.nec.nle.siafu.behaviormodels.BaseAgentModel;
import de.nec.nle.siafu.exceptions.PlaceTypeUndefinedException;
import de.nec.nle.siafu.model.*;
import de.nec.nle.siafu.safepet.Constants.Activity;
import de.nec.nle.siafu.types.EasyTime;
import lac.cnclib.net.NodeConnection;
import lac.cnclib.net.NodeConnectionListener;
import lac.cnclib.net.groups.Group;
import lac.cnclib.net.groups.GroupCommunicationManager;
import lac.cnclib.net.groups.GroupMembershipListener;
import lac.cnclib.net.mrudp.MrUdpNodeConnection;
import lac.cnclib.sddl.message.ApplicationMessage;
import lac.cnclib.sddl.message.Message;
//import org.example.Model.Usuario;
import org.openstreetmap.gui.jmapviewer.Coordinate;
import com.google.gson.Gson;

/**
 * This Agent Model defines the behavior of users in this test simulation.
 * Essentially, most users will wander around in a zombie like state, except
 * for Pietro and Teresa, who will stay put, and the postman, who will spend a
 * simulation life time running between the two ends of the map.
 *
 * @author Miquel Martin
 *
 */
public class AgentModel extends BaseAgentModel {
	// constantes
//	private final static String GATEWAY_IP = "127.0.0.1";
//	private final static int GATEWAY_PORTA = 5500;
//
//	// propriedades
//	private GroupCommunicationManager groupManager;
//	private HashSet<Integer> listaDeGrupos =null;
//	private List<Posicao> listaDeLugares =null;
//	private NodeConnection remoteCon =null;
//	private Thread percorreCaminho =null;
//	private UUID uuid;				// UUID do usuário local
//	private String username;	// nome do usuário
//	private String filename;	// nome do arquivo com o caminho
//
//	// variáveis compartilhadas pelas threads
//	static class Global {
//		public volatile static Coordinate coordenadas;
//		public volatile static boolean		statusFree =true;	// free = recebe mensagens, busy = não recebe mensagem (não mostra para o usuário)
//	};

	/**
	 * The time at which the psotman moves fastest.
	 */
	private static final int POSTMAN_PEAK = 12;

	/**
	 * A random number generator.
	 */
	private static final Random RAND = new Random();

	/**
	 * The top speed at which agents will move.
	 */
	private static final int TOP_SPEED = 10;

	/**
	 * A special user that plays a courrier of the Czar.
	 */
	private Agent dogOne;
	private Agent dogTwo;
	private Agent catOne;

	/**
	 * The current time.
	 */
	private EasyTime now;

	/** Place one in the simulation. */
	private Place placeHome;
	private Place placeHomeTwo;
	private Place placeHomeThree;
	private Place placeOneZumbi;
	private Place placeTwoZumbi;
	private Place placeThreeZumbi;
	private Place placeFourZumbi;
	private Place placeFiveZumbi;
	private Place placeSixZumbi;
	private Place placeSevenZumbi;

	private List<Place> places;

	private Coordinate coordinate;
	/** Place two in the simulation. */

	/**
	 * Constructor for the agent model.
	 *
	 * @param world the simulation's world
	 */
	public AgentModel(final World world) {
		super(world);

//		this.uuid = UUID.randomUUID();
//		this.username = UUID.randomUUID().toString();
//		System.err.println("[" + this.getClass().getName() + "." + "ClienteMovel]"
//				+ " UUID = " + this.uuid.toString());
//
////		leCaminho();
//        Global.coordenadas = new Coordinate(-2, -45);	// ponto de partida
////		this.coordinate = new Coordinate(5.5, 5.5);
//		InetSocketAddress endereco = new InetSocketAddress(GATEWAY_IP, GATEWAY_PORTA);
//		try {
//			MrUdpNodeConnection conexao = new MrUdpNodeConnection(uuid);
//			// *** inverti a ordem para ver se o pedido de conexão fica mais estável
//			conexao.addNodeConnectionListener(this);
//			conexao.connect(endereco);
//
//			try {
//				Thread.sleep(0);
//			}
//			catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//		}
//		catch (IOException e) {
//			e.printStackTrace();
//		}
	}

//	@Override
//	@SuppressWarnings("unchecked")
//	public void connected(NodeConnection remoteCon) {
//		System.err.println("[" + this.getClass().getName() + "." + new Object(){}.getClass().getEnclosingMethod().getName() + "] "
//				+ "");
//		groupManager = new GroupCommunicationManager(remoteCon);
//		groupManager.addMembershipListener(this);
//
//		this.remoteCon = remoteCon;
//
//		// informa as coordenadas ao seletor
//		try {
////			ApplicationMessage mensagem = new ApplicationMessage();
////			Usuario user = new Usuario(this.username, Global.coordenadas.getLat(), Global.coordenadas.getLon());
////			Gson gson = new Gson();
////			String json = gson.toJson(user);
////
////			mensagem.setContentObject(json);
////			this.remoteCon.sendMessage(mensagem);
//			informaCoordenadas();
//		}
//		catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//
//	/**
//	 * Informa as coordenadas ao seletor.
//	 */
//	public void informaCoordenadas()
//	{
//		// informa as coordenadas ao seletor
//		try {
//			ApplicationMessage mensagem = new ApplicationMessage();
//			Usuario user = new Usuario(this.username, Global.coordenadas.getLat(), Global.coordenadas.getLon());
//			Gson gson = new Gson();
//			String json = gson.toJson(user);
//
//			mensagem.setContentObject(json);
//			this.remoteCon.sendMessage(mensagem);
//		}
//		catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//	@Override
//	@SuppressWarnings("unchecked")
//	public void newMessageReceived(NodeConnection remoteCon, Message message) {
////        Object mensagem = message.getContentObject();
////        /*
////         * o cliente pode receber 2 tipos de mensagem:
////         * a) String: um texto qualquer vindo de outro cliente (nada a fazer)
////         * b) HashSet: a sua lista de grupos vida do seletor
////         */
////        if(mensagem instanceof HashSet)
////        {
////            this.listaDeGrupos = (HashSet<Integer>) mensagem;
////            System.err.print("[" + this.getClass().getName() + "." + new Object(){}.getClass().getEnclosingMethod().getName() + "] "
////                    + "\n" + message.getSenderID() + "\nGrupos: ");
////            for(int i : this.listaDeGrupos) System.err.print(i + " ");
////            System.err.println();
////
////            enviaMensagemGrupos("Usuário " + this.username + " está na área!");
////        }
////        else if(!message.getSenderID().equals(this.uuid) && Global.statusFree) {
////            System.out.println("Mensagem recebida de " + message.getSenderID() + " para " + this.uuid + "\n" + message.getContentObject());
////            prompt();
////        }
//	}
//
//	@Override
//	public void reconnected(NodeConnection remoteCon, SocketAddress endPoint, boolean wasHandover, boolean wasMandatory) {
////        System.err.println("[" + this.getClass().getName() + "." + new Object(){}.getClass().getEnclosingMethod().getName() + "] "
////                + "");
////        if(percorreCaminho != null) percorreCaminho.start();
//	}
//
//	@Override
//	public void disconnected(NodeConnection remoteCon) {
////        System.err.println("[" + this.getClass().getName() + "." + new Object(){}.getClass().getEnclosingMethod().getName() + "] "
////                + "");
////        if(percorreCaminho != null) percorreCaminho.stop();
//	}
//
//	public void unsentMessages(NodeConnection remoteCon, List<Message> unsentMessages) {}
//	@Override
//	public void internalException(NodeConnection remoteCon, Exception e) {}
//	@Override
//	public void enteringGroups(List<Group> groups) {}
//	@Override
//	public void leavingGroups(List<Group> groups) {}
//
//	private void prompt()
//	{
//		System.out.println("Seu status agora é " + (Global.statusFree? "Livre" : "Ocupado"));
//		System.out.println("Entre com uma mensagem para o(s) grupo(s) ou status '[FREE]'/'[BUSY]': ");
//	}
//	private void enviaMensagemGrupos(String texto)
//	{
//		try {
//			if(this.listaDeGrupos != null) {
//				for(int i : this.listaDeGrupos) {
//					Group group = new Group(3, i);
//
//					ApplicationMessage message = new ApplicationMessage();
//					String serializableContent = "Mensagem para o grupo " + i + ": " + texto;
//					message.setSenderID(this.uuid);
//					message.setContentObject(serializableContent);
//
//					groupManager.sendGroupcastMessage(message, group);
//					System.err.println("[" + this.getClass().getName() + "." + new Object(){}.getClass().getEnclosingMethod().getName() + "] "
//							+ " Mensagem de\n" + this.uuid + "\npara o grupo " + i + " enviada");
//				}
//			}
//		}
//		catch (IOException e) {
//			e.printStackTrace();
//		}
//	}



	/**
	 * Create a bunch of agents that will wander around aimlessly. Tweak them
	 * for testing purposes as needed. Two agents, Pietro and Teresa, are
	 * singled out and left under the control of the user. A third agent,
	 * Postman, is set to run errands between the two places int he map.
	 *
	 * @return the created agents
	 */
	public ArrayList<Agent> createAgents() {
		world.stopSpinning(true);

		try {
			placeHome = world.getPlacesOfType("Home").iterator().next();
			placeHomeTwo = world.getPlacesOfType("HomeTwo").iterator().next();
			placeHomeThree = world.getPlacesOfType("HomeThree").iterator().next();
			placeOneZumbi = world.getPlacesOfType("PlaceOneZumbi").iterator().next();
			placeTwoZumbi = world.getPlacesOfType("PlaceTwoZumbi").iterator().next();
			placeThreeZumbi = world.getPlacesOfType("PlaceThreeZumbi").iterator().next();
			placeFourZumbi = world.getPlacesOfType("PlaceFourZumbi").iterator().next();
			placeFiveZumbi = world.getPlacesOfType("PlaceFiveZumbi").iterator().next();
			placeSixZumbi = world.getPlacesOfType("PlaceSixZumbi").iterator().next();
			placeSevenZumbi = world.getPlacesOfType("PlaceSevenZumbi").iterator().next();

			places =  Arrays.asList(placeOneZumbi, placeTwoZumbi,
					placeThreeZumbi, placeFourZumbi, placeFiveZumbi,
					placeSixZumbi, placeSevenZumbi);
		} catch (PlaceTypeUndefinedException e) {
			throw new RuntimeException(e);
		}

		int totalPetNumbers = 1;

		System.out.println("Creating " + totalPetNumbers + " pets zumbis.");
		ArrayList<Agent> population = AgentGenerator.createRandomPopulation(POPULATION_OF_POINTS,
				places, world);

		/** Creating controlled pets home one */
		System.out.println("Home one");

		dogOne = AgentGenerator.createConttroledAgent(placeHome.getPos(),
				"Dog", String.format("dog-%d", totalPetNumbers+1), world);
		population.add(dogOne);
		System.out.println(dogOne.getName());

		dogTwo = AgentGenerator.createConttroledAgent(placeHome.getPos(),
				"Dog", String.format("dog-%d", totalPetNumbers+2), world);
		population.add(dogTwo);
		System.out.println(dogTwo.getName());

		catOne = AgentGenerator.createConttroledAgent(placeHome.getPos(),
				"cat", String.format("cat-%d", totalPetNumbers+3), world);
		population.add(catOne);
		System.out.println(catOne.getName());

		/** Creating controlled pets home two */
		System.out.println("Home two");


		return population;
	}

	/**
	 * Make all the normal agents wander around, and the postman, run errands
	 * from one place to another. His speed depends on the time, slowing down
	 * at night.
	 *
	 * @param agents the list of agents
	 */
	public void doIteration(final Collection<Agent> agents) {
		Calendar time = world.getTime();
		now = new EasyTime(time.get(Calendar.HOUR_OF_DAY), time
				.get(Calendar.MINUTE));
		handleDogOne();
		handleDogTwo();
		handleCatOne();
		for (Agent a : agents) {
			if (!a.isOnAuto()) {
				continue; // This guy's being managed by the user interface
			}
			if (a.equals(dogOne) || a.equals(dogTwo) || a.equals(catOne)) {
				continue;
			}
			handleAgent(a);
			System.out.println(a.getName());
		}
	}

	/**
	 * Move the postman from one place to the next, increasing the speed the
	 * closer to noon it is.
	 *
	 */
	private void handleDogOne() {
		dogOne.setSpeed(POSTMAN_PEAK
				- Math.abs(POSTMAN_PEAK - now.getHour()));
		if (dogOne.isAtDestination()) {
			if (dogOne.getPos().equals(placeHome.getPos())) {
				dogOne.setDestination(placeThreeZumbi);
			} else if(dogOne.getPos().equals(placeThreeZumbi.getPos())) {
				dogOne.setDestination(placeOneZumbi);
			} else if(dogOne.getPos().equals(placeOneZumbi.getPos())) {
				dogOne.setDestination(placeHome);
			}
		}
	}

	private void handleDogTwo() {
		dogTwo.setSpeed(POSTMAN_PEAK
				- Math.abs(POSTMAN_PEAK - now.getHour()));
		if (dogTwo.isAtDestination()) {
			if (dogTwo.getPos().equals(placeHome.getPos())) {
				//Sair depois de 3 segundos
				new Timer().schedule(new TimerTask() {
					@Override
					public void run() {
						dogTwo.setDestination(placeTwoZumbi);
					}
				}, 2000);
			} else if(dogTwo.getPos().equals(placeTwoZumbi.getPos())) {
				dogTwo.setDestination(placeSevenZumbi);
			} else if(dogTwo.getPos().equals(placeSevenZumbi.getPos())) {
				dogTwo.setDestination(placeHome);
			}
		}
	}

	private void handleCatOne() {
		catOne.setSpeed(POSTMAN_PEAK
				- Math.abs(POSTMAN_PEAK - now.getHour()));
		if (catOne.isAtDestination()) {
			if (catOne.getPos().equals(placeHome.getPos())) {
				//Sair depois de 4 segundos
				new Timer().schedule(new TimerTask() {
					@Override
					public void run() {
						catOne.setDestination(placeSevenZumbi);
					}
				}, 3000);
			} else if(catOne.getPos().equals(placeSevenZumbi.getPos())) {
				catOne.setDestination(placeFourZumbi);
			} else if(catOne.getPos().equals(placeFourZumbi.getPos())) {
				catOne.setDestination(placeHome);
			}
		}
	}

	/**
	 * Keep the agent wandering around zombie style.
	 *
	 * @param a the agent to zombiefy
	 */
	private void handleAgent(final Agent a) {
		switch ((Activity) a.get(ACTIVITY)) {
			case WAITING:
				break;

			case WALKING:
				a.wander();
				break;
			default:
				throw new RuntimeException("Unable to handle activity "
						+ (Activity) a.get(ACTIVITY));
		}

	}
}

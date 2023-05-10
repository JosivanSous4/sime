package br.com.meslin;

/**
 * Para compilar:
 * $ javac -classpath .:/media/meslin/16E65872E6585459/Program\ Files/Java/ContexNet/'*':/media/meslin/16E65872E6585459/Program\ Files/Java/JMapViewer-2.0/JMapViewer.jar:/media/meslin/16E65872E6585459/Program\ Files/Java/commons-cli-1.3.1/commons-cli-1.3.1.jar -d . br/com/meslin/ClienteMovel.java
 * 
 * Para executar:
 * $ java -classpath .:/media/meslin/16E65872E6585459/Program\ Files/Java/ContexNet/'*':/media/meslin/16E65872E6585459/Program\ Files/Java/JMapViewer-2.0/JMapViewer.jar:/media/meslin/16E65872E6585459/Program\ Files/Java/commons-cli-1.3.1/commons-cli-1.3.1.jar br.com.meslin.ClienteMovel -f <nome do arquivo com o caminho> -u <nome do usuário>
 */

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.Gson;
import lac.cnclib.net.NodeConnection;
import lac.cnclib.net.NodeConnectionListener;
import lac.cnclib.net.groups.Group;
import lac.cnclib.net.groups.GroupCommunicationManager;
import lac.cnclib.net.groups.GroupMembershipListener;
import lac.cnclib.net.mrudp.MrUdpNodeConnection;
import lac.cnclib.sddl.message.ApplicationMessage;
import lac.cnclib.sddl.message.Message;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.openstreetmap.gui.jmapviewer.Coordinate;

import br.com.meslin.auxiliar.Posicao;
import br.com.meslin.auxiliar.Usuario;

/**
 * Implementa um cliente móvel
 * 
 * @author meslin
 *
 */
public class ClienteMovel implements NodeConnectionListener, GroupMembershipListener {
	// constantes
	private final static String GATEWAY_IP = "127.0.0.1";
	private final static int GATEWAY_PORTA = 5500;
	
	// propriedades
	private GroupCommunicationManager groupManager;
	private HashSet<Integer> listaDeGrupos =null;
	private List<Posicao> listaDeLugares =null;
	private NodeConnection remoteCon =null;
	private Thread percorreCaminho =null;
	private UUID uuid;				// UUID do usuário local
	private String username;	// nome do usuário
	private String filename;	// nome do arquivo com o caminho
	
	// variáveis compartilhadas pelas threads
	static class Global {	
		public volatile static Coordinate	coordenadas;
		public volatile static boolean		statusFree =true;	// free = recebe mensagens, busy = não recebe mensagem (não mostra para o usuário) 
	};	
	
	/**
	 * Constroi um cliente móvel percorrendo o caminho descrito no arquivo
	 * 
	 */
	public ClienteMovel(String username, String filename) {
		this.filename = filename;
		this.username = username;

		this.uuid = UUID.randomUUID();
		System.err.println("[" + this.getClass().getName() + "." + "ClienteMovel]"
				+ " UUID = " + this.uuid.toString());

		leCaminho();
		Global.coordenadas = listaDeLugares.get(0).getCoordenadas();	// ponto de partida

		InetSocketAddress endereco = new InetSocketAddress(GATEWAY_IP, GATEWAY_PORTA);
		try {
			MrUdpNodeConnection conexao = new MrUdpNodeConnection(uuid);
			// *** inverti a ordem para ver se o pedido de conexão fica mais estável
			conexao.addNodeConnectionListener(this);
			conexao.connect(endereco);

//			try {
//				Thread.sleep(0);
//			}
//			catch (InterruptedException e) {
//				e.printStackTrace();
//			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Programa principal
	 * 
	 * @param args argumentos da linha de comando
	 */
	public static void main(String[] args) {
		// parse da linha de comando
		String dir = "/home/josivan/IdeaProjects/travelersgroup/src/main/java/br/com/meslin/";

//		String[] files = {"caminho.txt", "caminho2.txt", "caminho3.txt"};

//		String[] files = {"caminho2.txt"};
//		for (int i=0; i<files.length; i++)
//		{
//			filename = dir+files[i];
//			username = "usuário" + (int)(Math.random() * Integer.MAX_VALUE);
//			Options opcoes = new Options();
//			opcoes.addOption("f", "filename", true, "nome do arquivo com o caminho do usuario");
//			opcoes.addOption("u", "username", true, "nome do usuário");
//			CommandLineParser parser = new DefaultParser();
//			try
//			{
//				CommandLine cmd = parser.parse(opcoes, args);
//				if(cmd.hasOption('f')) filename = cmd.getOptionValue('f');
//				if(cmd.hasOption("filename")) filename = cmd.getOptionValue("filename");
//				if(cmd.hasOption('u')) username = cmd.getOptionValue('u');
//				if(cmd.hasOption("username")) username = cmd.getOptionValue("username");
//			}
//			catch (ParseException e)
//			{
//				System.err.println("Erro durante o parse da linha de comandos. Motivo: " + e.getMessage());
//				System.err.println("Uso: ClienteMovel -f <arquivo com caminho> -u <nome do usuario>");
//				return;
//			}
//
//			Logger.getLogger("").setLevel(Level.OFF);
////			new ClienteMovel();
//		}


	}

	/**
	 * Le o caminho do arquivo.
	 * <p>
	 * O caminho é composto por um tempo em milissegundos e as coordenadas x e y
	 * 
	 * @author Meslin
	 */
	private void leCaminho()
	{
		System.err.println("[" + this.getClass().getName() + ".] "
				+ " lendo caminhos");
		
		BufferedReader br = null;
		listaDeLugares = new ArrayList<Posicao>();
		try
		{
			br = new BufferedReader(new FileReader(this.filename));
			String linha;
			while((linha = br.readLine()) != null)
			{
				int duracao = Integer.parseInt(linha.substring(0, linha.indexOf(" ")).trim());
				linha = linha.substring(linha.indexOf(" ")).trim();
				Posicao posicao = new Posicao(
						duracao,
						Double.parseDouble(linha.substring(linha.indexOf(" ")).trim()),
						Double.parseDouble(linha.substring(0, linha.indexOf(" ")).trim())
				);
				listaDeLugares.add(posicao);
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		finally {
			if(br != null)
			{
				try {
					br.close();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public void connected(NodeConnection remoteCon) {
		System.err.println("[" + this.getClass().getName() + "." + new Object(){}.getClass().getEnclosingMethod().getName() + "] " 
				+ "");
		groupManager = new GroupCommunicationManager(remoteCon);
		groupManager.addMembershipListener(this);
		
		this.remoteCon = remoteCon;

		/**
		 * Thread para mudar o cliente de lugar de tempos em tempos
		 */
		percorreCaminho = new Thread() {
			public void run() {
				int indice =0;	// índice do lugar
				while(true) {
					informaCoordenadas();
					try
					{
						Thread.sleep(listaDeLugares.get(indice).getDuracao());
					}
					catch (InterruptedException e)
					{
						e.printStackTrace();
					}
					
					indice = (indice +1) % listaDeLugares.size();
					System.err.println("[" + this.getClass().getName() + ".SelecionaGrupo] "
							+ " mudando para o lugar " + indice + " em " + listaDeLugares.get(indice).getCoordenadas());
					Global.coordenadas = listaDeLugares.get(indice).getCoordenadas();
				}
			}
		};
		percorreCaminho.start();
		
		Thread chat = new Thread() {
			public void run() {
				@SuppressWarnings("resource")
				Scanner entrada = new Scanner(System.in);
				while(true)
				{
					prompt();
					String texto = entrada.nextLine();
					if(texto.equals("[FREE]")) Global.statusFree = true;
					else if(texto.equals("[BUSY]")) Global.statusFree = false;
					else enviaMensagemGrupos(texto);
				}
			}
		};
		chat.start();
	}

	/**
	 * Informa as coordenadas ao seletor.
	 */
	private void informaCoordenadas()
	{
		// informa as coordenadas ao seletor
		try {
			ApplicationMessage mensagem = new ApplicationMessage();
			Usuario user = new Usuario(this.username, Global.coordenadas.getLat(), Global.coordenadas.getLon());
			Gson gson = new Gson();
			String json = gson.toJson(user);

			mensagem.setContentObject(json);
			this.remoteCon.sendMessage(mensagem);
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public void newMessageReceived(NodeConnection remoteCon, Message message) {
		Object mensagem = message.getContentObject();
		/*
		 * o cliente pode receber 2 tipos de mensagem:
		 * a) String: um texto qualquer vindo de outro cliente (nada a fazer)
		 * b) HashSet: a sua lista de grupos vida do seletor
		 */
		if(mensagem instanceof HashSet)
		{
			this.listaDeGrupos = (HashSet<Integer>) mensagem;
			System.err.print("[" + this.getClass().getName() + "." + new Object(){}.getClass().getEnclosingMethod().getName() + "] "
					+ "\n" + message.getSenderID() + "\nGrupos: ");
			for(int i : this.listaDeGrupos) System.err.print(i + " ");
			System.err.println();
			
			enviaMensagemGrupos("Usuário " + this.username + " está na área!");
		}
		else if(!message.getSenderID().equals(this.uuid) && Global.statusFree) {
			System.out.println("Mensagem recebida de " + message.getSenderID() + " para " + this.uuid + "\n" + message.getContentObject());
			prompt();
		}
	}

	/**
	 * Envia mensagem para os grupos aos quais o cliente móvel pertença
	 */
	private void enviaMensagemGrupos(String texto)
	{
		try {
			if(this.listaDeGrupos != null) {
				for(int i : this.listaDeGrupos) {
					Group group = new Group(3, i);
	
					ApplicationMessage message = new ApplicationMessage();
					String serializableContent = "Mensagem para o grupo " + i + ": " + texto;
					message.setSenderID(this.uuid);
					message.setContentObject(serializableContent);
					
					groupManager.sendGroupcastMessage(message, group);
					System.err.println("[" + this.getClass().getName() + "." + new Object(){}.getClass().getEnclosingMethod().getName() + "] " 
							+ " Mensagem de\n" + this.uuid + "\npara o grupo " + i + " enviada");
				}
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void reconnected(NodeConnection remoteCon, SocketAddress endPoint, boolean wasHandover, boolean wasMandatory) {
		System.err.println("[" + this.getClass().getName() + "." + new Object(){}.getClass().getEnclosingMethod().getName() + "] " 
				+ "");
		if(percorreCaminho != null) percorreCaminho.start();
	}

	@SuppressWarnings("deprecation")
	@Override
	public void disconnected(NodeConnection remoteCon) {
		System.err.println("[" + this.getClass().getName() + "." + new Object(){}.getClass().getEnclosingMethod().getName() + "] " 
				+ "");
		if(percorreCaminho != null) percorreCaminho.stop();
	}
	@Override
	public void unsentMessages(NodeConnection remoteCon, List<Message> unsentMessages) {}
	@Override
	public void internalException(NodeConnection remoteCon, Exception e) {}
	@Override
	public void enteringGroups(List<Group> groups) {}
	@Override
	public void leavingGroups(List<Group> groups) {}
	
	private void prompt()
	{
		System.out.println("Seu status agora é " + (Global.statusFree? "Livre" : "Ocupado"));
		System.out.println("Entre com uma mensagem para o(s) grupo(s) ou status '[FREE]'/'[BUSY]': ");
	}
}

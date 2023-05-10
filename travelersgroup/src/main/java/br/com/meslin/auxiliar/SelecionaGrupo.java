package br.com.meslin.auxiliar;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.google.gson.Gson;
import lac.cnclib.net.NodeConnection;
import lac.cnclib.net.NodeConnectionListener;
import lac.cnclib.net.mrudp.MrUdpNodeConnection;
import lac.cnclib.sddl.message.ApplicationMessage;
import lac.cnet.groupdefiner.components.groupselector.GroupSelector;
import lac.cnet.sddl.objects.GroupRegion;
import lac.cnet.sddl.objects.Message;

import org.openstreetmap.gui.jmapviewer.Coordinate;

import br.com.meslin.mapa.Mapa;
import org.example.Model.Usuario;

public class SelecionaGrupo implements GroupSelector, NodeConnectionListener {
	// constantes
	private static final String GATEWAY_IP = "127.0.0.1";
	private static final int GATEWAY_PORT = 5500;

	// propriedades
	private Mapa mapa;
	private List<Regiao> listaRegioes;
	private List<Usuario>listaUsuarios;
	
	/**
	 * <p>Constroi regiões a partir de arquivos</p>
	 * <p>Arquivo de nomes: um nome de arquivo por linha com caminho completo ou relativo</p>
	 * <p>Arquivo de região: um ponto com coordenada x e y separados por espaço em branco. Um por linha</p>
	 * 
	 * @param nome nome do arquivo com a lista de arquivos de região
	 */
	public SelecionaGrupo(String nome)
	{
		// lê o arquivo com o nome dos arquivos de regiões (um nome por linha)
		BufferedReader br = null;
		List<String> nomesArquivos = new ArrayList<String>();
		String dir = "/home/josivan/IdeaProjects/travelersgroup/src/main/java/br/com/meslin/";
		try
		{
			br = new BufferedReader(new FileReader(dir+nome));
			String nomeArquivo;
			while((nomeArquivo = br.readLine()) != null)
			{
				nomesArquivos.add(dir+nomeArquivo.trim());
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
		
		// lê cada um dos arquivos de região
		this.listaRegioes = new ArrayList<Regiao>();	// lista de regiões
		int nRegiao = 1;	// número da região
		for(String nomeArquivo : nomesArquivos) {
			// lê uma região com um ponto por linha, ou seja, linha = x y
			Regiao regiao = new Regiao();
			regiao.setNumero(nRegiao);
			nRegiao++;
			System.err.println("[" + this.getClass().getName() + ".SelecionaGrupo] "
					+ " criando região número " + regiao.getNumero());
			try
			{
				br = new BufferedReader(new FileReader(nomeArquivo));
				String linha;
				while((linha = br.readLine()) != null)
				{
					Coordinate ponto = new Coordinate(
							Double.parseDouble(linha.substring(linha.indexOf(" ")).trim()),
							Double.parseDouble(linha.substring(0, linha.indexOf(" ")).trim())
							);
					regiao.add(ponto);
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
			listaRegioes.add(regiao);
		}
		mapa = new Mapa(listaRegioes);
		mapa.setVisible(true);
		listaUsuarios = new ArrayList<Usuario>();	// inicialmente a lista de usuários está vazia
	}

	@Override
	public int getGroupType() {
		return 3;
	}

	/**
	 * Seleciona o grupo do usuário de acordo com a sua região (longitude e latitude)
	 * <p>Sempre que o usuário trocar de região, o cliente do usuário deve enviar uma mensagem com as coordenadas da nova região
	 */
	@Override
		public Set<Integer> processGroups(Message nodeMessage) {
		System.out.println("[" + this.getClass().getName() + "." + new Object(){}.getClass().getEnclosingMethod().getName() + "]"
					+ " STARTED CLASSIFYING GROUP MESSAGE");
		Gson gson = new Gson();
		// obtém o ponto do usuário
		Usuario usuario = null;
		String stringUser = "";
		try
		{
			stringUser = (String) new ObjectInputStream(new ByteArrayInputStream(nodeMessage.getContent())).readObject();
			usuario = gson.fromJson(stringUser, Usuario.class);
		}
		catch (IOException | ClassNotFoundException e)
		{
			e.printStackTrace();
		}
		System.out.println("User: " + usuario.getUsername() + ".: Lat " + usuario.getLat());

//		listaUsuarios.removeIf(new SamplePredicate(usuario.getUsername()));
//		mapa.remove(usuario);
		listaUsuarios.add(usuario);
		
		System.err.println("[" + this.getClass().getName() + "." + new Object(){}.getClass().getEnclosingMethod().getName() + "] "
				+ " Longitude = " + usuario.getLon() + " Latitude = " + usuario.getLat());

		System.err.println(" Longitude = " + usuario.getLon() + " Latitude = " + usuario.getLat());

//		mapa.incluiUsuario(usuario);
		
		HashSet<Integer> grupos = new HashSet<Integer>(2, 1);

		UUID uuid = nodeMessage.getSenderId();
		// procura as regiões onde o usuário pode estar
		for(Regiao regiao : listaRegioes)
		{
			if (regiao.contem(usuario)) {
				grupos.add(regiao.getNumero());
				System.err.println("[" + this.getClass().getName() + "." + new Object(){}.getClass().getEnclosingMethod().getName() + "] "
						+ " Usuário " + formataUUID(uuid.getMostSignificantBits(), uuid.getLeastSignificantBits()) + " dentro da região " + regiao.getNumero());
			}
		}

		System.out.println("[" + this.getClass().getName() + "." + new Object(){}.getClass().getEnclosingMethod().getName() + "]"
					+ " ENDED CLASSIFYING GROUP MESSAGE\n");
		
		// informa a que grupo o usuário pertence agora
		InetSocketAddress address = new InetSocketAddress(GATEWAY_IP, GATEWAY_PORT);
		UUID senderUUID = UUID.randomUUID();
		try
		{
			MrUdpNodeConnection connection = new MrUdpNodeConnection(senderUUID);
			connection.addNodeConnectionListener(this);
			connection.connect(address);
			ApplicationMessage message = new ApplicationMessage();
			message.setContentObject(grupos);
			message.setRecipientID(uuid);
			connection.sendMessage(message);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		return grupos;
	}

	/**
	 * <p>Formata o UUID</p>
	 * <ul>
	 *  <li>XXXXXXXX: bits 127-96 (32 bits)</li>
	 *  <li>XXXX: bits 95-80 (16 bits)</li>
	 *  <li>XXXX: bits 79-64 (16 bits)</li>
	 *  <li>XXXX: bits 63-48 (16 bits)</li>
	 *  <li>XXXXXXXXXXXX: bits 47-0 (48 bits)</li>
	 * </ul>
	 * 
	 * @param mostSignificantBits
	 * @param leastSignificantBits
	 * @return UUID no formato XXXXXXXX-XXXX-XXXX-XXXX-XXXXXXXXXXXX
	 */
	private String formataUUID(long mostSignificantBits, long leastSignificantBits)
	{
		String resultado = "";
		resultado += Integer.toHexString((int)(mostSignificantBits>>(96-64)));				// 127-96
		resultado += "-";
		resultado += Integer.toHexString((int)((mostSignificantBits>>(80-64)) & 0xFFFF));	// 95-80
		resultado += "-";
		resultado += Integer.toHexString((int)(mostSignificantBits & 0xFFFF));				// 95-80
		resultado += "-";
		resultado += Integer.toHexString((int)((leastSignificantBits>>48) & 0xFFFF));					// 63-48
		resultado += "-";
		resultado += Integer.toHexString((int)((leastSignificantBits>>32) & 0xFFFF));		// 47-32
		resultado += Integer.toHexString((int)(leastSignificantBits & 0xFFFFFFFF));			// 31-0
		return resultado;
	}

	@Override
	public void createGroup(GroupRegion arg0) {}
	@Override
	public void connected(NodeConnection arg0) {}
	@Override
	public void disconnected(NodeConnection arg0) {}
	@Override
	public void internalException(NodeConnection arg0, Exception arg1) {}
	@Override
	public void newMessageReceived(NodeConnection arg0, lac.cnclib.sddl.message.Message arg1) {}
	@Override
	public void reconnected(NodeConnection arg0, SocketAddress arg1, boolean arg2, boolean arg3) {}
	@Override
	public void unsentMessages(NodeConnection arg0, List<lac.cnclib.sddl.message.Message> arg1) {}
}
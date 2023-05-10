package br.com.meslin.auxiliar;

import org.openstreetmap.gui.jmapviewer.Coordinate;

/**
 * Define onde e quando o cliente estará.
 * <p>
 * O caminho é composto pelo tempo que o cliente ficará nas coordenadas e pelas coordenadas x e y.
 * 
 * @author meslin
 *
 */
public class Posicao
{
	private int duracao;
	private Coordinate coordenadas;
	
	/**
	 * Cria um lugar
	 */
	public Posicao(int duracao, double lat, double lon)
	{
		super();
		this.coordenadas = new Coordinate(lat, lon);
		this.duracao = duracao;
	}
	/**
	 * retorna o tempo que o usuário irá ficar nesse lugar
	 * @return tempo em milissengundos
	 */
	public int getDuracao() { return duracao; }
	/**
	 * Configura o tempo que o usuário irá ficar nesse lugar
	 * @param duracao em milissegundos
	 */
	public void setDuracao(int duracao) { this.duracao = duracao; }
	/**
	 * Retorna as coordenadas geográficas do usuário
	 * @return coordenadas
	 */
	public Coordinate getCoordenadas() { return coordenadas; }
	/**
	 * Coloca o usuário em determinada coordenada geográfica
	 * @param coordenadas
	 */
	public void setCoordenadas(Coordinate coordenadas) { this.coordenadas = coordenadas; }
}

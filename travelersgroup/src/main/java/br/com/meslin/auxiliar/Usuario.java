package br.com.meslin.auxiliar;

import java.io.Serializable;

//import org.openstreetmap.gui.jmapviewer.Coordinate;

/**
 * Representa um usuario com nome e posição.
 * <p>
 * Classe serializável (ao contrário do que acontece com a classe Coordinator).
 * 
 * @author meslin
 *
 */
public class Usuario implements Serializable
{
	private String username;
	private double lat, lon;
	private static final long serialVersionUID = 1L;
	
	/**
	 * Constrói um usuário a partir de seu nome e suas coordenadas geográficas
	 * 
	 * @param username nome do usuário
	 * @param coordenadas coordenadas geográficas do usuario
	 */
//	public Usuario(String username, Coordinate coordenadas)
//	{
//		this(coordenadas);
//		this.username = username;
//	}
	/**
	 * Constrói um usuário a partir das suas coordenadas geográficas
	 * 
	 * @param lat
	 * @param lon
	 */
	public Usuario(String username, double lat, double lon)
	{
		this.lat = lat;
		this.lon = lon;
	}
	/**
	 * Constrói um usuário a partir das suas coordenadas geográficas
	 * 
	 * @param coordenadas
	 */
//	public Usuario(Coordinate coordenadas)
//	{
//		this.lat = coordenadas.getLat();
//		this.lon = coordenadas.getLon();
//	}
	/**
	 * Constrói um usuário
	 * 
	 */
	public Usuario()
	{
	}

	/**
	 * Retorna a latitude
	 * @return latitude
	 */
	public double getLat()
	{
		return lat;
	}
	/**
	 * Estabelece a latitude
	 * @param lat latitude
	 */
	public void setLat(double lat)
	{
		this.lat = lat;
	}
	/**
	 * Retorna a longitude
	 * @return longitude
	 */
	public double getLon()
	{
		return lon;
	}
	/**
	 * Estabelece a longitude
	 * @param lon longitude
	 */
	public void setLon(double lon)
	{
		this.lon = lon;
	}
	/**
	 * Retorna o username
	 * @return username
	 */
	public String getUsername()
	{
		return username;
	}
	/** 
	 * Estabelece o username
	 * @param username
	 */
	public void setUsername(String username)
	{
		this.username = username;
	}
}

package br.com.meslin.auxiliar;

import java.util.ArrayList;
import java.util.List;

import org.openstreetmap.gui.jmapviewer.Coordinate;
import org.example.Model.Usuario;

/**
 * Define uma região e métodos de acesso e verificação.
 * <p>
 * A região pode ser concava ou convexa
 * 
 * @author meslin
 *
 */
public class Regiao
{
	private List<Coordinate> pontos;
	private int numero;

	/**
	 * Constroi uma região vazia
	 */
	public Regiao()
	{
		super();
		pontos = new ArrayList<Coordinate>();
	}
	
	public void setNumero(int numero) { this.numero = numero; }
	public int getNumero() { return this.numero; }

	/**
	 * Adiciona um ponto à região
	 * 
	 * @param ponto
	 */
	public void add(Coordinate ponto)
	{
		this.pontos.add(ponto);
	}
	public List<Coordinate> getPontos()
	{
		return this.pontos;
	}
	
	/**
	 * Verifica se o ponto pertence a região
	 * 
	 * @param coordenadas
	 * @return verdadeiro se o ponto pertencer à região
	 */
	public boolean contem(Coordinate coordenadas)
	{
		boolean resultado = false;
		
		/*
		 * para todo segmento de reta da região cuja reta cruza a linha y do ponto,
		 * se a soma dos pontos x menores do que a posição x do ponto for ímpar,
		 * o ponto estará dentro da região
		 */
		for(int i=0, j=this.pontos.size()-1; i<this.pontos.size(); j=i++)
		{
			if(((this.pontos.get(i).getLat() > coordenadas.getLat()) != (this.pontos.get(j).getLat() > coordenadas.getLat()))
			&& (coordenadas.getLon() < ((this.pontos.get(j).getLon()-this.pontos.get(i).getLon()) * (coordenadas.getLat()-this.pontos.get(i).getLat()) / (this.pontos.get(j).getLat()-this.pontos.get(i).getLat()) + this.pontos.get(i).getLon())))
				resultado = !resultado;
		}
		return resultado;
	}

	public boolean contem(Usuario coordenadas)
	{
		return contem(new Coordinate(coordenadas.getLat(), coordenadas.getLon()));
	}
}

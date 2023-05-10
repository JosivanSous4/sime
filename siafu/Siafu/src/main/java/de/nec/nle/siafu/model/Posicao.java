package de.nec.nle.siafu.model;
import org.openstreetmap.gui.jmapviewer.Coordinate;

public class Posicao {
//    private int duracao;
    private Coordinate coordenadas;

    /**
     * Cria um lugar
     */
    public Posicao(double lat, double lon)
    {
//        super();
        this.coordenadas = new Coordinate(lat, lon);
//        this.duracao = duracao;
    }
    /**
     * retorna o tempo que o usuário irá ficar nesse lugar
     * @return tempo em milissengundos
     */
//    public int getDuracao() { return duracao; }
    /**
     * Configura o tempo que o usuário irá ficar nesse lugar
     * @param duracao em milissegundos
     */
//    public void setDuracao(int duracao) { this.duracao = duracao; }
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

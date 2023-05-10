package br.com.meslin.mapa;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.openstreetmap.gui.jmapviewer.Coordinate;
import org.openstreetmap.gui.jmapviewer.JMapViewer;
import org.openstreetmap.gui.jmapviewer.JMapViewerTree;
import org.openstreetmap.gui.jmapviewer.MapMarkerDot;
import org.openstreetmap.gui.jmapviewer.MapPolygonImpl;
import org.openstreetmap.gui.jmapviewer.OsmTileLoader;
import org.openstreetmap.gui.jmapviewer.events.JMVCommandEvent;
import org.openstreetmap.gui.jmapviewer.interfaces.JMapViewerEventListener;
import org.openstreetmap.gui.jmapviewer.interfaces.MapPolygon;
import org.openstreetmap.gui.jmapviewer.interfaces.TileLoader;
import org.openstreetmap.gui.jmapviewer.interfaces.TileSource;
import org.openstreetmap.gui.jmapviewer.tilesources.BingAerialTileSource;
import org.openstreetmap.gui.jmapviewer.tilesources.OsmTileSource;

import org.example.Model.Usuario;
import br.com.meslin.auxiliar.Regiao;

/**
 * Desenha o mapa com regiões e usuários
 * 
 * @author meslin
 *
 */
@SuppressWarnings("serial")
public class Mapa extends JFrame implements JMapViewerEventListener
{
	private final JMapViewerTree treeMap;
	private JLabel metrosPorPixelLabel;
	private JLabel metrosPorPixelValor;
	private JLabel zoomLabel;
	private JLabel zoomValor;
	
	private List<MapMarkerDot> pontosNoMapa;

	/**
	 * Constroi o {@code Demo}.
	 * @param listaRegioes 
	 */
	public Mapa(List<Regiao> listaRegioes) {
		super("Mapa das Regiões");
		pontosNoMapa = new ArrayList<MapMarkerDot>();	// inicialmente não existem pontos no mapa
		
        setSize(400, 400);
		
		treeMap = new JMapViewerTree("Regiões");
		
		map().addJMVListener(this);
		
		setLayout(new BorderLayout());
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		JPanel panel = new JPanel(new BorderLayout());
		JPanel panelTop = new JPanel();
		JPanel panelBottom = new JPanel();
		JPanel helpPanel = new JPanel();
		
		metrosPorPixelLabel = new JLabel("Metros/Pixels:");
		metrosPorPixelValor = new JLabel(String.format("%s", map().getMeterPerPixel()));
		
        zoomLabel = new JLabel("Zoom: ");
        zoomValor = new JLabel(String.format("%s", map().getZoom()));
		
        add(panel, BorderLayout.NORTH);
        add(helpPanel, BorderLayout.SOUTH);
        panel.add(panelTop, BorderLayout.NORTH);
        panel.add(panelBottom, BorderLayout.SOUTH);

        JLabel helpLabel = new JLabel("Use o botão direito do mouse para mover, duplo clique ou a roda do mouse para zoom. © OpenStreetMap contributors");
        helpPanel.add(helpLabel);

        JButton botaoMarcas = new JButton("Marcas");
        botaoMarcas.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                map().setDisplayToFitMapMarkers();
            }
        });
        JButton botaoRegioes = new JButton("Regiões");
        botaoRegioes.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                map().setDisplayToFitMapPolygons();
            }
        });
        JComboBox<TileSource> tileSourceSelector = new JComboBox<>(new TileSource[] {
                new OsmTileSource.Mapnik(),
//                new OsmTileSource.CycleMap(),
                new BingAerialTileSource()
        });
        tileSourceSelector.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                map().setTileSource((TileSource) e.getItem());
            }
        });

        JComboBox<TileLoader> tileLoaderSelector;
        tileLoaderSelector = new JComboBox<>(new TileLoader[] {
        		new OsmTileLoader(map())
        });
        tileLoaderSelector.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                map().setTileLoader((TileLoader) e.getItem());
            }
        });
        map().setTileLoader((TileLoader) tileLoaderSelector.getSelectedItem());
        panelTop.add(tileSourceSelector);
        panelTop.add(tileLoaderSelector);

        final JCheckBox showTileGrid = new JCheckBox("Grid visível");
        showTileGrid.setSelected(map().isTileGridVisible());
        showTileGrid.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                map().setTileGridVisible(showTileGrid.isSelected());
            }
        });
        panelBottom.add(showTileGrid);
        final JCheckBox showZoomControls = new JCheckBox("Controles de zoom visíveis");
        showZoomControls.setSelected(map().getZoomControlsVisible());
        showZoomControls.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                map().setZoomContolsVisible(showZoomControls.isSelected());
            }
        });
        panelBottom.add(showZoomControls);
        final JCheckBox scrollWrapEnabled = new JCheckBox("Scrollwrap enabled");
        scrollWrapEnabled.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                map().setScrollWrapEnabled(scrollWrapEnabled.isSelected());
            }
        });
        panelBottom.add(scrollWrapEnabled);
        panelBottom.add(botaoMarcas);
        panelBottom.add(botaoRegioes);

        panelTop.add(zoomLabel);
        panelTop.add(zoomValor);
        panelTop.add(metrosPorPixelLabel);
        panelTop.add(metrosPorPixelValor);

        add(treeMap, BorderLayout.CENTER);
        
        // adiciona as regiões
        for(Regiao regiao : listaRegioes) {
	        MapPolygon poligono = new MapPolygonImpl(regiao.getPontos());
	        map().addMapPolygon(poligono);
        }

        map().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    map().getAttribution().handleAttribution(e.getPoint(), true);
                }
            }
        });

        map().addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                Point p = e.getPoint();
                boolean cursorHand = map().getAttribution().handleAttributionCursor(p);
                if (cursorHand) {
                    map().setCursor(new Cursor(Cursor.HAND_CURSOR));
                } else {
                    map().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                }
            }
        });        
	}

    @Override
	public void processCommand(JMVCommandEvent command)
	{
        if (command.getCommand().equals(JMVCommandEvent.COMMAND.ZOOM) ||
                command.getCommand().equals(JMVCommandEvent.COMMAND.MOVE)) {
            updateZoomParameters();
        }
	}

    private JMapViewer map() {
        return treeMap.getViewer();
    }
    
    private void updateZoomParameters() {
        if (metrosPorPixelValor != null)
            metrosPorPixelValor.setText(String.format("%s", map().getMeterPerPixel()));
        if (zoomValor != null)
            zoomValor.setText(String.format("%s", map().getZoom()));
    }
    
    /**
     * inclui um usuário em uma coordenada geográfica
     */
    public void incluiUsuario(String username, Coordinate coordenadas)
    {
    	MapMarkerDot avatar = new MapMarkerDot(coordenadas);
    	avatar.setName(username);
    	map().addMapMarker(avatar);
    	pontosNoMapa.add(avatar);
    }

	public void incluiUsuario(Usuario usuario)
	{
		incluiUsuario(usuario.getUsername(), new Coordinate(usuario.getLat(), usuario.getLon()));
	}

	public void remove(Usuario usuario)
	{
		for(int i=0; i<pontosNoMapa.size(); i++)
			if(pontosNoMapa.get(i).getName().equals(usuario.getUsername()))
					map().removeMapMarker(pontosNoMapa.get(i));
	}
}

/**
 * OrthogonalDisplayController.java
 * 26/07/2011 16:46:50
 */
package pfc.controllers.algorithms;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.LayoutStyle;
import javax.swing.border.BevelBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeListener;

import pfc.models.Graph;
import pfc.models.algorithms.orthogonal.OrthogonalAlgorithm;
import pfc.models.algorithms.orthogonal.OrthogonalAlgorithmHistory;
import pfc.models.algorithms.visibilityrepresentation.VisibilityRepresentationDrawing;
import pfc.resources.StringBundle;
import pfc.settings.GraphSettings;
import pfc.settings.UserSettings;
import pfc.utilities.GeometryUtilities;
import pfc.utilities.SnapshotList;
import pfc.utilities.StateSupport;
import pfc.utilities.TransformUtilities;
import pfc.views.display.GraphDisplayView;
import pfc.views.display.algorithms.visibilityrepresentation.VisRepDrawingDisplayView;

/**
 * @author    Walber González
 */
@SuppressWarnings("serial")
public class OrthogonalDisplayController extends JPanel implements ExecutionControlPanelActions
{
	/**
	 * Clase para el componente del grafo dirigido y con paths seleccionados
	 */
	private class ViewportDigraph extends JComponent
	{
		private GraphSettings 		settings;
		private AffineTransform 	transform;
		private boolean				mouseWheelMoved;
		
		public ViewportDigraph ( )
		{
			super( );
			
			/* Crear la configuracion de mostrar el grafo */
			this.settings = new GraphSettings( );
			this.settings.showVertexLabels.set( true );
			
			/* Inicializar la transformacion del grafo */
			this.transform = new AffineTransform( );
			
			this.mouseWheelMoved = false;
			
			/* listener de la rueda del raton para hacer zoom */
			this.addMouseWheelListener( new MouseWheelListener( )
			{
				public void mouseWheelMoved( MouseWheelEvent e )
				{
					TransformUtilities.zoomCenter( ViewportDigraph.this.transform,
							new Point2D.Double( e.getX( ), e.getY( ) ),
							1 - e.getWheelRotation( ) * UserSettings.instance.scrollIncrementZoom.get( ) );
					ViewportDigraph.this.mouseWheelMoved = true;
					ViewportDigraph.this.update( );
				}
			} );
		}
		
		@Override
		public void paintComponent ( Graphics g )
		{			
			Graphics2D g2D = (Graphics2D) g;
			
			// Apply rendering settings
			if( UserSettings.instance.useAntiAliasing.get( ) )
				g2D.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
			
			if( UserSettings.instance.usePureStroke.get( ) )
				g2D.setRenderingHint( RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE );
			
			if( UserSettings.instance.useBicubicInterpolation.get( ) )
				g2D.setRenderingHint( RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC );
			
			if( UserSettings.instance.useFractionalMetrics.get( ) )
				g2D.setRenderingHint( RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON );
			
			// Clear everything
			super.paintComponent( g2D );
			
			// Center the graph			
			if ( this.mouseWheelMoved == false )
				TransformUtilities.zoomFit( this.transform, GeometryUtilities.getBounds( digraph ), this.getBounds( ) );
			else
				this.mouseWheelMoved = false;
			
			// Apply the transformation
			AffineTransform original = g2D.getTransform( );
			original.concatenate( this.transform );
			g2D.setTransform( original );
			
			// Paint the graph
			GraphDisplayView.paint( g2D, digraph, this.settings );
		}
		
		public void update ( )
		{
			this.validate( );
			this.repaint( );
		}
	}
	
	/** Clase para el componente de Orthgonal Drawing */
	private class ViewportOrthogonal extends JComponent
	{
		private GraphSettings 		settings;
		private AffineTransform 	transform;
		private boolean				mouseWheelMoved;
		
		public ViewportOrthogonal( )
		{
			super( );
			
			/* Crear la configuracion de mostrar el grafo */
			this.settings = new GraphSettings( );
			this.settings.showVertexLabels.set( true );
			
			/* Inicializar la transformacion del grafo */
			this.transform = new AffineTransform( );
			
			this.mouseWheelMoved = false;
			
			/* listener de la rueda del raton para hacer zoom */
			this.addMouseWheelListener( new MouseWheelListener( )
			{
				public void mouseWheelMoved( MouseWheelEvent e )
				{
					TransformUtilities.zoomCenter( ViewportOrthogonal.this.transform,
							new Point2D.Double( e.getX( ), e.getY( ) ),
							1 - e.getWheelRotation( ) * UserSettings.instance.scrollIncrementZoom.get( ) );
					ViewportOrthogonal.this.mouseWheelMoved = true;
					ViewportOrthogonal.this.update( );
				}
			} );
		}
		
		@Override
		public void paintComponent ( Graphics g )
		{
			Graphics2D g2D = (Graphics2D) g;
			
			// Apply rendering settings
			if( UserSettings.instance.useAntiAliasing.get( ) )
				g2D.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
			
			if( UserSettings.instance.usePureStroke.get( ) )
				g2D.setRenderingHint( RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE );
			
			if( UserSettings.instance.useBicubicInterpolation.get( ) )
				g2D.setRenderingHint( RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC );
			
			if( UserSettings.instance.useFractionalMetrics.get( ) )
				g2D.setRenderingHint( RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON );
			
			// Clear everything
			super.paintComponent( g2D );
			
			// Center the graph
			Rectangle2D graphBounds = new Rectangle2D.Double( );
			if ( this.isDrawableDrawing( ) && this.isDrawableOrthogonal( ) )
				Rectangle2D.union( VisRepDrawingDisplayView.getBounds( drawing ),
						GeometryUtilities.getBounds( ortogonal ),
						graphBounds );
			else if ( this.isDrawableDrawing( ) )
				graphBounds = VisRepDrawingDisplayView.getBounds( drawing );
			else if ( this.isDrawableOrthogonal( ) )
				graphBounds = GeometryUtilities.getBounds( ortogonal );
			
			if ( this.mouseWheelMoved == false )
				TransformUtilities.zoomFit( this.transform, graphBounds, this.getBounds( ) );
			else
				this.mouseWheelMoved = false;
			
			// Apply the transformation
			AffineTransform original = g2D.getTransform( );
			original.concatenate( this.transform );
			g2D.setTransform( original );
			
			if ( this.isDrawableDrawing( ) && this.isDrawableOrthogonal( ) )
			{
				VisRepDrawingDisplayView.paintDrawingBackgroundWithoutAxes( g2D, drawing );
				GraphDisplayView.paint( g2D, ortogonal, this.settings );
			}
			else if ( this.isDrawableDrawing( ) )
				VisRepDrawingDisplayView.paintDrawingBackground( g2D, drawing );
		}
		
		public void update ( )
		{
			this.validate( );
			this.repaint( );
		}
		
		private boolean isDrawableDrawing ( )
		{
			return drawing != null && drawing.vertexSegments.isEmpty( ) == false;
		}
		
		private boolean isDrawableOrthogonal ( )
		{
			return ortogonal != null && ortogonal.vertices.isEmpty( ) == false;
		}
		
	}

	/**
	 * grafo dirigido y con paths del algoritmo
	 */
	private Graph										digraph;
	/**
	 * drawing resultante de la ejecucion del algoritmo Contrained Visibility
	 */
	private VisibilityRepresentationDrawing				drawing;
	/**
	 * grafo del algoritmo orthogonal
	 */
	private Graph										ortogonal;
	/**
	 * ejecucion del algoritmo
	 */
	private OrthogonalAlgorithm							algorithm;
	/** Layout del panel de ejecucion del algoritmo */
	private GroupLayout									orthogonalDCLayout;
	/**
	 * atributos para panel de control de la ejecucion
	 */
	private ExecutionControlPanel 						exeStepByStepPanel;
	/** atributos del panel del grafo dirigido */
	private JPanel 										viewportDigraphPanel;
	private ViewportDigraph								viewportDigraph;
	/** atributos del panel de orthogonal */
	private JPanel 										viewportOrthogonalPanel;
	private ViewportOrthogonal							viewportOrthogonal;
	/** listener del evento cerrar el jinternalframe */
	private StateSupport								exeCancelEventListener;
	/** lista de cambios en el algoritmo */
	private SnapshotList<OrthogonalAlgorithmHistory>	history;
	/** inicio de la ejecucion, util para poner a inicio el historial */
	private boolean										started;
	
	/**
	 * @param graph Graph
	 */
	public OrthogonalDisplayController( Graph graph )
	{				
		this.digraph = graph;
		
		this.algorithm = new OrthogonalAlgorithm( graph );
		this.algorithm.addObserver( new Observer( )
		{
			@Override
			public void update(Observable o, Object arg)
			{
				OrthogonalDisplayController.this.onAlgorithmChanged( arg );
			}
		} );
				
		/* Agregar al panel principal el panel de control de ejecucion */
		this.exeStepByStepPanel = new ExecutionControlPanel( this );
		
		/* Lista de Listener de Eventos en el Panel */
		this.exeCancelEventListener = new StateSupport( );
		
		/* Agregar al panel principal el panel del digraph */
		this.setViewportDigraphPanel ( );
		
		/* Agregar al panel principal el panel de orthogonal drawing */
		this.setViewportOrthogonalPanel ( );
		
		/* Caracteristicas del Panel principal */
		this.organizeLayout( );

		/* Inicializar el historial de ejecucion del algoritmo */
		this.setHistory( );
		
		/* No se ha iniciado el algoritmo aun */
		this.setStarted( false );
		
		/* Ejecutar el algoritmo completamente guardando los datos
		 * de cada paso en el historial */
		this.algorithm.executeAlgorithm( );		
	}

	/**
	 * Organizar todo el Layout del Panel de Ejecucion del Algoritmo Orthogonal
	 */
	private void organizeLayout ( )
	{
		this.orthogonalDCLayout = new GroupLayout( this );
        this.setLayout( this.orthogonalDCLayout );
        
        // Organizar grupo horizontal
        this.orthogonalDCLayout.setHorizontalGroup(
        		this.orthogonalDCLayout.createParallelGroup( GroupLayout.Alignment.LEADING )
        		.addGroup( GroupLayout.Alignment.TRAILING, this.orthogonalDCLayout.createSequentialGroup( )
        				.addContainerGap( )
        				.addGroup( this.orthogonalDCLayout.createParallelGroup(GroupLayout.Alignment.TRAILING )
        						.addComponent( exeStepByStepPanel, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE )
        						.addGroup( this.orthogonalDCLayout.createSequentialGroup( )
        								.addComponent( viewportDigraphPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE )
        								.addPreferredGap( LayoutStyle.ComponentPlacement.UNRELATED )
        								.addComponent( viewportOrthogonalPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE ) ) )
        								.addContainerGap( ) )
        );
        
        // Organizar grupo vertical
        this.orthogonalDCLayout.setVerticalGroup(
        		this.orthogonalDCLayout.createParallelGroup( GroupLayout.Alignment.LEADING )
        		.addGroup(this.orthogonalDCLayout.createSequentialGroup( )
        				.addComponent( exeStepByStepPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE )
        				.addPreferredGap( LayoutStyle.ComponentPlacement.UNRELATED )
        				.addGroup( this.orthogonalDCLayout.createParallelGroup( GroupLayout.Alignment.TRAILING )
        						.addComponent( viewportOrthogonalPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE )
        						.addComponent( viewportDigraphPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE ) )
        						.addContainerGap( ) )
        );
        
	}
		
	private void setViewportDigraphPanel ( )
	{
		this.viewportDigraphPanel = new JPanel( new BorderLayout( ) )
		{
			{
				TitledBorder title = BorderFactory.createTitledBorder( new BevelBorder( BevelBorder.LOWERED ) );
				title.setTitle( StringBundle.get( "orthogonal_digraph_title" ) );
				title.setTitleJustification( TitledBorder.CENTER );
				this.setBorder( title );
			}
		};
		this.viewportDigraphPanel.setBackground( UserSettings.instance.graphBackground.get() );
		this.viewportDigraphPanel.setOpaque( true );
		
		this.viewportDigraph = new ViewportDigraph( );
		this.viewportDigraphPanel.add( viewportDigraph, BorderLayout.CENTER );
	}
	
	private void setViewportOrthogonalPanel ( )
	{
		this.viewportOrthogonalPanel = new JPanel( new BorderLayout( ) )
		{
			{
				TitledBorder title = BorderFactory.createTitledBorder( new BevelBorder( BevelBorder.LOWERED ) );
				title.setTitle( StringBundle.get( "orthogonal_representation_diagram_title" ) );
				title.setTitleJustification( TitledBorder.CENTER );
				this.setBorder( title );
			}
		};
		this.viewportOrthogonalPanel.setBackground( UserSettings.instance.graphBackground.get() );
		this.viewportOrthogonalPanel.setOpaque( true );
		
		this.viewportOrthogonal = new ViewportOrthogonal( );
		this.viewportOrthogonalPanel.add( viewportOrthogonal, BorderLayout.CENTER );
	}
	
	/**
	 * Inicializar el historial de ejecucion del algoritmo.
	 */
	private void setHistory ( )
	{
		OrthogonalAlgorithmHistory temp = new OrthogonalAlgorithmHistory( this.digraph.toString( ), null, null, "");
		this.history = new SnapshotList<OrthogonalAlgorithmHistory>( temp );
		this.history.setCapacity( Short.MAX_VALUE );
	}
	
	/**
	 * @see pfc.controllers.algorithms.ExecutionControlPanelActions#executeNextStep()
	 */
	@Override
	public String executeNextStep( )
	{
		StringBuffer explanation = new StringBuffer( );

		if ( isStarted( ) == false )
		{
			this.history.setBeginning( );
			this.setStarted( true );
		}
		
		if ( this.history.next( ) != null )
		{
			explanation.append( this.updateExecutionStep( ) );
			this.update( );
		}
		return explanation.toString( );
	}

	/**
	 * @see pfc.controllers.algorithms.ExecutionControlPanelActions#executeBackStep()
	 */
	@Override
	public String executeBackStep( )
	{
		StringBuffer explanation = new StringBuffer( );
		
		if ( isStarted( ) == false )
		{
			this.history.setBeginning( );
			this.setStarted( true );
		}
		
		if ( this.history.previous( ) != null )
		{
			explanation.append( this.updateExecutionStep( ) );
			this.update( );
		}
		return explanation.toString( );
	}

	/**
	 * @see pfc.controllers.algorithms.ExecutionControlPanelActions#executeEndStep()
	 */
	@Override
	public String executeEndStep( )
	{
		StringBuffer explanation = new StringBuffer( );
		String current, old;

		if ( isStarted( ) == false )
		{
			this.history.setBeginning( );
			this.setStarted( true );
		}
		
		old = this.history.current( ).getExplanation( );
		current = "";
		while ( this.history.next( ) != null )
		{
			current = this.updateExecutionStep( );
			
			// si la explicacion actual es distinta de la ultima que se puso
			if ( current != null && current.equals( old ) == false )
			{
				explanation.append( current );
				explanation.append( "\n" );
				old = current;
			}
		}
		this.update( );
		return explanation.toString( );
	}

	/**
	 * @see pfc.controllers.algorithms.ExecutionControlPanelActions#executeCancelStep()
	 */
	@Override
	public String executeCancelStep( )
	{
		this.exeCancelEventListener.fireStateChangedEvent( );
		return "";
	}

	/**
	 * Cambia el algoritmo. Actualizar el historial
	 * @param source objeto que ha cambiado
	 */
	private void onAlgorithmChanged ( Object source )
	{
		String digraph = this.algorithm.getDigraph( ).toString( );
		String drawing = ( this.algorithm.getVisibilityDrawing( ) != null )
				? this.algorithm.getVisibilityDrawing( ).toString( ) : null;
		String orthogonal = this.algorithm.getOrthogonal( ).toString( );
		String explanation = this.algorithm.getAlgorithmExplanation( );
		
		this.history.add( new OrthogonalAlgorithmHistory( digraph, drawing, orthogonal, explanation ) );	
	}
	
	/**
	 * Actualizar los graficos de todos los paneles
	 */
	private void update ( )
	{
		this.viewportDigraph.update( );
		this.viewportOrthogonal.update( );
	}
	
	/**
	 * Actualizar los datos del controlador despues de que el algoritmo haya cambiado. 
	 * @return String explicacion del la fase actual.
	 */
	private String updateExecutionStep ( )
	{
		OrthogonalAlgorithmHistory step = this.history.current( );
		
		this.setDigraph( step.getDigraph( ) != null ? new Graph( step.getDigraph( ) ) : null );
		this.setDrawing( step.getVisibilityDrawing( ) != null ? new VisibilityRepresentationDrawing( step.getVisibilityDrawing( ) ) : null );
		this.setOrtogonal( step.getOrthogonalGraph( ) != null ? new Graph( step.getOrthogonalGraph( ) ) : null );

		return step.getExplanation( );
	}

	/**
	 * @return the digraph
	 */
	public Graph getDigraph( )
	{
		return this.digraph;
	}

	/**
	 * @param digraph the digraph to set
	 */
	public void setDigraph( Graph digraph )
	{
		this.digraph = digraph;
	}

	/**
	 * @return the drawing
	 */
	public VisibilityRepresentationDrawing getDrawing( )
	{
		return this.drawing;
	}

	/**
	 * @param drawing the drawing to set
	 */
	public void setDrawing( VisibilityRepresentationDrawing drawing )
	{
		this.drawing = drawing;
	}

	/**
	 * @return the ortogonal
	 */
	public Graph getOrtogonal( )
	{
		return this.ortogonal;
	}

	/**
	 * @param ortogonal the ortogonal to set
	 */
	public void setOrtogonal( Graph ortogonal )
	{
		this.ortogonal = ortogonal;
	}

	/**
	 * @return the started
	 */
	public boolean isStarted( )
	{
		return this.started;
	}

	/**
	 * @param started the started to set
	 */
	public void setStarted( boolean started )
	{
		this.started = started;
	}

	/**
	 * @param listener JInternalFrame que esta escuchando el evento
	 */
	public void addExeCancelEventListener ( ChangeListener listener )
	{
		this.exeCancelEventListener.addStateChangeListener( listener);
	}

}


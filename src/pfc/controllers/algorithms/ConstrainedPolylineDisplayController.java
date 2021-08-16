/**
 * ConstrainedPolylineDisplayController.java
 * 24/07/2011 02:56:51
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
import pfc.models.algorithms.NonIntersectingPathList;
import pfc.models.algorithms.constrainedpolyline.ConstrainedPolylineAlgorithm;
import pfc.models.algorithms.polyline.UpwardPolylineAlgorithmHistory;
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
public class ConstrainedPolylineDisplayController extends JPanel implements ExecutionControlPanelActions
{
	/**
	 * Clase para el componente del grafo upward polyline
	 */
	private class ViewportPolyline extends JComponent
	{
		private GraphSettings 		settings;
		private AffineTransform 	transform;
		private boolean				mouseWheelMoved;
		
		public ViewportPolyline ( )
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
					TransformUtilities.zoomCenter( ViewportPolyline.this.transform,
							new Point2D.Double( e.getX( ), e.getY( ) ),
							1 - e.getWheelRotation( ) * UserSettings.instance.scrollIncrementZoom.get( ) );
					ViewportPolyline.this.mouseWheelMoved = true;
					ViewportPolyline.this.update( );
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
			if ( this.isDrawableDrawing( ) && this.isDrawablePolyline( ) )
				Rectangle2D.union( VisRepDrawingDisplayView.getBounds( drawing ),
						GeometryUtilities.getBounds( polyline ),
						graphBounds );
			else if ( this.isDrawableDrawing( ) )
				graphBounds = VisRepDrawingDisplayView.getBounds( drawing );
			else if ( this.isDrawablePolyline( ) )
				graphBounds = GeometryUtilities.getBounds( polyline );
			
			if ( this.mouseWheelMoved == false )
				TransformUtilities.zoomFit( this.transform, graphBounds, this.getBounds( ) );
			else
				this.mouseWheelMoved = false;
			
			// Apply the transformation
			AffineTransform original = g2D.getTransform( );
			original.concatenate( this.transform );
			g2D.setTransform( original );
			
			// Paint the graph
			if ( this.isDrawableDrawing( ) )
				VisRepDrawingDisplayView.paintDrawingBackground( g2D, drawing );
			if ( this.isDrawablePolyline( ) )
				GraphDisplayView.paint( g2D, polyline, this.settings );
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
		
		private boolean isDrawablePolyline ( )
		{
			return polyline != null && polyline.vertices.isEmpty( ) == false;
		}
	}
	
	/**
	 * grafo polyline del algoritmo
	 */
	private Graph											polyline;
	/**
	 * drawing representacion de visibilidad
	 */
	private VisibilityRepresentationDrawing					drawing;
	/**
	 * ejecucion del algoritmo
	 */
	private ConstrainedPolylineAlgorithm					algorithm;
	/** Layout del panel de ejecucion del algoritmo */
	private GroupLayout										polylineDCLayout;
	/**
	 * atributos para panel de control de la ejecucion
	 */
	private ExecutionControlPanel 							exeStepByStepPanel;
	/** atributos del panel del grafo polyline */
	private JPanel 											viewportPolylinePanel;
	private ViewportPolyline								viewportPolyline;
	/** listener del evento cerrar el jinternalframe */
	private StateSupport									exeCancelEventListener;
	/** lista de cambios en el algoritmo */
	private SnapshotList<UpwardPolylineAlgorithmHistory>	history;
	/** inicio de la ejecucion, util para poner a inicio el historial */
	private boolean											started;
	
	/**
	 * @param graph Graph
	 */
	public ConstrainedPolylineDisplayController( Graph graph, NonIntersectingPathList paths )
	{				
		this.algorithm = new ConstrainedPolylineAlgorithm( new Graph( graph.toString( ) ), paths );
		this.algorithm.addObserver( new Observer( )
		{
			@Override
			public void update(Observable o, Object arg)
			{
				ConstrainedPolylineDisplayController.this.onAlgorithmChanged( arg );
			}
		} );
				
		/* Agregar al panel principal el panel de control de ejecucion */
		this.exeStepByStepPanel = new ExecutionControlPanel( this );
		
		/* Lista de Listener de Eventos en el Panel */
		this.exeCancelEventListener = new StateSupport( );
		
		/* Agregar al panel principal el panel del grafo polyline */
		this.setViewportPolylinePanel ( );
		
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
	 * Organizar todo el Layout del Panel de Ejecucion del Algoritmo
	 * Upward Polyline
	 */
	private void organizeLayout ( )
	{
		this.polylineDCLayout = new GroupLayout( this );
        this.setLayout( this.polylineDCLayout );
        
        // Organizar grupo horizontal
        this.polylineDCLayout.setHorizontalGroup(
        		this.polylineDCLayout.createParallelGroup( GroupLayout.Alignment.LEADING )
        		.addGroup( GroupLayout.Alignment.TRAILING, this.polylineDCLayout.createSequentialGroup( )
        				.addContainerGap( )
        				.addGroup( this.polylineDCLayout.createParallelGroup(GroupLayout.Alignment.TRAILING )
        						.addComponent( exeStepByStepPanel, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE )
        						.addGroup( this.polylineDCLayout.createSequentialGroup( )
        								.addPreferredGap( LayoutStyle.ComponentPlacement.UNRELATED )
        								.addComponent( viewportPolylinePanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE ) ) )
        								.addContainerGap( ) )
        );
        
        // Organizar grupo vertical
        this.polylineDCLayout.setVerticalGroup(
        		this.polylineDCLayout.createParallelGroup( GroupLayout.Alignment.LEADING )
        		.addGroup(this.polylineDCLayout.createSequentialGroup( )
        				.addComponent( exeStepByStepPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE )
        				.addPreferredGap( LayoutStyle.ComponentPlacement.UNRELATED )
        				.addGroup( this.polylineDCLayout.createParallelGroup( GroupLayout.Alignment.TRAILING )
        						.addComponent( viewportPolylinePanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE ) )
        						.addContainerGap( ) )
        );
        
	}
	
	private void setViewportPolylinePanel ( )
	{
		this.viewportPolylinePanel = new JPanel( new BorderLayout( ) )
		{
			{
				TitledBorder title = BorderFactory.createTitledBorder( new BevelBorder( BevelBorder.LOWERED ) );
				title.setTitle( StringBundle.get( "constrained_upward_polyline_graph_title" ) );
				title.setTitleJustification( TitledBorder.CENTER );
				this.setBorder( title );
			}
		};
		this.viewportPolylinePanel.setBackground( UserSettings.instance.graphBackground.get() );
		this.viewportPolylinePanel.setOpaque( true );
		
		this.viewportPolyline = new ViewportPolyline( );
		this.viewportPolylinePanel.add( viewportPolyline, BorderLayout.CENTER );
	}
	
	/**
	 * Inicializar el historial de ejecucion del algoritmo.
	 */
	private void setHistory ( )
	{
		UpwardPolylineAlgorithmHistory temp = new UpwardPolylineAlgorithmHistory( null, null, "" );
		this.history = new SnapshotList<UpwardPolylineAlgorithmHistory>( temp );
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
		String drawing = this.algorithm.getVisibilityDrawing( ).toString( );
		String polyline = this.algorithm.getPolyline( ).toString( );
		String explanation = this.algorithm.getAlgorithmExplanation( );
		
		this.history.add( new UpwardPolylineAlgorithmHistory( drawing, polyline, explanation ) );	
	}
	
	/**
	 * Actualizar los graficos de todos los paneles
	 */
	private void update ( )
	{
		this.viewportPolyline.update( );
	}
	
	/**
	 * Actualizar los datos del controlador despues de que el algoritmo haya cambiado. 
	 * @return String explicacion del la fase actual.
	 */
	private String updateExecutionStep ( )
	{
		UpwardPolylineAlgorithmHistory step = this.history.current( );
		
		this.setDrawing( step.getVisibilityDrawing( ) != null ? new VisibilityRepresentationDrawing( step.getVisibilityDrawing( ) ) : null );
		this.setPolyline( step.getPolylineGraph( ) != null ? new Graph( step.getPolylineGraph( ) ) : null );
		
		return step.getExplanation( );
	}

	/**
	 * @return    the drawing
	 */
	public VisibilityRepresentationDrawing getDrawing( )
	{
		return drawing;
	}
	
	/**
	 * @return the polyline
	 */
	public Graph getPolyline( )
	{
		return this.polyline;
	}

	/**
	 * @param drawing    the drawing to set
	 */
	public void setDrawing( VisibilityRepresentationDrawing drawing )
	{
		this.drawing = drawing;
	}
	
	/**
	 * @param polyline the polyline to set
	 */
	public void setPolyline( Graph polyline )
	{
		this.polyline = polyline;
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


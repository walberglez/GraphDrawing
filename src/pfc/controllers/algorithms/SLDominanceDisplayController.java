/**
 * SLDominanceDisplayController.java
 * 01/08/2011 04:12:55
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
import pfc.models.algorithms.sldominance.DominanceDrawing;
import pfc.models.algorithms.sldominance.SLDominanceAlgorithm;
import pfc.models.algorithms.sldominance.SLDominanceAlgorithmHistory;
import pfc.resources.StringBundle;
import pfc.settings.GraphSettings;
import pfc.settings.UserSettings;
import pfc.utilities.SnapshotList;
import pfc.utilities.StateSupport;
import pfc.utilities.TransformUtilities;
import pfc.views.display.algorithms.dominance.DominanceDrawingDisplayView;


/**
 * @author    Walber Gonz?lez
 */
@SuppressWarnings("serial")
public class SLDominanceDisplayController extends JPanel implements ExecutionControlPanelActions
{		
	/**
	 * Clase para el componente del grafo straight-line dominance
	 */
	private class ViewportDominance extends JComponent
	{
		private GraphSettings 		settings;
		private AffineTransform 	transform;
		private boolean				mouseWheelMoved;
		
		public ViewportDominance ( )
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
					TransformUtilities.zoomCenter( ViewportDominance.this.transform,
							new Point2D.Double( e.getX( ), e.getY( ) ),
							1 - e.getWheelRotation( ) * UserSettings.instance.scrollIncrementZoom.get( ) );
					ViewportDominance.this.mouseWheelMoved = true;
					ViewportDominance.this.update( );
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
			Rectangle2D drawingBounds = new Rectangle2D.Double( );
			if ( this.isDrawableDrawing( ) )
				drawingBounds = DominanceDrawingDisplayView.getBounds( drawing );
			
			if ( this.mouseWheelMoved == false )
				TransformUtilities.zoomFit( this.transform, drawingBounds, this.getBounds( ) );
			else
				this.mouseWheelMoved = false;
			
			// Apply the transformation
			AffineTransform original = g2D.getTransform( );
			original.concatenate( this.transform );
			g2D.setTransform( original );
			
			// Paint the graph
			if ( this.isDrawableDrawing( ) )
				DominanceDrawingDisplayView.paintDrawing( g2D, drawing, settings );
		}
		
		public void update ( )
		{
			this.validate( );
			this.repaint( );
		}
		
		private boolean isDrawableDrawing ( )
		{
			return drawing != null && drawing.isEmpty( ) == false;
		}

	}

	/**
	 * drawing straight-line dominance
	 */
	private DominanceDrawing							drawing;
	/**
	 * ejecucion del algoritmo
	 */
	private SLDominanceAlgorithm						algorithm;
	/** Layout del panel de ejecucion del algoritmo */
	private GroupLayout									algorithmDCLayout;
	/**
	 * atributos para panel de control de la ejecucion
	 */
	private ExecutionControlPanel 						exeStepByStepPanel;
	/** atributos del panel del grafo polyline */
	private JPanel 										viewportDominancePanel;
	private ViewportDominance							viewportDominance;
	/** listener del evento cerrar el jinternalframe */
	private StateSupport								exeCancelEventListener;
	/** lista de cambios en el algoritmo */
	private SnapshotList<SLDominanceAlgorithmHistory>	history;
	/** inicio de la ejecucion, util para poner a inicio el historial */
	private boolean										started;
	
	/**
	 * @param graph Graph
	 */
	public SLDominanceDisplayController( Graph graph )
	{				
		this.algorithm = new SLDominanceAlgorithm( new Graph( graph.toString( ) ) );
		this.algorithm.addObserver( new Observer( )
		{
			@Override
			public void update(Observable o, Object arg)
			{
				SLDominanceDisplayController.this.onAlgorithmChanged( arg );
			}
		} );
				
		/* Agregar al panel principal el panel de control de ejecucion */
		this.exeStepByStepPanel = new ExecutionControlPanel( this );
		
		/* Lista de Listener de Eventos en el Panel */
		this.exeCancelEventListener = new StateSupport( );
		
		/* Agregar al panel principal el panel del dominance drawing */
		this.setViewportDominancePanel ( );
		
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
	 * Straight-Line Dominance
	 */
	private void organizeLayout ( )
	{
		this.algorithmDCLayout = new GroupLayout( this );
        this.setLayout( this.algorithmDCLayout );
        
        // Organizar grupo horizontal
        this.algorithmDCLayout.setHorizontalGroup(
        		this.algorithmDCLayout.createParallelGroup( GroupLayout.Alignment.LEADING )
        		.addGroup( GroupLayout.Alignment.TRAILING, this.algorithmDCLayout.createSequentialGroup( )
        				.addContainerGap( )
        				.addGroup( this.algorithmDCLayout.createParallelGroup(GroupLayout.Alignment.TRAILING )
        						.addComponent( exeStepByStepPanel, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE )
        						.addGroup( this.algorithmDCLayout.createSequentialGroup( )
        								.addPreferredGap( LayoutStyle.ComponentPlacement.UNRELATED )
        								.addComponent( viewportDominancePanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE ) ) )
        								.addContainerGap( ) )
        );
        
        // Organizar grupo vertical
        this.algorithmDCLayout.setVerticalGroup(
        		this.algorithmDCLayout.createParallelGroup( GroupLayout.Alignment.LEADING )
        		.addGroup(this.algorithmDCLayout.createSequentialGroup( )
        				.addComponent( exeStepByStepPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE )
        				.addPreferredGap( LayoutStyle.ComponentPlacement.UNRELATED )
        				.addGroup( this.algorithmDCLayout.createParallelGroup( GroupLayout.Alignment.TRAILING )
        						.addComponent( viewportDominancePanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE ) )
        						.addContainerGap( ) )
        );
        
	}
	
	private void setViewportDominancePanel ( )
	{
		this.viewportDominancePanel = new JPanel( new BorderLayout( ) )
		{
			{
				TitledBorder title = BorderFactory.createTitledBorder( new BevelBorder( BevelBorder.LOWERED ) );
				title.setTitle( StringBundle.get( "sl_dominance_diagram_title" ) );
				title.setTitleJustification( TitledBorder.CENTER );
				this.setBorder( title );
			}
		};
		this.viewportDominancePanel.setBackground( UserSettings.instance.graphBackground.get() );
		this.viewportDominancePanel.setOpaque( true );
		
		this.viewportDominance = new ViewportDominance( );
		this.viewportDominancePanel.add( viewportDominance, BorderLayout.CENTER );
	}
	
	/**
	 * Inicializar el historial de ejecucion del algoritmo.
	 */
	private void setHistory ( )
	{
		SLDominanceAlgorithmHistory temp = new SLDominanceAlgorithmHistory( null, "" );
		this.history = new SnapshotList<SLDominanceAlgorithmHistory>( temp );
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
		String drawing = this.algorithm.getDrawing( ).toString( );
		String explanation = this.algorithm.getAlgorithmExplanation( );
		
		this.history.add( new SLDominanceAlgorithmHistory( drawing, explanation ) );	
	}
	
	/**
	 * Actualizar los graficos de todos los paneles
	 */
	private void update ( )
	{
		this.viewportDominance.update( );
	}
	
	/**
	 * Actualizar los datos del controlador despues de que el algoritmo haya cambiado. 
	 * @return String explicacion del la fase actual.
	 */
	private String updateExecutionStep ( )
	{
		SLDominanceAlgorithmHistory step = this.history.current( );
		
		this.setDrawing( step.getDominanceDrawing( ) != null ? new DominanceDrawing( step.getDominanceDrawing( ) ) : null );
		
		return step.getExplanation( );
	}

	/**
	 * @return    the drawing
	 */
	public DominanceDrawing getDrawing( )
	{
		return drawing;
	}

	/**
	 * @param drawing    the drawing to set
	 */
	public void setDrawing( DominanceDrawing drawing )
	{
		this.drawing = drawing;
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

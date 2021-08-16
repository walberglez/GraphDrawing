/**
 * VisibilityRepDisplayController.java
 */
package pfc.controllers.algorithms;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.ChangeListener;

import pfc.models.Graph;
import pfc.models.algorithms.visibilityrepresentation.*;
import pfc.resources.*;
import pfc.settings.*;
import pfc.utilities.*;
import pfc.views.display.*;
import pfc.views.display.algorithms.DualGraphDisplayView;
import pfc.views.display.algorithms.visibilityrepresentation.VisRepDrawingDisplayView;


/**
 * @author    Walber González
 */
@SuppressWarnings("serial")
public class VisibilityRepDisplayController extends JPanel implements ExecutionControlPanelActions
{
	/**
	 * Clase para el componente del grafo dual
	 */
	private class ViewportDual extends JComponent
	{
		private GraphSettings 		settings;
		private AffineTransform 	transform;
		private boolean				mouseWheelMoved;
		
		public ViewportDual ( )
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
					TransformUtilities.zoomCenter( ViewportDual.this.transform,
							new Point2D.Double( e.getX( ), e.getY( ) ),
							1 - e.getWheelRotation( ) * UserSettings.instance.scrollIncrementZoom.get( ) );
					ViewportDual.this.mouseWheelMoved = true;
					ViewportDual.this.update( );
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
			if ( this.isDrawable( ) )
				Rectangle2D.union( GeometryUtilities.getBounds( graph ), GeometryUtilities.getBounds( dual ), graphBounds );
			else
				graphBounds = GeometryUtilities.getBounds( graph );
			
			if ( this.mouseWheelMoved == false )
				TransformUtilities.zoomFit( this.transform, graphBounds, this.getBounds( ) );
			else
				this.mouseWheelMoved = false;
			
			// Apply the transformation
			AffineTransform original = g2D.getTransform( );
			original.concatenate( this.transform );
			g2D.setTransform( original );
			
			// Paint the graph
			GraphDisplayView.paint( g2D, graph, this.settings );
			if ( this.isDrawable( ) )
				DualGraphDisplayView.paint( g2D, dual, this.settings );
		}
		
		public void update ( )
		{
			this.validate( );
			this.repaint( );
		}
		
		private boolean isDrawable ( )
		{
			return dual != null && dual.vertices.isEmpty( ) == false;
		}
		
	}
	
	/** Clase para el componente de la representacion de visibilidad */
	private class ViewportVR extends JComponent
	{
		private AffineTransform 	transform;
		private boolean				mouseWheelMoved;
		
		public ViewportVR( )
		{
			super( );
			
			/* Inicializar la transformacion del grafo */
			this.transform = new AffineTransform( );
			
			this.mouseWheelMoved = false;
			
			/* listener de la rueda del raton para hacer zoom */
			this.addMouseWheelListener( new MouseWheelListener( )
			{
				public void mouseWheelMoved( MouseWheelEvent e )
				{
					TransformUtilities.zoomCenter( ViewportVR.this.transform,
							new Point2D.Double( e.getX( ), e.getY( ) ),
							1 - e.getWheelRotation( ) * UserSettings.instance.scrollIncrementZoom.get( ) );
					ViewportVR.this.mouseWheelMoved = true;
					ViewportVR.this.update( );
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
			if ( this.isDrawable( ) )
				drawingBounds = VisRepDrawingDisplayView.getBounds( drawing );
			
			if ( this.mouseWheelMoved == false )
				TransformUtilities.zoomFit( this.transform, drawingBounds, this.getBounds( ) );
			else
				this.mouseWheelMoved = false;
			
			// Apply the transformation
			AffineTransform original = g2D.getTransform( );
			original.concatenate( this.transform );
			g2D.setTransform( original );
			
			if ( this.isDrawable( ) )
				VisRepDrawingDisplayView.paintDrawing( g2D, drawing );
		}
		
		public void update ( )
		{
			this.validate( );
			this.repaint( );
		}
		
		private boolean isDrawable ( )
		{
			return drawing != null && drawing.vertexSegments.isEmpty( ) == false;
		}
		
	}
	
	/**
	 * grafo primal del algoritmo
	 */
	private Graph									graph;
	/**
	 * grafo dual del algoritmo
	 */
	private Graph									dual;
	/**
	 * drawing resultante de la ejecucion del algoritmo
	 */
	private VisibilityRepresentationDrawing			drawing;
	/**
	 * ejecucion del algoritmo
	 */
	private VisibilityRepresentationAlgorithm		algorithm;
	/** Layout del panel de ejecucion del algoritmo */
	private GroupLayout								visRepDCLayout;
	/**
	 * atributos para panel de control de la ejecucion
	 */
	private ExecutionControlPanel 					exeStepByStepPanel;
	/** atributos del panel del grafo dual */
	private JPanel 									viewportDualPanel;
	private ViewportDual							viewportDual;
	/** atributos del panel de la representacion de visibilidad */
	private JPanel 									viewportVRPanel;
	private ViewportVR								viewportVR;
	/** listener del evento cerrar el jinternalframe */
	private StateSupport							exeCancelEventListener;
	/** lista de cambios en el algoritmo */
	private SnapshotList<VisRepAlgorithmHistory>	history;
	/** inicio de la ejecucion, util para poner a inicio el historial */
	private boolean										started;
	
	/**
	 * @param graph Graph
	 */
	public VisibilityRepDisplayController( Graph graph )
	{		
		this.graph = new Graph( graph.toString( ) );
		
		this.algorithm = new VisibilityRepresentationAlgorithm( this.graph );
		this.algorithm.addObserver( new Observer( )
		{
			@Override
			public void update(Observable o, Object arg)
			{
				VisibilityRepDisplayController.this.onAlgorithmChanged( arg );
			}
		} );
				
		/* Agregar al panel principal el panel de control de ejecucion */
		this.exeStepByStepPanel = new ExecutionControlPanel( this );
		
		/* Lista de Listener de Eventos en el Panel */
		this.exeCancelEventListener = new StateSupport( );
		
		/* Agregar al panel principal el panel del grafo dual */
		this.setViewportDualPanel ( );
		
		/* Agregar al panel principal el panel de rep. de visibilidad */
		this.setViewportVRPanel ( );
		
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
	 * de Representacion de Visivilidad
	 */
	private void organizeLayout ( )
	{
		this.visRepDCLayout = new GroupLayout( this );
        this.setLayout( this.visRepDCLayout );
        
        // Organizar grupo horizontal
        this.visRepDCLayout.setHorizontalGroup(
        		this.visRepDCLayout.createParallelGroup( GroupLayout.Alignment.LEADING )
        		.addGroup( GroupLayout.Alignment.TRAILING, this.visRepDCLayout.createSequentialGroup( )
        				.addContainerGap( )
        				.addGroup( this.visRepDCLayout.createParallelGroup(GroupLayout.Alignment.TRAILING )
        						.addComponent( exeStepByStepPanel, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE )
        						.addGroup( this.visRepDCLayout.createSequentialGroup( )
        								.addComponent( viewportDualPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE )
        								.addPreferredGap( LayoutStyle.ComponentPlacement.UNRELATED )
        								.addComponent( viewportVRPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE ) ) )
        								.addContainerGap( ) )
        );
        
        // Organizar grupo vertical
        this.visRepDCLayout.setVerticalGroup(
        		this.visRepDCLayout.createParallelGroup( GroupLayout.Alignment.LEADING )
        		.addGroup(this.visRepDCLayout.createSequentialGroup( )
        				.addComponent( exeStepByStepPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE )
        				.addPreferredGap( LayoutStyle.ComponentPlacement.UNRELATED )
        				.addGroup( this.visRepDCLayout.createParallelGroup( GroupLayout.Alignment.TRAILING )
        						.addComponent( viewportVRPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE )
        						.addComponent( viewportDualPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE ) )
        						.addContainerGap( ) )
        );
        
	}
		
	private void setViewportDualPanel ( )
	{
		this.viewportDualPanel = new JPanel( new BorderLayout( ) )
		{
			{
				TitledBorder title = BorderFactory.createTitledBorder( new BevelBorder( BevelBorder.LOWERED ) );
				title.setTitle( StringBundle.get( "visibility_representation_dual_graph_title" ) );
				title.setTitleJustification( TitledBorder.CENTER );
				this.setBorder( title );
			}
		};
		this.viewportDualPanel.setBackground( UserSettings.instance.graphBackground.get() );
		this.viewportDualPanel.setOpaque( true );
		
		this.viewportDual = new ViewportDual( );
		this.viewportDualPanel.add( viewportDual, BorderLayout.CENTER );
	}
	
	private void setViewportVRPanel ( )
	{
		this.viewportVRPanel = new JPanel( new BorderLayout( ) )
		{
			{
				TitledBorder title = BorderFactory.createTitledBorder( new BevelBorder( BevelBorder.LOWERED ) );
				title.setTitle( StringBundle.get( "visibility_representation_diagram_title" ) );
				title.setTitleJustification( TitledBorder.CENTER );
				this.setBorder( title );
			}
		};
		this.viewportVRPanel.setBackground( UserSettings.instance.graphBackground.get() );
		this.viewportVRPanel.setOpaque( true );
		
		this.viewportVR = new ViewportVR( );
		this.viewportVRPanel.add( viewportVR, BorderLayout.CENTER );
	}
	
	/**
	 * Inicializar el historial de ejecucion del algoritmo.
	 */
	private void setHistory ( )
	{
		VisRepAlgorithmHistory temp = new VisRepAlgorithmHistory( this.graph.toString( ), null, null, "");
		this.history = new SnapshotList<VisRepAlgorithmHistory>( temp );
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
		String graph = this.algorithm.getStGraphG( ).toString( );
		String dual = this.algorithm.getStDualG( ).toString( );
		String drawing = this.algorithm.getDrawing( ).toString( );
		String explanation = this.algorithm.getAlgorithmExplanation( );
		
		this.history.add( new VisRepAlgorithmHistory( graph, dual, drawing, explanation ) );	
	}
	
	/**
	 * Actualizar los graficos de todos los paneles
	 */
	private void update ( )
	{
		this.viewportDual.update( );
		this.viewportVR.update( );
	}
	
	/**
	 * Actualizar los datos del controlador despues de que el algoritmo haya cambiado. 
	 * @return String explicacion del la fase actual.
	 */
	private String updateExecutionStep ( )
	{
		VisRepAlgorithmHistory step = this.history.current( );
		
		this.setGraph( step.getGraph( ) != null ? new Graph( step.getGraph( ) ) : null );
		this.setDual( step.getDual( ) != null ? new Graph( step.getDual( ) ) : null );
		this.setDrawing( step.getDrawing( ) != null ? new VisibilityRepresentationDrawing( step.getDrawing( ) ) : null );
		
		return step.getExplanation( );
	}
	
	/**
	 * @return    the graph
	 */
	public Graph getGraph( )
	{
		return graph;
	}
	
	/**
	 * @return    the dual
	 */
	public Graph getDual( )
	{
		return dual;
	}

	/**
	 * @return    the drawing
	 */
	public VisibilityRepresentationDrawing getDrawing( )
	{
		return drawing;
	}

	/**
	 * @param graph    the graph to set
	 */
	public void setGraph( Graph graph )
	{
		this.graph = graph;
	}
	
	/**
	 * @param dual    the dual to set
	 */
	public void setDual( Graph dual )
	{
		this.dual = dual;
	}
	
	/**
	 * @param drawing    the drawing to set
	 */
	public void setDrawing( VisibilityRepresentationDrawing drawing )
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

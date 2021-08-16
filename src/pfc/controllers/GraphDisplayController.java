/**
 * GraphDisplayController.java
 */
package pfc.controllers;

import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.awt.print.*;
import java.util.EventListener;
import java.util.EventObject;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.event.EventListenerList;

import pfc.gui.layouts.WrapLayout;
import pfc.models.Edge;
import pfc.models.Graph;
import pfc.models.Vertex;
import pfc.models.algorithms.NonIntersectingPathList;
import pfc.resources.CursorBundle;
import pfc.resources.ImageIconBundle;
import pfc.resources.StringBundle;
import pfc.settings.GlobalSettings;
import pfc.settings.GraphSettings;
import pfc.settings.UserSettings;
import pfc.utilities.GeometryUtilities;
import pfc.utilities.GraphUtilities;
import pfc.utilities.SnapshotList;
import pfc.views.display.EdgeDisplayView;
import pfc.views.display.GraphDisplayView;
import pfc.views.display.VertexDisplayView;


/**
 * @author    Cameron Behar
 */
@SuppressWarnings("serial")
public class GraphDisplayController extends JPanel
{

	/**
	 * @author Cameron Behar
	 *
	 */
	public class GraphChangeEvent extends EventObject
	{

		public GraphChangeEvent( Object source )
		{
			super( source );
		}
	}
	
	public interface GraphChangeEventListener extends EventListener
	{
		public void graphChangeEventOccurred( GraphChangeEvent evt );
	}

	public enum Tool
	{
		POINTER_TOOL, GRAPH_TOOL, CUT_TOOL
	}
	
	// Barra de herramientas de puntero, insertar arista y vertice y suprimir elementos
	private class ToolToolBar extends JToolBar
	{
		private final JButton		pointerToolButton;
		private final JButton		graphToolButton;
		private final JButton		cutToolButton;
		
		public ToolToolBar( )
		{	
			this.pointerToolButton = new JButton( ImageIconBundle.get( "pointer_tool_icon" ) )
			{
				{
					this.addActionListener( new ActionListener( )
					{
						public void actionPerformed( ActionEvent e )
						{
							GraphDisplayController.this.setTool( Tool.POINTER_TOOL );
						}
					} );
					this.setToolTipText( StringBundle.get( "pointer_tool_tooltip" ) );
					this.setSelected( true );
				}
			};
			this.add( this.pointerToolButton );
			
			this.graphToolButton = new JButton( ImageIconBundle.get( "graph_tool_icon" ) )
			{
				{
					this.addActionListener( new ActionListener( )
					{
						public void actionPerformed( ActionEvent e )
						{
							GraphDisplayController.this.setTool( Tool.GRAPH_TOOL );
						}
					} );
					this.setToolTipText( StringBundle.get( "graph_tool_tooltip" ) );
				}
			};
			this.add( this.graphToolButton );

			this.cutToolButton = new JButton( ImageIconBundle.get( "cut_tool_icon" ) )
			{
				{
					this.addActionListener( new ActionListener( )
					{
						public void actionPerformed( ActionEvent e )
						{
							GraphDisplayController.this.setTool( Tool.CUT_TOOL );
						}
					} );
					this.setToolTipText( StringBundle.get( "cut_tool_tooltip" ) );
				}
			};
			this.add( this.cutToolButton );
			
			GraphDisplayController.this.setTool( Tool.POINTER_TOOL );
			
			this.refresh( );
		}
		
		public void refresh( )
		{
			for( Component toolButton : this.getComponents( ) )
				if( toolButton instanceof JButton )
					( (JButton) toolButton ).setSelected( false );
			
			switch( GraphDisplayController.this.tool )
			{
				case POINTER_TOOL:
					this.pointerToolButton.setSelected( true );
					break;
				case GRAPH_TOOL:
					this.graphToolButton.setSelected( true );
					break;
				case CUT_TOOL:
					this.cutToolButton.setSelected( true );
					break;
			}
		}
	}
	
	// Menu emergente cuando se realiza click derecho sobre un vertice o una arista
	private class ViewportPopupMenu extends JPopupMenu
	{
		private final JMenuItem	vertexItem;
		private final JMenuItem	vertexLabelItem;
		private final JMenuItem	vertexRadiusItem;
		private final JMenuItem	vertexWeightItem;
		private final JMenuItem	edgeItem;
		private final JMenuItem	edgeLabelItem;
		private final JMenuItem	edgeThicknessItem;
		private final JMenuItem	edgeWeightItem;
		
		public ViewportPopupMenu( )
		{
			this.vertexItem = new JMenu( StringBundle.get( "properties_vertex_menu_text" ) );
			this.add( this.vertexItem );
			
			this.vertexLabelItem = new JMenuItem( StringBundle.get( "properties_vertex_label_menu_text" ) )
			{
				{
					this.addActionListener( new ActionListener( )
					{
						public void actionPerformed( ActionEvent e )
						{
							List<Vertex> selectedVertices = GraphDisplayController.this.graph.getSelectedVertices( );
							
							String oldLabel = null;
							for( Vertex vertex : selectedVertices )
								if( oldLabel == null )
									oldLabel = vertex.label.get( );
								else if( !oldLabel.equals( vertex.label.get( ) ) )
								{
									oldLabel = null;
									break;
								}
							
							Object value = JOptionPane.showInputDialog( GraphDisplayController.this.viewport, StringBundle.get( "new_vertex_label_dialog_text" ), GlobalSettings.applicationName, JOptionPane.QUESTION_MESSAGE, null, null, ( oldLabel != null ? oldLabel : "" ) );
							if( value != null )
								for( Vertex vertex : selectedVertices )
									vertex.label.set( value.toString( ) );
						}
					} );
				}
			};
			this.vertexItem.add( this.vertexLabelItem );
			
			this.vertexRadiusItem = new JMenuItem( StringBundle.get( "properties_vertex_radius_menu_text" ) )
			{
				{
					this.addActionListener( new ActionListener( )
					{
						public void actionPerformed( ActionEvent e )
						{
							List<Vertex> selectedVertices = GraphDisplayController.this.graph.getSelectedVertices( );
							
							Double oldRadius = null;
							for( Vertex vertex : selectedVertices )
								if( oldRadius == null )
									oldRadius = vertex.radius.get( );
								else if( !oldRadius.equals( vertex.radius.get( ) ) )
								{
									oldRadius = null;
									break;
								}
							
							Object value = JOptionPane.showInputDialog( GraphDisplayController.this.viewport, StringBundle.get( "new_vertex_radius_dialog_text" ), GlobalSettings.applicationName, JOptionPane.QUESTION_MESSAGE, null, null, ( oldRadius != null ? oldRadius : "" ) );
							if( value != null )
							{
								double newRadius = Double.parseDouble( value.toString( ) );
								for( Vertex vertex : selectedVertices )
									vertex.radius.set( newRadius );
							}
						}
					} );
				}
			};
			this.vertexItem.add( this.vertexRadiusItem );
			
			this.vertexWeightItem = new JMenuItem( StringBundle.get( "properties_vertex_weight_menu_text" ) )
			{
				{
					this.addActionListener( new ActionListener( )
					{
						public void actionPerformed( ActionEvent e )
						{
							List<Vertex> selectedVertices = GraphDisplayController.this.graph.getSelectedVertices( );
							
							Double oldWeight = null;
							for( Vertex vertex : selectedVertices )
								if( oldWeight == null )
									oldWeight = vertex.weight.get( );
								else if( !oldWeight.equals( vertex.weight.get( ) ) )
								{
									oldWeight = null;
									break;
								}
							
							Object value = JOptionPane.showInputDialog( GraphDisplayController.this.viewport, StringBundle.get( "new_vertex_weight_dialog_text" ), GlobalSettings.applicationName, JOptionPane.QUESTION_MESSAGE, null, null, ( oldWeight != null ? oldWeight : "" ) );
							if( value != null )
							{
								double newWeight = Double.parseDouble( value.toString( ) );
								for( Vertex vertex : selectedVertices )
									vertex.weight.set( newWeight );
							}
						}
					} );
				}
			};
			this.vertexItem.add( this.vertexWeightItem );
			
			this.edgeItem = new JMenu( StringBundle.get( "properties_edge_menu_text" ) );
			this.add( this.edgeItem );
			
			this.edgeLabelItem = new JMenuItem( StringBundle.get( "properties_edge_label_menu_text" ) )
			{
				{
					this.addActionListener( new ActionListener( )
					{
						public void actionPerformed( ActionEvent e )
						{
							List<Edge> selectedEdges = GraphDisplayController.this.graph.getSelectedEdges( );
							
							String oldLabel = null;
							for( Edge edge : selectedEdges )
								if( oldLabel == null )
									oldLabel = edge.label.get( );
								else if( !oldLabel.equals( edge.label.get( ) ) )
								{
									oldLabel = null;
									break;
								}
							
							Object value = JOptionPane.showInputDialog( GraphDisplayController.this.viewport, StringBundle.get( "new_edge_label_dialog_text" ), GlobalSettings.applicationName, JOptionPane.QUESTION_MESSAGE, null, null, ( oldLabel != null ? oldLabel : "" ) );
							if( value != null )
								for( Edge edge : selectedEdges )
									edge.label.set( value.toString( ) );
						}
					} );
				}
			};
			this.edgeItem.add( this.edgeLabelItem );
			
			this.edgeThicknessItem = new JMenuItem( StringBundle.get( "properties_edge_thickness_menu_text" ) )
			{
				{
					this.addActionListener( new ActionListener( )
					{
						public void actionPerformed( ActionEvent e )
						{
							List<Edge> selectedEdges = GraphDisplayController.this.graph.getSelectedEdges( );
							
							Double oldThickness = null;
							for( Edge edge : selectedEdges )
								if( oldThickness == null )
									oldThickness = edge.thickness.get( );
								else if( !oldThickness.equals( edge.thickness.get( ) ) )
								{
									oldThickness = null;
									break;
								}
							
							Object value = JOptionPane.showInputDialog( GraphDisplayController.this.viewport, StringBundle.get( "new_edge_thickness_dialog_text" ), GlobalSettings.applicationName, JOptionPane.QUESTION_MESSAGE, null, null, ( oldThickness != null ? oldThickness : "" ) );
							if( value != null )
							{
								double newThickness = Double.parseDouble( value.toString( ) );
								for( Edge edge : selectedEdges )
									edge.thickness.set( newThickness );
							}
						}
					} );
				}
			};
			this.edgeItem.add( this.edgeThicknessItem );
			
			this.edgeWeightItem = new JMenuItem( StringBundle.get( "properties_edge_weight_menu_text" ) )
			{
				{
					this.addActionListener( new ActionListener( )
					{
						public void actionPerformed( ActionEvent e )
						{
							List<Edge> selectedEdges = GraphDisplayController.this.graph.getSelectedEdges( );
							
							Double oldWeight = null;
							for( Edge edge : selectedEdges )
								if( oldWeight == null )
									oldWeight = edge.weight.get( );
								else if( !oldWeight.equals( edge.weight.get( ) ) )
								{
									oldWeight = null;
									break;
								}
							
							Object value = JOptionPane.showInputDialog( GraphDisplayController.this.viewport, StringBundle.get( "new_edge_weight_dialog_text" ), GlobalSettings.applicationName, JOptionPane.QUESTION_MESSAGE, null, null, ( oldWeight != null ? oldWeight : "" ) );
							if( value != null )
							{
								double newWeight = Double.parseDouble( value.toString( ) );
								for( Edge edge : selectedEdges )
									edge.weight.set( newWeight );
							}
						}
					} );
				}
			};
			this.edgeItem.add( this.edgeWeightItem );
		}
		
		public void setEdgeMenuEnabled( boolean enable )
		{
			this.edgeItem.setEnabled( enable );
		}
		
		public void setVertexMenuEnabled( boolean enable )
		{
			this.vertexItem.setEnabled( enable );
		}
	}
	
	private class ViewportPrinter implements Printable
	{
		public void print( ) throws PrinterException
		{
			PrinterJob printJob = PrinterJob.getPrinterJob( );
			printJob.setPrintable( this );
			
			if( printJob.printDialog( ) )
				printJob.print( );
		}
		
		public int print( Graphics g, PageFormat pageFormat, int pageIndex )
		{
			if( pageIndex > 0 )
				return ( NO_SUCH_PAGE );
			else
			{
				Graphics2D g2d = (Graphics2D) g;
				
				double widthRatio = ( GraphDisplayController.this.viewport.getWidth( ) + UserSettings.instance.zoomGraphPadding.get( ) ) / pageFormat.getImageableWidth( );
				double heightRatio = ( GraphDisplayController.this.viewport.getHeight( ) + UserSettings.instance.zoomGraphPadding.get( ) ) / pageFormat.getImageableHeight( );
				double maxRatio = 1.0 / Math.max( widthRatio, heightRatio );
				
				g2d.scale( maxRatio, maxRatio );
				g2d.translate( pageFormat.getImageableX( ) + UserSettings.instance.zoomGraphPadding.get( ) / 4.0, pageFormat.getImageableY( ) + UserSettings.instance.zoomGraphPadding.get( ) / 4.0 );
				
				RepaintManager.currentManager( GraphDisplayController.this.viewport ).setDoubleBufferingEnabled( false );
				GraphDisplayController.this.viewport.paint( g2d );
				RepaintManager.currentManager( GraphDisplayController.this.viewport ).setDoubleBufferingEnabled( true );
				
				return ( PAGE_EXISTS );
			}
		}
	}
	
	// Barra de herramientas con mostrar etiqueta, peso de Vertice o Arista
	private class ViewToolBar extends JToolBar
	{
		private final JButton	showVertexLabelsButton;
		private final JButton	showVertexWeightsButton;
		private final JButton	showEdgeLabelsButton;
		private final JButton	showEdgeWeightsButton;
		
		public ViewToolBar( )
		{
			this.showVertexLabelsButton = new JButton( ImageIconBundle.get( "show_vertex_labels_icon" ) )
			{
				{
					this.addActionListener( new ActionListener( )
					{
						public void actionPerformed( ActionEvent e )
						{
							GraphDisplayController.this.settings.showVertexLabels.set( !GraphDisplayController.this.settings.showVertexLabels.get( ) );
						}
					} );
					this.setToolTipText( StringBundle.get( "show_vertex_labels_button_tooltip" ) );
				}
			};
			this.add( this.showVertexLabelsButton );
			
			this.showVertexWeightsButton = new JButton( ImageIconBundle.get( "show_vertex_weights_icon" ) )
			{
				{
					this.addActionListener( new ActionListener( )
					{
						public void actionPerformed( ActionEvent e )
						{
							GraphDisplayController.this.settings.showVertexWeights.set( !GraphDisplayController.this.settings.showVertexWeights.get( ) );
						}
					} );
					this.setToolTipText( StringBundle.get( "show_vertex_weights_button_tooltip" ) );
				}
			};
			this.add( this.showVertexWeightsButton );
			
			this.showEdgeLabelsButton = new JButton( ImageIconBundle.get( "show_edge_labels_icon" ) )
			{
				{
					this.addActionListener( new ActionListener( )
					{
						public void actionPerformed( ActionEvent e )
						{
							GraphDisplayController.this.settings.showEdgeLabels.set( !GraphDisplayController.this.settings.showEdgeLabels.get( ) );
						}
					} );
					this.setToolTipText( StringBundle.get( "show_edge_labels_button_tooltip" ) );
				}
			};
			this.add( this.showEdgeLabelsButton );
			
			this.showEdgeWeightsButton = new JButton( ImageIconBundle.get( "show_edge_weights_icon" ) )
			{
				{
					this.addActionListener( new ActionListener( )
					{
						public void actionPerformed( ActionEvent e )
						{
							GraphDisplayController.this.settings.showEdgeWeights.set( !GraphDisplayController.this.settings.showEdgeWeights.get( ) );
						}
					} );
					this.setToolTipText( StringBundle.get( "show_edge_weights_button_tooltip" ) );
				}
			};
			this.add( this.showEdgeWeightsButton );

			this.refresh( );
		}
		
		public void refresh( )
		{
			if( this.showVertexLabelsButton != null && this.showVertexLabelsButton.isSelected( ) != GraphDisplayController.this.settings.showVertexLabels.get( ) )
				this.showVertexLabelsButton.setSelected( GraphDisplayController.this.settings.showVertexLabels.get( ) );
			
			if( this.showVertexWeightsButton != null && this.showVertexWeightsButton.isSelected( ) != GraphDisplayController.this.settings.showVertexWeights.get( ) )
				this.showVertexWeightsButton.setSelected( GraphDisplayController.this.settings.showVertexWeights.get( ) );
			
			if( this.showEdgeLabelsButton != null && this.showEdgeLabelsButton.isSelected( ) != GraphDisplayController.this.settings.showEdgeLabels.get( ) )
				this.showEdgeLabelsButton.setSelected( GraphDisplayController.this.settings.showEdgeLabels.get( ) );
			
			if( this.showEdgeWeightsButton != null && this.showEdgeWeightsButton.isSelected( ) != GraphDisplayController.this.settings.showEdgeWeights.get( ) )
				this.showEdgeWeightsButton.setSelected( GraphDisplayController.this.settings.showEdgeWeights.get( ) );
		}
	}
	
	// Barra de herramientas para hacer Zoom sobre el grafo
	private class ZoomToolBar extends JToolBar
	{
		private final JButton	zoomGraphButton;
		private final JButton	zoomOneToOneButton;
		private final JButton	zoomInButton;
		private final JButton	zoomOutButton;
		
		public ZoomToolBar( )
		{
			this.zoomGraphButton = new JButton( ImageIconBundle.get( "zoom_graph_icon" ) )
			{
				{
					this.addActionListener( new ActionListener( )
					{
						public void actionPerformed( ActionEvent e )
						{
							GraphDisplayController.this.zoomFit( );
						}
					} );
					this.setToolTipText( StringBundle.get( "zoom_graph_button_tooltip" ) );
				}
			};
			this.add( this.zoomGraphButton );
			
			this.zoomOneToOneButton = new JButton( ImageIconBundle.get( "zoom_one_to_one_icon" ) )
			{
				{
					this.addActionListener( new ActionListener( )
					{
						public void actionPerformed( ActionEvent e )
						{
							GraphDisplayController.this.zoomOneToOne( );
						}
					} );
					this.setToolTipText( StringBundle.get( "zoom_one_to_one_button_tooltip" ) );
				}
			};
			this.add( this.zoomOneToOneButton );
			
			this.zoomInButton = new JButton( ImageIconBundle.get( "zoom_in_icon" ) )
			{
				{
					this.addActionListener( new ActionListener( )
					{
						public void actionPerformed( ActionEvent e )
						{
							Point2D.Double viewportCenter = new Point2D.Double( GraphDisplayController.this.viewport.getWidth( ) / 2.0, GraphDisplayController.this.viewport.getHeight( ) / 2.0 );
							Point2D.Double zoomCenter = new Point2D.Double( );
							try
							{
								GraphDisplayController.this.transform.inverseTransform( viewportCenter, zoomCenter );
							}
							catch( NoninvertibleTransformException ex )
							{
								System.out.println( "An exception occurred while inverting transformation." );
							}
							
							GraphDisplayController.this.zoomCenter( zoomCenter, UserSettings.instance.zoomInFactor.get( ) );
						}
					} );
					this.setToolTipText( StringBundle.get( "zoom_in_button_tooltip" ) );
				}
			};
			this.add( this.zoomInButton );
			
			this.zoomOutButton = new JButton( ImageIconBundle.get( "zoom_out_icon" ) )
			{
				{
					this.addActionListener( new ActionListener( )
					{
						public void actionPerformed( ActionEvent e )
						{
							Point2D.Double viewportCenter = new Point2D.Double( GraphDisplayController.this.viewport.getWidth( ) / 2.0, GraphDisplayController.this.viewport.getHeight( ) / 2.0 );
							Point2D.Double zoomCenter = new Point2D.Double( );
							try
							{
								GraphDisplayController.this.transform.inverseTransform( viewportCenter, zoomCenter );
							}
							catch( NoninvertibleTransformException ex )
							{
								System.out.println( "An exception occurred while inverting transformation." );
							}
							
							GraphDisplayController.this.zoomCenter( zoomCenter, UserSettings.instance.zoomOutFactor.get( ) );
						}
					} );
					this.setToolTipText( StringBundle.get( "zoom_out_button_tooltip" ) );
				}
			};
			this.add( this.zoomOutButton );
		}
	}
	
	// Barra de herramientas para realizar operaciones de orientacion del grafo
	private class OrientationToolBar extends JToolBar {
		// boton para aplicar la orientacion bipolar
		private final JButton   bipolarOrientationButton;

		public OrientationToolBar( )
		{
			this.bipolarOrientationButton = new JButton( ImageIconBundle.get( "bipolar_orientation_icon" ) )
			{
				{
					this.addActionListener( new ActionListener( )
					{
						public void actionPerformed( ActionEvent e )
						{
							GraphDisplayController.this.executeBipolarOrientation( );
						}
					} );
					this.setToolTipText( StringBundle.get( "bipolar_orientation_button_tooltip" ) );
				}
			};
			this.add( this.bipolarOrientationButton );
		}
	}
	
	// Barra de herramientas para realizar operaciones de seleccion de caminos del grafo
	private class PathToolBar extends JToolBar {
	    // boton para almacenar una seleccion de elementos como un camino
	    private final JButton   savePathButton;
	    // boton para borrar los caminos almacenados anteriormente
        private final JButton   deletePathsButton;
        // boton para mostrar todos los paths definidos de forma correcta
        private final JButton   showPathsButton;
        
	    public PathToolBar( )
	    {
	        this.savePathButton = new JButton( ImageIconBundle.get( "save_path_icon" ) )
	        {
	            {
	                this.addActionListener( new ActionListener( )
	                {
	                    public void actionPerformed( ActionEvent e )
	                    {
	                        GraphDisplayController.this.addPath( );
	                    }
	                } );
	                this.setToolTipText( StringBundle.get( "save_path_button_tooltip" ) );
	            }
	        };
	        this.add( this.savePathButton );
	        
	        this.deletePathsButton = new JButton( ImageIconBundle.get( "delete_paths_icon" ) )
            {
                {
                    this.addActionListener( new ActionListener( )
                    {
                        public void actionPerformed( ActionEvent e )
                        {
                            GraphDisplayController.this.removeAllPaths( );
                        }
                    } );
                    this.setToolTipText( StringBundle.get( "delete_paths_button_tooltip" ) );
                }
            };
            this.add( this.deletePathsButton );
            
            this.showPathsButton = new JButton( ImageIconBundle.get( "show_paths_icon" ) )
            {
                {
                    this.addActionListener( new ActionListener( )
                    {
                        public void actionPerformed( ActionEvent e )
                        {
                            GraphDisplayController.this.selectAllPaths( true );
                        }
                    } );
                    this.setToolTipText( StringBundle.get( "show_paths_button_tooltip" ) );
                }
            };
            this.add( this.showPathsButton );
            
            this.refresh( );
	    }
	    
	    public void refresh( )
		{
	    	if ( GraphDisplayController.this.graph != null &&
	    			GraphDisplayController.this.graph.areDirectedEdgesAllowed )
	    	{
	    		this.savePathButton.setEnabled( true );
	    		this.deletePathsButton.setEnabled( true );
	    		this.showPathsButton.setEnabled( true );
	    	}
	    	else
	    	{
	    		this.savePathButton.setEnabled( false );
	    		this.deletePathsButton.setEnabled( false );
	    		this.showPathsButton.setEnabled( false );
	    	}
		}
	}

		
	private Graph                      graph;
	public final GraphSettings         settings;
	private JPanel                     nonToolToolbarPanel;
	private ToolToolBar                toolToolBar;
	private ViewToolBar                viewToolBar;
	private ZoomToolBar                zoomToolBar;
	private OrientationToolBar		   orientationToolBar;
	private PathToolBar                pathToolBar;
	private JPanel                     viewportPanel;
	private JComponent                 viewport;
	private ViewportPopupMenu          viewportPopupMenu;
	private Tool                       tool;
	private boolean                    isMouseDownOnCanvas;
	private boolean                    pointerToolClickedObject;
	private boolean                    cutToolClickedObject;
	private boolean                    isMouseOverViewport;
	private Point                      currentMousePoint;
	private Point                      pastMousePoint;
	private Vertex                     fromVertex;
	private final AffineTransform      transform;
	private final SnapshotList<String> undoHistory;
	private Timer                      undoTimer;
	private Timer                      panTimer;
	private final EventListenerList    graphChangeListenerList;
	private boolean                    isViewportInvalidated;
	private final Timer                viewportValidationTimer;
	private NonIntersectingPathList    nonIntersectingPaths;
	
	public GraphDisplayController( Graph graph )
	{
		// Initialize the list of GraphChangeEvent listeners
		this.graphChangeListenerList = new EventListenerList( );
		
		// Add/bind graph
		this.setGraph( graph );
		this.undoHistory = new SnapshotList<String>( graph.toString( ) );
		
		// Add/bind palette
		UserSettings.instance.addObserver( new Observer( )
		{
			@Override
			public void update( Observable o, Object arg )
			{
				GraphDisplayController.this.onSettingChanged( arg );
			}
		} );
		
		// Add/bind display settings
		this.settings = new GraphSettings( )
		{
			{
				this.addObserver( new Observer( )
				{
					@Override
					public void update( Observable o, Object arg )
					{
						GraphDisplayController.this.onSettingChanged( arg );
					}
				} );
			}
		};
		
		// Initialize the toolbar, buttons, and viewport
		this.initializeComponents( );
		
		// Initialize the viewport's affine transform
		this.transform = new AffineTransform( );
		
		// Initialize the viewport's frame delimiter
		this.isViewportInvalidated = true;
		this.viewportValidationTimer = new Timer( 30, new ActionListener( )
		{
			@Override
			public void actionPerformed( ActionEvent e )
			{
				if( GraphDisplayController.this.isViewportInvalidated )
					GraphDisplayController.this.repaint( );
				
				GraphDisplayController.this.isViewportInvalidated = false;
			}
		} );
		this.viewportValidationTimer.start( );
		
		// Initialize paths
		nonIntersectingPaths = new NonIntersectingPathList( );
	}

    public void addGraphChangeListener( GraphChangeEventListener listener )
	{
		this.graphChangeListenerList.add( GraphChangeEventListener.class, listener );
	}
	
    public void addPath( )
    {                
        if ( nonIntersectingPaths.add(
                this.graph.getSelectedVertices( ),
                this.graph.getSelectedEdges( ) ) )
        {
            this.graph.selectAll( false );
        }
        else
            Toolkit.getDefaultToolkit( ).beep( );
    }
    
	public void copy( )
	{
		// Make a copy of graph containing only selected elements
		Graph copy = new Graph( "copy", this.graph.areLoopsAllowed, this.graph.areDirectedEdgesAllowed, this.graph.areMultipleEdgesAllowed, this.graph.areCyclesAllowed );
		
		for( Vertex vertex : this.graph.getSelectedVertices( ) )
			copy.vertices.add( vertex );
		
		for( Edge edge : this.graph.getSelectedEdges( ) )
			if( edge.from.isSelected.get( ) && edge.to.isSelected.get( ) )
				copy.edges.add( edge );

		// Send the JSON to the clipboard
		StringSelection stringSelection = new StringSelection( copy.toString( ) );
		Clipboard clipboard = Toolkit.getDefaultToolkit( ).getSystemClipboard( );
		clipboard.setContents( stringSelection, new ClipboardOwner( )
		{
			@Override
			public void lostOwnership( Clipboard c, Transferable t )
			{
				// Ignore?
			}
		} );
	}
	
	public void cut( )
	{
		this.copy( );
		
        // borrar todos los paths con las aristas y los vertices seleccionados
        this.nonIntersectingPaths.removeAllVertices( this.graph.getSelectedVertices( ) );
        this.nonIntersectingPaths.removeAllEdges( this.graph.getSelectedEdges( ) );
        
		this.graph.vertices.removeAll( this.graph.getSelectedVertices( ) );
		this.graph.edges.removeAll( this.graph.getSelectedEdges( ) );
	}
	
	public void dispose( )
	{
		this.undoTimer.stop( );
	}
	
	public void executeBipolarOrientation( )
	{
        // Si grafo es no dirigido, convertirlo a dirigido
        if ( this.graph.areDirectedEdgesAllowed == false && GraphUtilities.isBiconnected( this.graph ) )
        {
            this.setGraph( GraphUtilities.getDirectedPlanarGraph( graph ) );
        }
        else
        {
            JOptionPane.showMessageDialog( GraphDisplayController.this, StringBundle.get( "error_bipolar_orientation_dialog_message" ));
        }
	}
	
	private void fireGraphChangeEvent( GraphChangeEvent event )
	{
		Object[ ] listeners = this.graphChangeListenerList.getListenerList( );
		// Each listener occupies two elements - the first is the listener class and the second is the listener instance
		for( int i = 0; i < listeners.length; i += 2 )
			if( listeners[i] == GraphChangeEventListener.class )
				( (GraphChangeEventListener) listeners[i + 1] ).graphChangeEventOccurred( event );
	}
	
	/**
	 * @return the graph
	 */
	public Graph getGraph( )
	{
		return this.graph;
	}
	
	/**
	 * @return RenderedImage
	 */
	public RenderedImage getImage( )
	{
		int width = this.viewport.getWidth( ), height = this.viewport.getHeight( );
		
		// Create a buffered image on which to draw
		BufferedImage bufferedImage = new BufferedImage( width, height, BufferedImage.TYPE_INT_RGB );
		
		// Create a graphics contents on the buffered image
		Graphics g = bufferedImage.createGraphics( );
		
		// Draw graphics
		this.viewport.paint( g );
		
		// Graphics context no longer needed so dispose it
		g.dispose( );
		
		return bufferedImage;
	}
	
	/**
     * @return the nonIntersectingPaths
     */
    public NonIntersectingPathList getNonIntersectingPaths( )
    {
        return this.nonIntersectingPaths;
    }

    public Rectangle getSelectionRectangle( )
	{
		return new Rectangle( )
		{
			{
				this.x = Math.min( GraphDisplayController.this.pastMousePoint.x, GraphDisplayController.this.currentMousePoint.x );
				this.y = Math.min( GraphDisplayController.this.pastMousePoint.y, GraphDisplayController.this.currentMousePoint.y );
				this.width = Math.abs( GraphDisplayController.this.pastMousePoint.x - GraphDisplayController.this.currentMousePoint.x );
				this.height = Math.abs( GraphDisplayController.this.pastMousePoint.y - GraphDisplayController.this.currentMousePoint.y );
			}
		};
	}
	
	public void initializeComponents( )
	{
		this.setLayout( new BorderLayout( ) );
		this.setBackground( UserSettings.instance.graphBackground.get( ) );
		this.setOpaque( true );
		
		this.nonToolToolbarPanel = new JPanel( new WrapLayout( FlowLayout.LEFT ) )
		{
			{
				this.setMaximumSize( new Dimension( Integer.MAX_VALUE, 32 ) );
			}
		};
		this.add( this.nonToolToolbarPanel, BorderLayout.NORTH );
		
		this.toolToolBar = new ToolToolBar( );
		this.nonToolToolbarPanel.add( this.toolToolBar );
		
		this.viewToolBar = new ViewToolBar( );
		this.nonToolToolbarPanel.add( this.viewToolBar );
		
		this.zoomToolBar = new ZoomToolBar( );
		this.nonToolToolbarPanel.add( this.zoomToolBar );

		this.orientationToolBar = new OrientationToolBar( );
		this.nonToolToolbarPanel.add( this.orientationToolBar );
		
		this.pathToolBar = new PathToolBar( );
		this.nonToolToolbarPanel.add( this.pathToolBar );
		
		this.viewportPanel = new JPanel( new BorderLayout( ) )
		{
			{
				this.setBorder( new BevelBorder( BevelBorder.LOWERED ) );
			}
		};
		this.add( this.viewportPanel, BorderLayout.CENTER );
		
		this.viewport = new JComponent( )
		{
			@Override
			public void paintComponent( Graphics g )
			{
				GraphDisplayController.this.paintViewport( (Graphics2D) g );
			}
		};
		this.viewport.addMouseListener( new MouseAdapter( )
		{
			@Override
			public void mouseEntered( MouseEvent event )
			{
				GraphDisplayController.this.isMouseOverViewport = true;
			}
			
			@Override
			public void mouseExited( MouseEvent event )
			{
				GraphDisplayController.this.isMouseOverViewport = false;
			}
			
			@Override
			public void mousePressed( MouseEvent event )
			{
				try
				{
					GraphDisplayController.this.viewportMousePressed( event );
				}
				catch( NoninvertibleTransformException e )
				{
					System.out.println( "An exception occurred while inverting transformation." );
				}
				
				if( event.getClickCount( ) > 1 )
					try
					{
						GraphDisplayController.this.viewportMouseDoubleClicked( event );
					}
					catch( NoninvertibleTransformException e )
					{
						System.out.println( "An exception occurred while inverting transformation." );
					}
			}
			
			@Override
			public void mouseReleased( MouseEvent event )
			{
				try
				{
					GraphDisplayController.this.viewportMouseReleased( event );
				}
				catch( NoninvertibleTransformException e )
				{
					System.out.println( "An exception occurred while inverting transformation." );
				}
			}
		} );
		this.viewport.addMouseMotionListener( new MouseMotionAdapter( )
		{
			@Override
			public void mouseDragged( MouseEvent event )
			{
				try
				{
					GraphDisplayController.this.viewportMouseDragged( event );
				}
				catch( NoninvertibleTransformException e )
				{
					System.out.println( "An exception occurred while inverting transformation." );
				}
			}
			
			@Override
			public void mouseMoved( MouseEvent event )
			{
				try
				{
					GraphDisplayController.this.transform.inverseTransform( event.getPoint( ), GraphDisplayController.this.currentMousePoint );
				}
				catch( NoninvertibleTransformException e )
				{
					System.out.println( "An exception occurred while inverting transformation." );
				}
			}
		} );
		this.viewport.addMouseWheelListener( new MouseWheelListener( )
		{
			public void mouseWheelMoved( MouseWheelEvent e )
			{
				GraphDisplayController.this.zoomCenter( new Point2D.Double( GraphDisplayController.this.currentMousePoint.x, GraphDisplayController.this.currentMousePoint.y ), 1 - e.getWheelRotation( ) * UserSettings.instance.scrollIncrementZoom.get( ) );
			}
		} );
		this.viewport.addKeyListener( new KeyListener( )
		{
			public void keyPressed( KeyEvent e )
			{
				GraphDisplayController.this.viewportKeyPressed( e );
			}
			
			public void keyReleased( KeyEvent e )
			{
				// Do nothing
			}
			
			public void keyTyped( KeyEvent e )
			{
				// Do nothing
			}
		} );
		this.viewportPanel.add( this.viewport, BorderLayout.CENTER );
		
		this.viewportPopupMenu = new ViewportPopupMenu( );
		
		this.undoTimer = new Timer( UserSettings.instance.undoLoggingInterval.get( ), new ActionListener( )
		{
			@Override
			public void actionPerformed( ActionEvent e )
			{
				if( GraphDisplayController.this.undoHistory.getCapacity( ) > 0 )
					GraphDisplayController.this.undoHistory.add( GraphDisplayController.this.graph.toString( ) );
			}
		} );
		this.undoTimer.start( );
	}
	
	public void onGraphChanged( Object source )
	{
		this.isViewportInvalidated = true;
		this.fireGraphChangeEvent( new GraphChangeEvent( this.graph ) );
	}
	
	public void onSettingChanged( Object source )
	{
		this.isViewportInvalidated = true;
		
		if( this.viewToolBar != null )
			this.viewToolBar.refresh( );
		
		if( this.undoTimer != null )
			this.undoTimer.setDelay( UserSettings.instance.undoLoggingInterval.get( ) );
		
		if( this.undoHistory != null && this.undoHistory.getCapacity( ) != UserSettings.instance.undoLoggingMaximum.get( ) )
		{
			this.undoHistory.setCapacity( UserSettings.instance.undoLoggingMaximum.get( ) );
			this.undoHistory.clear( );
		}
	}
	
	public void paintSelectionRectangle( Graphics2D g2D )
	{
		Rectangle selection = this.getSelectionRectangle( );
		
		g2D.setColor( UserSettings.instance.selectionBoxFill.get( ) );
		g2D.fillRect( selection.x, selection.y, selection.width, selection.height );
		
		g2D.setColor( UserSettings.instance.selectionBoxLine.get( ) );
		g2D.drawRect( selection.x, selection.y, selection.width, selection.height );
	}
	
	public void paintViewport( Graphics2D g2D )
	{
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
		
		// Apply the transformation
		AffineTransform original = g2D.getTransform( );
		original.concatenate( this.transform );
		g2D.setTransform( original );
		
		// Paint the graph
		GraphDisplayView.paint( g2D, this.graph, this.settings );
		
		// Paint controller-specific stuff
		if( this.isMouseDownOnCanvas )
			switch( this.tool )
			{
				case POINTER_TOOL:
					if( !this.pointerToolClickedObject )
						this.paintSelectionRectangle( g2D );
					break;
				case CUT_TOOL:
					if( !this.cutToolClickedObject )
						this.paintSelectionRectangle( g2D );
					break;
				case GRAPH_TOOL:
					// For the edge tool we might need to paint the temporary drag-edge
					if( this.fromVertex != null )
					{
						g2D.setColor( UserSettings.instance.draggingEdge.get( ) );
						g2D.drawLine( this.fromVertex.x.get( ).intValue( ), this.fromVertex.y.get( ).intValue( ), this.currentMousePoint.x, this.currentMousePoint.y );
					}	
					break;
			}
	}
	
	public void paste( )
	{
		String result = "";
		Clipboard clipboard = Toolkit.getDefaultToolkit( ).getSystemClipboard( );
		
		Transferable contents = clipboard.getContents( null );
		boolean hasTransferableText = ( contents != null ) && contents.isDataFlavorSupported( DataFlavor.stringFlavor );
		
		if( hasTransferableText )
			try
			{
				result = (String) contents.getTransferData( DataFlavor.stringFlavor );
				
				Graph pasted = new Graph( result );
				
				// Find the centroid
				double elementCount = 0.0;
				Point2D.Double centroid = new Point2D.Double( 0.0, 0.0 );
				
				for( Vertex vertex : pasted.vertices )
				{
					centroid.x += vertex.x.get( );
					centroid.y += vertex.y.get( );
					++elementCount;
				}
				
				for( Edge edge : pasted.edges )
				{
					centroid.x += edge.handleX.get( );
					centroid.y += edge.handleY.get( );
					++elementCount;
				}
				
				centroid.x /= elementCount;
				centroid.y /= elementCount;
				
				// Center everything around the mouse or in the center of the viewport
				Point2D.Double pastePoint = new Point2D.Double( );
				
				if( this.isMouseOverViewport )
					pastePoint = new Point2D.Double( this.currentMousePoint.x, this.currentMousePoint.y );
				else
					this.transform.inverseTransform( new Point2D.Double( this.viewport.getWidth( ) / 2.0, this.viewport.getHeight( ) / 2.0 ), pastePoint );
				
				pasted.suspendNotifications( true );
				
				for( Edge edge : pasted.edges )
					edge.suspendNotifications( true );
				
				for( Vertex vertex : pasted.vertices )
				{
					vertex.x.set( vertex.x.get( ) - centroid.x + pastePoint.x );
					vertex.y.set( vertex.y.get( ) - centroid.y + pastePoint.y );
				}
				
				for( Edge edge : pasted.edges )
				{
					edge.handleX.set( edge.handleX.get( ) - centroid.x + pastePoint.x );
					edge.handleY.set( edge.handleY.get( ) - centroid.y + pastePoint.y );
					edge.suspendNotifications( false );
				}

				pasted.suspendNotifications( false );
				
				this.graph.selectAll( false );
				this.graph.union( pasted );
			}
			catch( Exception ex )
			{
				System.out.println( "An exception occurred while painting the viewport." );
			}
	}
	
	public void printGraph( ) throws PrinterException
	{
		new ViewportPrinter( ).print( );
	}
	
	public void redo( )
	{
		if( this.undoHistory.next( ) != null )
			this.setGraph( new Graph( this.undoHistory.current( ) ) );
	}
	
	public void removeAllPaths( )
    {
	    this.nonIntersectingPaths.clear( );
    }
	
	public void removeGraphChangeListener( GraphChangeEventListener listener )
	{
		this.graphChangeListenerList.remove( GraphChangeEventListener.class, listener );
	}
	
	public void selectAll( )
	{
		this.graph.selectAll( true );
	}
	
	public void selectAllEdges( )
	{
		for( Edge edge : this.graph.edges )
			edge.isSelected.set( true );
	}
	
	public void selectAllVertices( )
	{
		for( Vertex vertex : this.graph.vertices )
			vertex.isSelected.set( true );
	}
	
    protected void selectAllPaths( boolean selected )
    {
        this.nonIntersectingPaths.setSelected( selected );
    }
    
	/**
	 * @param graph Graph
	 */
	public void setGraph( Graph graph )
	{
		this.graph = graph;
		graph.addObserver( new Observer( )
		{
			@Override
			public void update( Observable o, Object arg )
			{
				GraphDisplayController.this.onGraphChanged( arg );
			}
		} );
		
		this.isMouseDownOnCanvas = false;
		this.currentMousePoint = new Point( 0, 0 );
		this.pastMousePoint = new Point( 0, 0 );
		this.pointerToolClickedObject = false;
		this.cutToolClickedObject = false;
		this.fromVertex = null;
		// reinicializar los paths
		this.nonIntersectingPaths = new NonIntersectingPathList( );
		if ( this.pathToolBar != null )
			this.pathToolBar.refresh( );
		
		this.isViewportInvalidated = true;
	}
	
	/**
	 * @param tool Tool
	 */
	public void setTool( Tool tool )
	{
		this.tool = tool;
		if( this.toolToolBar != null )
			this.toolToolBar.refresh( );
		
		Cursor cursor;
		
		switch( this.tool )
		{
//			case POINTER_TOOL:
//				cursor = CursorBundle.get( "pointer_tool_cursor" );
//				break;
			case GRAPH_TOOL:
				cursor = CursorBundle.get( "graph_tool_cursor" );
				break;
			case CUT_TOOL:
				cursor = CursorBundle.get( "cut_tool_cursor" );
				break;
			default:
				cursor = Cursor.getPredefinedCursor( Cursor.DEFAULT_CURSOR );
				break;
		}
		
		this.setCursor( cursor );
		
		this.fromVertex = null;
		this.graph.selectAll( false );
	}
	
	public void undo( )
	{
		if( this.undoHistory.previous( ) != null )
			this.setGraph( new Graph( this.undoHistory.current( ) ) );
	}
	
	private void viewportKeyPressed( KeyEvent event )
	{
		switch( event.getKeyCode( ) )
		{
			case KeyEvent.VK_BACK_SPACE: // Fall through...
			case KeyEvent.VK_DELETE:
				if( this.tool == Tool.POINTER_TOOL )
				{
                    // borrar todos los paths con las aristas y los vertices seleccionados
                    this.nonIntersectingPaths.removeAllVertices( this.graph.getSelectedVertices( ) );
                    this.nonIntersectingPaths.removeAllEdges( this.graph.getSelectedEdges( ) );
                    
					this.graph.vertices.removeAll( this.graph.getSelectedVertices( ) );
					this.graph.edges.removeAll( this.graph.getSelectedEdges( ) );
				}
				
				break;
			case KeyEvent.VK_ESCAPE:
				this.graph.selectAll( false );
				this.fromVertex = null;
				if( this.panTimer != null )
					this.panTimer.stop( );
				
				break;
			case KeyEvent.VK_UP:
			case KeyEvent.VK_RIGHT:
			case KeyEvent.VK_DOWN:
			case KeyEvent.VK_LEFT:
				double increment = UserSettings.instance.arrowKeyIncrement.get( );
				
				if( event.isAltDown( ) )
					increment /= 10.0;
				if( event.isShiftDown( ) )
					increment *= 10.0;
				
				if( ( event.getModifiers( ) & Toolkit.getDefaultToolkit( ).getMenuShortcutKeyMask( ) ) != 0 )
				{
					switch( event.getKeyCode( ) )
					{
						case KeyEvent.VK_UP:
							this.transform.translate( 0.0, increment );
							break;
						case KeyEvent.VK_RIGHT:
							this.transform.translate( -increment, 0.0 );
							break;
						case KeyEvent.VK_DOWN:
							this.transform.translate( 0.0, -increment );
							break;
						case KeyEvent.VK_LEFT:
							this.transform.translate( increment, 0.0 );
							break;
					}
					
					this.isViewportInvalidated = true;
				}
				else
					switch( event.getKeyCode( ) )
					{
						case KeyEvent.VK_UP:
							this.graph.translateSelected( 0.0, -increment );
							break;
						case KeyEvent.VK_RIGHT:
							this.graph.translateSelected( increment, 0.0 );
							break;
						case KeyEvent.VK_DOWN:
							this.graph.translateSelected( 0.0, increment );
							break;
						case KeyEvent.VK_LEFT:
							this.graph.translateSelected( -increment, 0.0 );
							break;
					}
				
				for( Edge edge : this.graph.getSelectedEdges( ) )
					if( edge.isLinear( ) && !edge.isLoop )
						edge.reset( );
				
				break;
		}
	}
	
	private void viewportMouseDoubleClicked( MouseEvent event ) throws NoninvertibleTransformException {}
	
	private void viewportMouseDragged( MouseEvent event ) throws NoninvertibleTransformException
	{
		Point oldPoint = new Point( this.currentMousePoint );
		this.transform.inverseTransform( event.getPoint( ), this.currentMousePoint );
		
		if( this.tool == Tool.POINTER_TOOL && this.pointerToolClickedObject )
			this.graph.translateSelected( this.currentMousePoint.getX( ) - oldPoint.x, this.currentMousePoint.getY( ) - oldPoint.y );
		
		this.isViewportInvalidated = true;
	}
	
	private void viewportMousePressed( MouseEvent event ) throws NoninvertibleTransformException
	{
		if( !this.viewport.hasFocus( ) )
			this.viewport.requestFocus( );
		
		int modifiers = event.getModifiersEx( );
		this.transform.inverseTransform( event.getPoint( ), this.pastMousePoint );
		this.transform.inverseTransform( event.getPoint( ), this.currentMousePoint );
		
		switch( this.tool )
		{
			case POINTER_TOOL:
				boolean isCtrlDown = ( ( modifiers & InputEvent.CTRL_DOWN_MASK ) == InputEvent.CTRL_DOWN_MASK );
				this.pointerToolClickedObject = false;
				
				for( int i = this.graph.vertices.size( ) - 1; i >= 0; --i )
				{
					Vertex vertex = this.graph.vertices.get( i );
					if( VertexDisplayView.wasClicked( vertex, this.currentMousePoint, this.transform.getScaleX( ) ) )
					{
						if( !vertex.isSelected.get( ) && !isCtrlDown )
							this.graph.selectAll( false );
						
						vertex.isSelected.set( true );
						this.pointerToolClickedObject = true;
						break;
					}
				}
				
				if( !this.pointerToolClickedObject )
					for( int i = this.graph.edges.size( ) - 1; i >= 0; --i )
					{
						Edge edge = this.graph.edges.get( i );
						if( EdgeDisplayView.wasClicked( edge, this.currentMousePoint, this.transform.getScaleX( ) ) )
						{
							if( !edge.isSelected.get( ) && !isCtrlDown )
								this.graph.selectAll( false );
							
							edge.isSelected.set( true );
							this.pointerToolClickedObject = true;
							break;
						}
					}
				
				if( !this.pointerToolClickedObject && !isCtrlDown )
					this.graph.selectAll( false );
				
				break;
			case GRAPH_TOOL:
				if( event.getButton( ) == MouseEvent.BUTTON1 )
				{
					// The procedure for adding an edge using the edge tool is to click a vertex the edge will come from and subsequently a vertex
					// the edge will go to
					boolean fromVertexClicked = false;
					boolean toVertexClicked = false;
					
					for( Vertex vertex : this.graph.vertices )
						if( VertexDisplayView.wasClicked( vertex, this.currentMousePoint, this.transform.getScaleX( ) ) )
							if( this.fromVertex == null )
							{
								// If the user has not yet defined a from Vertex, make this one so
								vertex.isSelected.set( true );
								this.fromVertex = vertex;
								fromVertexClicked = true;
								break;
							}
							else
							{
								// If the user has already defined a from Vertex, try to add an edge between it and this one
								if( this.graph.edges.add( new Edge( this.graph.areDirectedEdgesAllowed, this.fromVertex, vertex ) ) )
								{
									this.fromVertex.isSelected.set( false );
									this.fromVertex = !UserSettings.instance.deselectVertexWithNewEdge.get( ) ? vertex : null;
									if( this.fromVertex != null )
										this.fromVertex.isSelected.set( true );
									toVertexClicked = true;
								}
								else
									Toolkit.getDefaultToolkit( ).beep( );
								
								if( !toVertexClicked )
								{
									this.fromVertex.isSelected.set( false );
									this.fromVertex = null;
									fromVertexClicked = true;
								}
							}
					
					if( !fromVertexClicked && !toVertexClicked )
						this.graph.vertices.add( new Vertex( this.currentMousePoint.x, this.currentMousePoint.y, this.graph.getNewLabelAvailable() ) );
				}
				
				break;

			case CUT_TOOL:
				this.cutToolClickedObject = false;
				
				if( event.getButton( ) == MouseEvent.BUTTON1 )
				{
					for( Vertex vertex : this.graph.vertices )
						if( VertexDisplayView.wasClicked( vertex, this.currentMousePoint, this.transform.getScaleX( ) ) )
						{
							this.graph.vertices.remove( vertex );
							// borrar todos los paths el vertice seleccionado
							this.nonIntersectingPaths.removeVertex( vertex );
							
							this.cutToolClickedObject = true;
							break;
						}
					
					if( !this.cutToolClickedObject )
						for( Edge edge : this.graph.edges )
							if( EdgeDisplayView.wasClicked( edge, this.currentMousePoint, this.transform.getScaleX( ) ) )
							{
								this.graph.edges.remove( edge );
								// borrar todos los paths con la arista seleccionada
								this.nonIntersectingPaths.removeEdge( edge );
								
								this.cutToolClickedObject = true;
								break;
							}

				}
				
				break;
		}
		
		this.isMouseDownOnCanvas = true;
		this.isViewportInvalidated = true;
	}
	
	private void viewportMouseReleased( MouseEvent event ) throws NoninvertibleTransformException
	{
		switch( this.tool )
		{
			case POINTER_TOOL:
				if( !this.pastMousePoint.equals( this.currentMousePoint ) )
					if( !this.pointerToolClickedObject )
					{
						Rectangle selection = this.getSelectionRectangle( );
						
						for( Vertex vertex : this.graph.vertices )
							if( VertexDisplayView.wasSelected( vertex, selection ) )
								vertex.isSelected.set( true );
						
						for( Edge edge : this.graph.edges )
							if( EdgeDisplayView.wasSelected( edge, selection ) )
								edge.isSelected.set( true );

					}
					else
						for( Edge edge : this.graph.getSelectedEdges( ) )
							if( edge.isLinear( ) && !edge.isLoop )
								edge.reset( );
				
				if( event.getButton( ) == MouseEvent.BUTTON3 )
				{
					this.viewportPopupMenu.setVertexMenuEnabled( this.graph.hasSelectedVertices( ) );
					this.viewportPopupMenu.setEdgeMenuEnabled( this.graph.hasSelectedEdges( ) );
					this.viewportPopupMenu.show( this.viewport, event.getPoint( ).x, event.getPoint( ).y );
				}
				
				break;
			case GRAPH_TOOL:
				if( this.fromVertex != null )
					for( Vertex vertex : this.graph.vertices )
						if( this.fromVertex != vertex && VertexDisplayView.wasClicked( vertex, this.currentMousePoint, this.transform.getScaleX( ) ) )
						    if( this.graph.edges.add( new Edge( this.graph.areDirectedEdgesAllowed, this.fromVertex, vertex ) ) )
							{
								this.fromVertex.isSelected.set( false );
								this.fromVertex = !UserSettings.instance.deselectVertexWithNewEdge.get( ) ? vertex : null;
								if( this.fromVertex != null )
									this.fromVertex.isSelected.set( true );
							}
							else
								Toolkit.getDefaultToolkit( ).beep( );
				
				break;
			case CUT_TOOL:
				if( !this.cutToolClickedObject && !this.pastMousePoint.equals( this.currentMousePoint ) )
				{
					Rectangle selection = this.getSelectionRectangle( );
					this.graph.selectAll( false );
					
					for( Vertex vertex : this.graph.vertices )
						if( VertexDisplayView.wasSelected( vertex, selection ) )
							vertex.isSelected.set( true );
					
					for( Edge edge : this.graph.edges )
						if( EdgeDisplayView.wasSelected( edge, selection ) )
							edge.isSelected.set( true );
					        
					// borrar todos los paths con las aristas y los vertices seleccionados
					this.nonIntersectingPaths.removeAllVertices( this.graph.getSelectedVertices( ) );
					this.nonIntersectingPaths.removeAllEdges( this.graph.getSelectedEdges( ) );
					
					this.graph.vertices.removeAll( this.graph.getSelectedVertices( ) );
					this.graph.edges.removeAll( this.graph.getSelectedEdges( ) );
				}
				
				break;
		}
		
		this.isMouseDownOnCanvas = false;
		this.isViewportInvalidated = true;
	}
	
	public void zoomCenter( Point2D.Double center, double factor )
	{
		this.transform.translate( Math.round( center.x ), Math.round( center.y ) );
		
		this.transform.scale( factor, factor );
		if( this.transform.getScaleX( ) > UserSettings.instance.maximumZoomFactor.get( ) )
			this.zoomMax( );
		
		this.transform.translate( Math.round( -center.x ), Math.round( -center.y ) );
		
		this.isViewportInvalidated = true;
	}
	
	public void zoomFit( )
	{
		if( this.graph.vertices.size( ) > 0 )
			this.zoomFit( GeometryUtilities.getBounds( this.graph ) );
	}
	
	public void zoomFit( Rectangle2D rectangle )
	{
		// First we need to reset and translate the graph to the viewport's center
		this.transform.setToIdentity( );
		this.transform.translate( Math.round( this.viewport.getWidth( ) / 2.0 ), Math.round( this.viewport.getHeight( ) / 2.0 ) );
		
		// We need to fit it to the viewport. So we want to scale according to the lowest viewport-to-graph dimension ratio.
		double widthRatio = ( this.viewport.getWidth( ) - UserSettings.instance.zoomGraphPadding.get( ) ) / rectangle.getWidth( );
		double heightRatio = ( this.viewport.getHeight( ) - UserSettings.instance.zoomGraphPadding.get( ) ) / rectangle.getHeight( );
		double minRatio = Math.min( widthRatio, heightRatio );
		
		if( minRatio < 1 )
			this.transform.scale( minRatio, minRatio );
		
		// Only now that we've properly scaled can we translate to the graph's center
		Point2D.Double graphCenter = new Point2D.Double( rectangle.getCenterX( ), rectangle.getCenterY( ) );
		this.transform.translate( Math.round( -graphCenter.x ), Math.round( -graphCenter.y ) );
		
		// And of course, we want to refresh the viewport
		this.isViewportInvalidated = true;
	}
	
	public void zoomMax( )
	{
		this.transform.setTransform( UserSettings.instance.maximumZoomFactor.get( ), this.transform.getShearY( ), this.transform.getShearX( ), UserSettings.instance.maximumZoomFactor.get( ), Math.round( this.transform.getTranslateX( ) ), Math.round( this.transform.getTranslateY( ) ) );
		this.isViewportInvalidated = true;
	}
	
	public void zoomOneToOne( )
	{
		this.transform.setTransform( 1, this.transform.getShearY( ), this.transform.getShearX( ), 1, (int) this.transform.getTranslateX( ), (int) this.transform.getTranslateY( ) );
		this.isViewportInvalidated = true;
	}
}

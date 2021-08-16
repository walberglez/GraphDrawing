/**
 * MainWindow.java
 */
package pfc.gui.windows;

import java.io.*;
import java.awt.*;
import java.util.*;
import java.beans.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.print.*;
import javax.swing.event.*;
import javax.swing.filechooser.*;

import pfc.controllers.GraphDisplayController;
import pfc.gui.dialogs.*;
import pfc.gui.windows.algorithms.ConstrainedUpwardPolylineWindow;
import pfc.gui.windows.algorithms.ConstrainedVisRepWindow;
import pfc.gui.windows.algorithms.OrthogonalWindow;
import pfc.gui.windows.algorithms.PolylineDominanceWindow;
import pfc.gui.windows.algorithms.SLDominanceWindow;
import pfc.gui.windows.algorithms.UpwardPolylineWindow;
import pfc.gui.windows.algorithms.VisibilityRepWindow;
import pfc.models.*;
import pfc.models.algorithms.NonIntersectingPathList;
import pfc.resources.*;
import pfc.settings.*;
import pfc.utilities.*;


/**
 * @author Cameron Behar
 */
public class MainWindow extends JFrame
{	
	private static final long serialVersionUID = 8366309116024014649L;
	
	private final JMenuBar		menuBar;
	private final JMenu			fileMenu;
	private final JMenuItem		newGraphMenuItem;
	private final JMenuItem		duplicateGraphMenuItem;
	private final JMenuItem		openGraphMenuItem;
	private final JMenuItem		saveGraphMenuItem;
	private final JMenuItem		saveAsGraphMenuItem;
	private final JMenuItem		printGraphMenuItem;
	private final JMenuItem		exitGraphMenuItem;
	private final JMenu			editMenu;
	private final JMenuItem		undoMenuItem;
	private final JMenuItem		redoMenuItem;
	private final JMenuItem		cutMenuItem;
	private final JMenuItem		copyMenuItem;
	private final JMenuItem		pasteMenuItem;
	private final JMenuItem		selectAllMenuItem;
	private final JMenuItem		selectAllVerticesMenuItem;
	private final JMenuItem		selectAllEdgesMenuItem;
	private final JMenu			algorithmsMenu;                	// menu de algoritmos
	private final JMenuItem		visibilityMenuItem;            	// item de menu visibilidad
	private final JMenuItem     constrainedVisMenuItem;        	// item de menu visibilidad con restricciones
	private final JMenuItem     upwardPolylineMenuItem;        	// item de menu upward polyline
	private final JMenuItem     constrainedPolylineMenuItem;   	// item de menu constrained upward polyline
	private final JMenuItem     orthogonalMenuItem;   			// item de menu orthogonal
	private final JMenuItem     slDominanceMenuItem;			// item de menu straight-line dominance
	private final JMenuItem     polylineDominanceMenuItem;		// item de menu polyline dominance
	private final JMenu			windowsMenu;
	private final JMenuItem		cascadeMenuItem;
	private final JMenuItem		showSideBySideMenuItem;
	private final JMenuItem		showStackedMenuItem;
	private final JMenuItem		tileWindowsMenuItem;
	private final JMenuItem		showPreviousMenuItem;
	private final JMenuItem		showNextMenuItem;
	private final JMenu			helpMenu;
	private final JMenuItem		helpContentsMenuItem;
	private final JMenuItem		aboutVisiGraphMenuItem;
	private final JDesktopPane	desktopPane;
	private final JFileChooser	fileChooser;
	
	@SuppressWarnings("serial")
	public MainWindow( )
	{
		super( GlobalSettings.applicationName );
		this.setIconImage( ImageBundle.get( "app_icon_128x128" ) );
		this.setSize( new Dimension( UserSettings.instance.mainWindowWidth.get( ), UserSettings.instance.mainWindowHeight.get( ) ) );
		this.setDefaultCloseOperation( DO_NOTHING_ON_CLOSE );
		this.setLocationRelativeTo( null );
		this.addWindowListener( new WindowListener( )
		{
			@Override
			public void windowActivated( WindowEvent e )
			{}
			
			@Override
			public void windowClosed( WindowEvent e )
			{}
			
			@Override
			public void windowClosing( WindowEvent e )
			{
				MainWindow.this.closingWindow( e );
			}
			
			@Override
			public void windowDeactivated( WindowEvent e )
			{}
			
			@Override
			public void windowDeiconified( WindowEvent e )
			{}
			
			@Override
			public void windowIconified( WindowEvent e )
			{}
			
			@Override
			public void windowOpened( WindowEvent e )
			{}
		} );
		
		this.desktopPane = new JDesktopPane( );
		this.desktopPane.setBackground( GlobalSettings.defaultDesktopPaneColor );
		this.getContentPane( ).add( this.desktopPane, BorderLayout.CENTER );
		
		this.menuBar = new JMenuBar( );
		
		this.fileChooser = new JFileChooser( );
		
		this.fileMenu = new JMenu( StringBundle.get( "file_menu_text" ) );
		this.menuBar.add( this.fileMenu );
		
		this.newGraphMenuItem = new JMenuItem( StringBundle.get( "file_new_menu_text" ), ImageIconBundle.get( "new" ) )
		{
			{
				this.addActionListener( new ActionListener( )
				{
					@Override
					public void actionPerformed( ActionEvent e )
					{
						Graph newGraph = NewGraphDialog.showDialog( MainWindow.this );
						if( newGraph != null )
						{	
							MainWindow.this.addGraphWindow( newGraph );
						}
					}
				} );
				this.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_N, Toolkit.getDefaultToolkit( ).getMenuShortcutKeyMask( ) ) );
			}
		};
		this.fileMenu.add( this.newGraphMenuItem );
		
		this.duplicateGraphMenuItem = new JMenuItem( StringBundle.get( "file_duplicate_menu_text" ), ImageIconBundle.get( "duplicate" ) )
		{
			{
				this.addActionListener( new ActionListener( )
				{
					@Override
					public void actionPerformed( ActionEvent e )
					{
						JInternalFrame selectedFrame = MainWindow.this.desktopPane.getSelectedFrame( );
						
						if( selectedFrame instanceof GraphWindow )
						{
							Graph graph = ( (GraphWindow) selectedFrame ).getGdc( ).getGraph( );
							MainWindow.this.addGraphWindow( new Graph( graph.toString( ) ) );
						}
					}
				} );
				this.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_D, Toolkit.getDefaultToolkit( ).getMenuShortcutKeyMask( ) ) );
			}
		};
		this.fileMenu.add( this.duplicateGraphMenuItem );
		
		this.fileMenu.addSeparator( );
		
		this.openGraphMenuItem = new JMenuItem( StringBundle.get( "file_open_menu_text" ), ImageIconBundle.get( "open" ) )
		{
			{
				this.addActionListener( new ActionListener( )
				{
					@Override
					public void actionPerformed( ActionEvent e )
					{
						MainWindow.this.fileChooser.resetChoosableFileFilters( );
						MainWindow.this.fileChooser.setAcceptAllFileFilterUsed( false );
						MainWindow.this.fileChooser.setFileFilter( new FileNameExtensionFilter( StringBundle.get( "visigraph_file_description" ), "vsg" ) );
						MainWindow.this.fileChooser.setMultiSelectionEnabled( true );
						
						boolean success = false;
						
						while( !success )
						{
							success = false;
							
							if( MainWindow.this.fileChooser.showOpenDialog( MainWindow.this ) == JFileChooser.APPROVE_OPTION )
								try
								{
									for( File selectedFile : MainWindow.this.fileChooser.getSelectedFiles( ) )
									{
										MainWindow.this.openFile( selectedFile );
										success = true;
									}
								}
								catch( IOException ex )
								{
									System.out.println( "An exception occurred while loading a graph from file." );
								}
							else
								success = true;
						}
					}
				} );
				this.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_O, Toolkit.getDefaultToolkit( ).getMenuShortcutKeyMask( ) ) );
			}
		};
		this.fileMenu.add( this.openGraphMenuItem );

		this.saveGraphMenuItem = new JMenuItem( StringBundle.get( "file_save_menu_text" ), ImageIconBundle.get( "save" ) )
		{
			{
				this.addActionListener( new ActionListener( )
				{
					@Override
					public void actionPerformed( ActionEvent e )
					{
						JInternalFrame selectedFrame = MainWindow.this.desktopPane.getSelectedFrame( );
						
						if( selectedFrame instanceof GraphWindow )
						{
							GraphWindow graphWindow = ( (GraphWindow) selectedFrame );

							if( graphWindow != null )
								try
							{
									graphWindow.save( );
							}
							catch( IOException ex )
							{
								System.out.println( "An exception occurred while saving the selected graph." );
							}
						}
					}
				} );
				this.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_S, Toolkit.getDefaultToolkit( ).getMenuShortcutKeyMask( ) ) );
			}
		};
		this.fileMenu.add( this.saveGraphMenuItem );
		
		this.saveAsGraphMenuItem = new JMenuItem( StringBundle.get( "file_save_as_menu_text" ), ImageIconBundle.get( "save_as" ) )
		{
			{
				this.addActionListener( new ActionListener( )
				{
					@Override
					public void actionPerformed( ActionEvent e )
					{
						JInternalFrame selectedFrame = MainWindow.this.desktopPane.getSelectedFrame( );
						
						if( selectedFrame instanceof GraphWindow )
						{
							GraphWindow graphWindow = ( (GraphWindow) selectedFrame );
						
							if ( graphWindow != null )
								graphWindow.saveAs( );
						}
					}
				} );
			}
		};
		this.fileMenu.add( this.saveAsGraphMenuItem );
		
		this.fileMenu.addSeparator( );
		
		this.printGraphMenuItem = new JMenuItem( StringBundle.get( "file_print_menu_text" ), ImageIconBundle.get( "print" ) )
		{
			{
				this.addActionListener( new ActionListener( )
				{
					@Override
					public void actionPerformed( ActionEvent e )
					{
						JInternalFrame selectedFrame = MainWindow.this.desktopPane.getSelectedFrame( );
						if( selectedFrame instanceof GraphWindow )
						{
							GraphWindow graphWindow = ( (GraphWindow) selectedFrame );
							try
							{
								graphWindow.getGdc( ).printGraph( );
							}
							catch( PrinterException ex )
							{
								System.out.println( "An exception occurred while printing the selected graph." );
							}
						}
					}
				} );
				this.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_P, Toolkit.getDefaultToolkit( ).getMenuShortcutKeyMask( ) ) );
			}
		};
		this.fileMenu.add( this.printGraphMenuItem );
		
		if( !System.getProperty( "os.name" ).startsWith( "Mac" ) )
		{
			this.fileMenu.addSeparator( );
			
			this.exitGraphMenuItem = new JMenuItem( StringBundle.get( "file_exit_menu_text" ), ImageIconBundle.get( "exit" ) )
			{
				{
					this.addActionListener( new ActionListener( )
					{
						@Override
						public void actionPerformed( ActionEvent e )
						{
							System.exit( 0 );
						}
					} );
				}
			};
			this.fileMenu.add( this.exitGraphMenuItem );
		}
		else
			this.exitGraphMenuItem = null;
		
		this.editMenu = new JMenu( StringBundle.get( "edit_menu_text" ) );
		this.menuBar.add( this.editMenu );
		
		this.undoMenuItem = new JMenuItem( StringBundle.get( "edit_undo_menu_text" ), ImageIconBundle.get( "undo" ) )
		{
			{
				this.addActionListener( new ActionListener( )
				{
					@Override
					public void actionPerformed( ActionEvent e )
					{
						JInternalFrame selectedFrame = MainWindow.this.desktopPane.getSelectedFrame( );
						
						if( selectedFrame instanceof GraphWindow )
							( (GraphWindow) selectedFrame ).getGdc( ).undo( );
					}
				} );
				this.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_Z, Toolkit.getDefaultToolkit( ).getMenuShortcutKeyMask( ) ) );
			}
		};
		this.editMenu.add( this.undoMenuItem );
		
		this.redoMenuItem = new JMenuItem( StringBundle.get( "edit_redo_menu_text" ), ImageIconBundle.get( "redo" ) )
		{
			{
				this.addActionListener( new ActionListener( )
				{
					@Override
					public void actionPerformed( ActionEvent e )
					{
						JInternalFrame selectedFrame = MainWindow.this.desktopPane.getSelectedFrame( );
						
						if( selectedFrame instanceof GraphWindow )
							( (GraphWindow) selectedFrame ).getGdc( ).redo( );
					}
				} );
				this.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_Y, Toolkit.getDefaultToolkit( ).getMenuShortcutKeyMask( ) ) );
			}
		};
		this.editMenu.add( this.redoMenuItem );
		
		this.editMenu.addSeparator( );
		
		this.cutMenuItem = new JMenuItem( StringBundle.get( "edit_cut_menu_text" ), ImageIconBundle.get( "cut" ) )
		{
			{
				this.addActionListener( new ActionListener( )
				{
					@Override
					public void actionPerformed( ActionEvent e )
					{
						JInternalFrame selectedFrame = MainWindow.this.desktopPane.getSelectedFrame( );
						
						if( selectedFrame instanceof GraphWindow )
							( (GraphWindow) selectedFrame ).getGdc( ).cut( );
					}
				} );
				this.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_X, Toolkit.getDefaultToolkit( ).getMenuShortcutKeyMask( ) ) );
			}
		};
		this.editMenu.add( this.cutMenuItem );
		
		this.copyMenuItem = new JMenuItem( StringBundle.get( "edit_copy_menu_text" ), ImageIconBundle.get( "copy" ) )
		{
			{
				this.addActionListener( new ActionListener( )
				{
					@Override
					public void actionPerformed( ActionEvent e )
					{
						JInternalFrame selectedFrame = MainWindow.this.desktopPane.getSelectedFrame( );
						
						if( selectedFrame instanceof GraphWindow )
							( (GraphWindow) selectedFrame ).getGdc( ).copy( );
					}
				} );
				this.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_C, Toolkit.getDefaultToolkit( ).getMenuShortcutKeyMask( ) ) );
			}
		};
		this.editMenu.add( this.copyMenuItem );
		
		this.pasteMenuItem = new JMenuItem( StringBundle.get( "edit_paste_menu_text" ), ImageIconBundle.get( "paste" ) )
		{
			{
				this.addActionListener( new ActionListener( )
				{
					@Override
					public void actionPerformed( ActionEvent e )
					{
						JInternalFrame selectedFrame = MainWindow.this.desktopPane.getSelectedFrame( );
						
						if( selectedFrame instanceof GraphWindow )
							( (GraphWindow) selectedFrame ).getGdc( ).paste( );
					}
				} );
				this.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_V, Toolkit.getDefaultToolkit( ).getMenuShortcutKeyMask( ) ) );
			}
		};
		this.editMenu.add( this.pasteMenuItem );
		
		this.editMenu.addSeparator( );
		
		this.selectAllMenuItem = new JMenuItem( StringBundle.get( "edit_select_all_menu_text" ), ImageIconBundle.get( "select_all" ) )
		{
			{
				this.addActionListener( new ActionListener( )
				{
					public void actionPerformed( ActionEvent e )
					{
						JInternalFrame selectedFrame = MainWindow.this.desktopPane.getSelectedFrame( );
						
						if( selectedFrame instanceof GraphWindow )
							( (GraphWindow) selectedFrame ).getGdc( ).selectAll( );
					}
				} );
				this.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_A, Toolkit.getDefaultToolkit( ).getMenuShortcutKeyMask( ) ) );
			}
		};
		this.editMenu.add( this.selectAllMenuItem );
		
		this.selectAllVerticesMenuItem = new JMenuItem( StringBundle.get( "edit_select_all_vertices_menu_text" ), ImageIconBundle.get( "select_vertices" ) )
		{
			{
				this.addActionListener( new ActionListener( )
				{
					public void actionPerformed( ActionEvent e )
					{
						JInternalFrame selectedFrame = MainWindow.this.desktopPane.getSelectedFrame( );
						
						if( selectedFrame instanceof GraphWindow )
							( (GraphWindow) selectedFrame ).getGdc( ).selectAllVertices( );
					}
				} );
			}
		};
		this.editMenu.add( this.selectAllVerticesMenuItem );
		
		this.selectAllEdgesMenuItem = new JMenuItem( StringBundle.get( "edit_select_all_edges_menu_text" ), ImageIconBundle.get( "select_edges" ) )
		{
			{
				this.addActionListener( new ActionListener( )
				{
					public void actionPerformed( ActionEvent e )
					{
						JInternalFrame selectedFrame = MainWindow.this.desktopPane.getSelectedFrame( );
						
						if( selectedFrame instanceof GraphWindow )
							( (GraphWindow) selectedFrame ).getGdc( ).selectAllEdges( );
					}
				} );
			}
		};
		this.editMenu.add( this.selectAllEdgesMenuItem );
		
		// Configuracion del menu de Algoritmos		
		this.algorithmsMenu = new JMenu( StringBundle.get( "algorithms_menu_text" ) );
		this.menuBar.add( this.algorithmsMenu );
		
		this.visibilityMenuItem = new JMenuItem( StringBundle.get( "algorithm_visibility_representation_menu_text" ), ImageIconBundle.get( "algorithm" ) )
		{
			{
				this.addActionListener( new ActionListener( )
				{
					@Override
					public void actionPerformed( ActionEvent e )
					{
						JInternalFrame selectedFrame = MainWindow.this.desktopPane.getSelectedFrame( );
						
						if ( selectedFrame instanceof GraphWindow )
						{
							Graph graph = ( (GraphWindow) selectedFrame ).getGdc( ).getGraph( );							
							// Realizar comprobaciones sobre el tipo de grafo
							if ( graph.vertices.size() > 0 && graph.edges.size() > 0 )
							{
							    if ( GraphUtilities.isPlanarEmbedding( graph ) &&
							    		GraphUtilities.isSTGraph( graph ) &&
							    		GraphUtilities.areSTOnOuterFace( graph ) )
							    {
							        // Agregar la ventana de ejecucion del algoritmo
							        MainWindow.this.addVisibilityRepWindow( graph );
							    }
							    else
							    {
			                         JOptionPane.showMessageDialog( MainWindow.this, StringBundle.get( "error_algorithm_graph_dialog_message" ));
							    }
							}
						}
						else if ( selectedFrame != null ) 
						{
							JOptionPane.showMessageDialog( MainWindow.this, StringBundle.get( "error_algorithm_window_dialog_message" ));
						}
					}
				} );
			}
		};
		this.algorithmsMenu.add( this.visibilityMenuItem );
		
		this.constrainedVisMenuItem = new JMenuItem( StringBundle.get( "algorithm_constrained_vis_rep_menu_text" ), ImageIconBundle.get( "algorithm" ) )
        {
            {
                this.addActionListener( new ActionListener( )
                {
                    @Override
                    public void actionPerformed( ActionEvent e )
                    {
                        JInternalFrame selectedFrame = MainWindow.this.desktopPane.getSelectedFrame( );
                        
                        if( selectedFrame instanceof GraphWindow )
                        {
                            GraphDisplayController gdc = ( (GraphWindow) selectedFrame ).getGdc( );
                            Graph graph = gdc.getGraph( );
                            // Realizar comprobaciones sobre el tipo de grafo
                            if ( graph.vertices.size() > 0 && graph.edges.size() > 0 )
                            {
                                if ( GraphUtilities.isPlanarEmbedding( graph ) &&
                                		GraphUtilities.isSTGraph( graph ) &&
							    		GraphUtilities.areSTOnOuterFace( graph ) )
                                {
                                    // Agregar la ventana de ejecucion del algoritmo
                                    MainWindow.this.addConstrainedVisibilityRepWindow( graph, gdc.getNonIntersectingPaths( ) );
                                }
                                else
                                {
                                     JOptionPane.showMessageDialog( MainWindow.this, StringBundle.get( "error_algorithm_graph_dialog_message" ));
                                }
                            }
                        }
                        else if ( selectedFrame != null ) 
                        {
                            JOptionPane.showMessageDialog( MainWindow.this, StringBundle.get( "error_algorithm_window_dialog_message" ));
                        }
                    }
                } );
            }
        };
        this.algorithmsMenu.add( this.constrainedVisMenuItem );
        
        this.algorithmsMenu.addSeparator( );
        
        this.upwardPolylineMenuItem = new JMenuItem( StringBundle.get( "algorithm_upward_polyline_menu_text" ), ImageIconBundle.get( "algorithm" ) )
        {
            {
                this.addActionListener( new ActionListener( )
                {
                    @Override
                    public void actionPerformed( ActionEvent e )
                    {
                        JInternalFrame selectedFrame = MainWindow.this.desktopPane.getSelectedFrame( );
                        
                        if( selectedFrame instanceof GraphWindow )
                        {
                            GraphDisplayController gdc = ( (GraphWindow) selectedFrame ).getGdc( );
                            Graph graph = gdc.getGraph( );
                            // Realizar comprobaciones sobre el tipo de grafo
                            if ( graph.vertices.size() > 0 && graph.edges.size() > 0 )
                            {
                                if ( GraphUtilities.isPlanarEmbedding( graph ) &&
                                		GraphUtilities.isSTGraph( graph ) &&
							    		GraphUtilities.areSTOnOuterFace( graph ) )
                                {
                                    // Agregar la ventana de ejecucion del algoritmo
                                    MainWindow.this.addUpwardPolylineWindow( graph );
                                }
                                else
                                {
                                     JOptionPane.showMessageDialog( MainWindow.this, StringBundle.get( "error_algorithm_graph_dialog_message" ));
                                }
                            }
                        }
                        else if ( selectedFrame != null ) 
                        {
                            JOptionPane.showMessageDialog( MainWindow.this, StringBundle.get( "error_algorithm_window_dialog_message" ));
                        }
                    }
                } );
            }
        };
        this.algorithmsMenu.add( this.upwardPolylineMenuItem );
        
        this.constrainedPolylineMenuItem = new JMenuItem( StringBundle.get( "algorithm_constrained_upward_polyline_menu_text" ), ImageIconBundle.get( "algorithm" ) )
        {
            {
                this.addActionListener( new ActionListener( )
                {
                    @Override
                    public void actionPerformed( ActionEvent e )
                    {
                        JInternalFrame selectedFrame = MainWindow.this.desktopPane.getSelectedFrame( );
                        
                        if( selectedFrame instanceof GraphWindow )
                        {
                            GraphDisplayController gdc = ( (GraphWindow) selectedFrame ).getGdc( );
                            Graph graph = gdc.getGraph( );
                            // Realizar comprobaciones sobre el tipo de grafo
                            if ( graph.vertices.size() > 0 && graph.edges.size() > 0 )
                            {
                                if ( GraphUtilities.isPlanarEmbedding( graph ) &&
                                		GraphUtilities.isSTGraph( graph ) &&
							    		GraphUtilities.areSTOnOuterFace( graph ) )
                                {
                                    // Comprobar si son vertex disjoint los paths
                                	if ( gdc.getNonIntersectingPaths( ).areVertexDisjoint( ) )
                                	{
                                		// Agregar la ventana de ejecucion del algoritmo
                                		MainWindow.this.addConstrainedUpwardPolylineWindow( graph, gdc.getNonIntersectingPaths( ) );
                                	}
                                	else
                                	{
                                        JOptionPane.showMessageDialog( MainWindow.this, StringBundle.get( "error_algorithm_paths_dialog_message" ) );
                                	}
                                }
                                else
                                {
                                     JOptionPane.showMessageDialog( MainWindow.this, StringBundle.get( "error_algorithm_graph_dialog_message" ) );
                                }
                            }
                        }
                        else if ( selectedFrame != null ) 
                        {
                            JOptionPane.showMessageDialog( MainWindow.this, StringBundle.get( "error_algorithm_window_dialog_message" ) );
                        }
                    }
                } );
            }
        };
        this.algorithmsMenu.add( this.constrainedPolylineMenuItem );
        
        this.algorithmsMenu.addSeparator( );

        this.orthogonalMenuItem = new JMenuItem( StringBundle.get( "algorithm_orthogonal_menu_text" ), ImageIconBundle.get( "algorithm" ) )
        {
            {
                this.addActionListener( new ActionListener( )
                {
                    @Override
                    public void actionPerformed( ActionEvent e )
                    {
                        JInternalFrame selectedFrame = MainWindow.this.desktopPane.getSelectedFrame( );
                        
                        if( selectedFrame instanceof GraphWindow )
                        {
                            GraphDisplayController gdc = ( (GraphWindow) selectedFrame ).getGdc( );
                            Graph graph = gdc.getGraph( );
                            // Realizar comprobaciones sobre el tipo de grafo
                            if ( graph.vertices.size( ) > 0 && graph.edges.size( ) > 0 )
                            {
                            	if ( graph.areDirectedEdgesAllowed && GraphUtilities.isSTGraph( graph ) == false )
                            	{
                            		JOptionPane.showMessageDialog( MainWindow.this, StringBundle.get( "error_algorithm_graph_dialog_message" ) );
                            	}
                            	else if ( GraphUtilities.getMaximumDegree( graph ) <= 4 &&
                                		GraphUtilities.isPlanarEmbedding( graph ) &&
                                		GraphUtilities.isBiconnected( graph ) )
                                {
                                    // Agregar la ventana de ejecucion del algoritmo
                                    MainWindow.this.addOrthogonalWindow( graph );
                                }
                                else
                                {
                                     JOptionPane.showMessageDialog( MainWindow.this, StringBundle.get( "error_algorithm_orthogonal_graph_dialog_message" ));
                                }
                            }
                        }
                        else if ( selectedFrame != null ) 
                        {
                            JOptionPane.showMessageDialog( MainWindow.this, StringBundle.get( "error_algorithm_window_dialog_message" ));
                        }
                    }
                } );
            }
        };
        this.algorithmsMenu.add( this.orthogonalMenuItem );
        
        this.algorithmsMenu.addSeparator( );

        this.slDominanceMenuItem = new JMenuItem( StringBundle.get( "algorithm_sl_dominance_menu_text" ), ImageIconBundle.get( "algorithm" ) )
        {
            {
                this.addActionListener( new ActionListener( )
                {
                    @Override
                    public void actionPerformed( ActionEvent e )
                    {
                        JInternalFrame selectedFrame = MainWindow.this.desktopPane.getSelectedFrame( );
                        
                        if( selectedFrame instanceof GraphWindow )
                        {
                            GraphDisplayController gdc = ( (GraphWindow) selectedFrame ).getGdc( );
                            Graph graph = gdc.getGraph( );
                            // Realizar comprobaciones sobre el tipo de grafo
                            if ( graph.vertices.size() > 0 && graph.edges.size() > 0 )
                            {
                                if ( GraphUtilities.isPlanarEmbedding( graph ) &&
                                		GraphUtilities.isSTGraph( graph ) &&
							    		GraphUtilities.areSTOnOuterFace( graph ) &&
                                		GraphUtilities.isReducedDigraph( graph ) )
                                {
                                    // Agregar la ventana de ejecucion del algoritmo
                                    MainWindow.this.addStraightLineDominanceWindow( graph );
                                }
                                else
                                {
                                     JOptionPane.showMessageDialog( MainWindow.this, StringBundle.get( "error_algorithm_reduced_graph_dialog_message" ));
                                }
                            }
                        }
                        else if ( selectedFrame != null ) 
                        {
                            JOptionPane.showMessageDialog( MainWindow.this, StringBundle.get( "error_algorithm_window_dialog_message" ));
                        }
                    }
                } );
            }
        };
        this.algorithmsMenu.add( this.slDominanceMenuItem );
        
        this.polylineDominanceMenuItem = new JMenuItem( StringBundle.get( "algorithm_polyline_dominance_menu_text" ), ImageIconBundle.get( "algorithm" ) )
        {
            {
                this.addActionListener( new ActionListener( )
                {
                    @Override
                    public void actionPerformed( ActionEvent e )
                    {
                        JInternalFrame selectedFrame = MainWindow.this.desktopPane.getSelectedFrame( );
                        
                        if( selectedFrame instanceof GraphWindow )
                        {
                            GraphDisplayController gdc = ( (GraphWindow) selectedFrame ).getGdc( );
                            Graph graph = gdc.getGraph( );
                            // Realizar comprobaciones sobre el tipo de grafo
                            if ( graph.vertices.size() > 0 && graph.edges.size() > 0 )
                            {
                                if ( GraphUtilities.isPlanarEmbedding( graph ) &&
                                		GraphUtilities.isSTGraph( graph ) &&
							    		GraphUtilities.areSTOnOuterFace( graph ) )
                                {
                                    // Agregar la ventana de ejecucion del algoritmo
                                    MainWindow.this.addPolylineDominanceWindow( graph );
                                }
                                else
                                {
                                     JOptionPane.showMessageDialog( MainWindow.this, StringBundle.get( "error_algorithm_graph_dialog_message" ));
                                }
                            }
                        }
                        else if ( selectedFrame != null ) 
                        {
                            JOptionPane.showMessageDialog( MainWindow.this, StringBundle.get( "error_algorithm_window_dialog_message" ));
                        }
                    }
                } );
            }
        };
        this.algorithmsMenu.add( this.polylineDominanceMenuItem );
                
		// Configuracion del menu de Ventanas
		this.windowsMenu = new JMenu( StringBundle.get( "windows_menu_text" ) );
		this.menuBar.add( this.windowsMenu );
		
		this.cascadeMenuItem = new JMenuItem( StringBundle.get( "windows_cascade_menu_text" ), ImageIconBundle.get( "view_cascade" ) )
		{
			{
				this.addActionListener( new ActionListener( )
				{
					@Override
					public void actionPerformed( ActionEvent e )
					{
						JInternalFrame[ ] frames = MainWindow.this.getInternalFrames( );
						
						for( int i = 0; i < frames.length; ++i )
							try
							{
								frames[frames.length - i - 1].setIcon( false );
								frames[frames.length - i - 1].setMaximum( false );
								frames[frames.length - i - 1].setLocation( i * UserSettings.instance.cascadeWindowOffset.get( ), i * UserSettings.instance.cascadeWindowOffset.get( ) );
								frames[frames.length - i - 1].setSize( new Dimension( UserSettings.instance.graphWindowWidth.get( ), UserSettings.instance.graphWindowHeight.get( ) ) );
							}
							catch( PropertyVetoException ex )
							{
								System.out.println( "An exception occurred while repositioning an internal window." );
							}
					}
				} );
			}
		};
		this.windowsMenu.add( this.cascadeMenuItem );
		
		this.showSideBySideMenuItem = new JMenuItem( StringBundle.get( "windows_show_side_by_side_menu_text" ), ImageIconBundle.get( "view_left_right" ) )
		{
			{
				this.addActionListener( new ActionListener( )
				{
					@Override
					public void actionPerformed( ActionEvent e )
					{
						JInternalFrame[ ] frames = MainWindow.this.getInternalFrames( );
						
						if( frames.length > 0 )
						{
							double frameWidth = MainWindow.this.desktopPane.getWidth( ) / frames.length;
							
							for( int i = 0; i < frames.length; ++i )
								try
								{
									frames[i].setIcon( false );
									frames[i].setMaximum( false );
									frames[i].setLocation( (int) ( i * frameWidth ), 0 );
									frames[i].setSize( (int) frameWidth, MainWindow.this.desktopPane.getHeight( ) );
								}
								catch( PropertyVetoException ex )
								{
									System.out.println( "An exception occurred while repositioning an internal window." );
								}
						}
					}
				} );
			}
		};
		this.windowsMenu.add( this.showSideBySideMenuItem );
		
		this.showStackedMenuItem = new JMenuItem( StringBundle.get( "windows_show_stacked_menu_text" ), ImageIconBundle.get( "view_top_bottom" ) )
		{
			{
				this.addActionListener( new ActionListener( )
				{
					@Override
					public void actionPerformed( ActionEvent e )
					{
						JInternalFrame[ ] frames = MainWindow.this.getInternalFrames( );
						
						if( frames.length > 0 )
						{
							double frameHeight = MainWindow.this.desktopPane.getHeight( ) / frames.length;
							
							for( int i = 0; i < frames.length; ++i )
								try
								{
									frames[i].setIcon( false );
									frames[i].setMaximum( false );
									frames[i].setLocation( 0, (int) ( i * frameHeight ) );
									frames[i].setSize( MainWindow.this.desktopPane.getWidth( ), (int) frameHeight );
								}
								catch( PropertyVetoException ex )
								{
									System.out.println( "An exception occurred while repositioning an internal window." );
								}
						}
					}
				} );
			}
		};
		this.windowsMenu.add( this.showStackedMenuItem );
		
		this.tileWindowsMenuItem = new JMenuItem( StringBundle.get( "windows_tile_menu_text" ), ImageIconBundle.get( "view_tile" ) )
		{
			{
				this.addActionListener( new ActionListener( )
				{
					@Override
					public void actionPerformed( ActionEvent e )
					{
						JInternalFrame[ ] frames = MainWindow.this.getInternalFrames( );
						
						if( frames.length > 0 )
						{
							int rows = (int) Math.round( Math.sqrt( frames.length ) );
							int columns = (int) Math.ceil( frames.length / (double) rows );
							double rowSpace = MainWindow.this.desktopPane.getHeight( ) / rows;
							double colSpace = MainWindow.this.desktopPane.getWidth( ) / columns;
							
							for( int i = 0; i < frames.length; ++i )
								try
								{
									frames[i].setIcon( false );
									frames[i].setMaximum( false );
									frames[i].setLocation( (int) ( ( i % columns ) * colSpace ), (int) ( ( i / columns ) * rowSpace ) );
									frames[i].setSize( (int) colSpace, (int) rowSpace );
								}
								catch( PropertyVetoException ex )
								{
									System.out.println( "An exception occurred while repositioning an internal window." );
								}
						}
					}
				} );
			}
		};
		this.windowsMenu.add( this.tileWindowsMenuItem );
		
		this.windowsMenu.addSeparator( );
		
		this.showPreviousMenuItem = new JMenuItem( StringBundle.get( "windows_show_previous_menu_text" ), ImageIconBundle.get( "left_window" ) )
		{
			{
				this.addActionListener( new ActionListener( )
				{
					@Override
					public void actionPerformed( ActionEvent e )
					{
						JInternalFrame[ ] frames = MainWindow.this.getInternalFrames( );
						JInternalFrame selectedFrame = MainWindow.this.desktopPane.getSelectedFrame( );
						
						if( selectedFrame != null )
						{
							int selectedFrameIndex = 0;
							for( selectedFrameIndex = 0; selectedFrameIndex < frames.length; ++selectedFrameIndex )
								if( frames[selectedFrameIndex] == selectedFrame )
									break;
							
							if( selectedFrameIndex < frames.length )
								try
								{
									frames[( selectedFrameIndex + frames.length - 1 ) % frames.length].setSelected( true );
								}
								catch( PropertyVetoException ex )
								{
									System.out.println( "An exception occurred while selecting the previous graph window." );
								}
						}
					}
				} );
				this.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_PAGE_UP, Toolkit.getDefaultToolkit( ).getMenuShortcutKeyMask( ) ) );
			}
		};
		this.windowsMenu.add( this.showPreviousMenuItem );
		
		this.showNextMenuItem = new JMenuItem( StringBundle.get( "windows_show_next_menu_text" ), ImageIconBundle.get( "right_window" ) )
		{
			{
				this.addActionListener( new ActionListener( )
				{
					@Override
					public void actionPerformed( ActionEvent e )
					{
						JInternalFrame[ ] frames = MainWindow.this.getInternalFrames( );
						JInternalFrame selectedFrame = MainWindow.this.desktopPane.getSelectedFrame( );
						
						if( selectedFrame != null )
						{
							int selectedFrameIndex = 0;
							for( selectedFrameIndex = 0; selectedFrameIndex < frames.length; ++selectedFrameIndex )
								if( frames[selectedFrameIndex] == selectedFrame )
									break;
							
							if( selectedFrameIndex < frames.length )
								try
								{
									frames[( selectedFrameIndex + 1 ) % frames.length].setSelected( true );
								}
								catch( PropertyVetoException ex )
								{
									System.out.println( "An exception occurred while selecting the next graph window." );
								}
						}
					}
				} );
				this.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_PAGE_DOWN, Toolkit.getDefaultToolkit( ).getMenuShortcutKeyMask( ) ) );
			}
		};
		this.windowsMenu.add( this.showNextMenuItem );
		
		this.helpMenu = new JMenu( StringBundle.get( "help_menu_text" ) );
		this.menuBar.add( this.helpMenu );
		
		this.helpContentsMenuItem = new JMenuItem( StringBundle.get( "help_contents_menu_text" ), ImageIconBundle.get( "contents" ) )
		{
			{
				this.addActionListener( new ActionListener( )
				{
					@Override
					public void actionPerformed( ActionEvent e )
					{
						WebUtilities.launchBrowser( GlobalSettings.applicationWebsite );
					}
				} );
			}
		};
		this.helpMenu.add( this.helpContentsMenuItem );
		
		this.helpMenu.addSeparator( );
		
		this.aboutVisiGraphMenuItem = new JMenuItem( StringBundle.get( "help_about_menu_text" ), ImageIconBundle.get( "help" ) )
		{
			{
				this.addActionListener( new ActionListener( )
				{
					@Override
					public void actionPerformed( ActionEvent e )
					{
						AboutDialog.showDialog( MainWindow.this );
					}
				} );
			}
		};
		this.helpMenu.add( this.aboutVisiGraphMenuItem );
		
		this.setJMenuBar( this.menuBar );
		
		this.setVisible( true );
	}
	
	/**
	 * Agregar ventana de edicion de Grafo al desktopPane de MainWindow
	 * @param graph Graph
	 * @return graphWindow GraphWindow
	 */
	public GraphWindow addGraphWindow( Graph graph )
	{
		GraphWindow graphWindow = new GraphWindow( graph );
		MainWindow.this.desktopPane.add( graphWindow );
		try
		{
			graphWindow.setMaximum( true );
			graphWindow.setSelected( true );
		}
		catch( PropertyVetoException ex )
		{
			System.out.println( "An exception occurred while loading the graph window." );
		}
		
		return graphWindow;
	}
	
	/**
	 * Agregar ventana de ejecucion del algoritmo de Representacion
	 * de Visibilidad al desktopPane de MainWindow.
	 * @param graph Graph
	 */
	public void addVisibilityRepWindow( Graph graph )
	{
		VisibilityRepWindow visRepWindow = new VisibilityRepWindow ( graph );
		MainWindow.this.desktopPane.add( visRepWindow );
		try
		{
			visRepWindow.setMaximum( true );
			visRepWindow.setSelected( true );
		}
		catch( PropertyVetoException ex )
		{
			System.out.println( "An exception occurred while loading the visRep window." );
		}		
	}
	
	/**
	 * Agregar ventana de de ejecucion del algoritmo de Representacion
	 * de Visibilidad con Restricciones al desktopPane de MainWindow.
	 * @param graph Graph
	 * @param paths NonIntersectingPathList
	 */
    public void addConstrainedVisibilityRepWindow( Graph graph, NonIntersectingPathList paths )
    {
        ConstrainedVisRepWindow constrainedVisRepWindow = new ConstrainedVisRepWindow( graph, paths );
        MainWindow.this.desktopPane.add( constrainedVisRepWindow );
        try
        {
            constrainedVisRepWindow.setMaximum( true );
            constrainedVisRepWindow.setSelected( true );
        }
        catch( PropertyVetoException ex )
        {
            System.out.println( "An exception occurred while loading the constrainedVisRep window." );
        }       
    }
    
	/**
	 * Agregar ventana de ejecucion del algoritmo Upward Polyline
	 * al desktopPane de MainWindow.
	 * @param graph Graph
	 */
	public void addUpwardPolylineWindow( Graph graph )
	{
		UpwardPolylineWindow polylineWindow = new UpwardPolylineWindow ( graph );
		MainWindow.this.desktopPane.add( polylineWindow );
		try
		{
			polylineWindow.setMaximum( true );
			polylineWindow.setSelected( true );
		}
		catch( PropertyVetoException ex )
		{
			System.out.println( "An exception occurred while loading the UpwardPolyline window." );
		}		
	}
	
	/**
	 * Agregar ventana de de ejecucion del algoritmo Constrained Upward Polyline
	 * al desktopPane de MainWindow.
	 * @param graph Graph
	 * @param paths NonIntersectingPathList
	 */
    public void addConstrainedUpwardPolylineWindow( Graph graph, NonIntersectingPathList paths )
    {
        ConstrainedUpwardPolylineWindow constrainedPolylineWindow = new ConstrainedUpwardPolylineWindow( graph, paths );
        MainWindow.this.desktopPane.add( constrainedPolylineWindow );
        try
        {
        	constrainedPolylineWindow.setMaximum( true );
        	constrainedPolylineWindow.setSelected( true );
        }
        catch( PropertyVetoException ex )
        {
            System.out.println( "An exception occurred while loading the constrainedUpwardPolyline window." );
        }       
    }
    
	/**
	 * Agregar ventana de ejecucion del algoritmo Orthogonal
	 * al desktopPane de MainWindow.
	 * @param graph Graph
	 */
	public void addOrthogonalWindow( Graph graph )
	{
		OrthogonalWindow orthogonalWindow = new OrthogonalWindow ( graph );
		MainWindow.this.desktopPane.add( orthogonalWindow );
		try
		{
			orthogonalWindow.setMaximum( true );
			orthogonalWindow.setSelected( true );
		}
		catch( PropertyVetoException ex )
		{
			System.out.println( "An exception occurred while loading the orthogonal window." );
		}		
	}
	
	/**
	 * Agregar ventana de ejecucion del algoritmo Straight-Line Dominance
	 * al desktopPane de MainWindow.
	 * @param graph Graph
	 */
	public void addStraightLineDominanceWindow( Graph graph )
	{
		SLDominanceWindow slDominanceWindow = new SLDominanceWindow ( graph );
		MainWindow.this.desktopPane.add( slDominanceWindow );
		try
		{
			slDominanceWindow.setMaximum( true );
			slDominanceWindow.setSelected( true );
		}
		catch( PropertyVetoException ex )
		{
			System.out.println( "An exception occurred while loading the StraightLineDominance window." );
		}		
	}
	
	/**
	 * Agregar ventana de ejecucion del algoritmo Polyline Dominance
	 * al desktopPane de MainWindow.
	 * @param graph Graph
	 */
	public void addPolylineDominanceWindow( Graph graph )
	{
		PolylineDominanceWindow polyDominanceWindow = new PolylineDominanceWindow ( graph );
		MainWindow.this.desktopPane.add( polyDominanceWindow );
		try
		{
			polyDominanceWindow.setMaximum( true );
			polyDominanceWindow.setSelected( true );
		}
		catch( PropertyVetoException ex )
		{
			System.out.println( "An exception occurred while loading the PolylineDominanceHistory window." );
		}		
	}
	
	public void closingWindow( WindowEvent e )
	{
		JInternalFrame[ ] frames = this.desktopPane.getAllFrames( );
		
		for( JInternalFrame frame : frames )
		{
			if ( frame instanceof GraphWindow )
			{	
				GraphWindow window = (GraphWindow) frame;
				window.closingWindow( new InternalFrameEvent( frame, 0 ) );
				if( !window.isClosed( ) )
					break;
			}
			else
			{
				frame.doDefaultCloseAction();
			}
		}
		
		if( this.desktopPane.getAllFrames( ).length == 0 )
		{
			this.dispose( );
			System.exit( 0 );
		}
	}
	
	public JInternalFrame[ ] getInternalFrames( )
	{
		JInternalFrame[ ] frames = MainWindow.this.desktopPane.getAllFrames( );
		Arrays.sort( frames, new Comparator<JInternalFrame>( )
		{
			@Override
			public int compare( JInternalFrame frame0, JInternalFrame frame1 )
			{
				// Why sort by the seemingly arbitrary value returned by toString()? Well, we actually don't really care about any
				// particular order, just so long as it stays the same (getAllFrames() does not always return them in a consistent order for some
				// reason). This is the only property that is guaranteed to be unique to each frame and unchanging throughout the object's lifetime.
				return frame0.toString( ).compareTo( frame1.toString( ) );
			}
		} );
		return frames;
	}
	
	public void openFile( File file ) throws IOException
	{
		Scanner scanner = new Scanner( file );
		StringBuilder sb = new StringBuilder( );
		while( scanner.hasNextLine( ) )
			sb.append( scanner.nextLine( ) );
		
		scanner.close( );
		
		Graph newGraph = new Graph( sb.toString( ) );
		if( newGraph != null )
			this.addGraphWindow( newGraph ).setFile( file );
	}
}

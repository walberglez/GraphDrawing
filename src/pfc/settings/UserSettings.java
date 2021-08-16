/**
z * UserSettings.java
 */
package pfc.settings;

import java.awt.*;
import java.io.*;
import java.util.*;

import pfc.models.*;
import pfc.utilities.*;

/**
 * @author    Cameron Behar
 */
public class UserSettings extends ObservableModel
{
	public final static UserSettings instance = new UserSettings( );
	
	public final Property<Double>  defaultVertexWeight;
	public final Property<Integer> defaultVertexColor;
	public final Property<String>  defaultVertexPrefix;
	public final Property<Double>  defaultVertexRadius;
	public final Property<Boolean> defaultVertexIsSelected;
	public final Property<Double>  defaultEdgeWeight;
	public final Property<Integer> defaultEdgeColor;
	public final Property<String>  defaultEdgePrefix;
	public final Property<Double>  defaultEdgeThickness;
	public final Property<Double>  defaultEdgeHandleRadiusRatio;
	public final Property<Double>  defaultLoopDiameter;
	public final Property<Boolean> defaultEdgeIsSelected;
	public final Property<Boolean> defaultShowVertexWeights;
	public final Property<Boolean> defaultShowVertexLabels;
	public final Property<Boolean> defaultShowEdgeHandles;
	public final Property<Boolean> defaultShowEdgeWeights;
	public final Property<Boolean> defaultShowEdgeLabels;
	public final Property<Color>   graphBackground;
	public final Property<Color>   selectionBoxFill;
	public final Property<Color>   selectionBoxLine;
	public final Property<Color>   vertexLine;
	public final Property<Color>   selectedVertexFill;
	public final Property<Color>   selectedVertexLine;
	public final Property<Color>   draggingEdge;
	public final Property<Color>   edgeHandle;
	public final Property<Color>   selectedEdge;
	public final Property<Color>   selectedEdgeHandle;
	public final Property<Color>   uncoloredEdgeLine;
	public final Property<Color>   uncoloredDualEdgeLine;
	public final Property<Color>   uncoloredVertexFill;
	public final Property<Color>   uncoloredDualVertexFill;
	public final Property<Color>   vertexLabelColor;
	public final ObservableList<Color> elementColors;
	public final Property<Double>  vertexClickMargin;
	public final Property<Double>  edgeHandleClickMargin;
	public final Property<Double>  panDecelerationFactor;
	public final Property<Boolean> panOnDoubleClick;
	public final Property<Boolean> deselectVertexWithNewEdge;
	public final Property<Double>  zoomInFactor;
	public final Property<Double>  zoomOutFactor;
	public final Property<Double>  maximumZoomFactor;
	public final Property<Double>  zoomGraphPadding;
	public final Property<Double>  scrollIncrementZoom;
	public final Property<Double>  arrangeCircleRadiusMultiplier;
	public final Property<Double>  arrangeGridSpacing;
	public final Property<Double>  autoArrangeAttractiveForce;
	public final Property<Double>  autoArrangeRepulsiveForce;
	public final Property<Double>  autoArrangeDecelerationFactor;
	public final Property<Double>  arrangeContractFactor;
	public final Property<Double>  arrangeExpandFactor;
	public final Property<Integer> undoLoggingInterval;
	public final Property<Integer> undoLoggingMaximum;
	public final Property<Boolean> useAntiAliasing;
	public final Property<Boolean> usePureStroke;
	public final Property<Boolean> useBicubicInterpolation;
	public final Property<Boolean> useFractionalMetrics;
	public final Property<Integer> vertexWeightPrecision;
	public final Property<Integer> edgeWeightPrecision;
	public final Property<Integer> mainWindowWidth;
	public final Property<Integer> mainWindowHeight;
	public final Property<Integer> scriptLibraryWindowWidth;
	public final Property<Integer> scriptLibraryWindowHeight;
	public final Property<Integer> graphWindowWidth;
	public final Property<Integer> graphWindowHeight;
	public final Property<Integer> cascadeWindowOffset;
	public final Property<String>  language;
	public final Property<String>  defaultGraphName;
	public final Property<Double>  directedEdgeArrowRatio;
	public final Property<Double>  arrowKeyIncrement;
	public final Property<Double>  edgeSnapMarginRatio;
	public final Property<Double>  areCloseDistance;
	public final Property<Integer> paintToolMenuDelay;
	
	private UserSettings ( )
	{
		this.defaultVertexWeight			= new Property<Double> ( GlobalSettings.defaultVertexWeight                  );
		this.defaultVertexColor				= new Property<Integer>( GlobalSettings.defaultVertexColor                   );
		this.defaultVertexPrefix			= new Property<String> ( GlobalSettings.defaultVertexPrefix                  );
		this.defaultVertexRadius			= new Property<Double> ( GlobalSettings.defaultVertexRadius                  );
		this.defaultVertexIsSelected		= new Property<Boolean>( GlobalSettings.defaultVertexIsSelected              );
		this.defaultEdgeWeight				= new Property<Double> ( GlobalSettings.defaultEdgeWeight                    );
		this.defaultEdgeColor				= new Property<Integer>( GlobalSettings.defaultEdgeColor                     );
		this.defaultEdgePrefix				= new Property<String> ( GlobalSettings.defaultEdgePrefix                    );
		this.defaultEdgeThickness			= new Property<Double> ( GlobalSettings.defaultEdgeThickness                 );
		this.defaultEdgeHandleRadiusRatio	= new Property<Double> ( GlobalSettings.defaultEdgeHandleRadiusRatio         );
		this.defaultLoopDiameter			= new Property<Double> ( GlobalSettings.defaultLoopDiameter                  );
		this.defaultEdgeIsSelected			= new Property<Boolean>( GlobalSettings.defaultEdgeIsSelected                );
		this.defaultShowVertexWeights		= new Property<Boolean>( GlobalSettings.defaultShowVertexWeights             );
		this.defaultShowVertexLabels		= new Property<Boolean>( GlobalSettings.defaultShowVertexLabels              );
		this.defaultShowEdgeHandles			= new Property<Boolean>( GlobalSettings.defaultShowEdgeHandles               );
		this.defaultShowEdgeWeights			= new Property<Boolean>( GlobalSettings.defaultShowEdgeWeights               );
		this.defaultShowEdgeLabels			= new Property<Boolean>( GlobalSettings.defaultShowEdgeLabels                );
		this.graphBackground				= new Property<Color>  ( GlobalSettings.defaultGraphBackgroundFill           );
		this.selectionBoxFill				= new Property<Color>  ( GlobalSettings.defaultSelectionBoxFill              );
		this.selectionBoxLine				= new Property<Color>  ( GlobalSettings.defaultSelectionBoxLine              );
		this.vertexLine						= new Property<Color>  ( GlobalSettings.defaultVertexLine                    );
		this.selectedVertexFill				= new Property<Color>  ( GlobalSettings.defaultSelectedVertexFill            );
		this.selectedVertexLine				= new Property<Color>  ( GlobalSettings.defaultSelectedVertexLine            );
		this.draggingEdge					= new Property<Color>  ( GlobalSettings.defaultDraggingEdgeLine              );
		this.edgeHandle						= new Property<Color>  ( GlobalSettings.defaultEdgeHandleFill                );
		this.selectedEdge					= new Property<Color>  ( GlobalSettings.defaultSelectedEdgeLine              );
		this.selectedEdgeHandle				= new Property<Color>  ( GlobalSettings.defaultSelectedEdgeHandleFill        );
		this.uncoloredEdgeLine				= new Property<Color>  ( GlobalSettings.defaultUncoloredEdgeLine             );
		this.uncoloredDualEdgeLine			= new Property<Color>  ( GlobalSettings.defaultUncoloredDualEdgeLine         );
		this.uncoloredVertexFill			= new Property<Color>  ( GlobalSettings.defaultUncoloredVertexFill           );
		this.uncoloredDualVertexFill		= new Property<Color>  ( GlobalSettings.defaultUncoloredDualVertexFill       );
		this.vertexLabelColor				= new Property<Color>  ( GlobalSettings.defaultVertexLabelColor		         );
		this.vertexClickMargin				= new Property<Double> ( GlobalSettings.defaultVertexClickMargin             );
		this.edgeHandleClickMargin			= new Property<Double> ( GlobalSettings.defaultEdgeHandleClickMargin         );
		this.panDecelerationFactor			= new Property<Double> ( GlobalSettings.defaultPanDecelerationFactor         );
		this.panOnDoubleClick				= new Property<Boolean>( GlobalSettings.defaultPanOnDoubleClick              );
		this.deselectVertexWithNewEdge		= new Property<Boolean>( GlobalSettings.defaultDeselectVertexWithNewEdge     );
		this.zoomInFactor					= new Property<Double> ( GlobalSettings.defaultZoomInFactor                  );
		this.zoomOutFactor					= new Property<Double> ( GlobalSettings.defaultZoomOutFactor                 );
		this.maximumZoomFactor				= new Property<Double> ( GlobalSettings.defaultMaximumZoomFactor             );
		this.zoomGraphPadding				= new Property<Double> ( GlobalSettings.defaultZoomGraphPadding              );
		this.scrollIncrementZoom			= new Property<Double> ( GlobalSettings.defaultScrollIncrementZoom           );
		this.arrangeCircleRadiusMultiplier	= new Property<Double> ( GlobalSettings.defaultArrangeCircleRadiusMultiplier );
		this.arrangeGridSpacing				= new Property<Double> ( GlobalSettings.defaultArrangeGridSpacing            );
		this.autoArrangeAttractiveForce		= new Property<Double> ( GlobalSettings.defaultAutoArrangeAttractiveForce    );
		this.autoArrangeRepulsiveForce		= new Property<Double> ( GlobalSettings.defaultAutoArrangeRepulsiveForce     );
		this.autoArrangeDecelerationFactor	= new Property<Double> ( GlobalSettings.defaultAutoArrangeDecelerationFactor );
		this.arrangeContractFactor			= new Property<Double> ( GlobalSettings.defaultArrangeContractFactor         );
		this.arrangeExpandFactor			= new Property<Double> ( GlobalSettings.defaultArrangeExpandFactor           );
		this.undoLoggingInterval			= new Property<Integer>( GlobalSettings.defaultUndoLoggingInterval           );
		this.undoLoggingMaximum				= new Property<Integer>( GlobalSettings.defaultUndoLoggingMaximum            );
		this.useAntiAliasing				= new Property<Boolean>( GlobalSettings.defaultUseAntiAliasing               );
		this.usePureStroke					= new Property<Boolean>( GlobalSettings.defaultUsePureStroke                 );
		this.useBicubicInterpolation		= new Property<Boolean>( GlobalSettings.defaultUseBicubicInterpolation       );
		this.useFractionalMetrics			= new Property<Boolean>( GlobalSettings.defaultUseFractionalMetrics          );
		this.vertexWeightPrecision			= new Property<Integer>( GlobalSettings.defaultVertexWeightPrecision         );
		this.edgeWeightPrecision			= new Property<Integer>( GlobalSettings.defaultEdgeWeightPrecision           );
		this.mainWindowWidth				= new Property<Integer>( GlobalSettings.defaultMainWindowWidth               );
		this.mainWindowHeight				= new Property<Integer>( GlobalSettings.defaultMainWindowHeight              );
		this.scriptLibraryWindowWidth		= new Property<Integer>( GlobalSettings.defaultScriptLibraryWindowWidth      );
		this.scriptLibraryWindowHeight		= new Property<Integer>( GlobalSettings.defaultScriptLibraryWindowHeight     );
		this.graphWindowWidth				= new Property<Integer>( GlobalSettings.defaultGraphWindowWidth              );
		this.graphWindowHeight				= new Property<Integer>( GlobalSettings.defaultGraphWindowHeight             );
		this.cascadeWindowOffset			= new Property<Integer>( GlobalSettings.defaultCascadeWindowOffset           );
		this.language						= new Property<String> ( GlobalSettings.defaultLanguage	                     );
		this.defaultGraphName				= new Property<String> ( GlobalSettings.defaultGraphName                     );
		this.directedEdgeArrowRatio			= new Property<Double> ( GlobalSettings.defaultDirectedEdgeArrowRatio        );
		this.arrowKeyIncrement				= new Property<Double> ( GlobalSettings.defaultArrowKeyIncrement             );
		this.edgeSnapMarginRatio			= new Property<Double> ( GlobalSettings.defaultEdgeSnapMarginRatio           );
		this.areCloseDistance				= new Property<Double> ( GlobalSettings.defaultAreCloseDistance              );
		this.paintToolMenuDelay				= new Property<Integer>( GlobalSettings.defaultPaintToolMenuDelay            );
		
		this.elementColors = new ObservableList<Color>( );
		this.elementColors.addAll( Arrays.asList( GlobalSettings.defaultElementColors ) );
		this.elementColors.addObserver( new Observer( )
		{
			@Override
			public void update( Observable o, Object arg )
			{
				setChanged( );
				notifyObservers( arg );
			}
		} );
		
		fromFile( new File( "UserSettings.json" ) );
	}
	
	public Color getVertexColor( int i )
	{
		return ( i < 0 || i >= elementColors.size( ) ? uncoloredVertexFill.get( ) : elementColors.get( i ) );
	}
	
	public Color getEdgeColor( int i )
	{
		return ( i < 0 || i >= elementColors.size( ) ? uncoloredEdgeLine.get( ) : elementColors.get( i ) );
	}
	
	public void fromFile( File file )
	{
		if ( file.exists( ) )
		{
			try
			{
				Scanner in = new Scanner( file );
				
				StringBuilder sb = new StringBuilder( );
				while ( in.hasNextLine( ) )
					sb.append( in.nextLine( ) );
				
				fromString( sb.toString( ) );
				
				in.close( );
			}
			catch ( IOException ex )
			{
				System.out.println( "An exception occurred while loading user settings." );
			}
		}
	}
	
	public void fromString (String json)
	{
		Map<String, Object> members = JsonUtilities.parseObject( json );
		
		if ( members.containsKey( "defaultVertexWeight" ) )				defaultVertexWeight			 .set( (Double)  members.get( "defaultVertexWeight"				) );
		if ( members.containsKey( "defaultVertexColor" ) )				defaultVertexColor			 .set( (Integer) members.get( "defaultVertexColor"				) );
		if ( members.containsKey( "defaultVertexPrefix" ) )				defaultVertexPrefix			 .set( (String)  members.get( "defaultVertexPrefix"				) );
		if ( members.containsKey( "defaultVertexRadius" ) )				defaultVertexRadius			 .set( (Double)  members.get( "defaultVertexRadius"				) );
		if ( members.containsKey( "defaultVertexIsSelected" ) )			defaultVertexIsSelected		 .set( (Boolean) members.get( "defaultVertexIsSelected"			) );
		if ( members.containsKey( "defaultEdgeWeight" ) )				defaultEdgeWeight			 .set( (Double)  members.get( "defaultEdgeWeight"				) );
		if ( members.containsKey( "defaultEdgeColor" ) )				defaultEdgeColor			 .set( (Integer) members.get( "defaultEdgeColor"				) );
		if ( members.containsKey( "defaultEdgePrefix" ) )				defaultEdgePrefix			 .set( (String)  members.get( "defaultEdgePrefix"				) );
		if ( members.containsKey( "defaultEdgeThickness" ) )			defaultEdgeThickness		 .set( (Double)  members.get( "defaultEdgeThickness"			) );
		if ( members.containsKey( "defaultEdgeHandleRadiusRatio" ) )	defaultEdgeHandleRadiusRatio .set( (Double)  members.get( "defaultEdgeHandleRadiusRatio"	) );
		if ( members.containsKey( "defaultLoopDiameter" ) )				defaultLoopDiameter			 .set( (Double)  members.get( "defaultLoopDiameter"				) );
		if ( members.containsKey( "defaultEdgeIsSelected" ) )			defaultEdgeIsSelected		 .set( (Boolean) members.get( "defaultEdgeIsSelected"			) );
		if ( members.containsKey( "defaultShowVertexWeights" ) )		defaultShowVertexWeights	 .set( (Boolean) members.get( "defaultShowVertexWeights"		) );
		if ( members.containsKey( "defaultShowVertexLabels" ) )			defaultShowVertexLabels		 .set( (Boolean) members.get( "defaultShowVertexLabels"			) );
		if ( members.containsKey( "defaultShowEdgeHandles" ) )			defaultShowEdgeHandles		 .set( (Boolean) members.get( "defaultShowEdgeHandles"			) );
		if ( members.containsKey( "defaultShowEdgeWeights" ) )			defaultShowEdgeWeights		 .set( (Boolean) members.get( "defaultShowEdgeWeights"			) );
		if ( members.containsKey( "defaultShowEdgeLabels" ) )			defaultShowEdgeLabels		 .set( (Boolean) members.get( "defaultShowEdgeLabels"			) );
		if ( members.containsKey( "graphBackground" ) )					graphBackground				 .set( (Color)   members.get( "graphBackground"					) );
		if ( members.containsKey( "selectionBoxFill" ) )				selectionBoxFill			 .set( (Color)   members.get( "selectionBoxFill"				) );
		if ( members.containsKey( "selectionBoxLine" ) )				selectionBoxLine			 .set( (Color)   members.get( "selectionBoxLine"				) );
		if ( members.containsKey( "vertexLine" ) )						vertexLine					 .set( (Color)   members.get( "vertexLine"		 				) );
		if ( members.containsKey( "selectedVertexFill" ) )				selectedVertexFill			 .set( (Color)   members.get( "selectedVertexFill"				) );
		if ( members.containsKey( "selectedVertexLine" ) )				selectedVertexLine			 .set( (Color)   members.get( "selectedVertexLine"				) );
		if ( members.containsKey( "draggingEdge" ) )					draggingEdge				 .set( (Color)   members.get( "draggingEdge"					) );
		if ( members.containsKey( "edgeHandle" ) )						edgeHandle			 		 .set( (Color)   members.get( "edgeHandle"						) );
		if ( members.containsKey( "selectedEdge" ) )					selectedEdge				 .set( (Color)   members.get( "selectedEdge"					) );
		if ( members.containsKey( "selectedEdgeHandle" ) )				selectedEdgeHandle			 .set( (Color)   members.get( "selectedEdgeHandle"				) );
		if ( members.containsKey( "uncoloredEdgeLine" ) )				uncoloredEdgeLine			 .set( (Color)   members.get( "uncoloredEdgeLine"				) );
		if ( members.containsKey( "uncoloredDualEdgeLine" ) )			uncoloredDualEdgeLine		 .set( (Color)   members.get( "uncoloredDualEdgeLine"			) );
		if ( members.containsKey( "uncoloredVertexFill" ) )				uncoloredVertexFill		 	 .set( (Color)   members.get( "uncoloredVertexFill"				) );
		if ( members.containsKey( "uncoloredDualVertexFill" ) )			uncoloredDualVertexFill	 	 .set( (Color)   members.get( "uncoloredDualVertexFill"			) );
		if ( members.containsKey( "vertexLabelColor" ) )				vertexLabelColor		 	 .set( (Color)   members.get( "vertexLabelColor"				) );
		if ( members.containsKey( "vertexClickMargin" ) )				vertexClickMargin			 .set( (Double)  members.get( "vertexClickMargin"				) );
		if ( members.containsKey( "edgeHandleClickMargin" ) )			edgeHandleClickMargin		 .set( (Double)  members.get( "edgeHandleClickMargin"			) );
		if ( members.containsKey( "panDecelerationFactor" ) )			panDecelerationFactor		 .set( (Double)  members.get( "panDecelerationFactor"			) );
		if ( members.containsKey( "panOnDoubleClick" ) )				panOnDoubleClick			 .set( (Boolean) members.get( "panOnDoubleClick"				) );
		if ( members.containsKey( "deselectVertexWithNewEdge" ) )		deselectVertexWithNewEdge	 .set( (Boolean) members.get( "deselectVertexWithNewEdge"		) );
		if ( members.containsKey( "zoomInFactor" ) )					zoomInFactor				 .set( (Double)  members.get( "zoomInFactor"					) );
		if ( members.containsKey( "zoomOutFactor" ) )					zoomOutFactor				 .set( (Double)  members.get( "zoomOutFactor"					) );
		if ( members.containsKey( "maximumZoomFactor" ) )				maximumZoomFactor			 .set( (Double)  members.get( "maximumZoomFactor"				) );
		if ( members.containsKey( "zoomGraphPadding" ) )				zoomGraphPadding			 .set( (Double)  members.get( "zoomGraphPadding"				) );
		if ( members.containsKey( "scrollIncrementZoom" ) )				scrollIncrementZoom			 .set( (Double)  members.get( "scrollIncrementZoom"				) );
		if ( members.containsKey( "arrangeCircleRadiusMultiplier" ) )	arrangeCircleRadiusMultiplier.set( (Double)  members.get( "arrangeCircleRadiusMultiplier"	) );
		if ( members.containsKey( "arrangeGridSpacing" ) )				arrangeGridSpacing			 .set( (Double)  members.get( "arrangeGridSpacing"				) );
		if ( members.containsKey( "autoArrangeAttractiveForce" ) )		autoArrangeAttractiveForce	 .set( (Double)  members.get( "autoArrangeAttractiveForce"		) );
		if ( members.containsKey( "autoArrangeRepulsiveForce" ) )		autoArrangeRepulsiveForce	 .set( (Double)  members.get( "autoArrangeRepulsiveForce"		) );
		if ( members.containsKey( "autoArrangeDecelerationFactor" ) )	autoArrangeDecelerationFactor.set( (Double)  members.get( "autoArrangeDecelerationFactor"	) );
		if ( members.containsKey( "arrangeContractFactor" ) )			arrangeContractFactor		 .set( (Double)  members.get( "arrangeContractFactor"			) );
		if ( members.containsKey( "arrangeExpandFactor" ) )				arrangeExpandFactor			 .set( (Double)  members.get( "arrangeExpandFactor"				) );
		if ( members.containsKey( "undoLoggingInterval" ) )				undoLoggingInterval			 .set( (Integer) members.get( "undoLoggingInterval"				) );
		if ( members.containsKey( "undoLoggingMaximum" ) )				undoLoggingMaximum			 .set( (Integer) members.get( "undoLoggingMaximum"				) );
		if ( members.containsKey( "useAntiAliasing" ) )					useAntiAliasing				 .set( (Boolean) members.get( "useAntiAliasing"					) );
		if ( members.containsKey( "usePureStroke" ) )					usePureStroke				 .set( (Boolean) members.get( "usePureStroke"					) );
		if ( members.containsKey( "useBicubicInterpolation" ) )			useBicubicInterpolation		 .set( (Boolean) members.get( "useBicubicInterpolation"			) );
		if ( members.containsKey( "useFractionalMetrics" ) )			useFractionalMetrics		 .set( (Boolean) members.get( "useFractionalMetrics"			) );
		if ( members.containsKey( "vertexWeightPrecision" ) )			vertexWeightPrecision		 .set( (Integer) members.get( "vertexWeightPrecision"			) );
		if ( members.containsKey( "edgeWeightPrecision" ) )				edgeWeightPrecision			 .set( (Integer) members.get( "edgeWeightPrecision"				) );
		if ( members.containsKey( "mainWindowWidth" ) )					mainWindowWidth				 .set( (Integer) members.get( "mainWindowWidth"					) );
		if ( members.containsKey( "mainWindowHeight" ) )				mainWindowHeight			 .set( (Integer) members.get( "mainWindowHeight"				) );
		if ( members.containsKey( "scriptLibraryWindowWidth" ) )		scriptLibraryWindowWidth	 .set( (Integer) members.get( "scriptLibraryWindowWidth"		) );
		if ( members.containsKey( "scriptLibraryWindowHeight" ) )		scriptLibraryWindowHeight	 .set( (Integer) members.get( "scriptLibraryWindowHeight"		) );
		if ( members.containsKey( "graphWindowWidth" ) )				graphWindowWidth			 .set( (Integer) members.get( "graphWindowWidth"				) );
		if ( members.containsKey( "graphWindowHeight" ) )				graphWindowHeight			 .set( (Integer) members.get( "graphWindowHeight"				) );
		if ( members.containsKey( "cascadeWindowOffset" ) )				cascadeWindowOffset			 .set( (Integer) members.get( "cascadeWindowOffset"				) );
		if ( members.containsKey( "language" ) )						language					 .set( (String)  members.get( "language"						) );
		if ( members.containsKey( "defaultGraphName" ) )				defaultGraphName			 .set( (String)  members.get( "defaultGraphName"				) );
		if ( members.containsKey( "directedEdgeArrowRatio" ) )			directedEdgeArrowRatio		 .set( (Double)  members.get( "directedEdgeArrowRatio"			) );
		if ( members.containsKey( "arrowKeyIncrement" ) )				arrowKeyIncrement			 .set( (Double)  members.get( "arrowKeyIncrement"				) );
		if ( members.containsKey( "edgeSnapMarginRatio" ) )				edgeSnapMarginRatio			 .set( (Double)  members.get( "edgeSnapMarginRatio"				) );
		if ( members.containsKey( "areCloseDistance" ) )				areCloseDistance			 .set( (Double)  members.get( "areCloseDistance"				) );
		if ( members.containsKey( "paintToolMenuDelay" ) )				paintToolMenuDelay			 .set( (Integer) members.get( "paintToolMenuDelay"				) );
		
		elementColors.clear( );
		if ( members.containsKey( "elementColors" ) )
			for ( Object color : (Iterable<?>) members.get( "elementColors" ) )
				elementColors.add( (Color) color );
	}
	
	@Override
	public String toString( )
	{
		HashMap<String, Object> members = new HashMap<String, Object>( );
		
		members.put( "defaultVertexWeight",				defaultVertexWeight				);
		members.put( "defaultVertexColor",				defaultVertexColor				);
		members.put( "defaultVertexPrefix",				defaultVertexPrefix				);
		members.put( "defaultVertexRadius",				defaultVertexRadius				);
		members.put( "defaultVertexIsSelected",			defaultVertexIsSelected			);
		members.put( "defaultEdgeWeight",				defaultEdgeWeight				);
		members.put( "defaultEdgeColor",				defaultEdgeColor				);
		members.put( "defaultEdgePrefix",				defaultEdgePrefix				);
		members.put( "defaultEdgeThickness",			defaultEdgeThickness			);
		members.put( "defaultEdgeHandleRadiusRatio",	defaultEdgeHandleRadiusRatio	);
		members.put( "defaultLoopDiameter",				defaultLoopDiameter				);
		members.put( "defaultEdgeIsSelected",			defaultEdgeIsSelected			);
		members.put( "defaultShowVertexWeights",		defaultShowVertexWeights		);
		members.put( "defaultShowVertexLabels",			defaultShowVertexLabels			);
		members.put( "defaultShowEdgeHandles",			defaultShowEdgeHandles			);
		members.put( "defaultShowEdgeWeights",			defaultShowEdgeWeights			);
		members.put( "defaultShowEdgeLabels",			defaultShowEdgeLabels			);
		members.put( "graphBackground",					graphBackground					);
		members.put( "selectionBoxFill",				selectionBoxFill				);
		members.put( "selectionBoxLine",				selectionBoxLine				);
		members.put( "vertexLine",						vertexLine						);
		members.put( "selectedVertexFill",				selectedVertexFill				);
		members.put( "selectedVertexLine",				selectedVertexLine				);
		members.put( "draggingEdge",					draggingEdge					);
		members.put( "edgeHandle",						edgeHandle						);
		members.put( "selectedEdge",					selectedEdge					);
		members.put( "selectedEdgeHandle",				selectedEdgeHandle				);
		members.put( "uncoloredEdgeLine",				uncoloredEdgeLine				);
		members.put( "uncoloredVertexFill",				uncoloredVertexFill				);
		members.put( "vertexLabelColor",				vertexLabelColor				);
		members.put( "elementColors",					elementColors					);
		members.put( "vertexClickMargin",				vertexClickMargin				);
		members.put( "edgeHandleClickMargin",			edgeHandleClickMargin			);
		members.put( "panDecelerationFactor",			panDecelerationFactor			);
		members.put( "panOnDoubleClick",				panOnDoubleClick				);
		members.put( "deselectVertexWithNewEdge",		deselectVertexWithNewEdge		);
		members.put( "zoomInFactor",					zoomInFactor					);
		members.put( "zoomOutFactor",					zoomOutFactor					);
		members.put( "maximumZoomFactor",				maximumZoomFactor				);
		members.put( "zoomGraphPadding",				zoomGraphPadding				);
		members.put( "scrollIncrementZoom",				scrollIncrementZoom				);
		members.put( "arrangeCircleRadiusMultiplier",	arrangeCircleRadiusMultiplier	);
		members.put( "arrangeGridSpacing",				arrangeGridSpacing				);
		members.put( "autoArrangeAttractiveForce",		autoArrangeAttractiveForce		);
		members.put( "autoArrangeRepulsiveForce",		autoArrangeRepulsiveForce		);
		members.put( "autoArrangeDecelerationFactor",	autoArrangeDecelerationFactor	);
		members.put( "arrangeContractFactor",			arrangeContractFactor			);
		members.put( "arrangeExpandFactor",				arrangeExpandFactor				);
		members.put( "undoLoggingInterval",				undoLoggingInterval				);
		members.put( "undoLoggingMaximum",				undoLoggingMaximum				);
		members.put( "useAntiAliasing",					useAntiAliasing					);
		members.put( "usePureStroke",					usePureStroke					);
		members.put( "useBicubicInterpolation",			useBicubicInterpolation			);
		members.put( "useFractionalMetrics",			useFractionalMetrics			);
		members.put( "vertexWeightPrecision",			vertexWeightPrecision			);
		members.put( "edgeWeightPrecision",				edgeWeightPrecision				);
		members.put( "mainWindowWidth",					mainWindowWidth					);
		members.put( "mainWindowHeight",				mainWindowHeight				);
		members.put( "scriptLibraryWindowWidth",		scriptLibraryWindowWidth		);
		members.put( "scriptLibraryWindowHeight",		scriptLibraryWindowHeight		);
		members.put( "graphWindowWidth",				graphWindowWidth				);
		members.put( "graphWindowHeight",				graphWindowHeight				);
		members.put( "cascadeWindowOffset",				cascadeWindowOffset				);
		members.put( "language",						language						);
		members.put( "defaultGraphName",				defaultGraphName				);
		members.put( "directedEdgeArrowRatio",			directedEdgeArrowRatio			);
		members.put( "arrowKeyIncrement",				arrowKeyIncrement				);
		members.put( "edgeSnapMarginRatio",				edgeSnapMarginRatio				);
		members.put( "areCloseDistance",				areCloseDistance				);
		members.put( "paintToolMenuDelay",				paintToolMenuDelay				);

		return JsonUtilities.formatObject( members );
	}
}

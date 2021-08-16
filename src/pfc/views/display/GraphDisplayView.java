/**
 * GraphDisplayView.java
 */
package pfc.views.display;

import java.awt.*;

import pfc.models.*;
import pfc.settings.*;


/**
 * @author Cameron Behar
 */
public class GraphDisplayView
{	
	public static void paint( Graphics2D g2D, Graph graph, GraphSettings s )
	{
		// Draw all the edges first
		for( Edge edge : graph.edges )
			EdgeDisplayView.paintEdge( g2D, s, edge );
		
		// Then draw all the vertices
		for( Vertex vertex : graph.vertices )
			VertexDisplayView.paint( g2D, s, vertex );
		
	}
}

/**
 * DualPathGraphDisplayView.java
 * 21/07/2011 01:31:19
 */
package pfc.views.display.algorithms;

import java.awt.Graphics2D;

import pfc.models.Edge;
import pfc.models.Graph;
import pfc.models.Vertex;
import pfc.settings.GraphSettings;


/**
 * @author walber
 *
 */
public class DualPathGraphDisplayView {

	public static void paint( Graphics2D g2D, Graph graph, GraphSettings s )
	{
		// Draw all the edges first
		for( Edge edge : graph.edges )
			DualEdgeDisplayView.paintEdge( g2D, s, edge );
		
		// Then draw all the vertices
		for( Vertex vertex : graph.vertices )
		{
			if ( vertex.tag != null && vertex.tag.get( ).equals( "path" ) )
				DualPathVertexDisplayView.paint( g2D, s, vertex );
			else
				DualVertexDisplayView.paint( g2D, s, vertex );
		}		
	}
}
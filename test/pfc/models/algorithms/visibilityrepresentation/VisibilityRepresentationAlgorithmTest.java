/**
 * 
 */
package pfc.models.algorithms.visibilityrepresentation;

import org.junit.Before;
import org.junit.Test;

import pfc.models.Graph;
import pfc.models.Vertex;
import pfc.models.algorithms.DualGraph;
import pfc.models.algorithms.visibilityrepresentation.EdgeSegment;
import pfc.models.algorithms.visibilityrepresentation.VertexSegment;
import pfc.models.algorithms.visibilityrepresentation.VisibilityRepresentationAlgorithm;
import pfc.models.algorithms.visibilityrepresentation.VisibilityRepresentationDrawing;


/**
 * @author  walber
 */
public class VisibilityRepresentationAlgorithmTest {

	/**
	 */
	private Graph graph;
	/**
	 */
	private VisibilityRepresentationDrawing drawing;
	private String graphStr = "{ \"areMultipleEdgesAllowed\" : false, \"edges\" : [ { \"weight\" : 1.0, \"isSelected\" : false, \"color\" : -1, \"to.id\" : \"3d484fc2-9598-4fb2-93f0-0accd26777e1\", \"from.id\" : \"010bc110-7e3a-40dc-b779-d11e0db14739\", \"isLinear\" : true, \"isDirected\" : true, \"thickness\" : 1.5, \"label\" : \"e\" }, { \"weight\" : 1.0, \"isSelected\" : false, \"color\" : -1, \"to.id\" : \"2584329e-796c-4bac-870e-038912e4b48c\", \"from.id\" : \"010bc110-7e3a-40dc-b779-d11e0db14739\", \"isLinear\" : true, \"isDirected\" : true, \"thickness\" : 1.5, \"label\" : \"e\" }, { \"weight\" : 1.0, \"isSelected\" : false, \"color\" : -1, \"to.id\" : \"3d484fc2-9598-4fb2-93f0-0accd26777e1\", \"from.id\" : \"cf26437a-5e05-453f-90cf-f4dee85d819a\", \"isLinear\" : true, \"isDirected\" : true, \"thickness\" : 1.5, \"label\" : \"e\" }, { \"weight\" : 1.0, \"isSelected\" : false, \"color\" : -1, \"to.id\" : \"2584329e-796c-4bac-870e-038912e4b48c\", \"from.id\" : \"cf26437a-5e05-453f-90cf-f4dee85d819a\", \"isLinear\" : true, \"isDirected\" : true, \"thickness\" : 1.5, \"label\" : \"e\" }, { \"weight\" : 1.0, \"isSelected\" : false, \"color\" : -1, \"to.id\" : \"cf26437a-5e05-453f-90cf-f4dee85d819a\", \"from.id\" : \"010bc110-7e3a-40dc-b779-d11e0db14739\", \"isLinear\" : true, \"isDirected\" : true, \"thickness\" : 1.5, \"label\" : \"e\" }, { \"weight\" : 1.0, \"isSelected\" : false, \"color\" : -1, \"to.id\" : \"2584329e-796c-4bac-870e-038912e4b48c\", \"from.id\" : \"3d484fc2-9598-4fb2-93f0-0accd26777e1\", \"isLinear\" : true, \"isDirected\" : true, \"thickness\" : 1.5, \"label\" : \"e\" } ], \"vertices\" : [ { \"id\" : \"010bc110-7e3a-40dc-b779-d11e0db14739\", \"weight\" : 1.0, \"isSelected\" : false, \"color\" : -1, \"radius\" : 5.0, \"label\" : \"0\", \"y\" : 399.0, \"x\" : 493.0 }, { \"id\" : \"3d484fc2-9598-4fb2-93f0-0accd26777e1\", \"weight\" : 1.0, \"isSelected\" : false, \"color\" : -1, \"radius\" : 5.0, \"label\" : \"1\", \"y\" : 156.0, \"x\" : 235.0 }, { \"id\" : \"2584329e-796c-4bac-870e-038912e4b48c\", \"weight\" : 1.0, \"isSelected\" : false, \"color\" : -1, \"radius\" : 5.0, \"label\" : \"2\", \"y\" : 234.0, \"x\" : 447.0 }, { \"id\" : \"cf26437a-5e05-453f-90cf-f4dee85d819a\", \"weight\" : 1.0, \"isSelected\" : false, \"color\" : -1, \"radius\" : 5.0, \"label\" : \"3\", \"y\" : 125.0, \"x\" : 664.0 } ], \"name\" : \"error2\", \"areCyclesAllowed\" : false, \"areDirectedEdgesAllowed\" : true, \"areLoopsAllowed\" : false }";
	/**
	 */
	private VisibilityRepresentationAlgorithm alg;

	@Before
	public void setUp() throws Exception
	{
		graph = new Graph( graphStr );
		alg = new VisibilityRepresentationAlgorithm( graph );
		alg.executeAlgorithm( );
		drawing = alg.getDrawing( );
	}

	/**
	 * Test method for {@link pfc.models.algorithms.visibilityrepresentation.VisibilityRepresentationAlgorithm#executeAlgorithm()}.
	 */
	@Test
	public void testExecuteAlgorithm()
	{

		System.out.println( "Topological Numbering Y (G):");
		System.out.println( "[" );
		for ( Vertex v : alg.getTopologicalNumberingY( ).keySet( ) )
		{
			System.out.print( "\t{ " );
//			System.out.print( "id: " + v.id.get( ) + ", ");
			System.out.print( "tag: " + v.tag.get( ) + ", ");
//			System.out.print( "label: " + v.label.get( ));
			System.out.println( " }= " + alg.getTopologicalNumberingY( ).get( v ) + ",");
		}
		System.out.println( "]" );
		
		System.out.println( "Topological Numbering X (G*):");
		System.out.println( "[" );
		for ( Vertex v : alg.getTopologicalNumberingX( ).keySet( ) )
		{
			System.out.print( "\t{ " );
//			System.out.print( "id: " + v.id.get( ) + ", ");
			System.out.print( "tag: " + v.tag.get( ) + ", ");
//			System.out.print( "label: " + v.label.get( ));
			System.out.println( " }= " + alg.getTopologicalNumberingX( ).get( v ) + ",");
		}
		System.out.println( "]" );
		
		System.out.println( "Left face of each vertex of G:");
		System.out.println( "[" );
		for ( Vertex v : alg.getStGraphG().vertices )
		{
			System.out.print( "\t{ " );
//			System.out.print( "id: " + v.id.get( ) + ", ");
			System.out.print( "tag: " + v.tag.get( ) + ", ");
//			System.out.print( "label: " + v.label.get( ));
			System.out.print( " }= { " );
//			System.out.print( "id: " + ((DualGraph)alg.getStDualG()).left(v).id.get( ) + ", ");
			System.out.print( "tag: " + ((DualGraph)alg.getStDualG()).left(v).tag.get( ) + ", ");
//			System.out.print( "label: " + ((DualGraph)alg.getStDualG()).left(v).label.get( ));
			System.out.println( " }" );
		}
		System.out.println( "]" );
		
		System.out.println( "Right face of each vertex of G:");
		System.out.println( "[" );
		for ( Vertex v : alg.getStGraphG().vertices )
		{
			System.out.print( "\t{ " );
//			System.out.print( "id: " + v.id.get( ) + ", ");
			System.out.print( "tag: " + v.tag.get( ) + ", ");
//			System.out.print( "label: " + v.label.get( ));
			System.out.print( " }= { " );
//			System.out.print( "id: " + ((DualGraph)alg.getStDualG()).left(v).id.get( ) + ", ");
			System.out.print( "tag: " + ((DualGraph)alg.getStDualG()).right(v).tag.get( ) + ", ");
//			System.out.print( "label: " + ((DualGraph)alg.getStDualG()).left(v).label.get( ));
			System.out.println( " }" );
		}
		System.out.println( "]" );
		
		System.out.println( "Drawing vertex segments:");
		System.out.println( "[" );
		for ( VertexSegment vS : drawing.vertexSegments )
		{
			System.out.print( "\t{ " );
			System.out.print( "vertex: {" );
//			System.out.print( "id: " + vS.vertex.id.get( ) + ", ");
			System.out.print( "tag: " + vS.vertex.tag.get( ) + ", ");
//			System.out.print( "label: " + vS.vertex.label.get( ));
			System.out.print( "}, ");
			System.out.print( "y: " + vS.yCoordinate.get( ) + ", ");
			System.out.print( "xL: " + vS.xLeftCoordinate.get( ) + ", ");
			System.out.print( "xR: " + vS.xRightCoordinate.get( ) );
			System.out.println( " }," );
		}
		System.out.println( "]" );
		
		System.out.println( "Drawing edge segments:");
		System.out.println( "[" );
		for ( EdgeSegment eS : drawing.edgeSegments )
		{
			System.out.print( "\t{ " );
			System.out.print( "e.from: {" );
//			System.out.print( "id: " + eS.edge.from.id.get( ) + ", ");
			System.out.print( "tag: " + eS.edge.from.tag.get( ) + ", ");
//			System.out.print( "label: " + eS.edge.from.label.get( ));
			System.out.print( "}, ");
			System.out.print( "e.to: {" );
//			System.out.print( "id: " + eS.edge.to.id.get( ) + ", ");
			System.out.print( "tag: " + eS.edge.to.tag.get( ) + ", ");
//			System.out.print( "label: " + eS.edge.to.label.get( ));
			System.out.print( "}, ");
			System.out.print( "x: " + eS.xCoordinate.get( ) + ", ");
			System.out.print( "yB: " + eS.yBottomCoordinate.get( ) + ", ");
			System.out.print( "yT: " + eS.yTopCoordinate.get( ) );
			System.out.println( " }," );
		}
		System.out.println( "]" );
		
	}
}

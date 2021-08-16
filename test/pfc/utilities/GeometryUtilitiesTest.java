/**
 * GeometryUtilitiesTest.java
 * 11/07/2011 02:58:31
 */
package pfc.utilities;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import pfc.models.Graph;
import pfc.utilities.GeometryUtilities;


/**
 * @author walber
 *
 */
public class GeometryUtilitiesTest {

    private Graph graph1;
    private String graphStr1 = "{ \"areMultipleEdgesAllowed\" : false, \"edges\" : [ { \"weight\" : 1.0, \"isSelected\" : false, \"color\" : -1, \"to.id\" : \"16491c9e-1d03-4c61-ad16-f2c707e90303\", \"from.id\" : \"e1516215-fef0-4c4d-bb4e-13068810421e\", \"isLinear\" : true, \"isDirected\" : false, \"thickness\" : 1.5, \"label\" : \"e\" }, { \"weight\" : 1.0, \"isSelected\" : false, \"color\" : -1, \"to.id\" : \"2b9b5759-d31c-4fa6-b9b4-bfc49c028293\", \"from.id\" : \"16491c9e-1d03-4c61-ad16-f2c707e90303\", \"isLinear\" : true, \"isDirected\" : false, \"thickness\" : 1.5, \"label\" : \"e\" }, { \"weight\" : 1.0, \"isSelected\" : false, \"color\" : -1, \"to.id\" : \"e1516215-fef0-4c4d-bb4e-13068810421e\", \"from.id\" : \"2b9b5759-d31c-4fa6-b9b4-bfc49c028293\", \"isLinear\" : true, \"isDirected\" : false, \"thickness\" : 1.5, \"label\" : \"e\" }, { \"weight\" : 1.0, \"isSelected\" : false, \"color\" : -1, \"to.id\" : \"fcb6794e-eaec-47bf-9cba-98dd0b58daf9\", \"from.id\" : \"16491c9e-1d03-4c61-ad16-f2c707e90303\", \"isLinear\" : true, \"isDirected\" : false, \"thickness\" : 1.5, \"label\" : \"e\" }, { \"weight\" : 1.0, \"isSelected\" : false, \"color\" : -1, \"to.id\" : \"fcb6794e-eaec-47bf-9cba-98dd0b58daf9\", \"from.id\" : \"1af70c4c-832b-4680-814c-9197450d00b4\", \"isLinear\" : true, \"isDirected\" : false, \"thickness\" : 1.5, \"label\" : \"e\" }, { \"weight\" : 1.0, \"isSelected\" : false, \"color\" : -1, \"to.id\" : \"1af70c4c-832b-4680-814c-9197450d00b4\", \"from.id\" : \"2b9b5759-d31c-4fa6-b9b4-bfc49c028293\", \"isLinear\" : true, \"isDirected\" : false, \"thickness\" : 1.5, \"label\" : \"e\" } ], \"vertices\" : [ { \"id\" : \"e1516215-fef0-4c4d-bb4e-13068810421e\", \"weight\" : 1.0, \"isSelected\" : false, \"color\" : -1, \"radius\" : 5.0, \"label\" : \"0\", \"y\" : 161.0, \"x\" : 43.0 }, { \"id\" : \"16491c9e-1d03-4c61-ad16-f2c707e90303\", \"weight\" : 1.0, \"isSelected\" : false, \"color\" : -1, \"radius\" : 5.0, \"label\" : \"1\", \"y\" : 87.0, \"x\" : 124.0 }, { \"id\" : \"2b9b5759-d31c-4fa6-b9b4-bfc49c028293\", \"weight\" : 1.0, \"isSelected\" : false, \"color\" : -1, \"radius\" : 5.0, \"label\" : \"2\", \"y\" : 97.0, \"x\" : 62.0 }, { \"id\" : \"fcb6794e-eaec-47bf-9cba-98dd0b58daf9\", \"weight\" : 1.0, \"isSelected\" : false, \"color\" : -1, \"radius\" : 5.0, \"label\" : \"3\", \"y\" : 64.0, \"x\" : 103.0 }, { \"id\" : \"1af70c4c-832b-4680-814c-9197450d00b4\", \"weight\" : 1.0, \"isSelected\" : false, \"color\" : -1, \"radius\" : 5.0, \"label\" : \"5\", \"y\" : 32.0, \"x\" : 119.0 } ], \"name\" : \"1\", \"areCyclesAllowed\" : true, \"areDirectedEdgesAllowed\" : false, \"areLoopsAllowed\" : false }";
    private Graph graph2;
    private String graphStr2 = "{ \"areMultipleEdgesAllowed\" : false, \"edges\" : [ { \"weight\" : 1.0, \"isSelected\" : false, \"color\" : -1, \"to.id\" : \"16491c9e-1d03-4c61-ad16-f2c707e90303\", \"from.id\" : \"e1516215-fef0-4c4d-bb4e-13068810421e\", \"isLinear\" : true, \"isDirected\" : false, \"thickness\" : 1.5, \"label\" : \"e\" }, { \"weight\" : 1.0, \"isSelected\" : false, \"color\" : -1, \"to.id\" : \"2b9b5759-d31c-4fa6-b9b4-bfc49c028293\", \"from.id\" : \"16491c9e-1d03-4c61-ad16-f2c707e90303\", \"isLinear\" : true, \"isDirected\" : false, \"thickness\" : 1.5, \"label\" : \"e\" }, { \"weight\" : 1.0, \"isSelected\" : false, \"color\" : -1, \"to.id\" : \"e1516215-fef0-4c4d-bb4e-13068810421e\", \"from.id\" : \"2b9b5759-d31c-4fa6-b9b4-bfc49c028293\", \"isLinear\" : true, \"isDirected\" : false, \"thickness\" : 1.5, \"label\" : \"e\" }, { \"weight\" : 1.0, \"isSelected\" : false, \"color\" : -1, \"to.id\" : \"fcb6794e-eaec-47bf-9cba-98dd0b58daf9\", \"from.id\" : \"16491c9e-1d03-4c61-ad16-f2c707e90303\", \"isLinear\" : true, \"isDirected\" : false, \"thickness\" : 1.5, \"label\" : \"e\" }, { \"weight\" : 1.0, \"isSelected\" : false, \"color\" : -1, \"to.id\" : \"fcb6794e-eaec-47bf-9cba-98dd0b58daf9\", \"from.id\" : \"1af70c4c-832b-4680-814c-9197450d00b4\", \"isLinear\" : true, \"isDirected\" : false, \"thickness\" : 1.5, \"label\" : \"e\" }, { \"weight\" : 1.0, \"isSelected\" : false, \"color\" : -1, \"to.id\" : \"1af70c4c-832b-4680-814c-9197450d00b4\", \"from.id\" : \"2b9b5759-d31c-4fa6-b9b4-bfc49c028293\", \"isLinear\" : true, \"isDirected\" : false, \"thickness\" : 1.5, \"label\" : \"e\" }, { \"weight\" : 1.0, \"isSelected\" : false, \"color\" : -1, \"to.id\" : \"16491c9e-1d03-4c61-ad16-f2c707e90303\", \"from.id\" : \"1af70c4c-832b-4680-814c-9197450d00b4\", \"isLinear\" : true, \"isDirected\" : false, \"thickness\" : 1.5, \"label\" : \"e\" } ], \"vertices\" : [ { \"id\" : \"e1516215-fef0-4c4d-bb4e-13068810421e\", \"weight\" : 1.0, \"isSelected\" : false, \"color\" : -1, \"radius\" : 5.0, \"label\" : \"0\", \"y\" : 271.0, \"x\" : 24.0 }, { \"id\" : \"16491c9e-1d03-4c61-ad16-f2c707e90303\", \"weight\" : 1.0, \"isSelected\" : false, \"color\" : -1, \"radius\" : 5.0, \"label\" : \"1\", \"y\" : 251.0, \"x\" : 463.0 }, { \"id\" : \"2b9b5759-d31c-4fa6-b9b4-bfc49c028293\", \"weight\" : 1.0, \"isSelected\" : false, \"color\" : -1, \"radius\" : 5.0, \"label\" : \"2\", \"y\" : 43.0, \"x\" : 13.0 }, { \"id\" : \"fcb6794e-eaec-47bf-9cba-98dd0b58daf9\", \"weight\" : 1.0, \"isSelected\" : false, \"color\" : -1, \"radius\" : 5.0, \"label\" : \"3\", \"y\" : 12.0, \"x\" : 337.0 }, { \"id\" : \"1af70c4c-832b-4680-814c-9197450d00b4\", \"weight\" : 1.0, \"isSelected\" : false, \"color\" : -1, \"radius\" : 5.0, \"label\" : \"5\", \"y\" : 32.0, \"x\" : 119.0 } ], \"name\" : \"2\", \"areCyclesAllowed\" : true, \"areDirectedEdgesAllowed\" : false, \"areLoopsAllowed\" : false }";
    
    /**
     */
    @Before
    public void setUp() throws Exception {
        graph1 = new Graph( graphStr1 );
        graph2 = new Graph( graphStr2 );
    }

    /**
     * Test method for {@link pfc.utilities.GeometryUtilities#getTheClosestVertexToOrigin(pfc.models.Graph)}.
     */
    @Test
    public void testGetTheClosestVertexToOrigin() {
        String vertex1 = "5";
        String vertex2 = "3";
        assertTrue("no es el vertice esperado en grafo1", vertex1.equals(GeometryUtilities.getTheClosestVertexToOrigin(graph1).label.get()));
        assertTrue("no es el vertice esperado en grafo2", vertex2.equals(GeometryUtilities.getTheClosestVertexToOrigin(graph2).label.get()));
    }

    /**
     * Test method for {@link pfc.utilities.GeometryUtilities#getTheFarthestVertexToOrigin(pfc.models.Graph)}.
     */
    @Test
    public void testGetTheFarthestVertexToOrigin() {
        String vertex1 = "0";
        String vertex2 = "0";
        assertTrue("no es el vertice esperado en grafo1", vertex1.equals(GeometryUtilities.getTheFarthestVertexToOrigin(graph1).label.get()));
        assertTrue("no es el vertice esperado en grafo2", vertex2.equals(GeometryUtilities.getTheFarthestVertexToOrigin(graph2).label.get()));
    }

}

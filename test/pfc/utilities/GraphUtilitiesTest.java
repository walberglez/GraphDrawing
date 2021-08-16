/**
 * GraphUtilitiesTest.java
 * 10/07/2011 17:44:37
 */
package pfc.utilities;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import pfc.models.Edge;
import pfc.models.Graph;
import pfc.models.Vertex;
import pfc.utilities.GraphUtilities;


/**
 * @author walber
 *
 */
public class GraphUtilitiesTest {

    private Graph graph;
    private String graphStr = "{ \"areMultipleEdgesAllowed\" : false, \"edges\" : [ { \"weight\" : 1.0, \"isSelected\" : false, \"color\" : -1, \"to.id\" : \"e2e9a9df-596b-4ac3-8204-f3e7bbdb388d\", \"from.id\" : \"83634fa1-0561-43b1-8cfe-ae68ae3fc0cd\", \"isLinear\" : true, \"isDirected\" : false, \"thickness\" : 1.5, \"label\" : \"e\" }, { \"weight\" : 1.0, \"isSelected\" : false, \"color\" : -1, \"to.id\" : \"c7e7d26e-76ef-4eb7-9f70-0856473a2125\", \"from.id\" : \"e2e9a9df-596b-4ac3-8204-f3e7bbdb388d\", \"isLinear\" : true, \"isDirected\" : false, \"thickness\" : 1.5, \"label\" : \"e\" }, { \"weight\" : 1.0, \"isSelected\" : false, \"color\" : -1, \"to.id\" : \"f57f4989-42e6-41ab-932b-6a5e5b1626e5\", \"from.id\" : \"c7e7d26e-76ef-4eb7-9f70-0856473a2125\", \"isLinear\" : true, \"isDirected\" : false, \"thickness\" : 1.5, \"label\" : \"e\" }, { \"weight\" : 1.0, \"isSelected\" : false, \"color\" : -1, \"to.id\" : \"f57f4989-42e6-41ab-932b-6a5e5b1626e5\", \"from.id\" : \"e2e9a9df-596b-4ac3-8204-f3e7bbdb388d\", \"isLinear\" : true, \"isDirected\" : false, \"thickness\" : 1.5, \"label\" : \"e\" }, { \"weight\" : 1.0, \"isSelected\" : false, \"color\" : -1, \"to.id\" : \"5ad39bf9-128c-46fd-a01a-e31f3d25e17c\", \"from.id\" : \"83634fa1-0561-43b1-8cfe-ae68ae3fc0cd\", \"isLinear\" : true, \"isDirected\" : false, \"thickness\" : 1.5, \"label\" : \"e\" }, { \"weight\" : 1.0, \"isSelected\" : false, \"color\" : -1, \"to.id\" : \"7e5b8ae5-3b7d-4c1c-9902-f8edbd00ceca\", \"from.id\" : \"5ad39bf9-128c-46fd-a01a-e31f3d25e17c\", \"isLinear\" : true, \"isDirected\" : false, \"thickness\" : 1.5, \"label\" : \"e\" }, { \"weight\" : 1.0, \"isSelected\" : false, \"color\" : -1, \"to.id\" : \"b486c2ba-7ccf-4f40-a7eb-4c31db4771bc\", \"from.id\" : \"7e5b8ae5-3b7d-4c1c-9902-f8edbd00ceca\", \"isLinear\" : true, \"isDirected\" : false, \"thickness\" : 1.5, \"label\" : \"e\" }, { \"weight\" : 1.0, \"isSelected\" : false, \"color\" : -1, \"to.id\" : \"5ad39bf9-128c-46fd-a01a-e31f3d25e17c\", \"from.id\" : \"b486c2ba-7ccf-4f40-a7eb-4c31db4771bc\", \"isLinear\" : true, \"isDirected\" : false, \"thickness\" : 1.5, \"label\" : \"e\" }, { \"weight\" : 1.0, \"isSelected\" : false, \"color\" : -1, \"to.id\" : \"d0dda4f6-97b2-4968-894c-9a627256cd03\", \"from.id\" : \"5ad39bf9-128c-46fd-a01a-e31f3d25e17c\", \"isLinear\" : true, \"isDirected\" : false, \"thickness\" : 1.5, \"label\" : \"e\" }, { \"weight\" : 1.0, \"isSelected\" : false, \"color\" : -1, \"to.id\" : \"83634fa1-0561-43b1-8cfe-ae68ae3fc0cd\", \"from.id\" : \"d0dda4f6-97b2-4968-894c-9a627256cd03\", \"isLinear\" : true, \"isDirected\" : false, \"thickness\" : 1.5, \"label\" : \"e\" } ], \"vertices\" : [ { \"id\" : \"83634fa1-0561-43b1-8cfe-ae68ae3fc0cd\", \"weight\" : 1.0, \"isSelected\" : false, \"color\" : -1, \"radius\" : 5.0, \"label\" : \"1\", \"y\" : 66.0, \"x\" : 179.0 }, { \"id\" : \"e2e9a9df-596b-4ac3-8204-f3e7bbdb388d\", \"weight\" : 1.0, \"isSelected\" : false, \"color\" : -1, \"radius\" : 5.0, \"label\" : \"2\", \"y\" : 150.0, \"x\" : 111.0 }, { \"id\" : \"c7e7d26e-76ef-4eb7-9f70-0856473a2125\", \"weight\" : 1.0, \"isSelected\" : false, \"color\" : -1, \"radius\" : 5.0, \"label\" : \"3\", \"y\" : 201.0, \"x\" : 51.0 }, { \"id\" : \"f57f4989-42e6-41ab-932b-6a5e5b1626e5\", \"weight\" : 1.0, \"isSelected\" : false, \"color\" : -1, \"radius\" : 5.0, \"label\" : \"4\", \"y\" : 281.0, \"x\" : 62.0 }, { \"id\" : \"5ad39bf9-128c-46fd-a01a-e31f3d25e17c\", \"weight\" : 1.0, \"isSelected\" : false, \"color\" : -1, \"radius\" : 5.0, \"label\" : \"5\", \"y\" : 140.0, \"x\" : 241.0 }, { \"id\" : \"7e5b8ae5-3b7d-4c1c-9902-f8edbd00ceca\", \"weight\" : 1.0, \"isSelected\" : false, \"color\" : -1, \"radius\" : 5.0, \"label\" : \"6\", \"y\" : 212.0, \"x\" : 199.0 }, { \"id\" : \"b486c2ba-7ccf-4f40-a7eb-4c31db4771bc\", \"weight\" : 1.0, \"isSelected\" : false, \"color\" : -1, \"radius\" : 5.0, \"label\" : \"7\", \"y\" : 303.0, \"x\" : 230.0 }, { \"id\" : \"d0dda4f6-97b2-4968-894c-9a627256cd03\", \"weight\" : 1.0, \"isSelected\" : false, \"color\" : -1, \"radius\" : 5.0, \"label\" : \"8\", \"y\" : 143.0, \"x\" : 357.0 } ], \"name\" : \"componente\", \"areCyclesAllowed\" : true, \"areDirectedEdgesAllowed\" : false, \"areLoopsAllowed\" : false }";
    private Set<Set<Vertex>> bicomponents;
    private Set<Vertex> cut_points;
    private Graph graph2;
    private String graphStr2 = "{ \"areMultipleEdgesAllowed\" : false, \"edges\" : [ { \"weight\" : 1.0, \"isSelected\" : false, \"color\" : -1, \"to.id\" : \"7cfca45a-3d1f-48eb-acbf-08ae8d017168\", \"from.id\" : \"c6e23296-de6b-40bd-8bc2-a69e81b3cd19\", \"isLinear\" : true, \"isDirected\" : false, \"thickness\" : 1.5, \"label\" : \"e\" }, { \"weight\" : 1.0, \"isSelected\" : false, \"color\" : -1, \"to.id\" : \"77ee3553-b5e0-4caf-a572-b0b027f948e9\", \"from.id\" : \"7cfca45a-3d1f-48eb-acbf-08ae8d017168\", \"isLinear\" : true, \"isDirected\" : false, \"thickness\" : 1.5, \"label\" : \"e\" } ], \"vertices\" : [ { \"id\" : \"c6e23296-de6b-40bd-8bc2-a69e81b3cd19\", \"weight\" : 1.0, \"isSelected\" : false, \"color\" : -1, \"radius\" : 5.0, \"label\" : \"0\", \"y\" : 189.0, \"x\" : 148.0 }, { \"id\" : \"7cfca45a-3d1f-48eb-acbf-08ae8d017168\", \"weight\" : 1.0, \"isSelected\" : false, \"color\" : -1, \"radius\" : 5.0, \"label\" : \"1\", \"y\" : 189.0, \"x\" : 278.0 }, { \"id\" : \"77ee3553-b5e0-4caf-a572-b0b027f948e9\", \"weight\" : 1.0, \"isSelected\" : false, \"color\" : -1, \"radius\" : 5.0, \"label\" : \"2\", \"y\" : 190.0, \"x\" : 396.0 } ], \"name\" : \"2\", \"areCyclesAllowed\" : true, \"areDirectedEdgesAllowed\" : false, \"areLoopsAllowed\" : false }";
    private Set<Set<Vertex>> bicomponents2;
    private Set<Vertex> cut_points2;
    
    private Graph graph1;
    private String graphStr1 = "{ \"areMultipleEdgesAllowed\" : false, \"edges\" : [ { \"weight\" : 1.0, \"isSelected\" : false, \"color\" : -1, \"to.id\" : \"3d0e2502-3eb2-46bb-9fb5-ef38b2056173\", \"from.id\" : \"00faae11-b17a-4803-bcc4-3203d3a8d85d\", \"isLinear\" : true, \"isDirected\" : false, \"thickness\" : 1.5, \"label\" : \"e\" }, { \"weight\" : 1.0, \"isSelected\" : false, \"color\" : -1, \"to.id\" : \"e65a1fb3-99f0-4c64-a1aa-cc2958e4b102\", \"from.id\" : \"3d0e2502-3eb2-46bb-9fb5-ef38b2056173\", \"isLinear\" : true, \"isDirected\" : false, \"thickness\" : 1.5, \"label\" : \"e\" }, { \"weight\" : 1.0, \"isSelected\" : false, \"color\" : -1, \"to.id\" : \"d05d4e3c-ff72-4843-8908-1368b45ecc5e\", \"from.id\" : \"e65a1fb3-99f0-4c64-a1aa-cc2958e4b102\", \"isLinear\" : true, \"isDirected\" : false, \"thickness\" : 1.5, \"label\" : \"e\" }, { \"weight\" : 1.0, \"isSelected\" : false, \"color\" : -1, \"to.id\" : \"00faae11-b17a-4803-bcc4-3203d3a8d85d\", \"from.id\" : \"d05d4e3c-ff72-4843-8908-1368b45ecc5e\", \"isLinear\" : true, \"isDirected\" : false, \"thickness\" : 1.5, \"label\" : \"e\" } ], \"vertices\" : [ { \"id\" : \"00faae11-b17a-4803-bcc4-3203d3a8d85d\", \"weight\" : 1.0, \"isSelected\" : false, \"color\" : -1, \"radius\" : 5.0, \"label\" : \"0\", \"y\" : 279.0, \"x\" : 207.0 }, { \"id\" : \"3d0e2502-3eb2-46bb-9fb5-ef38b2056173\", \"weight\" : 1.0, \"isSelected\" : false, \"color\" : -1, \"radius\" : 5.0, \"label\" : \"1\", \"y\" : 138.0, \"x\" : 389.0 }, { \"id\" : \"e65a1fb3-99f0-4c64-a1aa-cc2958e4b102\", \"weight\" : 1.0, \"isSelected\" : false, \"color\" : -1, \"radius\" : 5.0, \"label\" : \"2\", \"y\" : 272.0, \"x\" : 570.0 }, { \"id\" : \"d05d4e3c-ff72-4843-8908-1368b45ecc5e\", \"weight\" : 1.0, \"isSelected\" : false, \"color\" : -1, \"radius\" : 5.0, \"label\" : \"3\", \"y\" : 371.0, \"x\" : 385.0 } ], \"name\" : \"1\", \"areCyclesAllowed\" : true, \"areDirectedEdgesAllowed\" : false, \"areLoopsAllowed\" : false }";
    private Graph directed;
    /**
     */
    @Before
    public void setUp() throws Exception {
        graph = new Graph( graphStr );
        bicomponents = GraphUtilities.findBiconnectedComponents( graph );
        cut_points = GraphUtilities.findBiconnectedCutPoints( graph );
        
        graph2 = new Graph( graphStr2 );
        bicomponents2 = GraphUtilities.findBiconnectedComponents( graph2 );
        cut_points2 = GraphUtilities.findBiconnectedCutPoints( graph2 );
        
        graph1 = new Graph( graphStr1 );
        directed = GraphUtilities.getDirectedPlanarGraph( graph1 );
    }


    /**
     * Test method for {@link pfc.utilities.GraphUtilities#getDirectedPlanarGraph(pfc.models.Graph)}.
     */
    @Test
    public void testGetDirectedPlanarGraph() {
        System.out.println("Directed:");
        System.out.println("areLoopsAllowed: " + directed.areLoopsAllowed );
        System.out.println("areDirectedEdgesAllowed " + directed.areDirectedEdgesAllowed );
        System.out.println("areMultipleEdgesAllowed " + directed.areMultipleEdgesAllowed );
        System.out.println("areCyclesAllowed: " + directed.areCyclesAllowed );
        System.out.println("Vertices:");
        for (Vertex v : directed.vertices) {
            System.out.println("\tlabel: " + v.label.get());
        }
        System.out.println("Edges:");
        for(Edge e : directed.edges) {
            System.out.println("\tfrom: " + e.from.label.get() + ", to: " + e.to.label.get() + ", directed: " + e.isDirected);
        }
    } 
    
    /**
     * Test method for {@link pfc.utilities.GraphUtilities#findBiconnectedComponents(pfc.models.Graph)}.
     */
    @Test
    public void testFindBiconnectedComponents() {
        // componentes
        ArrayList<ArrayList<String>> componentes = new ArrayList<ArrayList<String>>();
        // componente 0
        ArrayList<String> componente0 = new ArrayList<String>();
        componente0.add("1");
        componente0.add("2");
        componentes.add(componente0);
        // componente 1
        ArrayList<String> componente1 = new ArrayList<String>();
        componente1.add("2");
        componente1.add("3");
        componente1.add("4");
        componentes.add(componente1);
        // componente 2
        ArrayList<String> componente2 = new ArrayList<String>();
        componente2.add("1");
        componente2.add("5");
        componente2.add("8");
        componentes.add(componente2);
        // componente 3
        ArrayList<String> componente3 = new ArrayList<String>();
        componente3.add("5");
        componente3.add("6");
        componente3.add("7");
        componentes.add(componente3);
        
        // numero de componentes biconexas del grafo
        assertTrue("numero de componentes biconexas distinto al esperado",
                bicomponents.size() == componentes.size());
        
        boolean estaComponente0 = false;
        boolean estaComponente1 = false;
        boolean estaComponente2 = false;
        boolean estaComponente3 = false;
        for ( Set<Vertex> component : bicomponents )
        {
            boolean esComponente0 = true;
            boolean esComponente1 = true;
            boolean esComponente2 = true;
            boolean esComponente3 = true;
            for (Vertex v : component )
            {
                esComponente0 = esComponente0 && componente0.contains(v.label.get());
                esComponente1 = esComponente1 && componente1.contains(v.label.get());
                esComponente2 = esComponente2 && componente2.contains(v.label.get());
                esComponente3 = esComponente3 && componente3.contains(v.label.get());
            }
            // solo debe ser true una componente cada vez y siempre una componente distinta a las anteriores
            if (esComponente0) {
                assertTrue("componente0 no es unica", (esComponente1 == false) && (esComponente2 == false) && (esComponente3 == false));
                assertTrue("componente0 ya ha sido devuelta", estaComponente0 == false);
            }
            if (esComponente1) {
                assertTrue("componente1 no es unica", (esComponente0 == false) && (esComponente2 == false) && (esComponente3 == false));
                assertTrue("componente1 ya ha sido devuelta", estaComponente1 == false);
            }
            if (esComponente2) {
                assertTrue("componente2 no es unica", (esComponente0 == false) && (esComponente1 == false) && (esComponente3 == false));
                assertTrue("componente2 ya ha sido devuelta", estaComponente2 == false);
            }
            if (esComponente3) {
                assertTrue("componente3 no es unica", (esComponente0 == false) && (esComponente1 == false) && (esComponente2 == false));
                assertTrue("componente3 ya ha sido devuelta", estaComponente3 == false);
            }

            estaComponente0 = estaComponente0 || esComponente0;
            estaComponente1 = estaComponente1 || esComponente1;
            estaComponente2 = estaComponente2 || esComponente2;
            estaComponente3 = estaComponente3 || esComponente3;
        }
    }
    
    /**
     * Test method for {@link pfc.utilities.GraphUtilities#findBiconnectedComponents(pfc.models.Graph)}.
     */
    @Test
    public void testFindBiconnectedComponents2() {
        // componentes
        ArrayList<ArrayList<String>> componentes = new ArrayList<ArrayList<String>>();
        // componente 0
        ArrayList<String> componente0 = new ArrayList<String>();
        componente0.add("0");
        componente0.add("1");
        componentes.add(componente0);
        // componente 1
        ArrayList<String> componente1 = new ArrayList<String>();
        componente1.add("1");
        componente1.add("2");
        componentes.add(componente1);
        
        // numero de componentes biconexas del grafo
        assertTrue("numero de componentes biconexas distinto al esperado",
                bicomponents2.size() == componentes.size());
        
        boolean estaComponente0 = false;
        boolean estaComponente1 = false;
        for ( Set<Vertex> component : bicomponents2 )
        {
            boolean esComponente0 = true;
            boolean esComponente1 = true;
            for (Vertex v : component )
            {
                esComponente0 = esComponente0 && componente0.contains(v.label.get());
                esComponente1 = esComponente1 && componente1.contains(v.label.get());
            }
            // solo debe ser true una componente cada vez y siempre una componente distinta a las anteriores
            if (esComponente0) {
                assertTrue("componente0 no es unica", (esComponente1 == false));
                assertTrue("componente0 ya ha sido devuelta", estaComponente0 == false);
            }
            if (esComponente1) {
                assertTrue("componente1 no es unica", (esComponente0 == false));
                assertTrue("componente1 ya ha sido devuelta", estaComponente1 == false);
            }

            estaComponente0 = estaComponente0 || esComponente0;
            estaComponente1 = estaComponente1 || esComponente1;
        }
    }
    
    /**
     * Test method for {@link pfc.utilities.GraphUtilities#findBiconnectedCutPoints(pfc.models.Graph)}.
     */
    @Test
    public void testFindBiconnectedCutPoints() {
        String cp1 = "1";
        String cp2 = "2";
        String cp3 = "5";
        // numero de cut points es 3
        assertTrue("numero de cutpoints inesperado", cut_points.size() == 3);
        for ( Vertex v : cut_points )
        {
            assertTrue("no es un cutpoints esperado", cp1.equals(v.label.get()) || cp2.equals(v.label.get()) || cp3.equals(v.label.get()));
        }
    }
    
    /**
     * Test method for {@link pfc.utilities.GraphUtilities#findBiconnectedCutPoints(pfc.models.Graph)}.
     */
    @Test
    public void testFindBiconnectedCutPoints2() {
        String cp1 = "1";
        // numero de cut points es 1
        assertTrue("numero de cutpoints inesperado", cut_points2.size() == 1);
        for ( Vertex v : cut_points2 )
        {
            assertTrue("no es un cutpoints esperado", cp1.equals(v.label.get()));
        }
    }

}


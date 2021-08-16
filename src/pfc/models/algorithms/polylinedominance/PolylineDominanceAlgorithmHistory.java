/**
 * PolylineDominanceAlgorithmHistory.java
 * 03/08/2011 23:53:40
 */
package pfc.models.algorithms.polylinedominance;

/**
 * @author walber
 *
 */
public class PolylineDominanceAlgorithmHistory
{
	/**
	 */
	private String	drawing;
	/**
	 */
	private String	reducedGraph;
	/**
	 */
	private String	explanation;
	
	public PolylineDominanceAlgorithmHistory( )
	{
		this.drawing = "";
		this.reducedGraph = "";
		this.explanation = "";
	}

	/**
	 * @param drawing {@link String}
	 * @param reducedGraph {@link String}
	 * @param explanation {@link String}
	 */
	public PolylineDominanceAlgorithmHistory( String drawing, String reducedGraph, String explanation )
	{
		this.drawing = drawing;
		this.reducedGraph = reducedGraph;
		this.explanation = explanation;
	}

	/**
	 * @return the drawing
	 */
	public String getDrawing( )
	{
		return this.drawing;
	}

	/**
	 * @param drawing the drawing to set
	 */
	public void setDrawing( String drawing )
	{
		this.drawing = drawing;
	}

	/**
	 * @return the reducedGraph
	 */
	public String getReducedGraph( )
	{
		return this.reducedGraph;
	}

	/**
	 * @param reducedGraph the reducedGraph to set
	 */
	public void setReducedGraph( String reducedGraph )
	{
		this.reducedGraph = reducedGraph;
	}

	/**
	 * @return the explanation
	 */
	public String getExplanation( )
	{
		return this.explanation;
	}

	/**
	 * @param explanation the explanation to set
	 */
	public void setExplanation( String explanation )
	{
		this.explanation = explanation;
	}
}

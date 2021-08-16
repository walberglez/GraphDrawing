/**
 * OrthogonalAlgorithmHistory.java
 * 26/07/2011 18:19:59
 */
package pfc.models.algorithms.orthogonal;

/**
 * @author Walber Gonzalez
 *
 */
public class OrthogonalAlgorithmHistory
{
	/**
	 */
	private String	digraph;
	/**
	 */
	private String	visibilityDrawing;
	/**
	 */
	private String	orthogonalGraph;
	/**
	 */
	private String	explanation;
	
	public OrthogonalAlgorithmHistory( )
	{
		this.digraph = "";
		this.visibilityDrawing = "";
		this.orthogonalGraph = "";
		this.explanation = "";
	}
	
	/**
	 * @param digraph String
	 * @param visibilityDrawing String
	 * @param orthogonalGraph String
	 * @param explanation String
	 */
	public OrthogonalAlgorithmHistory( String digraph, String visibilityDrawing,
			String orthogonalGraph, String explanation )
	{
		this.digraph = digraph;
		this.visibilityDrawing = visibilityDrawing;
		this.orthogonalGraph = orthogonalGraph;
		this.explanation = explanation;
	}


	/**
	 * @return the digraph
	 */
	public String getDigraph( )
	{
		return this.digraph;
	}


	/**
	 * @param digraph the digraph to set
	 */
	public void setDigraph( String digraph )
	{
		this.digraph = digraph;
	}


	/**
	 * @return the visibilityDrawing
	 */
	public String getVisibilityDrawing( )
	{
		return this.visibilityDrawing;
	}


	/**
	 * @param visibilityDrawing the visibilityDrawing to set
	 */
	public void setVisibilityDrawing( String visibilityDrawing )
	{
		this.visibilityDrawing = visibilityDrawing;
	}


	/**
	 * @return the orthogonalGraph
	 */
	public String getOrthogonalGraph( )
	{
		return this.orthogonalGraph;
	}


	/**
	 * @param orthogonalGraph the orthogonalGraph to set
	 */
	public void setOrthogonalGraph( String orthogonalGraph )
	{
		this.orthogonalGraph = orthogonalGraph;
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

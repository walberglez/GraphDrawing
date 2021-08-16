package pfc.models.algorithms.visibilityrepresentation;

/**
 * @author    walber
 */
public class VisRepAlgorithmHistory
{
	/**
	 */
	private String	graph;
	/**
	 */
	private String	dual;
	/**
	 */
	private String	drawing;
	/**
	 */
	private String	explanation;
	
	public VisRepAlgorithmHistory ( )
	{
		this.graph = "";
		this.dual = "";
		this.drawing = "";
		this.explanation = "";
	}

	/**
	 * @param graph String
	 * @param dual String
	 * @param drawing String
	 * @param explanation String
	 */
	public VisRepAlgorithmHistory( String graph, String dual, String drawing, String explanation )
	{
		this.graph = graph;
		this.dual = dual;
		this.drawing = drawing;
		this.explanation = explanation;
	}

	/**
	 * @return    the graph
	 */
	public String getGraph( )
	{
		return graph;
	}

	/**
	 * @return    the dual
	 */
	public String getDual( )
	{
		return dual;
	}

	/**
	 * @return    the drawing
	 */
	public String getDrawing( )
	{
		return drawing;
	}

	/**
	 * @return    the explanation
	 */
	public String getExplanation( )
	{
		return explanation;
	}

	/**
	 * @param graph    the graph to set
	 */
	public void setGraph( String graph )
	{
		this.graph = graph;
	}

	/**
	 * @param dual    the dual to set
	 */
	public void setDual( String dual )
	{
		this.dual = dual;
	}

	/**
	 * @param drawing    the drawing to set
	 */
	public void setDrawing( String drawing )
	{
		this.drawing = drawing;
	}

	/**
	 * @param explanation    the explanation to set
	 */
	public void setExplanation( String explanation )
	{
		this.explanation = explanation;
	}
}
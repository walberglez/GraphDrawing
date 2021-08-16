/**
 * UpwardPolylineAlgorithmHistory.java
 * 24/07/2011 04:36:08
 */
package pfc.models.algorithms.polyline;

/**
 * @author walber
 *
 */
public class UpwardPolylineAlgorithmHistory
{
	/**
	 */
	private String	visibilityDrawing;
	/**
	 */
	private String	polylineGraph;
	/**
	 */
	private String	explanation;
	
	public UpwardPolylineAlgorithmHistory ( )
	{
		this.visibilityDrawing = "";
		this.polylineGraph = "";
		this.explanation = "";
	}

	/**
	 * @param visibilityDrawing String
	 * @param polylineGraph String
	 * @param explanation String
	 */
	public UpwardPolylineAlgorithmHistory( String visibilityDrawing, String polylineGraph, String explanation )
	{
		this.visibilityDrawing = visibilityDrawing;
		this.polylineGraph = polylineGraph;
		this.explanation = explanation;
	}

	/**
	 * @return    the visibilityDrawing
	 */
	public String getVisibilityDrawing( )
	{
		return visibilityDrawing;
	}

	/**
	 * @return the polylineGraph
	 */
	public String getPolylineGraph( )
	{
		return this.polylineGraph;
	}

	/**
	 * @return    the explanation
	 */
	public String getExplanation( )
	{
		return explanation;
	}

	/**
	 * @param drawing    the drawing to set
	 */
	public void setVisibilityDrawing( String drawing )
	{
		this.visibilityDrawing = drawing;
	}
	
	/**
	 * @param polylineGraph the polylineGraph to set
	 */
	public void setPolylineGraph( String polylineGraph )
	{
		this.polylineGraph = polylineGraph;
	}

	/**
	 * @param explanation    the explanation to set
	 */
	public void setExplanation( String explanation )
	{
		this.explanation = explanation;
	}
}

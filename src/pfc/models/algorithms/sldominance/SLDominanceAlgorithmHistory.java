/**
 * SLDominanceAlgorithmHistory.java
 * 24/07/2011 04:36:08
 */
package pfc.models.algorithms.sldominance;

/**
 * @author walber
 *
 */
public class SLDominanceAlgorithmHistory
{
	/**
	 */
	private String	dominanceDrawing;
	/**
	 */
	private String	explanation;
	
	public SLDominanceAlgorithmHistory ( )
	{
		this.dominanceDrawing = "";
		this.explanation = "";
	}

	/**
	 * @param dominanceGraph String
	 * @param explanation String
	 */
	public SLDominanceAlgorithmHistory(String dominanceGraph, String explanation) {
		this.dominanceDrawing = dominanceGraph;
		this.explanation = explanation;
	}

	/**
	 * @return the dominanceDrawing
	 */
	public String getDominanceDrawing() {
		return this.dominanceDrawing;
	}

	/**
	 * @param dominanceDrawing the dominanceDrawing to set
	 */
	public void setDominanceDrawing(String dominanceDrawing) {
		this.dominanceDrawing = dominanceDrawing;
	}

	/**
	 * @return the explanation
	 */
	public String getExplanation() {
		return this.explanation;
	}

	/**
	 * @param explanation the explanation to set
	 */
	public void setExplanation(String explanation) {
		this.explanation = explanation;
	}
	
}

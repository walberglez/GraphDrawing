/**
 * ExecutionControlPanelActions.java
 * 20/07/2011 00:21:24
 */
package pfc.controllers.algorithms;

/**
 * @author Walber Gonzalez
 *
 */
public interface ExecutionControlPanelActions
{
	/**
	 * Ejecuta siguiente etapa
	 * @return String explicacion de la etapa
	 */
	public String executeNextStep ( );
	
	/**
	 * Ejecuta etapa anterior
	 * @return String explicacion de la etapa
	 */
	public String executeBackStep ( );
	
	/**
	 * Ejecuta todo hasta el final
	 * @return String explicacion de la etapa
	 */
	public String executeEndStep ( );
	
	/**
	 * Ejecuta la cancelacion de la ejecucion
	 * @return String explicacion de la etapa
	 */
	public String executeCancelStep ( );
}

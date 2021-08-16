/**
 * StateSupport.java
 * 20/05/2011 00:27:47
 */
package pfc.utilities;

import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

/**
 * @author walber
 * 
 */
public class StateSupport extends EventListenerList
{

	private static final long serialVersionUID = 5299391739252083094L;

	public StateSupport( )
	{
		super( );
	}

	/**
	 * Agregar un listener a la lista.
	 * 
	 * @param listener ChangeListener
	 */
	public void addStateChangeListener( ChangeListener listener )
	{
		this.add( ChangeListener.class, listener );
	}

	/**
	 * Borrar un listener de la lista.
	 * 
	 * @param listener ChangeListener
	 */
	public void removeStateChangeListener( ChangeListener listener )
	{
		this.remove( ChangeListener.class, listener );
	}

	/**
	 * Lanzar el evento del estado cambiado.
	 */
	public void fireStateChangedEvent( )
	{
		// Guaranteed to return a non-null array
		Object[ ] listeners = this.getListenerList( );
		// Process the listeners last to first, notifying
		// those that are interested in this event
		for ( int i = 0; i < listeners.length; i += 2 )
		{
			if ( listeners[ i ] == ChangeListener.class )
			{
				( (ChangeListener) listeners[i + 1] ).stateChanged( null );
			}
		}
	}
}

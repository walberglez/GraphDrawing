/**
 * GraphDisplaySettings.java
 */
package pfc.settings;

import java.util.*;

import pfc.models.*;
import pfc.utilities.*;


/**
 * @author    Cameron Behar
 */
public class GraphSettings extends ObservableModel
{
	public final Property<Boolean>	showEdgeWeights;
	public final Property<Boolean>	showEdgeLabels;
	public final Property<Boolean>	showVertexWeights;
	public final Property<Boolean>	showVertexLabels;
	
	public GraphSettings( )
	{
		UserSettings settings = UserSettings.instance;
		
		this.showEdgeWeights = new Property<Boolean>( settings.defaultShowEdgeWeights.get( ) );
		this.showEdgeLabels = new Property<Boolean>( settings.defaultShowEdgeLabels.get( ) );
		this.showVertexWeights = new Property<Boolean>( settings.defaultShowVertexWeights.get( ) );
		this.showVertexLabels = new Property<Boolean>( settings.defaultShowVertexLabels.get( ) );
	}
	
	public GraphSettings( GraphSettings graphSettings )
	{
		this.showEdgeWeights = new Property<Boolean>( graphSettings.showEdgeWeights.get( ) );
		this.showEdgeLabels = new Property<Boolean>( graphSettings.showEdgeLabels.get( ) );
		this.showVertexWeights = new Property<Boolean>( graphSettings.showVertexWeights.get( ) );
		this.showVertexLabels = new Property<Boolean>( graphSettings.showVertexLabels.get( ) );
	}
	
	@Override
	public String toString( )
	{
		Map<String, Object> members = new HashMap<String, Object>( );
		
		members.put( "showEdgeWeights", this.showEdgeWeights );
		members.put( "showEdgeLabels", this.showEdgeLabels );
		members.put( "showVertexWeights", this.showVertexWeights );
		members.put( "showVertexLabels", this.showVertexLabels );
		
		return JsonUtilities.formatObject( members );
	}
}

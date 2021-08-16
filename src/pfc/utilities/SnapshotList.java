package pfc.utilities;

import pfc.settings.UserSettings;

/**
 * @author    walber
 */
public class SnapshotList<T> {

	/**
	 * @author    walber
	 */
	private class Snapshot
	{
		public T		value;
		public Snapshot	previous;
		public Snapshot	next;

		public Snapshot( T value )
		{
			this.value = value;
			this.previous = this;
			this.next = this;
		}

		public Snapshot( T value, Snapshot previous, Snapshot next )
		{
			this.value = value;
			this.previous = previous;
			this.next = next;
		}
	}

	// SnapshotList is essentially a kind of doubly-linked circular list of strings with a maximum capacity
	private Snapshot	current;
	private Snapshot	newest;
	private Snapshot	oldest;
	private int			capacity;
	private int			size;

	public SnapshotList( T snapshot )
	{
		this.newest = this.oldest = this.current = new Snapshot( snapshot );
		this.capacity = UserSettings.instance.undoLoggingMaximum.get( );
		this.size = 0;
	}

	public void add( T snapshot )
	{
		if( !snapshot.equals( this.current.value ) )
			if( this.size < this.capacity )
			{
				Snapshot newSnapshot = new Snapshot( snapshot, this.current, this.current.next );
				this.current.next.previous = newSnapshot;
				this.current = this.newest = this.current.next = newSnapshot;
				++this.size;
			}
			else
			{
				this.current.next.value = snapshot;
				this.current = this.newest = this.current.next;
				if( this.current == this.oldest )
					this.oldest = this.current.next;
			}
	}

	public void clear( )
	{
		this.newest = this.oldest = this.current;
		this.current.previous = this.current;
		this.current.next = this.current;
	}

	public T current( )
	{
		return this.current.value;
	}

	/**
	 * @return capacity
	 */
	public int getCapacity( )
	{
		return this.capacity;
	}

	public T next( )
	{
		if( this.current == this.newest )
			return null;

		this.current = this.current.next;

		return this.current.value;
	}

	public T previous( )
	{
		if( this.current == this.oldest )
			return null;

		this.current = this.current.previous;

		return this.current.value;
	}

	/**
	 * @param capacity int
	 */
	public void setCapacity( int capacity )
	{
		this.capacity = capacity;
	}
	
	public void setBeginning ( )
	{
		this.current = this.oldest;
	}
}
package org.flowermc.event;

public abstract class Event
{
	private final boolean asynchronous;

	public Event()
	{
		this(false);
	}

	public Event(boolean asynchronous)
	{
		this.asynchronous = asynchronous;
	}

	public boolean asynchronous()
	{
		return this.asynchronous;
	}
}

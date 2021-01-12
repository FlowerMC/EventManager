package org.flowermc.event;

public interface Cancellable
{
	void cancel(boolean cancelled);

	boolean cancelled();
}

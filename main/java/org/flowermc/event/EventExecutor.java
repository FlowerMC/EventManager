package org.flowermc.event;

public interface EventExecutor
{
	void execute(Object listener, Event event);
}

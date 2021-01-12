package org.flowermc.event;

import java.lang.reflect.Method;

public class RegisteredListener
{
	private final EventExecutor executor;
	private final Object listener;
	private final Class<? extends Event> type;
	private final Method subscriber;
	private final EventPriority priority;
	private RegisteredListener next;

	public RegisteredListener(Object listener, Class<? extends Event> type, Method subscriber, EventPriority priority, EventExecutor executor)
	{
		this.listener = listener;
		this.type = type;
		this.subscriber = subscriber;
		this.priority = priority;
		this.executor = executor;
	}

	public void execute(Event event)
	{
		this.executor.execute(this.listener, event);
	}

	public Object listener()
	{
		return this.listener;
	}

	public Class<? extends Event> type()
	{
		return this.type;
	}

	public Method subscriber()
	{
		return this.subscriber;
	}

	public EventPriority priority()
	{
		return this.priority;
	}

	public void setNext(RegisteredListener next)
	{
		this.next = next;
	}

	public RegisteredListener next()
	{
		return this.next;
	}
}

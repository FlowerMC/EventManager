package org.flowermc.event;

import java.lang.reflect.Method;
import java.util.Set;

public class SimpleEventManager extends EventManager
{
	public SimpleEventManager(SubscriberList handlers)
	{
		super(handlers);
	}

	@Override
	public void registerAll(Object listener)
	{
		Set<Method> subscribers = this.subscribers(listener.getClass());
		for (Method subscriber : subscribers)
		{
			this.register(listener, subscriber);
		}
	}

	@Override
	public void register(Object listener, Method subscriber)
	{
		this.handlers.register(this.createRegisteredListener(listener, subscriber));
	}
}

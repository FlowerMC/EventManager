package org.flowermc.event;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class SubscriberList
{
	private final Map<Class<? extends Event>, EventType> types = new ConcurrentHashMap<>();
	private final Map<Object, Map<Method, RegisteredListener>> registeredListeners = new ConcurrentHashMap<>();

	public void register(RegisteredListener listener)
	{
		Objects.requireNonNull(listener);
		Map<Method, RegisteredListener> methodToListener = this.registeredListeners.computeIfAbsent(listener.listener(), l -> new ConcurrentHashMap<>());
		if (methodToListener.containsValue(listener))
		{
			throw new IllegalStateException("Listener has already registered");
		}
		this.findEventType(listener.type()).register(listener);
		methodToListener.put(listener.subscriber(), listener);
	}

	public void unregister(Object listener, Method subscriber)
	{
		Objects.requireNonNull(listener);
		Objects.requireNonNull(subscriber);

		Map<Method, RegisteredListener> methodToListener = this.registeredListeners.get(listener);
		if (methodToListener != null)
		{
			RegisteredListener registeredListener = methodToListener.get(subscriber);
			if (registeredListener != null)
			{
				this.types.get(registeredListener.type()).unregister(registeredListener);
			}
		}
	}

	public void callEvent(Event event)
	{
		EventType type = this.findEventType(event.getClass());
		if (event instanceof Cancellable)
		{
			Cancellable cancellable = (Cancellable) event;
			while (type != null)
			{
				if (cancellable.cancelled())
				{
					return;
				}

				type.callEvent(event);
				type = type.parent;
			}
		}
		else
		{
			while (type != null)
			{
				type.callEvent(event);
				type = type.parent;
			}
		}
	}

	private EventType findEventType(Class<? extends Event> type)
	{
		Objects.requireNonNull(type);
		return this.types.computeIfAbsent(type, t ->
		{
			EventType eventType = new EventType();
			Class<?> superType = t.getSuperclass();
			if (superType.isAssignableFrom(Event.class))
			{
				eventType.parent = SubscriberList.this.findEventType(superType.asSubclass(Event.class));
			}
			return eventType;
		});
	}
}

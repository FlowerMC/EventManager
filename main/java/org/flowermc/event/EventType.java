package org.flowermc.event;

import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

public class EventType
{
	public EventType parent;
	private final EnumMap<EventPriority, RegisteredListener> listeners = new EnumMap<>(EventPriority.class);

	public void register(RegisteredListener listener)
	{
		Objects.requireNonNull(listener);
		EventPriority priority = listener.priority();
		RegisteredListener rl = this.listeners.get(priority);
		if (rl == null)
		{
			this.listeners.put(priority, listener);
		}
		else
		{
			while (rl.next() != null)
			{
				rl = rl.next();
			}
			rl.setNext(listener);
		}
	}

	public void unregister(RegisteredListener listener)
	{
		Objects.requireNonNull(listener);

		EventPriority priority = listener.priority();
		RegisteredListener previous = null;
		RegisteredListener registered = this.listeners.get(priority);
		if (registered != null)
		{
			while (registered != null)
			{
				if (registered == listener)
				{
					if (previous == null)
					{
						this.listeners.remove(priority);
						this.listeners.put(priority, registered.next());
					}
					else
					{
						previous.setNext(registered.next());
					}
					return;
				}

				previous = registered;
				registered = registered.next();
			}
		}
	}

	public void callEvent(Event event)
	{
		if (event instanceof Cancellable)
		{
			for (Map.Entry<EventPriority, RegisteredListener> entry : this.listeners.entrySet())
			{
				RegisteredListener listener = entry.getValue();
				Cancellable cancellable = (Cancellable) event;
				while (listener != null)
				{
					if (cancellable.cancelled())
					{
						return;
					}

					listener.execute(event);
					listener = listener.next();
				}
			}
		}
		else
		{
			for (Map.Entry<EventPriority, RegisteredListener> entry : this.listeners.entrySet())
			{
				RegisteredListener listener = entry.getValue();
				while (listener != null)
				{
					listener.execute(event);
					listener = listener.next();
				}
			}
		}
	}
}

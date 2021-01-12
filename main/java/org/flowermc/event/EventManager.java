package org.flowermc.event;

import org.mve.invoke.MethodKind;
import org.mve.invoke.ReflectionFactory;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class EventManager
{
	protected final SubscriberList handlers;

	public EventManager(SubscriberList handlers)
	{
		this.handlers = handlers;
	}

	public abstract void registerAll(Object listener);

	public abstract void register(Object listener, Method subscriber);

	public RegisteredListener createRegisteredListener(Object listener, Method subscriber)
	{
		SubscribeEvent annotation = this.checkAnnotation(subscriber);
		Class<? extends Event> type = this.chuckMethod(subscriber);
		EventExecutor executor = this.createEventExecutor(subscriber);
		return new RegisteredListener(listener, type, subscriber, annotation.value(), executor);
	}

	public Class<? extends Event> chuckMethod(Method subscriber)
	{
		Class<?>[] parameters = subscriber.getParameterTypes();
		if (parameters.length != 1 || !parameters[0].isAssignableFrom(Event.class) || subscriber.getReturnType() != void.class)
		{
			throw new IllegalArgumentException("Illegal event subscriber method definition: " + subscriber);
		}

		return parameters[0].asSubclass(Event.class);
	}

	public SubscribeEvent checkAnnotation(Method subscriber)
	{
		SubscribeEvent annotation = subscriber.getAnnotation(SubscribeEvent.class);
		if (annotation == null)
		{
			throw new IllegalArgumentException("Subscribe method must have @SubscribeEvent");
		}

		return annotation;
	}

	public Set<Method> subscribers(Class<?> c)
	{
		return new HashSet<>(Arrays.asList(ReflectionFactory.ACCESSOR.getMethods(c)))
			.stream()
			.filter(m -> m.getAnnotation(SubscribeEvent.class) != null)
			.collect(Collectors.toSet());
	}

	public EventExecutor createEventExecutor(Method subscriber)
	{
		Class<? extends Event> eventType = this.chuckMethod(subscriber);
		Class<?> listenerClass = subscriber.getDeclaringClass();
		boolean abstracted = Modifier.isAbstract(subscriber.getModifiers());

		return new ReflectionFactory(EventExecutor.class, listenerClass)
			.method(
				new MethodKind("execute", void.class, Object.class, Event.class),
				new MethodKind(subscriber.getName(), void.class, eventType),
				abstracted ? ReflectionFactory.KIND_INVOKE_INTERFACE : ReflectionFactory.KIND_INVOKE_VIRTUAL
			)
			.allocate();
	}
}

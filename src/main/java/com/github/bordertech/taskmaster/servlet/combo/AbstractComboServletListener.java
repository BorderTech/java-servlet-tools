package com.github.bordertech.taskmaster.servlet.combo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Combo ServletListener that allows a group of Listeners to be defined in one class.
 * <p>
 * This allows multiple ServletListeners to be easily annotated in one class instead of multiple entries in a web.xml.
 * </p>
 * <p>
 * The class is abstract as projects are expected to extend this class and annotate it with the {@link WebListener} annotation.
 * </p>
 *
 * @see WebListener
 * @see ServletContextListener
 */
public abstract class AbstractComboServletListener implements ServletContextListener {

	private static final Log LOGGER = LogFactory.getLog(AbstractComboServletListener.class);

	private final List<ServletContextListener> listeners;

	/**
	 * @param listener the first listener to combine
	 * @param listeners the other listeners to combine into a single listener
	 */
	protected AbstractComboServletListener(final ServletContextListener listener, final ServletContextListener... listeners) {
		if (listener == null) {
			throw new IllegalArgumentException("Listener cannot be null");
		}
		boolean hasNull = Arrays.stream(listeners).anyMatch(Objects::isNull);
		if (hasNull) {
			throw new IllegalArgumentException("Listeners cannot contain a null listener");
		}
		List<ServletContextListener> backing = new ArrayList<>();
		backing.add(listener);
		backing.addAll(Arrays.asList(listeners));
		this.listeners = Collections.unmodifiableList(backing);
	}

	/**
	 * @param listeners the group of listeners to combine into a single listener
	 */
	protected AbstractComboServletListener(final List<ServletContextListener> listeners) {
		if (listeners == null || listeners.isEmpty()) {
			throw new IllegalArgumentException("List of listeners cannot be null or empty");
		}
		boolean hasNull = listeners.stream().anyMatch(Objects::isNull);
		if (hasNull) {
			throw new IllegalArgumentException("Listeners list cannot contain a null listener");
		}
		this.listeners = Collections.unmodifiableList(listeners);
	}

	/**
	 *
	 * @return the group of listeners to combine
	 */
	public final List<ServletContextListener> getListeners() {
		return listeners;
	}

	@Override
	public void contextInitialized(final ServletContextEvent sce) {
		for (ServletContextListener listener : getListeners()) {
			try {
				listener.contextInitialized(sce);
			} catch (Exception e) {
				LOGGER.error("Error calling initialized servlet context listener [" + listener.getClass().getName() + "]. "
						+ e.getMessage(), e);
			}
		}
	}

	@Override
	public void contextDestroyed(final ServletContextEvent sce) {
		for (ServletContextListener listener : getListeners()) {
			try {
				listener.contextDestroyed(sce);
			} catch (Exception e) {
				LOGGER.error("Error calling destroyed servlet context listener [" + listener.getClass().getName() + "]. "
						+ e.getMessage(), e);
			}
		}
	}

}

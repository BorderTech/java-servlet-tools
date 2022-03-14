package com.github.bordertech.taskmaster.servlet.combo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;

/**
 * Combo Filter that allows a group of Filters to be defined in one class.
 * <p>
 * This allows multiple filters to be easily annotated in one class instead of multiple entries in a web.xml.
 * </p>
 * <p>
 * The class is abstract as projects are expected to extend this class and annotate it with the {@link WebFilter} annotation.
 * </p>
 *
 * @see WebFilter
 * @see Filter
 */
public abstract class AbstractComboFilter implements Filter {

	private final List<Filter> filters;

	/**
	 * @param filter the first filter to combine
	 * @param filters the other filters to combine into a single filter
	 *
	 */
	protected AbstractComboFilter(final Filter filter, final Filter... filters) {
		if (filter == null) {
			throw new IllegalArgumentException("Filter cannot be null");
		}
		boolean hasNull = Arrays.stream(filters).anyMatch(Objects::isNull);
		if (hasNull) {
			throw new IllegalArgumentException("Filters cannot contain a null filter");
		}
		List<Filter> backing = new ArrayList<>();
		backing.add(filter);
		backing.addAll(Arrays.asList(filters));
		this.filters = Collections.unmodifiableList(backing);
	}

	/**
	 * @param filters the group of filters to combine into a single filter
	 */
	protected AbstractComboFilter(final List<Filter> filters) {
		if (filters == null || filters.isEmpty()) {
			throw new IllegalArgumentException("List of filters cannot be null or empty");
		}
		boolean hasNull = filters.stream().anyMatch(Objects::isNull);
		if (hasNull) {
			throw new IllegalArgumentException("Filters list cannot contain a null filter");
		}
		this.filters = Collections.unmodifiableList(filters);
	}

	/**
	 * @return the list of filters to be combined
	 */
	public final List<Filter> getFilters() {
		return filters;
	}

	@Override
	public void init(final FilterConfig config) throws ServletException {
		// Config each filter
		for (Filter filter : getFilters()) {
			filter.init(config);
		}
	}

	@Override
	public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain)
			throws IOException, ServletException {
		ComboFilterChain combo = new ComboFilterChain(chain, getFilters());
		combo.doFilter(request, response);
	}

	@Override
	public void destroy() {
		// Destroy in reverse order
		for (int i = getFilters().size(); i-- > 0;) {
			Filter filter = getFilters().get(i);
			filter.destroy();
		}
	}

}

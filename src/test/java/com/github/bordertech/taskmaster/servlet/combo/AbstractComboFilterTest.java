package com.github.bordertech.taskmaster.servlet.combo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

/**
 * Unit tests for {@link AbstractComboFilter}.
 */
public class AbstractComboFilterTest {

	@Rule
	public MockitoRule initRule = MockitoJUnit.rule();

	@Test(expected = IllegalArgumentException.class)
	public void testConstructor1NullFilter() {
		new AbstractComboFilter((Filter) null) {
		};
	}

	@Test(expected = IllegalArgumentException.class)
	public void testConstructor1NullArrayEntry() {
		Filter testFilter = Mockito.mock(Filter.class);
		new AbstractComboFilter(testFilter, (Filter) null) {
		};
	}

	@Test
	public void testConstructor1SingleFilter() {
		Filter testFilter = Mockito.mock(Filter.class);
		AbstractComboFilter filter = new AbstractComboFilter(testFilter) {
		};
		Assert.assertEquals("Backing filters should contain one entry", 1, filter.getFilters().size());
		Assert.assertSame("Backing filters should contain single filter", testFilter, filter.getFilters().get(0));
	}

	@Test
	public void testConstructor1MultiFilters() {
		Filter testFilter1 = Mockito.mock(Filter.class);
		Filter testFilter2 = Mockito.mock(Filter.class);
		Filter testFilter3 = Mockito.mock(Filter.class);
		AbstractComboFilter filter = new AbstractComboFilter(testFilter1, testFilter2, testFilter3) {
		};
		Assert.assertEquals("Backing filters should contain three entries", 3, filter.getFilters().size());
		Assert.assertSame("Backing filters should contain first filter", testFilter1, filter.getFilters().get(0));
		Assert.assertSame("Backing filters should contain second filter", testFilter2, filter.getFilters().get(1));
		Assert.assertSame("Backing filters should contain third filter", testFilter3, filter.getFilters().get(2));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testConstructor2NullList() {
		new AbstractComboFilter((List<Filter>) null) {
		};
	}

	@Test(expected = IllegalArgumentException.class)
	public void testConstructor2EmptyList() {
		new AbstractComboFilter(new ArrayList<>()) {
		};
	}

	@Test(expected = IllegalArgumentException.class)
	public void testConstructor2NullEntry() {
		new AbstractComboFilter(Arrays.asList((Filter) null)) {
		};
	}

	@Test
	public void testConstructor2MultiFilters() {
		Filter testFilter1 = Mockito.mock(Filter.class);
		Filter testFilter2 = Mockito.mock(Filter.class);
		Filter testFilter3 = Mockito.mock(Filter.class);
		List<Filter> testFilters = Arrays.asList(testFilter1, testFilter2, testFilter3);
		AbstractComboFilter filter = new AbstractComboFilter(testFilters) {
		};
		Assert.assertEquals("Backing filters should match test filters", testFilters, filter.getFilters());
	}

	@Test
	public void testInitFilter() throws ServletException {
		// Setup filters and config
		FilterConfig config = Mockito.mock(FilterConfig.class);
		Filter testFilter1 = Mockito.mock(Filter.class);
		Filter testFilter2 = Mockito.mock(Filter.class);

		// Call init on filter
		AbstractComboFilter filter = new AbstractComboFilter(testFilter1, testFilter2) {
		};
		filter.init(config);

		// Verify init called on each backing filter in correct order
		InOrder inOrder = Mockito.inOrder(testFilter1, testFilter2);
		inOrder.verify(testFilter1).init(config);
		inOrder.verify(testFilter2).init(config);
	}

	@Test
	public void testDestroyFilter() {
		// Setup filters
		Filter testFilter1 = Mockito.mock(Filter.class);
		Filter testFilter2 = Mockito.mock(Filter.class);

		// Call destroy on filter
		AbstractComboFilter filter = new AbstractComboFilter(testFilter1, testFilter2) {
		};
		filter.destroy();

		// Verify destroy called on each backing filter (in Reverse Order)
		InOrder inOrder = Mockito.inOrder(testFilter2, testFilter1);
		inOrder.verify(testFilter2).destroy();
		inOrder.verify(testFilter1).destroy();
	}

	@Test
	public void testDoFilter() throws IOException, ServletException {

		ServletRequest request = Mockito.mock(ServletRequest.class);
		ServletResponse response = Mockito.mock(ServletResponse.class);
		FilterChain chain = Mockito.mock(FilterChain.class);

		// Setup filters
		Filter testFilter1 = Mockito.mock(MyFilter.class);
		Filter testFilter2 = Mockito.mock(MyFilter.class);

		// Want to call the real method on the filter to call the next item in the chain (typical filter behaviour)
		Mockito.doCallRealMethod().when(testFilter1).doFilter(Mockito.any(), Mockito.any(), Mockito.any(ComboFilterChain.class));
		Mockito.doCallRealMethod().when(testFilter2).doFilter(Mockito.any(), Mockito.any(), Mockito.any(ComboFilterChain.class));

		// Call doFilter on filter
		AbstractComboFilter filter = new AbstractComboFilter(testFilter1, testFilter2) {
		};
		filter.doFilter(request, response, chain);

		// Verify doFilter called on each backing filter in correct order then the chain
		InOrder inOrder = Mockito.inOrder(testFilter1, testFilter2, chain);
		inOrder.verify(testFilter1).doFilter(Mockito.eq(request), Mockito.eq(response), Mockito.any(ComboFilterChain.class));
		inOrder.verify(testFilter2).doFilter(Mockito.eq(request), Mockito.eq(response), Mockito.any(ComboFilterChain.class));
		inOrder.verify(chain).doFilter(request, response);
	}

	/**
	 * Mimic a typical filter that calls the next filter in the chain.
	 */
	private static class MyFilter implements Filter {

		@Override
		public void init(final FilterConfig filterConfig) throws ServletException {
			// Do nothing
		}

		@Override
		public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain) throws IOException, ServletException {
			// Just call the next filter in the chain
			chain.doFilter(request, response);
		}

		@Override
		public void destroy() {
			// Do nothing
		}
	}

}

package com.github.bordertech.taskmaster.servlet.combo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletException;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

/**
 * Unit tests for {@link AbstractComboServletListener}.
 */
public class AbstractComboServletListenerTest {

	@Rule
	public MockitoRule initRule = MockitoJUnit.rule();

	@Test(expected = IllegalArgumentException.class)
	public void testConstructor1NullListener() {
		new AbstractComboServletListener((ServletContextListener) null) {
		};
	}

	@Test(expected = IllegalArgumentException.class)
	public void testConstructor1NullArrayEntry() {
		ServletContextListener testListener = Mockito.mock(ServletContextListener.class);
		new AbstractComboServletListener(testListener, (ServletContextListener) null) {
		};
	}

	@Test
	public void testConstructor1SingleListener() {
		ServletContextListener testListener = Mockito.mock(ServletContextListener.class);
		AbstractComboServletListener listener = new AbstractComboServletListener(testListener) {
		};
		Assert.assertEquals("Backing listeners should contain one entry", 1, listener.getListeners().size());
		Assert.assertSame("Backing listeners should contain single listener", testListener, listener.getListeners().get(0));
	}

	@Test
	public void testConstructor1MultiListeners() {
		ServletContextListener testListener1 = Mockito.mock(ServletContextListener.class);
		ServletContextListener testListener2 = Mockito.mock(ServletContextListener.class);
		ServletContextListener testListener3 = Mockito.mock(ServletContextListener.class);
		AbstractComboServletListener listener = new AbstractComboServletListener(testListener1, testListener2, testListener3) {
		};
		Assert.assertEquals("Backing listeners should contain three entries", 3, listener.getListeners().size());
		Assert.assertSame("Backing listeners should contain first listener", testListener1, listener.getListeners().get(0));
		Assert.assertSame("Backing listeners should contain second listener", testListener2, listener.getListeners().get(1));
		Assert.assertSame("Backing listeners should contain third listener", testListener3, listener.getListeners().get(2));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testConstructor2NullList() {
		new AbstractComboServletListener((List<ServletContextListener>) null) {
		};
	}

	@Test(expected = IllegalArgumentException.class)
	public void testConstructor2EmptyList() {
		new AbstractComboServletListener(new ArrayList<>()) {
		};
	}

	@Test(expected = IllegalArgumentException.class)
	public void testConstructor2NullEntry() {
		new AbstractComboServletListener(Arrays.asList((ServletContextListener) null)) {
		};
	}

	@Test
	public void testConstructor2MultiListeners() {
		ServletContextListener testListener1 = Mockito.mock(ServletContextListener.class);
		ServletContextListener testListener2 = Mockito.mock(ServletContextListener.class);
		ServletContextListener testListener3 = Mockito.mock(ServletContextListener.class);
		List<ServletContextListener> testListeners = Arrays.asList(testListener1, testListener2, testListener3);
		AbstractComboServletListener listener = new AbstractComboServletListener(testListeners) {
		};
		Assert.assertEquals("Backing listeners should match test listeners", testListeners, listener.getListeners());
	}

	@Test
	public void testInitListener() throws ServletException {

		// Setup listeners and config
		ServletContextEvent sce = Mockito.mock(ServletContextEvent.class);
		ServletContextListener testListener1 = Mockito.mock(ServletContextListener.class);
		ServletContextListener testListener2 = Mockito.mock(ServletContextListener.class);

		// Call init on listener
		AbstractComboServletListener listener = new AbstractComboServletListener(testListener1, testListener2) {
		};
		listener.contextInitialized(sce);

		// Verify init called on each backing listener in correct order
		InOrder inOrder = Mockito.inOrder(testListener1, testListener2);
		inOrder.verify(testListener1).contextInitialized(sce);
		inOrder.verify(testListener2).contextInitialized(sce);
	}

	@Test
	public void testDestroyListener() {
		// Setup listeners and config
		ServletContextEvent sce = Mockito.mock(ServletContextEvent.class);
		ServletContextListener testListener1 = Mockito.mock(ServletContextListener.class);
		ServletContextListener testListener2 = Mockito.mock(ServletContextListener.class);

		// Call destroy on listener
		AbstractComboServletListener listener = new AbstractComboServletListener(testListener1, testListener2) {
		};
		listener.contextDestroyed(sce);

		// Verify destroy called on each backing listener
		InOrder inOrder = Mockito.inOrder(testListener1, testListener2);
		inOrder.verify(testListener1).contextDestroyed(sce);
		inOrder.verify(testListener2).contextDestroyed(sce);
	}

	@Test
	public void testInitException() throws ServletException {

		// Setup listeners and config
		ServletContextEvent sce = Mockito.mock(ServletContextEvent.class);
		ServletContextListener testListener1 = Mockito.mock(ServletContextListener.class);
		ServletContextListener testListener2 = Mockito.mock(ServletContextListener.class);

		// Throw exception in listener1
		Mockito.doThrow(new IllegalStateException()).when(testListener1).contextInitialized(sce);

		// Call init on listener
		AbstractComboServletListener listener = new AbstractComboServletListener(testListener1, testListener2) {
		};
		listener.contextInitialized(sce);

		// Verify listener2 was still called even when listener1 had an exception
		Mockito.verify(testListener2).contextInitialized(sce);
	}

	@Test
	public void testDestoryException() throws ServletException {

		// Setup listeners and config
		ServletContextEvent sce = Mockito.mock(ServletContextEvent.class);
		ServletContextListener testListener1 = Mockito.mock(ServletContextListener.class);
		ServletContextListener testListener2 = Mockito.mock(ServletContextListener.class);

		// Throw exception in listener1
		Mockito.doThrow(new IllegalStateException()).when(testListener1).contextDestroyed(sce);

		// Call destroy on listener
		AbstractComboServletListener listener = new AbstractComboServletListener(testListener1, testListener2) {
		};
		listener.contextDestroyed(sce);

		// Verify listener2 was still called even when listener1 had an exception
		Mockito.verify(testListener2).contextDestroyed(sce);
	}

}

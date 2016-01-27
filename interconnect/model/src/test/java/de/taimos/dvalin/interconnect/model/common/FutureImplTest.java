package de.taimos.dvalin.interconnect.model.common;

import java.util.UUID;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.Assert;
import org.junit.Test;

import de.taimos.dvalin.interconnect.model.FutureImpl;
import de.taimos.dvalin.interconnect.model.FutureImpl.CancelListener;

@SuppressWarnings("javadoc")
public final class FutureImplTest {

	@Test(timeout = 1000)
	public void testGet() throws Exception {
		final FutureImpl<Integer> f = new FutureImpl<>();
		new Thread(new Runnable() {

			@Override
			public void run() {
				f.set(1);
			}
		}).start();
		final Integer i = f.get();
		Assert.assertEquals(Integer.valueOf(1), i);
		Assert.assertFalse(f.isCancelled());
		Assert.assertTrue(f.isDone());
	}

	@Test(timeout = 1000)
	public void testGetNull() throws Exception {
		final FutureImpl<Integer> f = new FutureImpl<>();
		new Thread(new Runnable() {

			@Override
			public void run() {
				f.set((Integer) null);
			}
		}).start();
		final Integer i = f.get();
		Assert.assertNull(i);
		Assert.assertFalse(f.isCancelled());
		Assert.assertTrue(f.isDone());
	}

	@Test(timeout = 1000, expected = ExecutionException.class)
	public void testGetWithException() throws Exception {
		final FutureImpl<Integer> f = new FutureImpl<>();
		new Thread(new Runnable() {

			@Override
			public void run() {
				f.set(new Exception("test"));
			}
		}).start();
		f.get();
	}

	@Test(timeout = 1000)
	public void testGetWithTimeout() throws Exception {
		final FutureImpl<Integer> f = new FutureImpl<>();
		new Thread(new Runnable() {

			@Override
			public void run() {
				f.set(1);
			}
		}).start();
		final Integer i = f.get(500, TimeUnit.MILLISECONDS);
		Assert.assertEquals(Integer.valueOf(1), i);
		Assert.assertFalse(f.isCancelled());
		Assert.assertTrue(f.isDone());
	}

	@Test(timeout = 1000, expected = TimeoutException.class)
	public void testGetWithTimeoutWithExpiry() throws Exception {
		final FutureImpl<Integer> f = new FutureImpl<>();
		f.get(500, TimeUnit.MILLISECONDS);
	}

	@Test(timeout = 1000, expected = ExecutionException.class)
	public void testGetWithTimeoutWithException() throws Exception {
		final FutureImpl<Integer> f = new FutureImpl<>();
		new Thread(new Runnable() {

			@Override
			public void run() {
				f.set(new Exception("test"));
			}
		}).start();
		f.get(500, TimeUnit.MILLISECONDS);
	}

	@Test
	public void testCancel() {
		final FutureImpl<Integer> f = new FutureImpl<>();
		Assert.assertTrue(f.cancel(true));
		Assert.assertTrue(f.isCancelled());
		Assert.assertTrue(f.isDone());
	}

	@Test
	public void testCancelCancelled() {
		final FutureImpl<Integer> f = new FutureImpl<>();
		Assert.assertTrue(f.cancel(true));
		Assert.assertFalse(f.cancel(true));
		Assert.assertTrue(f.isCancelled());
		Assert.assertTrue(f.isDone());
	}

	@Test
	public void testCancelDone() {
		final FutureImpl<Integer> f = new FutureImpl<>();
		f.set(1);
		Assert.assertFalse(f.cancel(true));
		Assert.assertFalse(f.isCancelled());
		Assert.assertTrue(f.isDone());
	}

	@Test(expected = CancellationException.class)
	public void testGetAfterCancel() throws Exception {
		final FutureImpl<Integer> f = new FutureImpl<>();
		Assert.assertTrue(f.cancel(true));
		Assert.assertTrue(f.isCancelled());
		Assert.assertTrue(f.isDone());
		f.get();
	}

	@Test
	public void testSetValueAfterCancel() {
		final FutureImpl<Integer> f = new FutureImpl<>();
		Assert.assertTrue(f.cancel(true));
		Assert.assertTrue(f.isCancelled());
		Assert.assertTrue(f.isDone());
		f.set(Integer.valueOf(1));
	}

	@Test
	public void testSetExceptionAfterCancel() {
		final FutureImpl<Integer> f = new FutureImpl<>();
		Assert.assertTrue(f.cancel(true));
		Assert.assertTrue(f.isCancelled());
		Assert.assertTrue(f.isDone());
		f.set(new Exception("test"));
	}

	@Test
	public void testCancelListener() {
		final FutureImpl<Integer> f = new FutureImpl<>();
		final AtomicReference<UUID> cancelledUUid = new AtomicReference<>(null);
		f.addCancelListener(new CancelListener<Integer>() {

			@Override
			public void wasCancelled(final UUID id) {
				cancelledUUid.compareAndSet(null, id);
			}
		});
		Assert.assertTrue(f.cancel(true));
		Assert.assertEquals(f.getId(), cancelledUUid.get());
	}

	@Test
	public void testCancelListenerAfterCancel() {
		final FutureImpl<Integer> f = new FutureImpl<>();
		final AtomicReference<UUID> cancelledUUid = new AtomicReference<>(null);
		Assert.assertTrue(f.cancel(true));
		f.addCancelListener(new CancelListener<Integer>() {

			@Override
			public void wasCancelled(final UUID id) {
				cancelledUUid.compareAndSet(null, id);
			}
		});
		Assert.assertEquals(f.getId(), cancelledUUid.get());
	}
}

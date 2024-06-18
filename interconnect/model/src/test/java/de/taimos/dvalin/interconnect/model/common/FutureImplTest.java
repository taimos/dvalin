package de.taimos.dvalin.interconnect.model.common;

/*
 * #%L
 * Dvalin interconnect transfer data model
 * %%
 * Copyright (C) 2016 Taimos GmbH
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import de.taimos.dvalin.interconnect.model.FutureImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;

@SuppressWarnings("javadoc")
final class FutureImplTest {

	@Test
    void testGet() throws Exception {
		final FutureImpl<Integer> f = new FutureImpl<>();
        Assertions.assertTimeout(Duration.ofMillis(1000), () ->
        new Thread(() -> f.set(1)).start());
		final Integer i = f.get();
		Assertions.assertEquals(Integer.valueOf(1), i);
		Assertions.assertFalse(f.isCancelled());
		Assertions.assertTrue(f.isDone());
	}

	@Test
    void testGetNull() throws Exception {
		final FutureImpl<Integer> f = new FutureImpl<>();
        Assertions.assertTimeout(Duration.ofMillis(1000), () ->
        new Thread(() -> f.set((Integer) null)).start());
		final Integer i = f.get();
		Assertions.assertNull(i);
		Assertions.assertFalse(f.isCancelled());
		Assertions.assertTrue(f.isDone());
	}

	@Test
    void testGetWithException() {
        Assertions.assertThrows(ExecutionException.class, () -> {
            final FutureImpl<Integer> f = new FutureImpl<>();
            Assertions.assertTimeout(Duration.ofMillis(1000), () ->
                new Thread(() -> f.set(new Exception("test"))).start());
            f.get();
        });
	}

	@Test
    void testGetWithTimeout() throws Exception {
		final FutureImpl<Integer> f = new FutureImpl<>();
        Assertions.assertTimeout(Duration.ofMillis(1000), () ->
		new Thread(() -> f.set(1)).start());
		final Integer i = f.get(500, TimeUnit.MILLISECONDS);
		Assertions.assertEquals(Integer.valueOf(1), i);
		Assertions.assertFalse(f.isCancelled());
		Assertions.assertTrue(f.isDone());
	}

	@Test
    void testGetWithTimeoutWithExpiry() {
        Assertions.assertThrows(TimeoutException.class, () -> {
            final FutureImpl<Integer> f = new FutureImpl<>();
            f.get(500, TimeUnit.MILLISECONDS);
        });
	}

	@Test
    void testGetWithTimeoutWithException() {
        Assertions.assertThrows(ExecutionException.class, () -> {
            final FutureImpl<Integer> f = new FutureImpl<>();
            Assertions.assertTimeout(Duration.ofMillis(1000), () ->
            new Thread(new Runnable() {

                @Override
                public void run() {
                    f.set(new Exception("test"));
                }
            }).start());
            f.get(500, TimeUnit.MILLISECONDS);
        });
	}

	@Test
    void testCancel() {
		final FutureImpl<Integer> f = new FutureImpl<>();
		Assertions.assertTrue(f.cancel(true));
		Assertions.assertTrue(f.isCancelled());
		Assertions.assertTrue(f.isDone());
	}

	@Test
    void testCancelCancelled() {
		final FutureImpl<Integer> f = new FutureImpl<>();
		Assertions.assertTrue(f.cancel(true));
		Assertions.assertFalse(f.cancel(true));
		Assertions.assertTrue(f.isCancelled());
		Assertions.assertTrue(f.isDone());
	}

	@Test
    void testCancelDone() {
		final FutureImpl<Integer> f = new FutureImpl<>();
		f.set(1);
		Assertions.assertFalse(f.cancel(true));
		Assertions.assertFalse(f.isCancelled());
		Assertions.assertTrue(f.isDone());
	}

	@Test
    void testGetAfterCancel() {
        Assertions.assertThrows(CancellationException.class, () -> {
            final FutureImpl<Integer> f = new FutureImpl<>();
            Assertions.assertTrue(f.cancel(true));
            Assertions.assertTrue(f.isCancelled());
            Assertions.assertTrue(f.isDone());
            f.get();
        });
	}

	@Test
	public void testSetValueAfterCancel() {
		final FutureImpl<Integer> f = new FutureImpl<>();
		Assertions.assertTrue(f.cancel(true));
		Assertions.assertTrue(f.isCancelled());
		Assertions.assertTrue(f.isDone());
		f.set(1);
	}

	@Test
	public void testSetExceptionAfterCancel() {
		final FutureImpl<Integer> f = new FutureImpl<>();
		Assertions.assertTrue(f.cancel(true));
		Assertions.assertTrue(f.isCancelled());
		Assertions.assertTrue(f.isDone());
		f.set(new Exception("test"));
	}

	@Test
    void testCancelListener() {
		final FutureImpl<Integer> f = new FutureImpl<>();
		final AtomicReference<UUID> cancelledUUid = new AtomicReference<>(null);
		f.addCancelListener(id -> cancelledUUid.compareAndSet(null, id));
		Assertions.assertTrue(f.cancel(true));
		Assertions.assertEquals(f.getId(), cancelledUUid.get());
	}

	@Test
    void testCancelListenerAfterCancel() {
		final FutureImpl<Integer> f = new FutureImpl<>();
		final AtomicReference<UUID> cancelledUUid = new AtomicReference<>(null);
		Assertions.assertTrue(f.cancel(true));
		f.addCancelListener(id -> cancelledUUid.compareAndSet(null, id));
		Assertions.assertEquals(f.getId(), cancelledUUid.get());
	}
}

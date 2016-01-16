package de.taimos.dvalin.interconnect.core;

import java.util.UUID;
import java.util.concurrent.CountDownLatch;

import de.taimos.dvalin.interconnect.model.ivo.daemon.VoidIVO;


public final class InterconnectConnectorBurstTest implements Runnable {

	private static final String queueName = "test.InterconnectConnectorBurstTest." + UUID.randomUUID();

	private static final int THREADS = 2;
	private static final int MESSAGES = 10000;
	private static final CountDownLatch cdl1 = new CountDownLatch(InterconnectConnectorBurstTest.THREADS);
	private static final CountDownLatch cdl2 = new CountDownLatch(InterconnectConnectorBurstTest.THREADS);


	/**
	 * @param args Arguments
	 * @throws Exception If ...
	 */
	public static void main(String[] args) throws Exception {
		TestHelper.initBrokerEnv("failover:tcp://localhost:61616");
		try {
			System.out.println("begin");
			for (int i = 0; i < InterconnectConnectorBurstTest.THREADS; i++) {
				new Thread(new InterconnectConnectorBurstTest()).start();
			}
			InterconnectConnectorBurstTest.cdl1.await();
			final Long begin = System.nanoTime();
			InterconnectConnectorBurstTest.cdl2.await();
			final Long end = System.nanoTime();
			System.out.println("end");
			System.out.println("duration: " + ((end - begin) / 1000l / 1000l) + " ms");
		} finally {
			TestHelper.closeBrokerEnv();
		}
	}

	@Override
	public void run() {
		System.out.println("started");
		try {
			InterconnectConnectorBurstTest.cdl1.countDown();
			InterconnectConnectorBurstTest.cdl1.await();
			for (int i = 0; i < InterconnectConnectorBurstTest.MESSAGES; i++) {
				InterconnectConnector.sendToQueue(InterconnectConnectorBurstTest.queueName, new VoidIVO.VoidIVOBuilder().build());
			}
		} catch (final Exception e) {
			e.printStackTrace();
		} finally {
			InterconnectConnectorBurstTest.cdl2.countDown();
		}
	}
}

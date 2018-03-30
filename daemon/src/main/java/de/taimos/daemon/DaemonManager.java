package de.taimos.daemon;

/*
 * #%L
 * Daemon Library
 * %%
 * Copyright (C) 2012 - 2016 Taimos GmbH
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


import java.util.concurrent.atomic.AtomicBoolean;

/**
 *
 * @author hoegertn
 *
 */
class DaemonManager {

	private final Object mutex = new Object();

	private final AtomicBoolean running = new AtomicBoolean(true);

	/**
	 * blocks until stop() is called
	 */
	void block() {
		while (this.isRunning()) {
			synchronized (this.mutex) {
				try {
					if (this.isRunning()) {
						this.mutex.wait();
					}
				} catch (final Exception e) {
					// ignore it and wait again
				}
			}
		}
	}

	/**
	 * @return is the daemon running
	 */
	boolean isRunning() {
		return this.running.get();
	}

	/**
	 * stop the daemon
	 */
	void stop() {
		synchronized (this.mutex) {
			this.running.set(false);
			this.mutex.notifyAll();
		}
	}
}

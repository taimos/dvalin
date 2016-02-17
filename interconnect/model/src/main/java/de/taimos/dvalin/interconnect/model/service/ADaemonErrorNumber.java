package de.taimos.dvalin.interconnect.model.service;

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

/**
 * @see DaemonError
 */
public abstract class ADaemonErrorNumber implements DaemonErrorNumber {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private final int number;
    private final String daemon;


    /**
     * @param aNumber Number
     * @param aDaemon Daemon
     */
    public ADaemonErrorNumber(final int aNumber, final String aDaemon) {
        if (aDaemon == null) {
            throw new NullPointerException();
        }
        this.number = aNumber;
        this.daemon = aDaemon;
    }

    /**
     * @param aNumber         Number
     * @param daemonInterface Daemon interface
     */
    public ADaemonErrorNumber(final int aNumber, final Class<? extends IDaemon> daemonInterface) {
        if (daemonInterface == null) {
            throw new NullPointerException();
        }
        if (daemonInterface.getAnnotation(Daemon.class) == null) {
            throw new NullPointerException();
        }
        this.number = aNumber;
        this.daemon = daemonInterface.getAnnotation(Daemon.class).name();
    }

    @Override
    public final String daemon() {
        return this.daemon;
    }

    @Override
    public final int get() {
        return this.number;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = (prime * result) + this.daemon.hashCode();
        result = (prime * result) + this.number;
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj instanceof DaemonErrorNumber) {
            final DaemonErrorNumber other = (DaemonErrorNumber) obj;
            if ((this.number == other.get()) && this.daemon.equals(other.daemon())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "ADaemonErrorNumber{" +
            "number=" + number +
            ", daemon='" + daemon + '\'' +
            '}';
    }
}

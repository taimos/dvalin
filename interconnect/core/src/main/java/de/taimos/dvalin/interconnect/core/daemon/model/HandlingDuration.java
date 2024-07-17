package de.taimos.dvalin.interconnect.core.daemon.model;

/**
 * Copyright 2024 Cinovo AG<br>
 * <br>
 *
 * @author fzwirn
 */
public class HandlingDuration {
    private final HandlingDurationType handlingDurationType;
    private final long runtime;

    /**
     * @param handlingDurationType type of the handling duration
     * @param runtime              runtime of the handling
     */
    public HandlingDuration(HandlingDurationType handlingDurationType, long runtime) {
        this.handlingDurationType = handlingDurationType;
        this.runtime = runtime;
    }

    /**
     * @return the handlingDurationType
     */
    public HandlingDurationType getHandlingDurationType() {
        return this.handlingDurationType;
    }

    /**
     * @return the runtime
     */
    public long getRuntime() {
        return this.runtime;
    }
}

package de.taimos.dvalin.jaxrs.monitoring;

/**
 * Copyright 2023 Cinovo AG<br>
 * <br>
 *
 * @author fzwirn
 */
public interface INanoTimer {
    long nanoTime();
    static INanoTimer system() {
        return System::nanoTime;
    }
}

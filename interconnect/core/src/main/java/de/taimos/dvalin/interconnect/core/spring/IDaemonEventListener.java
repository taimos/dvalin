package de.taimos.dvalin.interconnect.core.spring;


import de.taimos.dvalin.interconnect.model.ivo.IVO;

/**
 * @param <I> EventIVO type
 */
public interface IDaemonEventListener<I extends IVO> {

    /**
     * @param event Event
     */
    void onEvent(I event);

}

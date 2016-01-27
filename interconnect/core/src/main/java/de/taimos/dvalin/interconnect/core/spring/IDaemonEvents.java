package de.taimos.dvalin.interconnect.core.spring;


import de.taimos.dvalin.interconnect.model.ivo.IVO;

public interface IDaemonEvents {

    /**
     * Listen to event.
     *
     * @param <I>           EventIVO type
     * @param eventIVOClazz EventIVO class
     * @param listener      Listener
     */
    <I extends IVO> void listen(Class<I> eventIVOClazz, IDaemonEventListener<I> listener);

    /**
     * Unlisten from event.
     *
     * @param <I>           EventIVO type
     * @param eventIVOClazz EventIVO class
     * @param listener      Listener
     */
    <I extends IVO> void unlisten(Class<I> eventIVOClazz, IDaemonEventListener<I> listener);

    /**
     * Emit an event.
     *
     * @param event Event
     */
    void publish(final IVO event);

}

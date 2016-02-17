package de.taimos.dvalin.interconnect.core.spring;

/*
 * #%L
 * Dvalin interconnect core library
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

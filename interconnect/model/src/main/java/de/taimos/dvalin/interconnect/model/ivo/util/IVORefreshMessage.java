package de.taimos.dvalin.interconnect.model.ivo.util;

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

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import de.taimos.dvalin.interconnect.model.ivo.AbstractIVO;

public class IVORefreshMessage implements Serializable {
    
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    
    private Map<String, Set<String>> nameIdMap;
    private Set<AbstractIVO> ivos;
    
    
    /**
     * @param nameIdMap the ivo names and the changed ids
     */
    public IVORefreshMessage(Map<String, Set<String>> nameIdMap) {
        this.nameIdMap = (nameIdMap == null ? new HashMap<String, Set<String>>() : new HashMap<>(nameIdMap));
    }
    
    /**
     * @param nameIdMap the ivo names with corresponding changed ids
     * @param ivoCollection collection of ivos
     */
    public IVORefreshMessage(Map<String, Set<String>> nameIdMap, Collection<AbstractIVO> ivoCollection) {
        this.nameIdMap = (nameIdMap == null ? new HashMap<String, Set<String>>() : new HashMap<>(nameIdMap));
        this.ivos = ((ivoCollection == null ? new HashSet<AbstractIVO>() : new HashSet<>(ivoCollection)));
    }
    
    /**
     * @return the ivoNames
     */
    public Map<String, Set<String>> getIvoNames() {
        return this.nameIdMap;
    }
    
    /**
     * @return the ivos
     */
    public Set<AbstractIVO> getIvos() {
        return this.ivos;
    }
    
}

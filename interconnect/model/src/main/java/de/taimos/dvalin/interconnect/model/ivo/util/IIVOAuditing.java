/**
 *
 */
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


import de.taimos.dvalin.interconnect.model.ivo.IVO;
import org.joda.time.DateTime;

import jakarta.annotation.Nullable;

public interface IIVOAuditing extends IVO {

    /**
     * property constant for version property comment: The version
     */
    String PROP_VERSION = "version";
    /**
     * property constant for lastChange property comment: The last change date
     */
    String PROP_LASTCHANGE = "lastChange";
    /**
     * property constant for lastChange property comment: The last change date
     */
    String PROP_LASTCHANGEUSER = "lastChangeUser";


    /**
     * The version This field is optional, thus may be null.
     *
     * @return the value for version
     **/
    @Nullable
    Integer getVersion();

    /**
     * The last change date This field is optional, thus may be null.
     *
     * @return the last change date
     **/
    @Nullable
    DateTime getLastChange();

    /**
     * The last change user This field is optional, thus may be null.
     *
     * @return the last change user
     **/
    @Nullable
    String getLastChangeUser();

}

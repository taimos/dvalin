/**
 *
 */
package de.taimos.dvalin.interconnect.model.ivo.util;


import javax.annotation.Nullable;

import org.joda.time.DateTime;

import de.taimos.dvalin.interconnect.model.ivo.IVO;

public interface IIVOAuditing extends IVO {

    /**
     * property constant for version property comment: The version
     */
    public static final String PROP_VERSION = "version";
    /**
     * property constant for lastChange property comment: The last change date
     */
    public static final String PROP_LASTCHANGE = "lastChange";
    /**
     * property constant for lastChange property comment: The last change date
     */
    public static final String PROP_LASTCHANGEUSERID = "lastChangeUserId";


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
     * The last change user id This field is optional, thus may be null.
     *
     * @return the last change user id
     **/
    @Nullable
    Long getLastChangeUserId();

}

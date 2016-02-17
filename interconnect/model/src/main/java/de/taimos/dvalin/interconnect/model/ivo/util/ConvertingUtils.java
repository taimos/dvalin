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

import java.math.BigDecimal;

public final class ConvertingUtils {

    private ConvertingUtils() {
        // utility class
    }

    /**
     * @param s1 first string
     * @param s2 second string
     * @return true if strings are different
     */
    public static boolean stringsAreDifferent(String s1, String s2) {
        if ((s1 == null) || (s2 == null)) {
            if ((s1 != null) || (s2 != null)) {
                return true;
            }
        } else if (!s1.equals(s2)) {
            return true;
        }
        return false;
    }

    /**
     * @param bigDecimal the bigDecimal
     * @return the string
     */
    public static String convertBigDecimalToString(BigDecimal bigDecimal) {
        if (bigDecimal == null) {
            return null;
        }
        return String.valueOf(bigDecimal);
    }

    /**
     * @param string the string
     * @return the bigdecimal
     */
    public static BigDecimal convertStringToBigDecimal(String string) {
        if (string == null) {
            return null;
        }
        return new BigDecimal(string);
    }

    /**
     * @param resultSize the result size
     * @param limitSize  the limit size
     * @return isRequestLimitNotReached
     */
    public static boolean isRequestLimitNotReached(int resultSize, int limitSize) {
        return (resultSize < limitSize) || (limitSize == 0) || (limitSize == -1);
    }
}

/*
 * Copyright (c) 2016. Taimos GmbH http://www.taimos.de
 */

package de.taimos.dvalin.template.velocity.tools;

/*-
 * #%L
 * Dvalin Velocity support
 * %%
 * Copyright (C) 2016 - 2017 Taimos GmbH
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

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

public class DateTool {

    private static final String PATTERN_DATE = "dd.MM.yyyy";
    private static final String PATTERN_TIME = "HH:mm";

    private final DateTimeZone zone = DateTime.now().getZone();

    public String dateLocal(DateTime dt) {
        if (dt == null) {
            return "";
        }
        return dt.withZone(this.zone).toString(PATTERN_DATE);
    }

    public String timeLocal(DateTime dt) {
        if (dt == null) {
            return "";
        }
        return dt.withZone(this.zone).toString(PATTERN_TIME);
    }

    public String fullLocal(DateTime dt) {
        if (dt == null) {
            return "";
        }
        return dt.withZone(this.zone).toString(PATTERN_DATE + " " + PATTERN_TIME);
    }

    public String date(DateTime dt) {
        if (dt == null) {
            return "";
        }
        return dt.toString(PATTERN_DATE);
    }

    public String time(DateTime dt) {
        if (dt == null) {
            return "";
        }
        return dt.toString(PATTERN_TIME);
    }

    public String full(DateTime dt) {
        if (dt == null) {
            return "";
        }
        return dt.toString(PATTERN_DATE + " " + PATTERN_TIME);
    }

    public String format(DateTime dt, String format) {
        if (dt == null || format == null) {
            return "";
        }
        return dt.toString(format);
    }
}

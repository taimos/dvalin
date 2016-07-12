/*
 * Copyright (c) 2016. Taimos GmbH http://www.taimos.de
 */

package de.taimos.dvalin.template.velocity.tools;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

public class DateTool {

    private static final String PATTERN_DATE = "dd.MM.yyyy";
    private static final String PATTERN_TIME = "HH:mm";

    private DateTimeZone zone = DateTime.now().getZone();

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

package de.taimos.dvalin.interconnect.demo.api;

import de.taimos.dvalin.interconnect.model.service.ADaemonErrorNumber;

public class UserError extends ADaemonErrorNumber {

    public static final UserError USER_NOT_FOUND = new UserError(1);

    private UserError(int aNumber) {
        super(aNumber, IUserService.class);
    }

}

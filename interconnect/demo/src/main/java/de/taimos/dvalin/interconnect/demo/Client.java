package de.taimos.dvalin.interconnect.demo;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import de.taimos.daemon.spring.annotations.ProdComponent;
import de.taimos.dvalin.interconnect.core.spring.DaemonProxyFactory;
import de.taimos.dvalin.interconnect.demo.api.IUserService;
import de.taimos.dvalin.interconnect.demo.api.UserError;
import de.taimos.dvalin.interconnect.demo.model.UserIVO_v1;
import de.taimos.dvalin.interconnect.demo.model.requests.CreateUserIVO_v1;
import de.taimos.dvalin.interconnect.demo.model.requests.FindUserByIdIVO_v1;
import de.taimos.dvalin.interconnect.demo.model.requests.FindUserIVO_v1;
import de.taimos.dvalin.interconnect.model.service.DaemonError;

@ProdComponent
public class Client {

    @Autowired
    private DaemonProxyFactory proxy;

    @Scheduled(initialDelay = 1000, fixedRate = 60000)
    public void init() {
        IUserService svc = proxy.create(IUserService.class);

        try {
            System.out.println("Users:");
            for (UserIVO_v1 user : svc.findUsers(new FindUserIVO_v1.FindUserIVO_v1Builder().build()).getElements()) {
                System.out.println("  - " + user.getId() + " " + user.getName());
            }

            System.out.println();

            for (int i = 0; i < 5; i++) {
                String name = UUID.randomUUID().toString();
                UserIVO_v1.UserIVO_v1Builder b = new UserIVO_v1.UserIVO_v1Builder();
                b.withName(name);
                UserIVO_v1 user = b.build();
                System.out.println("Creating user: " + name);
                UserIVO_v1 cUser = svc.createUser(new CreateUserIVO_v1.CreateUserIVO_v1Builder().withValue(user).build());
                System.out.println("Created user: " + cUser.getName() + " with id " + cUser.getId());
            }

            System.out.println();

            System.out.println("Users:");
            for (UserIVO_v1 user : svc.findUsers(new FindUserIVO_v1.FindUserIVO_v1Builder().build()).getElements()) {
                System.out.println("  - " + user.getId() + " " + user.getName());
            }

            svc.findById(new FindUserByIdIVO_v1.FindUserByIdIVO_v1Builder().withId("1000").build());
        } catch (DaemonError daemonError) {
            if (daemonError.getNumber().equals(UserError.USER_NOT_FOUND)) {
                System.out.println("User does not exist");
            }

            System.out.println(daemonError.getNumber().toString());
            daemonError.printStackTrace();
        }

    }

}

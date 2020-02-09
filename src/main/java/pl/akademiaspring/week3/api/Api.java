package pl.akademiaspring.week3.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.akademiaspring.week3.entity.AppUser;
import pl.akademiaspring.week3.repo.AppUserRepo;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class Api {

    private Logger logger = LoggerFactory.getLogger(Api.class);
    private Map<String, Integer> allUsers;
    private AppUserRepo appUserRepo;

    @Autowired
    public Api(AppUserRepo appUserRepo) {
        this.appUserRepo = appUserRepo;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void loadAllUsers() {

        allUsers = appUserRepo
                .findAll()
                .stream()
                .collect(Collectors.toMap(AppUser::getUsername, o -> 0));

    }

    @EventListener
    public void authenticationSuccess(AuthenticationSuccessEvent successEvent) {
        String user = ((UserDetails)successEvent.getAuthentication().getPrincipal()).getUsername();

        logger.info("Authentication Success User : " + user);

        if (allUsers.get(user) == null) {
            allUsers.put(user, 1);
        } else {
            int count = allUsers.get(user);
            allUsers.put(user, ++count);
        }
    }

    @GetMapping("/forAll")
    public String forAll() {
        return "forAll";
    }

    @GetMapping("/helloAdmin")
    public String helloAdmin(Principal principal) {
        return "Cześć Admin: " + principal.getName() +
                ", zalogowałeś się : " + allUsers.get(principal.getName()) + " raz";
    }

    @GetMapping("/helloUser")
    public String helloUser(Principal principal) {

        return "Cześć User: " + principal.getName() +
                ", zalogowałeś się : " + allUsers.get(principal.getName()) + " raz";
    }

    @GetMapping("/helloAnonymous")
    public String helloAnonymous(Principal principal) {

        if (principal == null) {
            return "Cześć : Nieznajomy";
        }
        return "Cześć Anonymous: " + principal.getName();
    }
}

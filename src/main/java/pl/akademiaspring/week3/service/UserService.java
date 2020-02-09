package pl.akademiaspring.week3.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.akademiaspring.week3.entity.AppUser;
import pl.akademiaspring.week3.entity.Authority;
import pl.akademiaspring.week3.entity.VerificationToken;
import pl.akademiaspring.week3.entity.types.AuthorityType;
import pl.akademiaspring.week3.repo.AppUserRepo;
import pl.akademiaspring.week3.repo.AuthorityRepo;
import pl.akademiaspring.week3.repo.VerificationTokenRepo;


import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import java.util.UUID;

@Service
public class UserService {

    private Logger logger = LoggerFactory.getLogger(UserService.class);

    private AppUserRepo appUserRepo;
    private PasswordEncoder passwordEncoder;
    private VerificationTokenRepo verificationTokenRepo;
    private MailSenderService mailSenderService;
    private AuthorityRepo authorityRepo;

    @Autowired
    public UserService(AppUserRepo appUserRepo, PasswordEncoder passwordEncoder, VerificationTokenRepo verificationTokenRepo,
                       MailSenderService mailSenderService, AuthorityRepo authorityRepo) {
        this.appUserRepo = appUserRepo;
        this.passwordEncoder = passwordEncoder;
        this.verificationTokenRepo = verificationTokenRepo;
        this.mailSenderService = mailSenderService;
        this.authorityRepo = authorityRepo;
    }

    public boolean addNewUser(AppUser user, HttpServletRequest request) {

        if (appUserRepo.findAllByUsername(user.getUsername()) == null) {

            AuthorityType authorityType = AuthorityType.valueOf(user.getAuthorityName());

            logger.info("User authotity name : " + user.getAuthorityName());

            user.setPassword(passwordEncoder.encode(user.getPassword()));
            appUserRepo.save(user);

            logger.info("Send mail for ROLE_USER ");
            sendVerifyToken(user, "ROLE_USER", request);

            if (authorityType.equals(AuthorityType.ROLE_ADMIN)) {
                logger.info("Send mail for ROLE_ADMIN ");
                sendVerifyToken(user, "ROLE_ADMIN", request);
            }

            return true;
        } else {
            logger.info("Username :" + user.getUsername() + " exists in database");

            return false;
        }
    }

    private void sendVerifyToken(AppUser user, String role, HttpServletRequest request) {
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken(user, token);
        verificationTokenRepo.save(verificationToken);

        String url = "http://" + request.getServerName() + ":" +
                request.getServerPort() +
                request.getContextPath() +
                "/verify-token?role=" + role + "&token=" + token;

        try {
            mailSenderService.sendMail(user.getUsername(), "Verification Token", url, false);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    public void verifyToken(String role, String token) {
        logger.info("Role : " + role + ", token = " + token);
        VerificationToken verificationToken = verificationTokenRepo.findByValue(token);

        if (verificationToken != null) {
            verificationTokenRepo.deleteById(verificationToken.getId());

            AppUser appUser = verificationToken.getAppUser();
            if (appUser != null) {
                AuthorityType authorityType = AuthorityType.valueOf(role);
                Authority authority = authorityRepo.findAuthorityByName(authorityType);

                if ((appUser.getAuthority() == null) ||
                        (appUser.getAuthority().getName().equals(AuthorityType.ROLE_USER) && authorityType.equals(AuthorityType.ROLE_ADMIN))) {
                    appUser.setAuthority(authority);
                    appUser.setEnabled(true);
                    appUserRepo.save(appUser);
                }
            }
        }
    }
}

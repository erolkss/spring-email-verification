package br.com.ero.emailverificaitondemo.event.listener;

import br.com.ero.emailverificaitondemo.event.RegistrationCompleteEvent;
import br.com.ero.emailverificaitondemo.user.User;
import br.com.ero.emailverificaitondemo.user.UserService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Component
public class RegistrationCompleteEventListener implements ApplicationListener<RegistrationCompleteEvent> {

    private final UserService userService;
    private final JavaMailSender mailSender;
    private User theUser;


    @Override
    public void onApplicationEvent(RegistrationCompleteEvent event) {
        // 1. Get the new register user
        theUser = event.getUser();
        // 2. Create and verification token for the user
        String verificationToken = UUID.randomUUID().toString();
        // 3. Save the verification token the user
        userService.saveUserVerificationToken(theUser, verificationToken);
        // 4. Build the verification url to be sent to the user
        String url = event.getApplicationUrl() + "/register/verifyEmail?token=" + verificationToken;
        // 5. Send the email
        try {
            sendVerificationEmail(url);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        log.info("Click the link to verify your registration : {}", url);
    }

    @Override
    public boolean supportsAsyncExecution() {
        return ApplicationListener.super.supportsAsyncExecution();
    }

    public void sendVerificationEmail(String url) throws MessagingException, UnsupportedEncodingException {
        String subject = "Email Verification";
        String senderName = "User Registration Portal Service";
        String mailContent = "<p> Hi, " + theUser.getFirstName() + ", </p>" +
                "<p>Thank you for registering with us, " + "" +
                "Please, follow the link bellow to complete your registration. </p>" +
                "<a href=\"" + url + "\">Verify your email to activate your account</a>" +
                "<p> Thank you <br> Users Registration Portal Service </p>";
        MimeMessage message = mailSender.createMimeMessage();
        var messageHelper = new MimeMessageHelper(message);
        messageHelper.setFrom("${MAIL_USERNAME}", senderName);
        messageHelper.setTo(theUser.getEmail());
        messageHelper.setSubject(subject);
        messageHelper.setText(mailContent, true);
        mailSender.send(message);

    }
}

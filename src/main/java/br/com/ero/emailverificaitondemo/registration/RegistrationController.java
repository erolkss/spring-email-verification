package br.com.ero.emailverificaitondemo.registration;

import br.com.ero.emailverificaitondemo.event.RegistrationCompleteEvent;
import br.com.ero.emailverificaitondemo.registration.token.VerificationToken;
import br.com.ero.emailverificaitondemo.registration.token.VerificationTokenRepository;
import br.com.ero.emailverificaitondemo.user.User;
import br.com.ero.emailverificaitondemo.user.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/register")
@RequiredArgsConstructor
public class RegistrationController {

    private final UserService userService;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final VerificationTokenRepository tokenRepository;

    @PostMapping
    public String registerUser(@RequestBody RegistrationRequest registrationRequest, final HttpServletRequest servletRequest) {
        User user = userService.registerUser(registrationRequest);

        applicationEventPublisher.publishEvent(new RegistrationCompleteEvent(user, applicationUrl(servletRequest)));

        return "Success! Please check your email to complete your registration!";
    }

    @GetMapping("/verifyEmail")
    public String verifyEmail(@RequestParam("token") String token) {
        VerificationToken verificationToken = tokenRepository.findByToken(token);
        if (verificationToken.getUser().isEnabled()) {
            return "This account has already been verity, please login.";
        }
        String verificationResult = userService.validateToken(token);

        if (verificationResult.equalsIgnoreCase("Valid")) {
            return "Email verify successfully. Now you can login to your account";
        }
        return "Invalid verification token";
    }

    public String applicationUrl(HttpServletRequest servletRequest) {
        return "http://" + servletRequest.getServerName() + ":" + servletRequest.getServerPort() + servletRequest.getContextPath();
    }
}

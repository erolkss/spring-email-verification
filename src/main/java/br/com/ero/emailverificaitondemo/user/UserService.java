package br.com.ero.emailverificaitondemo.user;

import br.com.ero.emailverificaitondemo.registration.RegistrationRequest;
import br.com.ero.emailverificaitondemo.registration.token.VerificationToken;

import java.util.List;
import java.util.Optional;

public interface UserService {
    List<User> getUsers();
    User registerUser(RegistrationRequest request);
    Optional<User> findByEmail(String email);

    void saveUserVerificationToken(User theUser, String verificationToken);

    String validateToken(String token);
}

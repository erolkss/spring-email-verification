package br.com.ero.emailverificaitondemo.user;

import br.com.ero.emailverificaitondemo.exception.UserAlreadyExistsException;
import br.com.ero.emailverificaitondemo.registration.RegistrationRequest;
import br.com.ero.emailverificaitondemo.registration.token.VerificationToken;
import br.com.ero.emailverificaitondemo.registration.token.VerificationTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final VerificationTokenRepository verificationTokenRepository;

    @Override
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    @Override
    public User registerUser(RegistrationRequest request) {
        Optional<User> user = userRepository.findByEmail(request.email());
        if (user.isPresent()) {
            throw new UserAlreadyExistsException("User with email " + request.email() + "already exists.");
        }
        var newUser = new User();
        newUser.setFirstName(request.firstName());
        newUser.setLastName(request.lastName());
        newUser.setEmail(request.email());
        newUser.setPassword(passwordEncoder.encode(request.password()));
        newUser.setRole(request.role());
        return userRepository.save(newUser);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public void saveUserVerificationToken(User theUser, String token) {
        var verificationToken = new VerificationToken(token, theUser );
        verificationTokenRepository.save(verificationToken);
    }

    @Override
    public String validateToken(String token) {
        VerificationToken checkToken = verificationTokenRepository.findByToken(token);
        if (checkToken == null) {
            return "Invalid verification token";
        }
        User user = checkToken.getUser();
        Calendar calendar = Calendar.getInstance();
        if ((checkToken.getExpirationTime().getTime() - calendar.getTime().getTime()) <= 0) {
            verificationTokenRepository.delete(checkToken);
            return "Token already expired";
        }
        user.setEnabled(true);
        userRepository.save(user);
        return "Valid";
    }
}

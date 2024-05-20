package br.com.ero.emailverificaitondemo.registration;


public record RegistrationRequest(
        String firstName,
        String lastName,
        String email,
        String password,
        String role
) {
}

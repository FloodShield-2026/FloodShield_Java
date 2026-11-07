package br.fiap.floodshield.web.dto;

import br.fiap.floodshield.domain.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public final class AuthDtos {

    private AuthDtos() {
    }

    public record LoginRequest(
            @NotBlank @Email String email,
            @NotBlank String password) {
    }

    public record RegisterRequest(
            @NotBlank String name,
            @NotBlank @Email String email,
            @NotBlank @Size(min = 6) String password) {
    }

    public record UserResponse(Long id, String name, String email, Role role) {
    }

    public record AuthResponse(String token, UserResponse user) {
    }
}

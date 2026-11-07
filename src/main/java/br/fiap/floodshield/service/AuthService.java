package br.fiap.floodshield.service;

import br.fiap.floodshield.domain.Role;
import br.fiap.floodshield.domain.Usuario;
import br.fiap.floodshield.repository.UsuarioRepository;
import br.fiap.floodshield.security.JwtService;
import br.fiap.floodshield.web.dto.AuthDtos.AuthResponse;
import br.fiap.floodshield.web.dto.AuthDtos.LoginRequest;
import br.fiap.floodshield.web.dto.AuthDtos.RegisterRequest;
import br.fiap.floodshield.web.dto.AuthDtos.UserResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthService(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            AuthenticationManager authenticationManager) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (usuarioRepository.existsByEmailIgnoreCase(request.email())) {
            throw new IllegalStateException("Já existe uma conta com este e-mail.");
        }
        Usuario usuario = new Usuario();
        usuario.setNome(request.name().trim());
        usuario.setEmail(request.email().trim().toLowerCase());
        usuario.setSenha(passwordEncoder.encode(request.password()));
        usuario.setRole(Role.CIDADAO);
        usuario = usuarioRepository.save(usuario);
        return toAuth(usuario);
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password()));
        Usuario usuario = usuarioRepository.findByEmailIgnoreCase(request.email())
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));
        return toAuth(usuario);
    }

    private AuthResponse toAuth(Usuario usuario) {
        return new AuthResponse(
                jwtService.generateToken(usuario),
                new UserResponse(usuario.getId(), usuario.getNome(), usuario.getEmail(), usuario.getRole()));
    }
}

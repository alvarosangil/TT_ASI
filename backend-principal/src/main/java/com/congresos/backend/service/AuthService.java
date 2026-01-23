package com.congresos.backend.service;

import com.congresos.backend.dto.LoginRequest;
import com.congresos.backend.dto.LoginResponse;
import com.congresos.backend.dto.RegistroRequest;
import com.congresos.backend.exception.BusinessException;
import com.congresos.backend.model.domain.Usuario;
import com.congresos.backend.repository.UsuarioRepository;
import com.congresos.backend.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

/**
 * Servicio de autenticación
 * CU1: Registro
 * CU2: Login
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    /**
     * CU1: Registro de nuevo usuario
     */
    @Transactional
    public LoginResponse registrar(RegistroRequest request) {
        log.info("Registrando nuevo usuario: {}", request.getEmail());

        // Verificar que el email no exista
        if (usuarioRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new BusinessException("El email ya está registrado");
        }

        // Crear usuario
        Usuario usuario = new Usuario();
        usuario.setNombreCompleto(request.getNombreCompleto());
        usuario.setEmail(request.getEmail());
        usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        usuario.setTipoUsuario(request.getTipoUsuario() != null ? request.getTipoUsuario() : Usuario.TipoUsuario.ASISTENTE);
        usuario.setActivo(true);

        Usuario usuarioGuardado = usuarioRepository.save(usuario);

        // Generar token con claims personalizados
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", usuarioGuardado.getTipoUsuario().name());
        claims.put("userId", usuarioGuardado.getIdUsuario());
        
        String token = jwtService.generateToken(claims, 
            org.springframework.security.core.userdetails.User.builder()
                .username(usuarioGuardado.getEmail())
                .password(usuarioGuardado.getPassword())
                .authorities("ROLE_" + usuarioGuardado.getTipoUsuario().name())
                .build()
        );

        log.info("Usuario registrado exitosamente con ID: {}", usuarioGuardado.getIdUsuario());

        return new LoginResponse(
                token,
                usuarioGuardado.getIdUsuario(),
                usuarioGuardado.getEmail(),
                usuarioGuardado.getNombreCompleto(),
                usuarioGuardado.getTipoUsuario().name()
        );
    }

    /**
     * CU2: Inicio de sesión
     */
    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        log.info("Intento de login para usuario: {}", request.getEmail());

        try {
            // Autenticar
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );

            // Buscar usuario
            Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new BusinessException("Usuario no encontrado"));

            if (!usuario.getActivo()) {
                throw new BusinessException("La cuenta está inactiva");
            }

            // Generar token con claims personalizados
            Map<String, Object> claims = new HashMap<>();
            claims.put("role", usuario.getTipoUsuario().name());
            claims.put("userId", usuario.getIdUsuario());
            
            String token = jwtService.generateToken(claims,
                org.springframework.security.core.userdetails.User.builder()
                    .username(usuario.getEmail())
                    .password(usuario.getPassword())
                    .authorities("ROLE_" + usuario.getTipoUsuario().name())
                    .build()
            );

            log.info("Login exitoso para usuario: {}", request.getEmail());

            return new LoginResponse(
                    token,
                    usuario.getIdUsuario(),
                    usuario.getEmail(),
                    usuario.getNombreCompleto(),
                    usuario.getTipoUsuario().name()
            );

        } catch (BadCredentialsException e) {
            log.warn("Credenciales inválidas para usuario: {}", request.getEmail());
            throw new BusinessException("Email o contraseña incorrectos");
        }
    }
}

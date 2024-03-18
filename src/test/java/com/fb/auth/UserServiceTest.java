package com.fb.auth;

import com.fb.auth.dao.AuthorityRepository;
import com.fb.auth.dao.UserRepository;
import com.fb.auth.dto.UserDTO;
import com.fb.auth.entity.Authority;
import com.fb.auth.entity.User;
import com.fb.auth.mapper.UserMapper;
import com.fb.auth.service.EmailService;
import com.fb.auth.service.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Set;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private AuthorityRepository authorityRepository;
    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Mock
    private UserMapper userMapper;
    @Mock
    private EmailService emailService;
    @InjectMocks
    private UserServiceImpl userServiceImpl;

    /**
     * Teste la méthode createAccount() du UserServiceImpl
     * Vérifie si la méthode sauvegarde correctement un nouvel utilisateur et envoie la clé d'activation par email
     */
    @Test
    void testCreateAccount() {
        // crée un utilisateur DTO avec des données fictives
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("testuser");
        userDTO.setEmail("test@example.com");
        userDTO.setPassword("password123");
        // crée un rôle par défaut
        Authority authority = new Authority();
        authority.setAuthorityLabel("CUSTOMER");
        // crée un utilisateur avec les données du DTO
        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        user.setPassword("encryptedPassword");
        user.setAuthorities(Set.of(authority));
        // configure les comportements des mocks
        when(userRepository.existsByUsername(userDTO.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(userDTO.getEmail())).thenReturn(false);
        when(authorityRepository.findByAuthorityLabel("CUSTOMER")).thenReturn(authority);
        when(bCryptPasswordEncoder.encode(userDTO.getPassword())).thenReturn("encryptedPassword");
        when(userMapper.dtoToModel(userDTO)).thenReturn(user);

        // appelle la méthode à tester
        userServiceImpl.createAccount(userDTO);

        // vérifie si la méthode save() du UserRepository a été appelée une fois avec l'utilisateur en paramètre
        verify(userRepository, times(1)).save(user);
        // vérifie si la méthode sendKeyActivation() de l'EmailService a été appelée une fois avec l'utilisateur en paramètre
        verify(emailService, times(1)).sendKeyActivation(user);
    }
}

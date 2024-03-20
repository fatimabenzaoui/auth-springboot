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

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
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

    /**
     * Teste la méthode activateAccount() du UserServiceImpl
     * Vérifie le comportement de l'activation du compte utilisateur
     */
    @Test
    void testActivateAccount() {
        // prépare les données de test
        String activationKey = "990089";
        User user = new User();
        user.setActivationKey(activationKey);
        user.setExpirationKeyDate(Instant.now().plusSeconds(600)); // Expiration dans 10 minutes

        // configure le comportement du mock pour simuler la recherche de l'utilisateur
        when(userRepository.findByActivationKey(activationKey)).thenReturn(user);

        // appelle la méthode à tester
        Map<String, String> activation = new HashMap<>();
        activation.put("activationKey", activationKey);
        userServiceImpl.activateAccount(activation);

        // vérifie que la méthode save a été appelée une fois avec l'utilisateur modifié
        verify(userRepository, times(1)).save(user);
    }

    /**
     * Teste la méthode requestNewActivationKey() du UserServiceImpl
     * Vérifie le comportement de la demande de nouvelle clé d'activation
     */
    @Test
    void testRequestNewActivationKey() {
        // prépare les données de test
        String username = "testuser";
        User user = new User();
        user.setUsername(username);
        user.setActivated(false);
        user.setExpirationKeyDate(Instant.now().minusSeconds(600)); // (expiration dans 10 minutes passées)

        // configure le comportement du mock pour simuler la recherche de l'utilisateur
        when(userRepository.findByUsername(username)).thenReturn(user);

        // appelle la méthode à tester
        userServiceImpl.requestNewActivationKey(username);

        // vérifie que les méthodes appropriées ont été appelées avec les bons paramètres
        verify(userRepository, times(1)).findByUsername(username);
        verify(userRepository, times(1)).save(user);
        verify(emailService, times(1)).sendKeyActivation(user);
    }

    /**
     * Teste la méthode removeNotActivatedAccounts() du UserServiceImpl
     * Vérifie si la méthode supprime correctement les comptes d'utilisateurs non activés.
     */
    @Test
    void testRemoveNotActivatedAccounts() {
        // prépare les données de test
        User user1 = new User();
        user1.setUserId(1L);
        User user2 = new User();
        user2.setUserId(2L);
        List<User> userList = List.of(user1, user2);

        // configure le comportement du mock pour simuler la recherche d'utilisateurs non activés
        when(userRepository.findAllByActivatedIsFalseAndActivationKeyIsNotNullAndCreatedDateBefore(any(Instant.class)))
                .thenReturn(userList);

        // appelle la méthode à tester
        userServiceImpl.removeNotActivatedAccounts();

        // vérifie que la méthode deleteAll a été appelée une fois avec la liste des utilisateurs non activés
        verify(userRepository, times(1)).deleteAll(userList);
    }
}

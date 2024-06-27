package com.fb.auth;

import com.fb.auth.dao.AccountActivationRepository;
import com.fb.auth.dao.AuthorityRepository;
import com.fb.auth.dao.ProfilePhotoRepository;
import com.fb.auth.dao.UserRepository;
import com.fb.auth.dto.UserDTO;
import com.fb.auth.entity.AccountActivation;
import com.fb.auth.entity.Authority;
import com.fb.auth.entity.ProfilePhoto;
import com.fb.auth.entity.User;
import com.fb.auth.exception.AccountAlreadyActivatedException;
import com.fb.auth.exception.ActivationKeyExpiredException;
import com.fb.auth.exception.ActivationKeyNotExpiredException;
import com.fb.auth.exception.ActivationKeyNotFoundException;
import com.fb.auth.mapper.UserMapper;
import com.fb.auth.service.EmailService;
import com.fb.auth.service.UserRegistrationServiceImpl;
import com.fb.auth.service.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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
    @Mock
    private AccountActivationRepository accountActivationRepository;
    @Mock
    private ProfilePhotoRepository profilePhotoRepository;

    @InjectMocks
    private UserRegistrationServiceImpl userRegistrationServiceImpl;
    @InjectMocks
    private UserServiceImpl userServiceImpl;

    /**
     * Teste la méthode createAccount() du UserRegistrationServiceImpl
     * Vérifie que la méthode createAccount crée un utilisateur, génère une clé d'activation, enregistre l'utilisateur et la clé d'activation, et envoie les emails appropriés
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
        // crée une activation de compte
        Instant expirationDate = Instant.now().plus(10, ChronoUnit.MINUTES);
        String activationKey = "999999";
        AccountActivation accountActivation = new AccountActivation();
        accountActivation.setActivationKey(activationKey);
        accountActivation.setExpirationDate(expirationDate);
        accountActivation.setUser(user);
        // crée une photo de profil par défaut
        ProfilePhoto defaultProfilePhoto = new ProfilePhoto();
        defaultProfilePhoto.setFileName("default-profile-photo.jpg");
        // configure les comportements des mocks
        when(userRepository.existsByUsername(userDTO.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(userDTO.getEmail())).thenReturn(false);
        when(authorityRepository.findByAuthorityLabel("CUSTOMER")).thenReturn(authority);
        when(bCryptPasswordEncoder.encode(userDTO.getPassword())).thenReturn("encryptedPassword");
        when(userMapper.dtoToModel(userDTO)).thenReturn(user);
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(accountActivationRepository.save(any(AccountActivation.class))).thenReturn(accountActivation);
        when(profilePhotoRepository.findByFileName("default-profile-photo.jpg")).thenReturn(defaultProfilePhoto);
        // appelle la méthode à tester
        userRegistrationServiceImpl.createAccount(userDTO);
        // vérifie si la méthode save() du UserRepository a été appelée une fois avec l'utilisateur en paramètre
        verify(userRepository, times(1)).save(any(User.class));
        // vérifie si la méthode save() du AccountActivationRepository a été appelée une fois avec l'objet AccountActivation en paramètre
        verify(accountActivationRepository, times(1)).save(any(AccountActivation.class));
        // vérifie si la méthode sendWelcomeEmail() de l'EmailService a été appelée une fois avec l'utilisateur en paramètre
        verify(emailService, times(1)).sendWelcomeEmail(any(User.class));
        // vérifie si la méthode sendActivationKey() de l'EmailService a été appelée une fois avec l'utilisateur et l'activation de compte en paramètre
        verify(emailService, times(1)).sendActivationKey(any(User.class), any(AccountActivation.class));
    }

    /**
     * Teste la méthode activateAccount() du UserRegistrationServiceImpl
     * Test d'activation réussie
     */
    @Test
    void testActivateAccount() {
        // prépare les données de test
        String activationKey = "990089";
        Instant expirationDate = Instant.now().plusSeconds(600); // Expiration dans 10 minutes
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword("encryptedPassword");
        user.setActivated(false); // Initialement désactivé
        AccountActivation accountActivation = new AccountActivation();
        accountActivation.setActivationKey(activationKey);
        accountActivation.setExpirationDate(expirationDate);
        accountActivation.setUser(user);
        // configure les comportements des mocks
        when(accountActivationRepository.findByActivationKey(activationKey)).thenReturn(accountActivation);
        // appelle la méthode à tester
        Map<String, String> activation = new HashMap<>();
        activation.put("activationKey", activationKey);
        userRegistrationServiceImpl.activateAccount(activation);
        // vérifie que l'utilisateur a été activé
        assertTrue(user.isActivated(), "L'utilisateur doit être activé");
        // vérifie que la méthode save() du UserRepository a été appelée une fois avec l'utilisateur modifié
        verify(userRepository, times(1)).save(user);
        // vérifie que la méthode delete() du AccountActivationRepository a été appelée une fois avec l'objet AccountActivation
        verify(accountActivationRepository, times(1)).delete(accountActivation);
    }

    /**
     * Teste la méthode activateAccount() du UserRegistrationServiceImpl
     * Test d'activation avec une clé invalide
     */
    @Test
    void testActivateAccountWithInvalidKey() {
        // prépare les données de test
        String activationKey = "invalidKey";
        // configure le comportement du mock pour simuler l'absence de clé d'activation
        when(accountActivationRepository.findByActivationKey(activationKey)).thenReturn(null);
        // appelle la méthode à tester et vérifie qu'elle lève l'exception appropriée
        Map<String, String> activation = new HashMap<>();
        activation.put("activationKey", activationKey);
        assertThrows(ActivationKeyNotFoundException.class, () -> userRegistrationServiceImpl.activateAccount(activation));
        // vérifie que la méthode save() du UserRepository n'a pas été appelée
        verify(userRepository, times(0)).save(any(User.class));
        // vérifie que la méthode delete() du AccountActivationRepository n'a pas été appelée
        verify(accountActivationRepository, times(0)).delete(any(AccountActivation.class));
    }

    /**
     * Teste la méthode activateAccount() du UserRegistrationServiceImpl
     * Test d'activation avec une clé expirée
     */
    @Test
    void testActivateAccountWithExpiredKey() {
        // prépare les données de test
        String activationKey = "990089";
        Instant expirationDate = Instant.now().minusSeconds(600); // Expiration dans le passé
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword("encryptedPassword");
        user.setActivated(false); // Initialement désactivé
        AccountActivation accountActivation = new AccountActivation();
        accountActivation.setActivationKey(activationKey);
        accountActivation.setExpirationDate(expirationDate);
        accountActivation.setUser(user);
        // configure les comportements des mocks
        when(accountActivationRepository.findByActivationKey(activationKey)).thenReturn(accountActivation);
        // appelle la méthode à tester et vérifie qu'elle lève l'exception appropriée
        Map<String, String> activation = new HashMap<>();
        activation.put("activationKey", activationKey);
        assertThrows(ActivationKeyExpiredException.class, () -> userRegistrationServiceImpl.activateAccount(activation));
        // vérifie que la méthode save() du UserRepository n'a pas été appelée
        verify(userRepository, times(0)).save(any(User.class));
        // vérifie que la méthode delete() du AccountActivationRepository n'a pas été appelée
        verify(accountActivationRepository, times(0)).delete(any(AccountActivation.class));
    }

    /**
     * Teste la méthode requestNewActivationKey() du UserRegistrationServiceImpl
     * Test d'une demande réussie de nouvelle clé d'activation
     */
    @Test
    void testRequestNewActivationKey() {
        // prépare les données de test
        String username = "testuser";
        User user = new User();
        user.setUsername(username);
        user.setActivated(false);
        // configure les comportements des mocks
        when(userRepository.findByUsername(username)).thenReturn(user);
        // appelle la méthode à tester
        userRegistrationServiceImpl.requestNewActivationKey(username);
        // vérifie que la méthode findByUsername a été appelée exactement une fois avec le bon paramètre
        verify(userRepository, times(1)).findByUsername(username);
        // vérifie que la méthode save a été appelée une fois avec un argument de type AccountActivation
        verify(accountActivationRepository, times(1)).save(any(AccountActivation.class));
        // vérifie que la méthode sendActivationKey a été appelée une fois avec les bons paramètres
        verify(emailService, times(1)).sendActivationKey(eq(user), any(AccountActivation.class));
    }

    /**
     * Teste la méthode requestNewActivationKey() du UserRegistrationServiceImpl
     * Test d'une demande pour un utilisateur déjà activé
     */
    @Test
    void testRequestNewActivationKeyWithAlreadyActivatedUser() {
        // prépare les données de test
        String username = "testuser";
        User user = new User();
        user.setUsername(username);
        user.setActivated(true); // Utilisateur déjà activé
        // configure les comportements des mocks
        when(userRepository.findByUsername(username)).thenReturn(user);
        // appelle la méthode à tester et vérifie qu'elle lève l'exception appropriée
        assertThrows(AccountAlreadyActivatedException.class, () -> userRegistrationServiceImpl.requestNewActivationKey(username));
        // vérifie que les méthodes appropriées ont été appelées avec les bons paramètres
        verify(userRepository, times(1)).findByUsername(username);
        verify(accountActivationRepository, times(0)).findByUser(user);
        verify(accountActivationRepository, times(0)).save(any(AccountActivation.class));
        verify(emailService, times(0)).sendActivationKey(eq(user), any(AccountActivation.class));
    }

    /**
     * Teste la méthode requestNewActivationKey() du UserRegistrationServiceImpl
     * Test d'une demande avec une clé d'activation encore valide
     */
    @Test
    void testRequestNewActivationKeyWithExistingValidActivationKey() {
        // prépare les données de test
        String username = "testuser";
        User user = new User();
        user.setUsername(username);
        user.setActivated(false);
        AccountActivation accountActivation = new AccountActivation();
        accountActivation.setActivationKey("123456");
        accountActivation.setExpirationDate(Instant.now().plusSeconds(600));
        // configure les comportements des mocks
        when(userRepository.findByUsername(username)).thenReturn(user);
        when(accountActivationRepository.findByUser(user)).thenReturn(accountActivation);
        // appelle la méthode à tester et vérifie qu'elle lève l'exception appropriée
        assertThrows(ActivationKeyNotExpiredException.class, () -> userRegistrationServiceImpl.requestNewActivationKey(username));
        // vérifie que les méthodes appropriées ont été appelées avec les bons paramètres
        verify(userRepository, times(1)).findByUsername(username);
        verify(accountActivationRepository, times(1)).findByUser(user);
        verify(accountActivationRepository, times(0)).save(any(AccountActivation.class));
        verify(emailService, times(0)).sendActivationKey(eq(user), any(AccountActivation.class));
    }

    /**
     * Teste la méthode requestNewActivationKey() du UserRegistrationServiceImpl
     * Test d'une demande avec une clé d'activation expirée
     */
    @Test
    void testRequestNewActivationKeyWithExpiredActivationKey() {
        // prépare les données de test
        String username = "testuser";
        User user = new User();
        user.setUsername(username);
        user.setActivated(false);
        AccountActivation expiredActivation = new AccountActivation();
        expiredActivation.setActivationKey("123456");
        expiredActivation.setExpirationDate(Instant.now().minusSeconds(600));
        // configure les comportements des mocks
        when(userRepository.findByUsername(username)).thenReturn(user);
        // appelle la méthode à tester
        userRegistrationServiceImpl.requestNewActivationKey(username);
        // vérifie que les méthodes appropriées ont été appelées avec les bons paramètres
        verify(userRepository, times(1)).findByUsername(username);
        verify(accountActivationRepository, times(1)).save(any(AccountActivation.class));
        verify(emailService, times(1)).sendActivationKey(eq(user), any(AccountActivation.class));
    }

    /**
     * Teste la méthode removeNotActivatedAccounts() du UserRegistrationServiceImpl
     * Test la suppression des utilisateurs non activés ainsi que leurs entrées correspondantes dans AccountActivation
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
        when(userRepository.findAllNotActivatedUsersWithActivationKey(any(Instant.class))).thenReturn(userList);
        // appelle la méthode à tester
        userRegistrationServiceImpl.removeNotActivatedAccounts();
        // vérifie que la méthode delete() a été appelée pour chaque utilisateur non activé
        verify(accountActivationRepository, times(1)).deleteByUser(user1);
        verify(accountActivationRepository, times(1)).deleteByUser(user2);
        verify(userRepository, times(1)).delete(user1);
        verify(userRepository, times(1)).delete(user2);
    }

    /**
     * Teste la méthode findAll() du service UserServiceImpl
     * Vérifie si la méthode retourne correctement tous les utilisateurs de la base de données, triés par userId décroissant
     */
    @Test
    void testFindAll() {
        // crée des données fictives pour simuler le comportement du repository
        List<User> users = new ArrayList<>();
        users.add(createUser("user1", "user1@example.com", "123456"));
        users.add(createUser("user2", "user2@example.com", "789789"));
        // définit le comportement du mock userRepository
        when(userRepository.findAll(Sort.by(Sort.Direction.DESC, "userId"))).thenReturn(users);
        // appelle la méthode à tester
        userServiceImpl.findAll();
        // vérifie le résultat en utilisant Mockito
        verify(userRepository, times(1)).findAll(Sort.by(Sort.Direction.DESC, "userId"));
    }

    // méthode utilitaire pour créer un UserDTO avec les propriétés spécifiées
    private User createUser(String username, String email, String password) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(password);
        return user;
    }
}

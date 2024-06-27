package com.fb.auth.service;

import com.fb.auth.constant.Constant;
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
import com.fb.auth.exception.EmailAlreadyUsedException;
import com.fb.auth.exception.InvalidEmailException;
import com.fb.auth.exception.InvalidLengthPasswordException;
import com.fb.auth.exception.UsernameAlreadyUsedException;
import com.fb.auth.exception.UsernameNotFoundException;
import com.fb.auth.mapper.UserMapper;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

@Service
@AllArgsConstructor
@Transactional
public class UserRegistrationServiceImpl implements UserRegistrationService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final AuthorityRepository authorityRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final EmailService emailService;
    private final AccountActivationRepository accountActivationRepository;
    private final ProfilePhotoRepository profilePhotoRepository;
    private final Random random = new Random();


    /**
     * Crée un nouveau compte utilisateur en utilisant les informations fournies dans un objet UserDTO
     * Le mot de passe est crypté avant d'être enregistré en base de données
     * Si le rôle par défaut ("CUSTOMER") n'existe pas en base de données, il est créé
     * Ce rôle est ensuite associé au nouvel utilisateur
     * L'objet UserDTO est converti en une entité User avant d'être sauvegardé en base de données
     * Une clé d'activation est générée et associée à l'utilisateur pour activer le compte
     * Cette clé d'activation est envoyée à l'utilisateur par email
     *
     * @param userDTO Les informations de l'utilisateur à créer
     * @throws InvalidLengthPasswordException Si la longueur du mot de passe est invalide
     * @throws UsernameAlreadyUsedException Si le nom d'utilisateur est déjà utilisé
     * @throws EmailAlreadyUsedException Si l'adresse email est déjà utilisée
     * @throws InvalidEmailException Si l'adresse email est invalide
     */
    @Override
    public void createAccount(UserDTO userDTO) {

        // vérifie si la longueur du password est incorrecte
        if (isPasswordLengthInvalid(userDTO.getPassword())) {
            throw new InvalidLengthPasswordException("*** INVALID LENGTH PASSWORD ***");
        }
        // vérifie si le username existe déjà en bdd
        if (Boolean.TRUE.equals(userRepository.existsByUsername(userDTO.getUsername()))) {
            throw new UsernameAlreadyUsedException("*** USERNAME ALREADY USED ***");
        }
        // vérifie si l'email existe déjà en bdd
        if (Boolean.TRUE.equals(userRepository.existsByEmail(userDTO.getEmail()))) {
            throw new EmailAlreadyUsedException("*** EMAIL ALREADY USED ***");
        }
        // vérifie si l'email est valide
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        if (!userDTO.getEmail().matches(emailRegex)) {
            throw new InvalidEmailException("*** INVALID EMAIL ***");
        }
        // crypte le password
        String encryptedPassword = bCryptPasswordEncoder.encode(userDTO.getPassword());
        userDTO.setPassword(encryptedPassword);
        // récupère le rôle par défaut ("CUSTOMER")
        Authority authority = authorityRepository.findByAuthorityLabel("CUSTOMER");
        // vérifie si le rôle existe déjà en base de données
        if (authority == null) {
            // si le rôle n'existe pas, crée un nouveau rôle et sauvegarde-le
            authority = new Authority();
            authority.setAuthorityLabel("CUSTOMER");
            authority = authorityRepository.save(authority);
        }
        // ajoute ce rôle au nouvel utilisateur
        Set<String> authorities = new HashSet<>();
        authorities.add(authority.getAuthorityLabel());
        userDTO.setAuthorities(authorities);
        // assigne la photo de profil par défaut si aucune n'est spécifiée
        if (userDTO.getPhotoFileName() == null || userDTO.getPhotoFileName().isEmpty()) {
            userDTO.setPhotoFileName("default-profile-photo.jpg");
        }
        // convertit l'objet DTO en entité User
        User user = userMapper.dtoToModel(userDTO);
        // mappe photoFileName vers ProfilePhoto
        ProfilePhoto defaultProfilePhoto = profilePhotoRepository.findByFileName(userDTO.getPhotoFileName());
        user.setProfilePhoto(defaultProfilePhoto);
        // sauvegarde l'utilisateur en base de données
        user = userRepository.save(user);
        // génère la clé d'activation et définit sa date d'expiration
        AccountActivation accountActivation = generateActivationKey(user);
        // envoie un email de bienvenue
        emailService.sendWelcomeEmail(user);
        // envoie la clé d'activation par email
        emailService.sendActivationKey(user, accountActivation);
    }


    /**
     * Active le compte utilisateur en utilisant la clé d'activation fournie
     * Recherche l'utilisateur correspondant à la clé d'activation dans la base de données
     * Si aucun utilisateur n'est trouvé avec la clé d'activation, une exception est levée
     * Vérifie si la clé d'activation n'a pas expiré en comparant la date d'expiration avec l'instant actuel
     * Si la clé d'activation a expiré, une exception est levée
     * Active le compte de l'utilisateur en définissant le champ "activated" sur true
     * Enregistre les modifications dans la base de données
     *
     * @param activation Un objet Map contenant la clé d'activation sous la clé "activationKey"
     * @throws ActivationKeyNotFoundException Si aucun utilisateur n'est trouvé avec la clé d'activation fournie
     * @throws ActivationKeyExpiredException Si la clé d'activation a expiré
     */
    @Override
    public void activateAccount(Map<String, String> activation) {
        // récupère la clé d'activation fournie
        String activationKey = activation.get("activationKey");
        // recherche l'objet AccountActivation correspondant à la clé d'activation dans la base de données
        AccountActivation accountActivation = accountActivationRepository.findByActivationKey(activationKey);
        // vérifie si aucun enregistrement n'est trouvé avec la clé d'activation
        if (accountActivation == null) {
            throw new ActivationKeyNotFoundException("*** UNKNOWN ACTIVATION KEY ***");
        }
        // vérifie si la clé d'activation a expiré
        if (Instant.now().isAfter(accountActivation.getExpirationDate())) {
            throw new ActivationKeyExpiredException("*** ACTIVATION KEY EXPIRED ***");
        }
        // récupère l'utilisateur associé
        User user = accountActivation.getUser();
        // active le compte de l'utilisateur
        user.setActivated(true);
        // sauvegarde les modifications de l'utilisateur
        userRepository.save(user);
        // supprime la clé d'activation et sa date d'expiration
        accountActivationRepository.delete(accountActivation);
    }

    /**
     * Demande une nouvelle clé d'activation pour un utilisateur donné
     * Si l'utilisateur est trouvé, vérifie si son compte est déjà activé et si la clé d'activation précédente a expiré
     * Si l'utilisateur n'est pas trouvé ou si son compte est déjà activé, lance une exception appropriée
     * Génère une nouvelle clé d'activation pour l'utilisateur et met à jour sa date d'expiration
     * Enregistre les modifications en base de données et envoie la nouvelle clé d'activation par email
     *
     * @param username Le surnom de l'utilisateur pour lequel demander une nouvelle clé d'activation
     * @throws UsernameNotFoundException Si aucun utilisateur n'est trouvé avec le surnom donné
     * @throws AccountAlreadyActivatedException Si le compte de l'utilisateur est déjà activé
     * @throws ActivationKeyNotExpiredException Si la clé d'activation précédente est toujours valide et n'a pas encore expiré
     */
    @Override
    public void requestNewActivationKey(String username) {
        // recherche l'utilisateur par son nom d'utilisateur
        User user = userRepository.findByUsername(username);
        // vérifie si l'utilisateur existe dans la base de données
        if (user == null) {
            throw new UsernameNotFoundException("*** USER NOT FOUND ***");
        }
        // vérifie si le compte de l'utilisateur est déjà activé
        if (user.isActivated()) {
            throw new AccountAlreadyActivatedException("*** ACCOUNT IS ALREADY ACTIVATED ***");
        }
        // recherche l'objet AccountActivation correspondant
        AccountActivation accountActivation = accountActivationRepository.findByUser(user);
        // vérifie si la clé d'activation précédente a expiré
        if (accountActivation != null && Instant.now().isBefore(accountActivation.getExpirationDate())) {
            throw new ActivationKeyNotExpiredException("*** PREVIOUS ACTIVATION KEY IS STILL VALID ***");
        }
        // génère une nouvelle clé d'activation et définit sa date d'expiration
        accountActivation = generateActivationKey(user);
        // envoie la nouvelle clé d'activation par email
        emailService.sendActivationKey(user, accountActivation);
    }



    /**
     * Supprime quotidiennement à 1h00 du matin les comptes utilisateurs non activés qui ont été créés au moins 3 jours auparavant
     * Utilisation du calendrier Cron pour planifier l'exécution
     */
    @Override
    @Scheduled(cron = "0 0 1 * * ?")
    public void removeNotActivatedAccounts() {
        // crée un objet Instant représentant l'instant actuel moins 3 jours
        Instant date = Instant.now().minus(3, ChronoUnit.DAYS);
        // récupère la liste des utilisateurs non activés et créés avant la date calculée (il y a 3 jours ou plus)
        List<User> usersToDelete = userRepository.findAllNotActivatedUsersWithActivationKey(date);
        // pour chaque utilisateur dans la liste des utilisateurs à supprimer
        for (User user : usersToDelete) {
            // supprime l'enregistrement d'activation du compte de l'utilisateur
            accountActivationRepository.deleteByUser(user);
            // supprime l'utilisateur
            userRepository.delete(user);
        }
    }

    /**
     * Vérifie si la longueur du mot de passe est incorrecte
     * Retourne vrai si le mot de passe est vide ou si sa longueur est inférieure à la longueur minimale
     * spécifiée dans Constant.PASSWORD_MIN_LENGTH ou supérieure à la longueur maximale spécifiée dans Constant.PASSWORD_MAX_LENGTH
     *
     * @param password Le mot de passe à vérifier.
     * @return true si la longueur du mot de passe est invalide, sinon false.
     */
    private static boolean isPasswordLengthInvalid(String password) {
        return (
                StringUtils.isEmpty(password) ||
                        password.length() < Constant.PASSWORD_MIN_LENGTH ||
                        password.length() > Constant.PASSWORD_MAX_LENGTH
        );
    }

    /**
     * Génère une clé d'activation pour un utilisateur donné et définit sa date d'expiration
     * La clé d'activation est une chaîne aléatoire de 6 chiffres
     * La date d'expiration est définie à 10 minutes après la date de création
     *
     * @param user L'utilisateur pour lequel générer la clé d'activation
     * @return L'objet AccountActivation mis à jour ou créé, contenant la clé d'activation et la date d'expiration
     */
    private AccountActivation generateActivationKey(User user) {
        // définit la date d'expiration de la clé d'activation
        Instant keyExpirationDate = Instant.now().plus(10, ChronoUnit.MINUTES);
        // génère une clé d'activation aléatoire de 6 chiffres
        int randomInteger = random.nextInt(999999);
        String key = String.format("%06d", randomInteger);
        // recherche l'objet AccountActivation correspondant
        AccountActivation accountActivation = accountActivationRepository.findByUser(user);
        if (accountActivation == null) {
            // crée un nouvel enregistrement AccountActivation si inexistant
            accountActivation = AccountActivation.builder()
                    .user(user)
                    .activationKey(key)
                    .expirationDate(keyExpirationDate)
                    .build();
        } else {
            // met à jour les informations d'activation existantes
            accountActivation.setActivationKey(key);
            accountActivation.setExpirationDate(keyExpirationDate);
        }
        // enregistre les modifications en base de données
        accountActivationRepository.save(accountActivation);
        return accountActivation;
    }
}

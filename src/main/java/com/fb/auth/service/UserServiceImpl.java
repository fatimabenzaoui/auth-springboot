package com.fb.auth.service;

import com.fb.auth.constant.Constant;
import com.fb.auth.dao.AuthorityRepository;
import com.fb.auth.dao.UserRepository;
import com.fb.auth.dto.UserDTO;
import com.fb.auth.entity.Authority;
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
import java.util.Map;
import java.util.Random;
import java.util.Set;

@Service
@AllArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final AuthorityRepository authorityRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final EmailService emailService;
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
        String cryptedPassword = bCryptPasswordEncoder.encode(userDTO.getPassword());
        userDTO.setPassword(cryptedPassword);

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
        Set<Authority> authorities = new HashSet<>();
        authorities.add(authority);
        userDTO.setAuthorities(authorities);

        // convertit l'objet DTO en entité User
        User user = userMapper.dtoToModel(userDTO);

        // génère la clé d'activation et définit sa date d'expiration
        generateActivationKey(user);

        // sauvegarde l'utilisateur en base de données
        userRepository.save(user);

        // envoie la clé d'activation par email
        emailService.sendKeyActivation(user);
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
     *                   La clé d'activation est utilisée pour rechercher l'utilisateur à activer
     * @throws ActivationKeyNotFoundException Si aucun utilisateur n'est trouvé avec la clé d'activation fournie
     * @throws ActivationKeyExpiredException Si la clé d'activation a expiré
     */
    @Override
    public void activateAccount(Map<String, String> activation) {
        // récupère la clé d'activation fournie
        String activationKey = activation.get("activationKey");

        // recherche l'utilisateur correspondant à la clé d'activation dans la base de données
        User user = userRepository.findByActivationKey(activationKey);

        // vérifie si aucun utilisateur n'est trouvé avec la clé d'activation
        if (user == null) {
            throw new ActivationKeyNotFoundException("*** UNKNOWN ACTIVATION KEY ***");
        }

        // vérifie si la clé d'activation a expiré
        if (Instant.now().isAfter(user.getExpirationKeyDate())) {
            throw new ActivationKeyExpiredException("*** ACTIVATION KEY EXPIRED ***");
        }

        // active le compte de l'utilisateur
        user.setActivated(true);
        // enregistre les modifications dans la base de données
        userRepository.save(user);
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

        // vérifie si la clé d'activation précédente a expiré
        if (Instant.now().isBefore(user.getExpirationKeyDate())) {
            throw new ActivationKeyNotExpiredException("*** PREVIOUS ACTIVATION KEY IS STILL VALID ***");
        }

        // génère une nouvelle clé d'activation et définit sa date d'expiration
        generateActivationKey(user);

        // enregistre les modifications en base de données
        userRepository.save(user);

        // envoie la nouvelle clé d'activation par email
        emailService.sendKeyActivation(user);
    }

    /**
     * Supprime à minuit le premier jour de chaque mois (calendrier Cron) les comptes utilisateurs non activés et qui ont été créés au moins 3 jours avant
     */
    @Override
    @Scheduled(cron = "0 0 1 * * ?")
    public void removeNotActivatedAccounts() {
        userRepository.deleteAll(userRepository.findAllByActivatedIsFalseAndActivationKeyIsNotNullAndCreatedDateBefore(Instant.now().minus(3, ChronoUnit.DAYS)));
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
     * Génère une nouvelle clé d'activation pour un utilisateur donné et définit sa date d'expiration
     * La clé d'activation est une chaîne aléatoire de 6 chiffres
     * La date d'expiration est définie à 10 minutes après la date de création
     *
     * @param user L'utilisateur pour lequel générer la clé d'activation
     */
    private void generateActivationKey(User user) {
        Instant creationDate = Instant.now();
        Instant expirationDate = creationDate.plus(10, ChronoUnit.MINUTES);
        int randomInteger = random.nextInt(999999);
        String key = String.format("%06d", randomInteger);
        user.setActivationKey(key);
        user.setExpirationKeyDate(expirationDate);
    }

}

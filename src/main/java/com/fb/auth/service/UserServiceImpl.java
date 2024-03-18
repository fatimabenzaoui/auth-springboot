package com.fb.auth.service;

import com.fb.auth.constant.Constant;
import com.fb.auth.dao.AuthorityRepository;
import com.fb.auth.dao.UserRepository;
import com.fb.auth.dto.UserDTO;
import com.fb.auth.entity.Authority;
import com.fb.auth.entity.User;
import com.fb.auth.exception.EmailAlreadyUsedException;
import com.fb.auth.exception.InvalidEmailException;
import com.fb.auth.exception.InvalidLengthPasswordException;
import com.fb.auth.exception.UsernameAlreadyUsedException;
import com.fb.auth.mapper.UserMapper;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
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
            throw new InvalidLengthPasswordException();
        }

        // vérifie si le username existe déjà en bdd
        if (Boolean.TRUE.equals(userRepository.existsByUsername(userDTO.getUsername()))) {
            throw new UsernameAlreadyUsedException();
        }

        // vérifie si l'email existe déjà en bdd
        if (Boolean.TRUE.equals(userRepository.existsByEmail(userDTO.getEmail()))) {
            throw new EmailAlreadyUsedException();
        }

        // vérifie si l'email est valide
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        if (!userDTO.getEmail().matches(emailRegex)) {
            throw new InvalidEmailException();
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

        // génère la clé d'activation
        Instant creationDate = Instant.now();
        Instant expirationDate = creationDate.plus(10, ChronoUnit.MINUTES);
        int randomInteger = random.nextInt(999999);
        String code = String.format("%06d", randomInteger);
        user.setActivationKey(code);
        user.setExpirationKeyDate(expirationDate);

        // sauvegarde l'utilisateur en base de données
        userRepository.save(user);

        // envoie la clé d'activation par email
        emailService.sendKeyActivation(user);
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
}

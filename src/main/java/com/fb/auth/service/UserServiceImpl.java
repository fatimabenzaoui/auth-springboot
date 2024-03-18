package com.fb.auth.service;

import com.fb.auth.dao.AuthorityRepository;
import com.fb.auth.dao.UserRepository;
import com.fb.auth.dto.UserDTO;
import com.fb.auth.entity.Authority;
import com.fb.auth.entity.User;
import com.fb.auth.mapper.UserMapper;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
@AllArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final AuthorityRepository authorityRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    /**
     * Crée un nouveau compte utilisateur en utilisant les informations fournies dans un objet UserDTO
     * Le mot de passe est crypté avant d'être enregistré en base de données
     * Si le rôle par défaut ("CUSTOMER") n'existe pas en base de données, il est créé
     * Ce rôle est ensuite associé au nouvel utilisateur
     * L'objet UserDTO est converti en une entité User avant d'être sauvegardé en base de données
     *
     * @param userDTO Les informations de l'utilisateur à créer
     */
    @Override
    public void createAccount(UserDTO userDTO) {

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

        // sauvegarde l'utilisateur en base de données
        userRepository.save(user);
    }
}

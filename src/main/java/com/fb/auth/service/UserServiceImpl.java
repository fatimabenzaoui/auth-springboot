package com.fb.auth.service;

import com.fb.auth.dao.UserRepository;
import com.fb.auth.dto.UserDTO;
import com.fb.auth.entity.User;
import com.fb.auth.mapper.UserMapper;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@AllArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    /**
     * Récupère tous les utilisateurs de la base de données triés par userId par ordre décroissant
     *
     * @return Une liste de DTOs d'utilisateurs représentant tous les utilisateurs dans la base de données, triés par userId décroissant
     * @throws DataRetrievalFailureException si une erreur survient lors de la récupération des utilisateurs
     */
    @Override
    @Transactional(readOnly = true)
    public List<UserDTO> findAll() {
        try {
        List<User> users = userRepository.findAll(Sort.by(Sort.Direction.DESC, "userId"));
        return userMapper.modelsToDtos(users);
        } catch (Exception e) {
            throw new DataRetrievalFailureException("*** UNE ERREUR EST SURVENUE LORS DE LA RECUPERATION DES UTILISATEURS", e);
        }
    }
}

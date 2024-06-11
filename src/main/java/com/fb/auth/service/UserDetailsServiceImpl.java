package com.fb.auth.service;

import com.fb.auth.dao.UserRepository;
import com.fb.auth.entity.User;
import com.fb.auth.exception.UsernameNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
@Transactional
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Charge les détails de l'utilisateur à partir de la base de données en utilisant le nom d'utilisateur fourni
     * Cette méthode est appelée par le système de sécurité pour obtenir les détails de l'utilisateur lors de l'authentification
     *
     * @param username Le surnom de l'utilisateur dont les détails doivent être chargés
     * @return Les détails de l'utilisateur trouvés dans la base de données
     * @throws UsernameNotFoundException Si aucun utilisateur avec le nom d'utilisateur donné n'est trouvé dans la base de données
     */
    @Override
    public User loadUserByUsername(String username) throws org.springframework.security.core.userdetails.UsernameNotFoundException {
        return this.userRepository.findByUsername(username);
    }
}

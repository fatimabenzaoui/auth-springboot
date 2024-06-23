package com.fb.auth.dao;

import com.fb.auth.entity.AccountActivation;
import com.fb.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountActivationRepository extends JpaRepository<AccountActivation, Long> {
    AccountActivation findByActivationKey(String activationKey);
    AccountActivation findByUser(User user);
    void deleteByUser(User user);
}

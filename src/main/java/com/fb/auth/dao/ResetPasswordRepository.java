package com.fb.auth.dao;

import com.fb.auth.entity.ResetPassword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResetPasswordRepository extends JpaRepository<ResetPassword, Long> {
    ResetPassword findByResetKey(String resetKey);
}

package com.library.repository;

import com.library.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Kullanıcı veritabanı işlemlerini yöneten Repository.
 */

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Email adresine göre kullanıcı bulur.
     * Login işleminde kullanılır.
     */
    Optional<User> findByEmail(String email);

    /**
     * Email adresinin sistemde kayıtlı olup olmadığını kontrol eder.
     */
    Boolean existsByEmail(String email);

    /**
     * Belirli roldeki tüm kullanıcıları getirir.
     */
    List<User> findByRole(User.Role role);

    /**
     * Yasaklı veya yasaklı olmayan kullanıcıları getirir.
     */
    List<User> findByIsBanned(Boolean isBanned);
}
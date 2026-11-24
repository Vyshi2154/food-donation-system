
package com.fooddonation.repository;

import com.fooddonation.entity.User;
import com.fooddonation.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    List<User> findByRole(Role role);

    boolean existsByEmail(String email);
}

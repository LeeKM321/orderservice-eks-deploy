package com.playdata.userservice.user.repository;

import com.playdata.userservice.user.entity.User;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository
        extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    List<User> findByIdIn(List<Long> userIds);

}

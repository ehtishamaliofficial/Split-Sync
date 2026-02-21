package com.ehtisham.splitsync.domain.port.out;

import com.ehtisham.splitsync.domain.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {

    Optional<User> findById(Long id);
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);

    List<User> searchByUsernameOrEmail(String query, Long excludeUserId, int limit);

    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

    User save(User user);
    void deleteById(Long id);

}

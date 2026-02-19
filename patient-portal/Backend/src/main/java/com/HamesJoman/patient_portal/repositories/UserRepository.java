package com.HamesJoman.patient_portal.repositories;

import com.HamesJoman.patient_portal.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * You need to these to use SQL
 * It just provides all the basic CRUD operations
 * I'm pretty sure you can add your own queries if you add code
 * to the interface but that's scary so I'm not doing that
 *
 * @author Collin Fair
 */
@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    // I lied im adding code here
    // This one just verifies that the username is unique
    boolean existsByUsername(String username);
    // Optional is a keyword that basically just allows us to account for null input instead of crashing
    // This method finds someone by their username, duh
    Optional<User> findByUsername(String username);
}

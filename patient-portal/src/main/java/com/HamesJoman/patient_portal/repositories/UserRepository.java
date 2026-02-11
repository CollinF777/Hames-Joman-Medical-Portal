package com.HamesJoman.patient_portal.repositories;

import com.HamesJoman.patient_portal.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

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

}

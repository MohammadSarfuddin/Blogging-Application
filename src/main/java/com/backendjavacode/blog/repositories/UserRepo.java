package com.backendjavacode.blog.repositories;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.backendjavacode.blog.entities.User;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepo extends JpaRepository<User, Integer>{

	Optional<User> findByEmail(String email);
}

package com.psh.repository;

import com.psh.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    User findUserById( final String id );

    User findUserByIdAndPassword( final String id, final String password );
}

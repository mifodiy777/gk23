package ru.kircoop.gk23.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.kircoop.gk23.entity.User;

import java.util.Optional;

public interface UserDAO extends JpaRepository<User, Integer> {

    Optional<User> findByLogin(String login);
}
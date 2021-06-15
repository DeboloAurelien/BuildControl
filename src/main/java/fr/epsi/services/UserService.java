package fr.epsi.services;

import fr.epsi.entities.User;
import fr.epsi.repositories.UserRepository;

import java.sql.SQLException;
import java.util.List;

public class UserService {
    UserRepository userRepository = new UserRepository();

    public List<User> getAll() throws SQLException {
        return userRepository.findAll();
    }

    public int login(User user) throws SQLException {
        return userRepository.login(user);
    }
}

package in.kajalCoder.ContactSphere.services;

import java.util.List;
import java.util.Optional;

import in.kajalCoder.ContactSphere.entity.Users;

public interface UserService {

    Users saveUser(Users user);

    Users getUserById(Long id);

    Optional<Users> updateUser(Users user);

    void deleteUser(Long id);

    void deleteUserSoftly(Long id);

    boolean isUserExist(Long userId);

    boolean isUserExistByEmail(String email);

    List<Users> getAllUsers();

    Users getUserByEmail(String email);

}
package in.kajalCoder.ContactSphere.services.implementation;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import in.kajalCoder.ContactSphere.entity.Users;
import in.kajalCoder.ContactSphere.exceptions.ResourceNotFoundExceptions;
import in.kajalCoder.ContactSphere.repository.UserRepository;
import in.kajalCoder.ContactSphere.services.UserService;

@Service
public class UserServiceImplementation implements UserService {

    private UserRepository userRepository;

    private PasswordEncoder passwordEncoder;

    public UserServiceImplementation(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;

    }

    // logger

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public Users saveUser(Users user) {

        // password encode

        String encodePassword = passwordEncoder.encode(user.getPassword());

        user.setPassword(encodePassword);

        // save user in db
        return userRepository.save(user);
    }

    @Override
    public Users getUserById(Long id) {
        Users userResp = userRepository
                .findByUserIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundExceptions(
                        "User with id " + id + " not found"));

        return userResp;

    }

    @Override
    public Optional<Users> updateUser(Users user) {

        Optional<Users> existingUser = userRepository.findById(user.getUserId());

        if (existingUser.isPresent()) {
            Users userToUpdate = new Users();
            userToUpdate.setName(user.getName());
            userToUpdate.setEmail(user.getEmail());
            userToUpdate.setPassword(user.getPassword());
            userToUpdate.setAbout(user.getAbout());
            userToUpdate.setPhoneNumber(user.getPhoneNumber());
            userToUpdate.setProfilePic(user.getProfilePic());
            userToUpdate.setEnabled(user.isEnabled());
            userToUpdate.setProvider(user.getProvider());

            Users updateUser = userRepository.save(userToUpdate);

            return Optional.of(userToUpdate);

        }
        return Optional.empty();
    }

    @Override
    public void deleteUser(Long id) {

        Users userToBeDeleted = userRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundExceptions(
                        "User with id " + id + " not found"));

        userToBeDeleted.setDeleted(true);
        userRepository.save(userToBeDeleted);
    }

    @Override
    public boolean isUserExist(Long userId) {
        return userRepository.existsById(userId);

    }

    @Override
    public boolean isUserExistByEmail(String email) {
        return userRepository.existsByEmail(email);

    }

    @Override
    public List<Users> getAllUsers() {

        return userRepository.findByDeletedFalse();

    }

    @Override
    public Users getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    @Override
    public void deleteUserSoftly(Long id) {
        Users userToBeDeleted = userRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundExceptions(
                        "User with id " + id + " not found"));

        userToBeDeleted.setDeleted(true);

        userRepository.save(userToBeDeleted);
    }

}

package in.kajalCoder.ContactSphere.services.implementation;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import in.kajalCoder.ContactSphere.entity.Provider;
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

    @Override
    @org.springframework.transaction.annotation.Transactional
    public boolean processGoogleLogin(
            String email, String name, String googleSubject, String picture) {

        if (email == null || email.isBlank()
                || googleSubject == null || googleSubject.isBlank()) {
            return false;
        }

        // System.out.println("=== PROCESS GOOGLE LOGIN ===");
        // System.out.println("Email: " + email);
        // System.out.println("Google subject present: "
        // + (googleSubject != null && !googleSubject.isBlank()));

        Optional<Users> existingUserOptional = userRepository.findByEmail(email);

        // System.out.println("Existing user found: "
        // + existingUserOptional.isPresent());

        // Case 1: Email database mein pehle se hai
        if (existingUserOptional.isPresent()) {
            Users existingUser = existingUserOptional.get();

            // System.out.println("Existing provider: "
            // + existingUser.getProvider());
            // System.out.println("Existing user deleted: "
            // + existingUser.isDeleted());
            // System.out.println("Existing Google subject matches: "
            // + (googleSubject != null
            // && googleSubject.equals(existingUser.getProviderUserId())));

            // Deleted account ko login nahi karne dena
            if (existingUser.isDeleted()) {
                return false;
            }

            // Existing account ko Google account se silently link nahi karna
            if (existingUser.getProvider() != Provider.GOOGLE
                    || !googleSubject.equals(existingUser.getProviderUserId())) {
                return false;
            }

            if (name != null && !name.isBlank()) {
                existingUser.setName(name);
            }

            if (picture != null && !picture.isBlank()) {
                existingUser.setProfilePic(picture);
            }

            existingUser.setEnabled(true);
            existingUser.setEmailVerified(true);

            userRepository.save(existingUser);
            return true;
        }

        // Case 2: Naya Google user
        Users user = new Users();

        user.setName(name != null && !name.isBlank() ? name : email);
        user.setEmail(email);
        user.setProvider(Provider.GOOGLE);
        user.setProviderUserId(googleSubject);
        user.setProfilePic(picture);
        user.setEnabled(true);
        user.setEmailVerified(true);
        user.setDeleted(false);
        user.setAbout("\"Welcome to ContactSphere! Connect and manage your contacts easily.");

        // createdAt database mein NULL save hone se bachane ke liye
        user.setCreatedAt(LocalDateTime.now());

        userRepository.save(user);

        logger.info("New Google user saved: {}", email);
        return true;
    }
}

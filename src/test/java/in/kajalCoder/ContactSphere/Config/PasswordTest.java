package in.kajalCoder.ContactSphere.Config;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class PasswordTest {

    public static void main(String[] args) {

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        String rawPassword = "Kajal@123";

        String hash = passwordEncoder.encode(rawPassword);

        System.out.println("BCrypt Hash: " + hash);

        System.out.println(
                "Password matches: "
                        + passwordEncoder.matches(rawPassword, hash));
    }

}

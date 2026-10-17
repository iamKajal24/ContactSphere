package in.kajalCoder.ContactSphere.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import in.kajalCoder.ContactSphere.entity.Users;

@Repository
public interface UserRepository extends JpaRepository<Users, Long> {

    boolean existsByEmail(String email);

    Optional<Users> findByEmail(String email);

    List<Users> findByDeletedFalse();

    Optional<Users> findByUserIdAndDeletedFalse(Long userId);

}

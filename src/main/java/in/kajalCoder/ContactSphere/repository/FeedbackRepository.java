
package in.kajalCoder.ContactSphere.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import in.kajalCoder.ContactSphere.entity.Feedback;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {

    List<Feedback> findByUser_UserIdOrderByCreatedAtDesc(Long userId);

    List<Feedback> findAllByOrderByCreatedAtDesc();
}

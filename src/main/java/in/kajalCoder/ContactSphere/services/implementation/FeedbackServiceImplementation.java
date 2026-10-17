
package in.kajalCoder.ContactSphere.services.implementation;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import in.kajalCoder.ContactSphere.dto.FeedbackForm;
import in.kajalCoder.ContactSphere.entity.Feedback;
import in.kajalCoder.ContactSphere.entity.FeedbackStatus;
import in.kajalCoder.ContactSphere.entity.Users;
import in.kajalCoder.ContactSphere.repository.FeedbackRepository;
import in.kajalCoder.ContactSphere.services.FeedbackService;

@Service
public class FeedbackServiceImplementation implements FeedbackService {

    private final FeedbackRepository feedbackRepository;

    public FeedbackServiceImplementation(
            FeedbackRepository feedbackRepository) {
        this.feedbackRepository = feedbackRepository;
    }

    @Override
    @Transactional
    public Feedback submitFeedback(FeedbackForm form, Users user) {

        Feedback feedback = new Feedback();
        feedback.setCategory(form.getCategory());
        feedback.setRating(form.getRating());
        feedback.setMessage(form.getMessage().trim());
        feedback.setStatus(FeedbackStatus.NEW);
        feedback.setUser(user);

        return feedbackRepository.save(feedback);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Feedback> getMyFeedback(Long userId) {
        return feedbackRepository
                .findByUser_UserIdOrderByCreatedAtDesc(userId);
    }
}

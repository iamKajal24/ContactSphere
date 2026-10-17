
package in.kajalCoder.ContactSphere.services;

import java.util.List;

import in.kajalCoder.ContactSphere.dto.FeedbackForm;
import in.kajalCoder.ContactSphere.entity.Feedback;
import in.kajalCoder.ContactSphere.entity.Users;

public interface FeedbackService {

    Feedback submitFeedback(FeedbackForm form, Users user);

    List<Feedback> getMyFeedback(Long userId);
}

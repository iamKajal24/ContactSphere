
package in.kajalCoder.ContactSphere.dto;

import in.kajalCoder.ContactSphere.entity.FeedbackCategory;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class FeedbackForm {

    @NotNull(message = "Please select a category")
    private FeedbackCategory category;

    @NotNull(message = "Please select a rating")
    @Min(value = 1, message = "Minimum rating is 1")
    @Max(value = 5, message = "Maximum rating is 5")
    private Integer rating;

    @NotBlank(message = "Please enter your feedback")
    @Size(max = 2000, message = "Feedback cannot exceed 2000 characters")
    private String message;

    public FeedbackCategory getCategory() {
        return category;
    }

    public void setCategory(FeedbackCategory category) {
        this.category = category;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

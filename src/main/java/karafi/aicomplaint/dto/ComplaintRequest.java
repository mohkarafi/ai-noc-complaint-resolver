package karafi.aicomplaint.dto;


import jakarta.validation.constraints.NotBlank;

public record ComplaintRequest(

        @NotBlank(
                message = "Phone number is required"
        )
        String phoneNumber,


        @NotBlank(
                message = "Complaint text is required"
        )
        String text

) {
}

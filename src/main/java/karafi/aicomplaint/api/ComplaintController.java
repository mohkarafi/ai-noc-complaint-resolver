package karafi.aicomplaint.api;



import jakarta.validation.Valid;
import karafi.aicomplaint.api.dto.ComplaintRequest;
import karafi.aicomplaint.api.dto.ComplaintResponse;
import karafi.aicomplaint.complaint.ComplaintService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/complaints")
@RequiredArgsConstructor
public class ComplaintController {

    private final ComplaintService complaintService;
    
    @PostMapping
    public ComplaintResponse createComplaint(
            @Valid @RequestBody ComplaintRequest request) {

        var complaint = complaintService.createFromText(
                request.phoneNumber(),
                request.text()
        );

        return ComplaintResponse.from(complaint);
    }
}
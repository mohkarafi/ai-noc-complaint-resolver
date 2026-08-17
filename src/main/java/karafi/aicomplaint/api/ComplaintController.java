package karafi.aicomplaint.api;



import jakarta.validation.Valid;
import karafi.aicomplaint.dto.ComplaintAudioAnalysisResponse;
import karafi.aicomplaint.dto.ComplaintRequest;
import karafi.aicomplaint.dto.ComplaintResponse;
import karafi.aicomplaint.complaint.Complaint;
import karafi.aicomplaint.complaint.ComplaintService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/complaints")
@RequiredArgsConstructor
public class ComplaintController {

    private final ComplaintService complaintService;

    @PostMapping
    public ResponseEntity<ComplaintResponse> create(@RequestBody @Valid ComplaintRequest request) {
        Complaint complaint = complaintService.createFromText(request.phoneNumber(), request.text());
        ComplaintResponse response = new ComplaintResponse(
                complaint.getId(),
                complaint.getStatus().name(),
                complaint.getRawText()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);   // ← 201 maintenant
    }


    @PostMapping(value = "/analyze-audio", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ComplaintAudioAnalysisResponse> analyzeAudio(
            @RequestParam("phoneNumber") String phoneNumber,
            @RequestParam("file") MultipartFile audioFile) {

        Complaint complaint = complaintService.createFromAudio(phoneNumber, audioFile);

        ComplaintAudioAnalysisResponse response = new ComplaintAudioAnalysisResponse(
                complaint.getId(),
                complaint.getTranscription(),
                complaint.getCategory(),
                complaint.getIntent(),
                complaint.getSentiment(),
                complaint.getPriority(),
                complaint.getProbableCause(),
                complaint.getConfidence(),
                complaint.getStatus().name()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
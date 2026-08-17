package karafi.aicomplaint.dto;

public record ComplaintAudioAnalysisResponse(
        Long complaintId,
        String transcription,
        String category,
        String intent,
        String sentiment,
        String priority,
        String probableCause,
        Double confidence,
        String status
) {
}

package karafi.aicomplaint.dto;

public record ComplaintAnalysisResponse(
        Long complaintId,
        String category,
        String intent,
        String sentiment,
        String priority,
        String probableCause,
        Double confidence,
        String status
) {}
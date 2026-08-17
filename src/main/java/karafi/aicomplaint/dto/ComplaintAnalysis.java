package karafi.aicomplaint.dto;

public record ComplaintAnalysis(
        String category,
        String intent,
        String sentiment,
        String priority,
        String probableCause,
        Double confidence
) {
}

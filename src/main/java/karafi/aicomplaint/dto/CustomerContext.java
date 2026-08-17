package karafi.aicomplaint.dto;

public record CustomerContext(
        Long customerId,
        String plan,
        String region
) {
    public static CustomerContext empty() {
        return new CustomerContext(null, "UNKNOWN", "UNKNOWN");
    }
}
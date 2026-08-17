package karafi.aicomplaint.dto;


import karafi.aicomplaint.complaint.Complaint;

public record ComplaintResponse(

        Long id,

        String status,

        String rawText

) {

    public static ComplaintResponse from(
            Complaint complaint
    ) {

        return new ComplaintResponse(
                complaint.getId(),
                complaint.getStatus().name(),
                complaint.getRawText()
        );
    }
}
package karafi.aicomplaint.complaint;


import jakarta.persistence.*;
import karafi.aicomplaint.customer.Customer;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "complaints")
@Getter
@Setter
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class Complaint {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            nullable = false
    )
    private Customer customer;

    @Column(
            nullable = false,
            columnDefinition = "TEXT"
    )
    private String rawText;

    @Column(columnDefinition = "TEXT")
    private String transcription;

    private String category;

    private String intent;

    private String priority;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ComplaintStatus status;



    @Column(
            nullable = false
    )
    private LocalDateTime createdAt;
    @PrePersist
    protected void onCreate() {

        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }

        if (status == null) {
            status = ComplaintStatus.RECEIVED;
        }
    }
}

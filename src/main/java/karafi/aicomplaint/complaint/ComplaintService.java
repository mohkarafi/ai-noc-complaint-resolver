package karafi.aicomplaint.complaint;


import karafi.aicomplaint.customer.Customer;
import karafi.aicomplaint.customer.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ComplaintService {

    private final ComplaintRepository complaintRepository;

    private final CustomerService customerService;


    public Complaint createFromText( String phoneNumber, String rawText) {

        Customer customer = customerService.getOrCreate(phoneNumber);


        Complaint complaint = Complaint.builder().rawText(rawText).customer(customer).
                build();

        return complaintRepository.save(complaint);
    }
}
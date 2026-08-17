package karafi.aicomplaint.Complaint;

import karafi.aicomplaint.complaint.Complaint;
import karafi.aicomplaint.complaint.ComplaintRepository;
import karafi.aicomplaint.complaint.ComplaintService;
import karafi.aicomplaint.complaint.ComplaintStatus;
import karafi.aicomplaint.customer.Customer;
import karafi.aicomplaint.customer.CustomerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ComplaintServiceTest {

    @Mock
    private ComplaintRepository complaintRepository;

    @Mock
    private CustomerService customerService;

    @InjectMocks
    private ComplaintService complaintService;

    @Test
    void shouldCreateComplaintFromText() {
        Customer fakeCustomer = new Customer();
        fakeCustomer.setId(1L);
        fakeCustomer.setPhoneNumber("0600000000");

        when(customerService.getOrCreate("0600000000")).thenReturn(fakeCustomer);

        when(complaintRepository.save(any(Complaint.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Complaint result = complaintService.createFromText("0600000000", "Ma connexion est lente");

        assertThat(result).isNotNull();
        assertThat(result.getRawText()).isEqualTo("Ma connexion est lente");
        assertThat(result.getStatus()).isEqualTo(ComplaintStatus.RECEIVED);

        verify(complaintRepository, times(1)).save(any(Complaint.class));
    }
}
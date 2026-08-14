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

@ExtendWith(MockitoExtension.class)   // active Mockito pour cette classe de test
class ComplaintServiceTest {

    @Mock
    private ComplaintRepository complaintRepository;   // faux repository

    @Mock
    private CustomerService customerService;           // faux service client

    @InjectMocks
    private ComplaintService complaintService;          // le vrai service, avec les mocks injectés dedans

    @Test
    void shouldCreateComplaintFromText() {
        // GIVEN : je prépare le contexte du test
        Customer fakeCustomer = new Customer();
        fakeCustomer.setId(1L);
        fakeCustomer.setPhoneNumber("0600000000");

        // je dis au mock : "quand on t'appelle avec ce numéro, réponds ce client"
        when(customerService.getOrCreate("0600000000")).thenReturn(fakeCustomer);

        // je dis au mock repository : "quand on te demande de sauvegarder n'importe quelle Complaint, renvoie-la telle quelle"
        when(complaintRepository.save(any(Complaint.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // WHEN : j'exécute la méthode que je veux tester
        Complaint result = complaintService.createFromText("0600000000", "Ma connexion est lente");

        // THEN : je vérifie le résultat
        assertThat(result).isNotNull();
        assertThat(result.getRawText()).isEqualTo("Ma connexion est lente");
        assertThat(result.getStatus()).isEqualTo(ComplaintStatus.RECEIVED);

        // je vérifie que save() a bien été appelé une seule fois
        verify(complaintRepository, times(1)).save(any(Complaint.class));
    }
}
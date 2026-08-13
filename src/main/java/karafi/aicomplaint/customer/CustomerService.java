package karafi.aicomplaint.customer;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CustomerService {

    private final CustomerRepository customerRepository;


    public Customer getOrCreate(
            String phoneNumber
    ) {

        return customerRepository
                .findByPhoneNumber(phoneNumber)
                .orElseGet(() -> {
                    Customer customer = Customer.builder().phoneNumber(phoneNumber).build();
                    return customerRepository.save(customer);
                });
    }


    @Transactional(readOnly = true)
    public Customer getById(Long id) {

        return customerRepository
                .findById(id)
                .orElseThrow(
                        () -> new EntityNotFoundException(
                                "Customer not found with id: " + id
                        )
                );
    }
}
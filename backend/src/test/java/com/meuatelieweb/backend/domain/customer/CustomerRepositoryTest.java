package com.meuatelieweb.backend.domain.customer;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static com.meuatelieweb.backend.domain.customer.CustomerCreator.createValidCustomer;
import static org.junit.jupiter.api.Assertions.assertFalse;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("Tests for Customer Repository")
class CustomerRepositoryTest {

    @Autowired
    private CustomerRepository customerRepository;

    @Test
    @DisplayName("inactivateCustomerById inactivates customer when successful")
    void inactivateCustomerById_InactivatesCustomer_WhenSuccessful() {
        Customer savedCustomer = this.customerRepository.save(createValidCustomer());

        customerRepository.inactivateCustomerById(savedCustomer.getId());

        Customer inactivadedCustomer = customerRepository.findById(savedCustomer.getId()).get();

        assertFalse(inactivadedCustomer.getIsActive());
    }
}

package com.meuatelieweb.backend.domain.customer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("Tests for Customer Specification")
class CustomerSpecificationTest {

    @Autowired
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerSpecification customerSpecification;

    private List<Customer> customers;

    @BeforeEach
    public void setUpDatabase() {
        this.customers = List.of(
                Customer.builder()
                        .id(UUID.randomUUID())
                        .name("Ada LoveLace")
                        .email("ada@lovelace.com")
                        .isActive(true)
                        .build(),
                Customer.builder()
                        .id(UUID.randomUUID())
                        .name("Ada Love")
                        .email("ada@love.com")
                        .isActive(true)
                        .build(),
                Customer.builder()
                        .id(UUID.randomUUID())
                        .name("Ada Lace")
                        .email("ada@lace.com")
                        .phone("00100110100")
                        .isActive(false)
                        .build()
        );

        this.customers.forEach(customerRepository::save);
    }

    @Test
    @DisplayName("applyFilter returns page of customers filtered by name when successful")
    void applyFilter_ReturnsPageOfCustomersFilteredByName_WhenSuccessful() {

        Specification<Customer> specification = CustomerSpecification
                .applyFilter("Ada L", null, null, null);

        PageImpl<Customer> page = new PageImpl<>(this.customers, Pageable.ofSize(20), 20);

        List<Customer> result = customerRepository.findAll(specification, page.getPageable()).toList();

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(3, result.size());
        assertTrue(result.containsAll(this.customers));
    }

    @Test
    @DisplayName("applyFilter returns page of customers filtered by email when successful")
    void applyFilter_ReturnsPageOfCustomersFilteredByEmail_WhenSuccessful() {

        Specification<Customer> specification = CustomerSpecification
                .applyFilter(null, "ada@lovelace.com", null, null);

        PageImpl<Customer> page = new PageImpl<>(this.customers, Pageable.ofSize(20), 20);

        List<Customer> result = customerRepository.findAll(specification, page.getPageable()).toList();

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertTrue(result.contains(this.customers.get(0)));
    }

    @Test
    @DisplayName("applyFilter returns page of customers filtered by phone when successful")
    void applyFilter_ReturnsPageOfCustomersFilteredByPhone_WhenSuccessful() {

        Specification<Customer> specification = CustomerSpecification
                .applyFilter(null, null, "00100110100", null);

        PageImpl<Customer> page = new PageImpl<>(this.customers, Pageable.ofSize(20), 20);

        List<Customer> result = customerRepository.findAll(specification, page.getPageable()).toList();

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertTrue(result.contains(this.customers.get(2)));
    }

    @Test
    @DisplayName("applyFilter returns page of customers filtered by isActive when successful")
    void applyFilter_ReturnsPageOfCustomersFilteredByIsActive_WhenSuccessful() {

        Specification<Customer> specification = CustomerSpecification
                .applyFilter(null, null, null, false);

        PageImpl<Customer> page = new PageImpl<>(this.customers, Pageable.ofSize(20), 20);

        List<Customer> result = customerRepository.findAll(specification, page.getPageable()).toList();

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertTrue(result.contains(this.customers.get(2)));
    }

}

package com.meuatelieweb.backend.domain.order;

import com.meuatelieweb.backend.domain.customer.Customer;
import com.meuatelieweb.backend.domain.customer.CustomerRepository;
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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("Tests for Order Specification")
class OrderSpecificationTest {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @InjectMocks
    private OrderSpecification orderSpecification;

    private List<Customer> customers;

    private List<Order> orders;

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
                        .name("Ada Lace")
                        .email("ada@lace.com")
                        .isActive(true)
                        .build()
        );

        this.customers.forEach(customerRepository::save);

        this.orders = List.of(
                Order.builder()
                        .id(UUID.randomUUID())
                        .orderNumber(12345)
                        .createdAt(LocalDateTime.now().toLocalDate().atStartOfDay().minusDays(10))
                        .updatedAt(null)
                        .finishedAt(LocalDateTime.now().toLocalDate().atStartOfDay().minusDays(5))
                        .customer(customers.get(0))
                        .orderItems(new ArrayList<>())
                        .isActive(true)
                        .build(),
                Order.builder()
                        .id(UUID.randomUUID())
                        .orderNumber(67890)
                        .createdAt(LocalDateTime.now().minusDays(5))
                        .updatedAt(null)
                        .finishedAt(null)
                        .customer(customers.get(1))
                        .orderItems(new ArrayList<>())
                        .isActive(true)
                        .build(),
                Order.builder()
                        .id(UUID.randomUUID())
                        .orderNumber(98765)
                        .createdAt(LocalDateTime.now())
                        .updatedAt(null)
                        .finishedAt(null)
                        .customer(customers.get(1))
                        .orderItems(new ArrayList<>())
                        .isActive(false)
                        .build()
        );

        this.orders.forEach(orderRepository::save);
    }

    @Test
    @DisplayName("applyFilter returns page of orders filtered by order number when successful")
    void applyFilter_ReturnsPageOfOrdersFilteredByOrderNumber_WhenSuccessful() {

        Specification<Order> specification = OrderSpecification
                .applyFilter(
                        12345,
                        null,
                        null,
                        null,
                        null,
                        null
                );

        PageImpl<Order> page = new PageImpl<>(this.orders, Pageable.ofSize(20), 20);

        List<Order> result = orderRepository.findAll(specification, page.getPageable()).toList();

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("applyFilter returns page of orders filtered by creation date when successful")
    void applyFilter_ReturnsPageOfOrdersFilteredByCreationDate_WhenSuccessful() {

        Specification<Order> specification = OrderSpecification
                .applyFilter(
                        null,
                        LocalDateTime.now().toLocalDate().atStartOfDay().minusDays(10),
                        null,
                        null,
                        null,
                        null
                );

        PageImpl<Order> page = new PageImpl<>(this.orders, Pageable.ofSize(20), 20);

        List<Order> result = orderRepository.findAll(specification, page.getPageable()).toList();

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("applyFilter returns page of orders filtered by finish date when successful")
    void applyFilter_ReturnsPageOfOrdersFilteredByFinishDate_WhenSuccessful() {

        Specification<Order> specification = OrderSpecification
                .applyFilter(
                        null,
                        null,
                        LocalDateTime.now().toLocalDate().atStartOfDay().minusDays(5),
                        null,
                        null,
                        null
                );

        PageImpl<Order> page = new PageImpl<>(this.orders, Pageable.ofSize(20), 20);

        List<Order> result = orderRepository.findAll(specification, page.getPageable()).toList();

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("applyFilter returns page of orders filtered by customer name when successful")
    void applyFilter_ReturnsPageOfOrdersFilteredByCustomerName_WhenSuccessful() {

        Specification<Order> specification = OrderSpecification
                .applyFilter(
                        null,
                        null,
                        null,
                        "Ada Lace",
                        null,
                        null
                );

        PageImpl<Order> page = new PageImpl<>(this.orders, Pageable.ofSize(20), 20);

        List<Order> result = orderRepository.findAll(specification, page.getPageable()).toList();

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("applyFilter returns page of orders filtered by customer email when successful")
    void applyFilter_ReturnsPageOfOrdersFilteredByCustomerEmail_WhenSuccessful() {

        Specification<Order> specification = OrderSpecification
                .applyFilter(
                        null,
                        null,
                        null,
                        null,
                        "ada@lovelace.com",
                        null
                );

        PageImpl<Order> page = new PageImpl<>(this.orders, Pageable.ofSize(20), 20);

        List<Order> result = orderRepository.findAll(specification, page.getPageable()).toList();

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("applyFilter returns page of orders filtered by isActive when successful")
    void applyFilter_ReturnsPageOfOrdersFilteredByIsActive_WhenSuccessful() {

        Specification<Order> specification = OrderSpecification
                .applyFilter(
                        null,
                        null,
                        null,
                        null,
                        null,
                        false
                );

        PageImpl<Order> page = new PageImpl<>(this.orders, Pageable.ofSize(20), 20);

        List<Order> result = orderRepository.findAll(specification, page.getPageable()).toList();

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }
}

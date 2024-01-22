package com.meuatelieweb.backend.domain.customer;

import com.meuatelieweb.backend.domain.customer.dto.SaveCustomerDTO;
import com.meuatelieweb.backend.domain.customer.dto.UpdateCustomerDTO;
import jakarta.persistence.EntityNotFoundException;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class CustomerService {
    @Autowired
    private CustomerRepository repository;

    public Page<Customer> findAll(Pageable pageable, String name, String email, String phone, Boolean isActive) {
        Specification<Customer> specification = CustomerSpecification.applyFilter(name, email, phone, isActive);

        return repository.findAll(specification, pageable);
    }

    public Customer findById(@NonNull UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("The given customer does not exist"));
    }

    @Transactional
    public Customer addCustomer(
            @NonNull
            SaveCustomerDTO saveCustomerDTO
    ) {
        this.validateSavingCustomerDTO(saveCustomerDTO);

        Customer customer = Customer.builder()
                .name(saveCustomerDTO.getName())
                .email(saveCustomerDTO.getEmail())
                .phone(saveCustomerDTO.getPhone())
                .build();

        return repository.save(customer);
    }

    private void validateSavingCustomerDTO(SaveCustomerDTO saveCustomerDTO) {
        if (saveCustomerDTO.getName() == null) {
            throw new IllegalArgumentException("The given name cannot be empty");
        }

        if (saveCustomerDTO.getEmail() == null) {
            throw new IllegalArgumentException("The given email cannot be empty");
        }

        if (repository.existsByEmail(saveCustomerDTO.getEmail())) {
            throw new DuplicateKeyException("The given email is already being used by another customer");
        }

        if (saveCustomerDTO.getPhone() != null) {
            this.validateCustomerPhoneSize(saveCustomerDTO.getPhone());

            if (repository.existsByPhone(saveCustomerDTO.getPhone())) {
                throw new DuplicateKeyException("The given phone is already being used by another customer");
            }
        }
    }

    @Transactional
    public Customer updateCustomer(
            @NonNull
            UUID id,
            @NonNull
            UpdateCustomerDTO updateCustomerDTO) {

        Customer customer = repository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new EntityNotFoundException("The given customer does not exist or is already inactive"));

        if (updateCustomerDTO.getName() != null) {
            customer.setName(updateCustomerDTO.getName());
        }
        if (updateCustomerDTO.getPhone() != null) {

            this.validateCustomerPhoneSize(updateCustomerDTO.getPhone());

            if (repository.existsByPhoneAndIdNot(updateCustomerDTO.getPhone(), customer.getId())) {
                throw new DuplicateKeyException("The given phone is already being used by another customer");
            }
            customer.setPhone(updateCustomerDTO.getPhone());
        }
        return repository.save(customer);
    }

    private void validateCustomerPhoneSize(String phone) {
        if (phone.length() != 11) {
            throw new IllegalArgumentException("The given phone does not have the correct character size");
        }
    }

    @Transactional
    public void deleteCustomer(@NonNull UUID id) {
        if (!repository.existsByIdAndIsActiveTrue(id)) {
            throw new EntityNotFoundException("The given customer does not exist or is already inactive");
        }
        repository.inactivateCustomerById(id);
    }
}
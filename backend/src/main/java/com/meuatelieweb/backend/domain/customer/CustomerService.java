package com.meuatelieweb.backend.domain.customer;

import com.meuatelieweb.backend.domain.customer.dto.CustomerDTO;
import com.meuatelieweb.backend.domain.customer.dto.SaveCustomerDTO;
import com.meuatelieweb.backend.domain.customer.dto.UpdateCustomerDTO;
import jakarta.persistence.EntityNotFoundException;
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

    @Autowired
    private CustomerConverter converter;

    public Page<CustomerDTO> findAll(Pageable pageable, String name, String email, String phone, Boolean isActive) {
        Specification<Customer> specification = CustomerSpecification.applyFilter(name, email, phone, isActive);

        return repository.findAll(specification, pageable).map(converter::toCustomerDTO);
    }

    public CustomerDTO findById(UUID id) {
        return repository.findById(id)
                .map(converter::toCustomerDTO)
                .orElseThrow(() -> new EntityNotFoundException("The given user does not exist"));
    }

    @Transactional
    public CustomerDTO addCustomer(SaveCustomerDTO saveCustomerDTO) {

        if (saveCustomerDTO == null) {
            throw new IllegalArgumentException("Customer cannot be null");
        }

        this.validateSavingCustomerDTO(saveCustomerDTO);

        Customer customer = Customer.builder()
                .name(saveCustomerDTO.getName())
                .email(saveCustomerDTO.getEmail())
                .phone(saveCustomerDTO.getPhone())
                .isActive(true)
                .build();

        return converter.toCustomerDTO(repository.save(customer));
    }

    private void validateSavingCustomerDTO(SaveCustomerDTO saveCustomerDTO) {
        if (saveCustomerDTO.getName() == null) {
            throw new IllegalArgumentException("The given name cannot be empty");
        }

        if (saveCustomerDTO.getEmail() == null) {
            throw new IllegalArgumentException("The given email cannot be empty");
        }

        if (repository.existsByEmail(saveCustomerDTO.getEmail())) {
            throw new DuplicateKeyException("The given email is already being used by another user");
        }

        if (saveCustomerDTO.getPhone() != null) {
            this.validateCustomerPhoneSize(saveCustomerDTO.getPhone());

            if (repository.existsByPhone(saveCustomerDTO.getPhone())) {
                throw new DuplicateKeyException("The given phone is already being used by another user");
            }
        }
    }

    @Transactional
    public CustomerDTO updateCustomer(UUID id, UpdateCustomerDTO updateCustomerDTO) {

        Customer customer = repository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new EntityNotFoundException("The given user does not exist or is already inactive"));

        if (updateCustomerDTO.getName() != null) {
            customer.setName(updateCustomerDTO.getName());
        }
        if (updateCustomerDTO.getPhone() != null) {

            this.validateCustomerPhoneSize(updateCustomerDTO.getPhone());

            if (repository.existsByPhoneAndIdNot(updateCustomerDTO.getPhone(), customer.getId()) ) {
                throw new DuplicateKeyException("The given phone is already being used by another user");
            }
            customer.setPhone(updateCustomerDTO.getPhone());
        }
        return converter.toCustomerDTO(repository.save(customer));
    }

    private void validateCustomerPhoneSize(String phone) {
        if (phone.length() != 11) {
            throw new IllegalArgumentException("The given phone does not have the correct character size");
        }
    }

    @Transactional
    public void deleteCustomer(UUID id) {
        if (!repository.existsByIdAndIsActiveTrue(id)) {
            throw new EntityNotFoundException("The given user does not exist or is already inactive");
        }
        repository.inactivateCustomerById(id);
    }
}
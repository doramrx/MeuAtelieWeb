package com.meuatelieweb.backend.domain.customer;

import com.meuatelieweb.backend.domain.customer.dto.SaveCustomerDTO;
import com.meuatelieweb.backend.domain.customer.dto.UpdateCustomerDTO;
import jakarta.persistence.EntityNotFoundException;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;
import java.util.UUID;

@Service
public class CustomerService {
    @Autowired
    private CustomerRepository repository;

    @Autowired
    private MessageSource messageSource;

    public Page<Customer> findAll(Pageable pageable, String name, String email, String phone, Boolean isActive) {
        Specification<Customer> specification = CustomerSpecification.applyFilter(name, email, phone, isActive);

        return repository.findAll(specification, pageable);
    }

    public Customer findById(@NonNull UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                                this.getMessage("customer.error.doesNotExist")
                        )
                );
    }

    public Customer findByIdAndIsActiveTrue(@NonNull UUID id) {
        return repository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new EntityNotFoundException(
                                this.getMessage("customer.error.inactiveCustomer")
                        )
                );
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
            throw new IllegalArgumentException(
                    this.getMessage("shared.error.emptyName")
            );
        }

        if (saveCustomerDTO.getEmail() == null) {
            throw new IllegalArgumentException(
                    this.getMessage("customer.error.emptyEmail")
            );
        }

        if (repository.existsByEmail(saveCustomerDTO.getEmail())) {
            throw new DuplicateKeyException(
                    this.getMessage("customer.error.emailAlreadyInUse")
            );
        }

        if (saveCustomerDTO.getPhone() != null) {
            this.validateCustomerPhoneSize(saveCustomerDTO.getPhone());

            if (repository.existsByPhone(saveCustomerDTO.getPhone())) {
                throw new DuplicateKeyException(
                        this.getMessage("customer.error.phoneAlreadyInUse")
                );
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
                .orElseThrow(() -> new EntityNotFoundException(
                        this.getMessage("customer.error.customerDoesNotExistOrIsInactive")
                ));

        if (updateCustomerDTO.getName() != null) {
            customer.setName(updateCustomerDTO.getName());
        }
        if (updateCustomerDTO.getPhone() != null) {

            this.validateCustomerPhoneSize(updateCustomerDTO.getPhone());

            if (repository.existsByPhoneAndIdNot(updateCustomerDTO.getPhone(), customer.getId())) {
                throw new DuplicateKeyException(
                        this.getMessage("customer.error.phoneAlreadyInUse")
                );
            }
            customer.setPhone(updateCustomerDTO.getPhone());
        }
        return repository.save(customer);
    }

    private void validateCustomerPhoneSize(String phone) {
        if (phone.length() != 11) {
            throw new IllegalArgumentException(
                    this.getMessage("customer.error.invalidPhonePattern")
            );
        }
    }

    @Transactional
    public void deleteCustomer(@NonNull UUID id) {
        if (!repository.existsByIdAndIsActiveTrue(id)) {
            throw new EntityNotFoundException(
                    this.getMessage("customer.error.customerDoesNotExistOrIsInactive")
            );
        }
        repository.inactivateCustomerById(id);
    }

    private String getMessage(String key) {
        return messageSource.getMessage(key, null, Locale.getDefault());
    }
}
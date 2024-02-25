import {
  CustomerService,
  CustomerPage,
  QueryParams,
  SaveCustomerDTO,
  UpdateCustomerDTO,
} from './../../services/customer.service';

import { CommonModule } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit, inject } from '@angular/core';

import { PaginatorModule, PaginatorState } from 'primeng/paginator';
import { ToastModule } from 'primeng/toast';
import { Message, MessageService } from 'primeng/api';

import { HeaderComponent } from '../../shared/components/header/header.component';
import { PhonePipe } from '../../shared/pipes/phone.pipe';
import { ButtonComponent } from '../../shared/components/button/button.component';

import { AvailableFilters, FilterComponent } from './components/filter/filter.component';
import { AddCustomerData, AddCustomerDialogComponent } from './components/add-customer-dialog/add-customer-dialog.component';
import { UpdateCustomerData, UpdateCustomerDialogComponent } from './components/update-customer-dialog/update-customer-dialog.component';
import { InactivateCustomerDialogComponent } from './components/inactivate-customer-dialog/inactivate-customer-dialog.component';

@Component({
  selector: 'app-customers',
  standalone: true,
  imports: [
    CommonModule,
    HeaderComponent,
    FilterComponent,
    PaginatorModule,
    PhonePipe,
    ToastModule,
    ButtonComponent,
    AddCustomerDialogComponent,
    UpdateCustomerDialogComponent,
    InactivateCustomerDialogComponent
  ],
  providers: [MessageService],
  templateUrl: './customers.component.html',
  styleUrl: './customers.component.css',
})
export class CustomersComponent implements OnInit {
  private customerService: CustomerService = inject(CustomerService);
  private messageService: MessageService = inject(MessageService);

  private _activeFilters: AvailableFilters;
  private _customerPage?: CustomerPage;
  private _currentPage: number;

  public activeModal: AvailableModalsType | null;
  public customerId?: string;

  constructor() {
    this._activeFilters = {
      name: null,
      email: null,
      phone: null,
      status: null
    };
    this._currentPage = 0;
    this.activeModal = null;
  }

  ngOnInit(): void {
    this.fetchCustomers();
  }

  existCustomers() {
    return this._customerPage && this._customerPage.content.length > 0;
  }

  getCustomers() {
    if (this._customerPage) {
      return this._customerPage.content;
    }
    return [];
  }

  onPageChange(paginatorState: PaginatorState) {
    this._currentPage = paginatorState.page || 0;
    this.fetchCustomers();
  }

  getPageSize() {
    if (this._customerPage) {
      return this._customerPage.pageable.pageSize;
    }
    return 0;
  }

  getTotalRecords() {
    if (this._customerPage) {
      return this._customerPage.totalElements;
    }
    return 0;
  }

  showToast(message: Message) {
    this.messageService.add(message);
  }

  applyFilters(applyedFilter: AvailableFilters) {
    this._activeFilters = applyedFilter;
    this.fetchCustomers();
  }

  private fetchCustomers() {
    const params: QueryParams = {
      page: this._currentPage,
      name: this._activeFilters.name,
      email: this._activeFilters.email,
      phone: this._activeFilters.phone,
      isActive: this._activeFilters.status,
    };

    this.customerService.findAll(params).subscribe({
      next: (customerPage) => {
        this._customerPage = customerPage;
      },
      error: (error: HttpErrorResponse) => {
        this.showToast({
          severity: 'error',
          summary: 'Erro',
          detail: error.error.details,
        });
      },
    });
  }

  isModalVisible(type: AvailableModalsType) {
    return this.activeModal !== null && this.activeModal === type
      ? true
      : false;
  }

  closeModal() {
    this.activeModal = null;
  }

  showAddCustomerDialog() {
    this.activeModal = 'ADD';
  }

  saveCustomer(customerData: AddCustomerData) {
    const saveCustomer: SaveCustomerDTO = {
      name: customerData.name,
      email: customerData.email,
      phone: customerData.phone,
    };

    this.customerService.addCustomer(saveCustomer).subscribe({
      next: () => {
        this.closeModal();
        this.showToast({
          severity: 'success',
          summary: 'Sucesso',
          detail: 'Cliente cadastrado com sucesso',
        });
        this.fetchCustomers();
      },
      error: (error: HttpErrorResponse) => {
        console.error(error);
        this.showToast({
          severity: 'error',
          summary: 'Erro',
          detail: error.error.details,
        });
      },
    });
  }

  showUpdateCustomerDialog(id: string) {
    this.activeModal = 'UPDATE';
    this.customerId = id;
  }

  updateCustomer(customerData: UpdateCustomerData) {
    if (!customerData.id) {
      this.showToast({
        severity: 'error',
        summary: 'Erro',
        detail: 'Não foi possível atualizar os dados do cliente',
      });
      return;
    }

    const updateCustomer: UpdateCustomerDTO = {
      name: customerData.name || '',
      phone: customerData.phone,
    };

    this.customerService
      .updateCustomer(customerData.id, updateCustomer)
      .subscribe({
        next: () => {
          this.closeModal();
          this.showToast({
            severity: 'success',
            summary: 'Sucesso',
            detail: 'Cliente atualizado com sucesso',
          });
          this.fetchCustomers();
        },
        error: (error: HttpErrorResponse) => {
          this.showToast({
            severity: 'error',
            summary: 'Erro',
            detail: error.error.details,
          });
        },
      });
  }

  showDeleteCustomerDialog(id: string) {
    this.activeModal = 'DELETE';
    this.customerId = id;
  }

  deleteCustomer() {
    if (!this.customerId) {
      this.showToast({
        severity: 'error',
        summary: 'Erro',
        detail: 'Não foi possível inativar o cliente',
      });
      return;
    }

    this.customerService
      .deleteCustomer(this.customerId)
      .subscribe({
        next: () => {
          this.closeModal();
          this.showToast({
            severity: 'success',
            summary: 'Sucesso',
            detail: 'Cliente inativado com sucesso',
          });
          this.fetchCustomers();
        },
        error: (error: HttpErrorResponse) => {
          this.showToast({
            severity: 'error',
            summary: 'Erro',
            detail: error.error.details,
          });
        },
      });
  }
}

enum AvailableModals {
  ADD,
  UPDATE,
  DELETE,
}

type AvailableModalsType = keyof typeof AvailableModals;

import {
  CustomerPage,
  QueryParams,
  SaveCustomerDTO,
  UpdateCustomerDTO,
} from './../../services/customer.service';
import { CommonModule } from '@angular/common';
import { Component, EventEmitter, OnInit, inject } from '@angular/core';
import {
  FormControl,
  FormsModule,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';

import { RadioButtonModule } from 'primeng/radiobutton';
import { InputMaskModule } from 'primeng/inputmask';
import { ButtonModule } from 'primeng/button';
import { PaginatorModule, PaginatorState } from 'primeng/paginator';
import { DialogModule } from 'primeng/dialog';
import { ToastModule } from 'primeng/toast';

import { HeaderComponent } from '../../shared/components/header/header.component';
import { CustomerService } from '../../services/customer.service';
import { PhonePipe } from '../../shared/pipes/phone.pipe';
import { Message, MessageService } from 'primeng/api';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
  selector: 'app-customers',
  standalone: true,
  imports: [
    CommonModule,
    HeaderComponent,
    ReactiveFormsModule,
    RadioButtonModule,
    FormsModule,
    InputMaskModule,
    ButtonModule,
    PaginatorModule,
    PhonePipe,
    DialogModule,
    ToastModule,
  ],
  providers: [MessageService],
  templateUrl: './customers.component.html',
  styleUrl: './customers.component.css',
})
export class CustomersComponent implements OnInit {
  private customerService: CustomerService = inject(CustomerService);
  private messageService: MessageService = inject(MessageService);

  private _customerStatus: CustomerStatus[];
  private _filterFormGroup: FormGroup<FilterFormGroupFields>;
  private _addFormGroup: FormGroup<AddFormGroupFields>;
  private _updateFormGroup: FormGroup<UpdateFormGroupFields>;
  private _customerPage?: CustomerPage;
  private _currentPage: number;

  public activeModal: AvailableModalsType | null;
  public customerId?: string;

  private _deleteCustomerEvent: EventEmitter<void>;

  constructor() {
    this._filterFormGroup = new FormGroup<FilterFormGroupFields>({
      status: new FormControl(null),
      name: new FormControl(null),
      email: new FormControl(null),
      phone: new FormControl(null),
    });
    this._addFormGroup = new FormGroup<AddFormGroupFields>({
      name: new FormControl(null, [Validators.required]),
      email: new FormControl(null, [Validators.required, Validators.email]),
      phone: new FormControl(null),
    });
    this._updateFormGroup = new FormGroup<UpdateFormGroupFields>({
      id: new FormControl(null),
      name: new FormControl(null, [Validators.required]),
      phone: new FormControl(null),
    });
    this._customerStatus = [
      { text: 'Todos', key: null },
      { text: 'Ativos', key: true },
      { text: 'Inativos', key: false },
    ];
    this._currentPage = 0;
    this.activeModal = null;

    this._deleteCustomerEvent = new EventEmitter();
  }

  get filterFormGroup() {
    return this._filterFormGroup;
  }

  get addFormGroup() {
    return this._addFormGroup;
  }

  get updateFormGroup() {
    return this._updateFormGroup;
  }

  get customerStatus() {
    return this._customerStatus;
  }

  ngOnInit(): void {
    this.applyValidatorToCustomerPhone(this._addFormGroup);
    this.applyValidatorToCustomerPhone(this._updateFormGroup);
    this.fetchCustomers();
  }

  getTotalRecords() {
    if (this._customerPage) {
      return this._customerPage.totalElements;
    }
    return 0;
  }

  getPageSize() {
    if (this._customerPage) {
      return this._customerPage.pageable.pageSize;
    }
    return 0;
  }

  getCustomers() {
    if (this._customerPage) {
      return this._customerPage.content;
    }
    return [];
  }

  findById(id: any) {
    this.customerService.findById(id);
  }

  onPageChange(paginatorState: PaginatorState) {
    this._currentPage = paginatorState.page || 0;
    this.fetchCustomers();
  }

  applyFilters() {
    this.fetchCustomers();
  }

  showAddCustomerDialog() {
    this.activeModal = 'ADD';
  }

  showUpdateCustomerDialog(id: string) {
    this.activeModal = 'UPDATE';
    this.fetchCustomer(id);
  }

  showDeleteCustomerDialog(id: string) {
    this.activeModal = 'DELETE';

    this._deleteCustomerEvent.subscribe(
      () => {
        this.deleteCustomer(id);
      },
      (error) => { }
    );
  }

  notifyDeleteConfirmation() {
    this._deleteCustomerEvent.emit();
  }

  showToast(message: Message) {
    this.messageService.add(message);
  }

  saveCustomer() {
    let normalizedPhone = null;

    if (this._addFormGroup.value.phone) {
      normalizedPhone = this.normalizePhoneNumber(
        this._addFormGroup.value.phone
      );
    }

    const saveCustomer: SaveCustomerDTO = {
      name: this._addFormGroup.value.name || '',
      email: this._addFormGroup.value.email || '',
      phone: normalizedPhone,
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

  updateCustomer() {
    if (!this._updateFormGroup.value.id) {
      this.showToast({
        severity: 'error',
        summary: 'Erro',
        detail: 'Não foi possível atualizar os dados do cliente',
      });
      return;
    }

    let normalizedPhone = null;

    if (this._updateFormGroup.value.phone) {
      normalizedPhone = this.normalizePhoneNumber(
        this._updateFormGroup.value.phone
      );
    }

    const updateCustomer: UpdateCustomerDTO = {
      name: this._updateFormGroup.value.name || '',
      phone: normalizedPhone,
    };

    this.customerService
      .updateCustomer(this._updateFormGroup.value.id, updateCustomer)
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

  private deleteCustomer(id: string) {
    this.customerService
      .deleteCustomer(id)
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

  isModalVisible(type: AvailableModalsType) {
    return this.activeModal !== null && this.activeModal === type
      ? true
      : false;
  }

  closeModal() {
    this.activeModal = null;
  }

  existCustomers() {
    return this._customerPage && this._customerPage.content.length > 0;
  }

  private fetchCustomer(id: string) {
    this.customerService.findById(id).subscribe({
      next: (customerDTO) => {
        this._updateFormGroup.setValue({
          id: customerDTO.id,
          name: customerDTO.name,
          phone: customerDTO.phone,
        });
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

  private fetchCustomers() {
    let normalizedPhone = null;

    if (this._filterFormGroup.value.phone) {
      normalizedPhone = this.normalizePhoneNumber(
        this._filterFormGroup.value.phone
      );
    }

    const params: QueryParams = {
      page: this._currentPage,
      name: this._filterFormGroup.value.name || null,
      email: this._filterFormGroup.value.email || null,
      phone: normalizedPhone,
      isActive: this._filterFormGroup.value.status,
    };

    this.customerService.findAll(params).subscribe({
      next: (customerPage) => {
        this._customerPage = customerPage;
      },
      error: (error) => {
        console.error(error);
      },
    });
  }

  private normalizePhoneNumber(phone: string) {
    return phone.replace(/\D+/g, '');
  }

  private applyValidatorToCustomerPhone(formGroup: FormGroup<AddFormGroupFields> | FormGroup<UpdateFormGroupFields>) {
    formGroup.controls.phone.valueChanges.subscribe({
      next: (phone) => {
        console.log(phone);
        if (phone) {
          let normalizedPhone = this.normalizePhoneNumber(phone);
          console.log(normalizedPhone);

          if (normalizedPhone.length === 0) {
            formGroup.controls.phone.clearValidators();
            formGroup.controls.phone.setValue(null);
            return;
          }

          formGroup.controls.phone.setValidators(Validators.pattern(/^\(\d{2}\) \d \d{4}-\d{4}$/g));
        }
      },
      complete: () => {
        formGroup.controls.phone.updateValueAndValidity({ emitEvent: false, onlySelf: true });
      },
    });
  }
}

interface CustomerStatus {
  text: string;
  key: boolean | null;
}

interface FilterFormGroupFields {
  status: FormControl<boolean | null>;
  name: FormControl<string | null>;
  email: FormControl<string | null>;
  phone: FormControl<string | null>;
}

interface AddFormGroupFields {
  name: FormControl<string | null>;
  email: FormControl<string | null>;
  phone: FormControl<string | null>;
}

interface UpdateFormGroupFields {
  id: FormControl<string | null>;
  name: FormControl<string | null>;
  phone: FormControl<string | null>;
}

enum AvailableModals {
  ADD,
  UPDATE,
  DELETE,
}

type AvailableModalsType = keyof typeof AvailableModals;

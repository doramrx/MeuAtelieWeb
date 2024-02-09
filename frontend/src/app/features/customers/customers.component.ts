import {
  CustomerPage,
  QueryParams,
  SaveCustomerDTO,
} from './../../services/customer.service';
import { CommonModule } from '@angular/common';
import { Component, OnInit, inject } from '@angular/core';
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
  private _customerPage?: CustomerPage;
  private _currentPage: number;

  public visible: boolean = false;

  constructor() {
    this._filterFormGroup = new FormGroup<FilterFormGroupFields>({
      status: new FormControl(null),
      name: new FormControl(null),
      email: new FormControl(null),
      phone: new FormControl(null),
    });
    this._addFormGroup = new FormGroup<AddFormGroupFields>({
      name: new FormControl(null),
      email: new FormControl(null, [Validators.required, Validators.email]),
      phone: new FormControl(null),
    });
    this._customerStatus = [
      { text: 'Todos', key: null },
      { text: 'Ativos', key: true },
      { text: 'Inativos', key: false },
    ];
    this._currentPage = 0;
  }

  get filterFormGroup() {
    return this._filterFormGroup;
  }

  get addFormGroup() {
    return this._addFormGroup;
  }

  get customerStatus() {
    return this._customerStatus;
  }

  ngOnInit(): void {
    this.applyValidatorToCustomerPhone();
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

  onPageChange(paginatorState: PaginatorState) {
    this._currentPage = paginatorState.page || 0;
    this.fetchCustomers();
  }

  applyFilters() {
    this.fetchCustomers();
  }

  showDialog() {
    this.visible = true;
  }

  showToast(message: Message) {
    this.messageService.add(message);
  }

  saveCustomer() {
    let normalizedPhone = null;

    if (this._addFormGroup.value.phone) {
      normalizedPhone = this.normalizePhoneNumber(this._addFormGroup.value.phone);
    }

    const saveCustomer: SaveCustomerDTO = {
      name: this._addFormGroup.value.name || '',
      email: this._addFormGroup.value.email || '',
      phone: normalizedPhone,
    };

    this.customerService.addCustomer(saveCustomer).subscribe({
      next: () => {
        this.visible = false;
        this.showToast({
          severity: 'success',
          summary: 'Sucesso',
          detail: 'O cliente cadastrado com sucesso',
        });
      },
      error: (error: HttpErrorResponse) => {
        console.error(error);
        this.showToast({
          severity: 'error',
          summary: 'Erro',
          detail: error.error.details,
        });
      }
    });
  }

  private fetchCustomers() {
    let normalizedPhone = null;

    if (this._filterFormGroup.value.phone) {
      normalizedPhone = this.normalizePhoneNumber(this._filterFormGroup.value.phone);
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

  private applyValidatorToCustomerPhone() {
    this._addFormGroup.get('phone')?.valueChanges.subscribe({
      next: (phone) => {
        if (phone) {
          if (phone.length > 0) {
            this._addFormGroup
              .get('phone')
              ?.setValidators(
                Validators.pattern(/^\(\d{2}\) \d \d{4}-\d{4}$/g)
              );
            return;
          }
          this._addFormGroup.get('phone')?.clearValidators();
        }
      },
      complete: () => {
        this._addFormGroup
          .get('phone')
          ?.updateValueAndValidity({ emitEvent: false, onlySelf: true });
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

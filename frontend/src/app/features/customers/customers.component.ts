import { CustomerPage, QueryParams } from './../../services/customer.service';
import { CommonModule } from '@angular/common';
import { Component, OnInit, inject } from '@angular/core';
import {
  FormControl,
  FormsModule,
  FormGroup,
  ReactiveFormsModule,
} from '@angular/forms';

import { RadioButtonModule } from 'primeng/radiobutton';
import { InputMaskModule } from 'primeng/inputmask';
import { ButtonModule } from 'primeng/button';
import { PaginatorModule, PaginatorState } from 'primeng/paginator';

import { HeaderComponent } from '../../shared/components/header/header.component';
import { CustomerService } from '../../services/customer.service';
import { PhonePipe } from '../../shared/pipes/phone.pipe';

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
  ],
  templateUrl: './customers.component.html',
  styleUrl: './customers.component.css',
})
export class CustomersComponent implements OnInit {
  private customerService: CustomerService = inject(CustomerService);

  private _customerStatus: CustomerStatus[];
  private _filterFormGroup: FormGroup<FilterFormGroupFields>;
  private _customerPage?: CustomerPage;
  private _currentPage: number;

  constructor() {
    this._filterFormGroup = new FormGroup<FilterFormGroupFields>({
      status: new FormControl(null),
      name: new FormControl(null),
      email: new FormControl(null),
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

  get customerStatus() {
    return this._customerStatus;
  }

  ngOnInit(): void {
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

  private fetchCustomers() {
    let plainPhone = null;

    if (this._filterFormGroup.value.phone) {
      plainPhone = this._filterFormGroup.value.phone.replace(/\D+/g, '');
    }

    const params: QueryParams = {
      page: this._currentPage,
      name: this._filterFormGroup.value.name || null,
      email: this._filterFormGroup.value.email || null,
      phone: plainPhone,
      isActive: this._filterFormGroup.value.status
    };

    this.customerService.findAll(params).subscribe({
      next: (customerPage) => {
        this._customerPage = customerPage;
      },
      error: (error) => {
        console.error(error);
      }
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

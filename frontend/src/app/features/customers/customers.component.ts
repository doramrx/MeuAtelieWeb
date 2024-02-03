import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';

import { HeaderComponent } from '../../shared/components/header/header.component';
import { FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { RadioButtonModule } from 'primeng/radiobutton';

@Component({
  selector: 'app-customers',
  standalone: true,
  imports: [
    CommonModule,
    HeaderComponent,
    ReactiveFormsModule,
    RadioButtonModule,
  ],
  templateUrl: './customers.component.html',
  styleUrl: './customers.component.css',
})
export class CustomersComponent {
  private _customerStatus: CustomerStatus[];
  private _filterFormGroup: FormGroup;

  constructor() {
    this._filterFormGroup = new FormGroup({
      status: new FormControl(),
      name: new FormControl(),
      email: new FormControl(),
      phone: new FormControl(),
    });
    this._customerStatus = [
      { text: 'Todos', key: null },
      { text: 'Ativos', key: true },
      { text: 'Inativos', key: false },
    ];
  }

  get filterFormGroup() {
    return this._filterFormGroup;
  }

  get customerStatus() {
    return this._customerStatus;
  }
}

interface CustomerStatus {
  text: string;
  key: boolean | null;
}

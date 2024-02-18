import { Component, EventEmitter, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { InputMaskModule } from 'primeng/inputmask';
import { RadioButtonModule } from 'primeng/radiobutton';
import { ButtonComponent } from '../../../../shared/components/button/button.component';

@Component({
  selector: 'app-filter',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    InputMaskModule,
    RadioButtonModule,
    ButtonComponent,
  ],
  templateUrl: './filter.component.html',
  styleUrl: './filter.component.css',
})
export class FilterComponent {
  private _filterFormGroup: FormGroup<FilterFormGroupFields>;
  private _customerStatus: CustomerStatus[];

  @Output()
  public onApplyFiltersEvent: EventEmitter<AvailableFilters>;
  @Output()
  public onCleanFiltersEvent: EventEmitter<AvailableFilters>;

  constructor() {
    this.onApplyFiltersEvent = new EventEmitter();
    this.onCleanFiltersEvent = new EventEmitter();

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
  }

  get filterFormGroup() {
    return this._filterFormGroup;
  }

  get customerStatus() {
    return this._customerStatus;
  }

  notifyApplyFilter() {
    let normalizedPhone = null;

    if (this._filterFormGroup.value.phone) {
      normalizedPhone = this.normalizePhoneNumber(
        this._filterFormGroup.value.phone
      );
    }

    const status =
      typeof this._filterFormGroup.value.status === 'undefined'
        ? null
        : this._filterFormGroup.value.status;

    this.onApplyFiltersEvent.emit({
      name: this._filterFormGroup.value.name || null,
      phone: normalizedPhone,
      email: this._filterFormGroup.value.email || null,
      status,
    });
  }

  notifyCleanFilters() {
    this.onCleanFiltersEvent.emit({
      name: null,
      phone: null,
      email: null,
      status: null,
    });
  }

  private normalizePhoneNumber(phone: string) {
    return phone.replace(/\D+/g, '');
  }
}

export interface AvailableFilters {
  status: boolean | null;
  name: string | null;
  email: string | null;
  phone: string | null;
}

interface FilterFormGroupFields {
  status: FormControl<boolean | null>;
  name: FormControl<string | null>;
  email: FormControl<string | null>;
  phone: FormControl<string | null>;
}

interface CustomerStatus {
  text: string;
  key: boolean | null;
}

import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Output } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';

import { RadioButtonModule } from 'primeng/radiobutton';

import { ButtonComponent } from '../../../../shared/components/button/button.component';

@Component({
  selector: 'app-filter',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    RadioButtonModule,
    ButtonComponent,
  ],
  templateUrl: './filter.component.html',
  styleUrl: './filter.component.css'
})
export class FilterComponent {
  private _filterFormGroup: FormGroup<FilterFormGroupFields>;
  private _measureStatus: MeasureStatus[];

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
    });
    this._measureStatus = [
      { text: 'Todos', key: null },
      { text: 'Ativos', key: true },
      { text: 'Inativos', key: false },
    ]
  }

  get filterFormGroup() {
    return this._filterFormGroup;
  }

  get measureStatus() {
    return this._measureStatus;
  }

  notifyApplyFilter() {
    const status =
      typeof this._filterFormGroup.value.status === 'undefined'
        ? null
        : this._filterFormGroup.value.status;

    this.onApplyFiltersEvent.emit({
      name: this._filterFormGroup.value.name || null,
      status,
    });
  }

  notifyCleanFilters() {
    this.onCleanFiltersEvent.emit({
      name: null,
      status: null,
    });
  }
}

export interface AvailableFilters {
  status: boolean | null;
  name: string | null;
}

interface FilterFormGroupFields {
  status: FormControl<boolean | null>;
  name: FormControl<string | null>;
}

interface MeasureStatus {
  text: string;
  key: boolean | null;
}

import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Output } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { RadioButtonModule } from 'primeng/radiobutton';
import { ButtonComponent } from '../../../../shared/components/button/button.component';
import { InputMaskModule } from 'primeng/inputmask';

@Component({
  selector: 'app-filter',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    InputMaskModule,
    RadioButtonModule,
    ButtonComponent
  ],
  templateUrl: './filter.component.html',
  styleUrl: './filter.component.css'
})
export class FilterComponent {
  private _filterFormGroup: FormGroup<FilterFormGroupFields>;
  private _adjustStatus: AdjustStatus[];

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
    this._adjustStatus = [
      { text: 'Todos', key: null },
      { text: 'Ativos', key: true },
      { text: 'Inativos', key: false },
    ]
  }

  get filterFormGroup() {
    return this._filterFormGroup;
  }

  get adjustStatus() {
    return this._adjustStatus;
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

interface AdjustStatus {
  text: string;
  key: boolean | null;
}

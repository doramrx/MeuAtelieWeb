import { MeasureService } from '../../../../services/measure.service';

import { Component, EventEmitter, Input, OnChanges, Output, SimpleChanges, inject } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';

import { DialogModule } from 'primeng/dialog';
import { Message } from 'primeng/api';

import { InputComponent } from '../../../../shared/components/input/input.component';
import { ButtonComponent } from '../../../../shared/components/button/button.component';

@Component({
  selector: 'app-update-measure-dialog',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    DialogModule,
    InputComponent,
    ButtonComponent,
  ],
  templateUrl: './update-measure-dialog.component.html',
  styleUrls: [
    './update-measure-dialog.component.css',
    '/src/app/shared/styles/dialog.css',
  ]
})
export class UpdateMeasureDialogComponent implements OnChanges {
  private measureService: MeasureService = inject(MeasureService);
  private _updateFormGroup: FormGroup<UpdateFormGroupFields>;

  @Input()
  public isDialogVisible: boolean;

  @Input()
  public measureId?: string;

  @Output("onDialogVisibilityChange")
  public onDialogVisibilityChangeEvent = new EventEmitter();

  @Output('onUpdateMeasure')
  public onUpdateMeasureEvent: EventEmitter<UpdateMeasureData>;

  @Output('onFetchMeasureError')
  public onFetchMeasureErrorEvent: EventEmitter<Message>;

  constructor() {
    this._updateFormGroup = new FormGroup<UpdateFormGroupFields>({
      id: new FormControl(null),
      name: new FormControl(null, [Validators.required]),
    });

    this.onUpdateMeasureEvent = new EventEmitter();
    this.onDialogVisibilityChangeEvent = new EventEmitter();
    this.onFetchMeasureErrorEvent = new EventEmitter();

    this.isDialogVisible = false;
  }

  get updateFormGroup() {
    return this._updateFormGroup;
  }

  ngOnChanges(changes: SimpleChanges): void {
    const measureIdSimpleChange = changes['measureId'];

    if (measureIdSimpleChange && measureIdSimpleChange.currentValue) {
      this.fetchMeasure(measureIdSimpleChange.currentValue);
    }
  }

  private fetchMeasure(id: string) {
    this.measureService.findById(id).subscribe({
      next: (measureDTO) => {
        this._updateFormGroup.setValue({
          id: measureDTO.id,
          name: measureDTO.name,
        });
      },
      error: (error: HttpErrorResponse) => {
        this.onFetchMeasureErrorEvent.emit({
          severity: 'error',
          summary: 'Erro',
          detail: error.error.details,
        });
      },
    });
  }

  onCloseModal() {
    this.onDialogVisibilityChangeEvent.emit();
  }

  onUpdateMeasure() {
    this.onUpdateMeasureEvent.emit({
      id: this._updateFormGroup.value.id || null,
      name: this._updateFormGroup.value.name || null,
    });
  }
}

export interface UpdateMeasureData {
  id: string | null;
  name: string | null;
}

interface UpdateFormGroupFields {
  id: FormControl<string | null>;
  name: FormControl<string | null>;
}

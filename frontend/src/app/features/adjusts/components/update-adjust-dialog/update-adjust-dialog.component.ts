import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { AdjustService } from './../../../../services/adjust.service';
import { Component, EventEmitter, Input, OnChanges, Output, SimpleChanges, inject } from '@angular/core';
import { Message } from 'primeng/api';
import { HttpErrorResponse } from '@angular/common/http';
import { DialogModule } from 'primeng/dialog';
import { InputComponent } from '../../../../shared/components/input/input.component';
import { ButtonComponent } from '../../../../shared/components/button/button.component';

@Component({
  selector: 'app-update-adjust-dialog',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    DialogModule,
    InputComponent,
    ButtonComponent,
  ],
  templateUrl: './update-adjust-dialog.component.html',
  styleUrls: [
    './update-adjust-dialog.component.css',
    '/src/app/shared/styles/dialog.css',
  ]
})
export class UpdateAdjustDialogComponent implements OnChanges {
  private adjustService: AdjustService = inject(AdjustService);
  private _updateFormGroup: FormGroup<UpdateFormGroupFields>;

  @Input()
  public isDialogVisible: boolean;

  @Input()
  public adjustId?: string;

  @Output("onDialogVisibilityChange")
  public onDialogVisibilityChangeEvent = new EventEmitter();

  @Output('onUpdateAdjust')
  public onUpdateAdjustEvent: EventEmitter<UpdateAdjustData>;

  @Output('onFetchAdjustError')
  public onFetchAdjustErrorEvent: EventEmitter<Message>;

  constructor() {
    this._updateFormGroup = new FormGroup<UpdateFormGroupFields>({
      id: new FormControl(null),
      name: new FormControl(null, [Validators.required]),
      cost: new FormControl(null, [Validators.required]),
    });

    this.onUpdateAdjustEvent = new EventEmitter();
    this.onDialogVisibilityChangeEvent = new EventEmitter();
    this.onFetchAdjustErrorEvent = new EventEmitter();

    this.isDialogVisible = false;
  }

  get updateFormGroup() {
    return this._updateFormGroup;
  }

  ngOnChanges(changes: SimpleChanges): void {
    const adjustIdSimpleChange = changes['adjustId'];

    if (adjustIdSimpleChange && adjustIdSimpleChange.currentValue) {
      this.fetchAdjust(adjustIdSimpleChange.currentValue);
    }
  }

  private fetchAdjust(id: string) {
    this.adjustService.findById(id).subscribe({
      next: (adjustDTO) => {
        this._updateFormGroup.setValue({
          id: adjustDTO.id,
          name: adjustDTO.name,
          cost: adjustDTO.cost,
        });
      },
      error: (error: HttpErrorResponse) => {
        this.onFetchAdjustErrorEvent.emit({
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

  onUpdateAdjust() {
    this.onUpdateAdjustEvent.emit({
      id: this._updateFormGroup.value.id || null,
      name: this._updateFormGroup.value.name || null,
      cost: this.updateFormGroup.value.cost || null,
    });
  }
}

export interface UpdateAdjustData {
  id: string | null;
  name: string | null;
  cost: number | null;
}

interface UpdateFormGroupFields {
  id: FormControl<string | null>;
  name: FormControl<string | null>;
  cost: FormControl<number | null>;
}

import { Component, EventEmitter, Input, Output } from '@angular/core';
import {
  FormControl,
  FormGroup,
  Validators,
  ReactiveFormsModule
} from '@angular/forms';

import { DialogModule } from 'primeng/dialog';
import { InputNumberModule } from 'primeng/inputnumber';

import { InputComponent } from '../../../../shared/components/input/input.component';
import { ButtonComponent } from '../../../../shared/components/button/button.component';

@Component({
  selector: 'app-add-adjust-dialog',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    DialogModule,
    InputComponent,
    ButtonComponent,
    InputNumberModule,
  ],
  templateUrl: './add-adjust-dialog.component.html',
  styleUrls: [
    './add-adjust-dialog.component.css',
    '/src/app/shared/styles/dialog.css',
    '/src/app/shared/styles/input-number.css',
  ]
})
export class AddAdjustDialogComponent {
  private _addFormGroup: FormGroup<AddFormGroupFields>;

  @Input()
  public isDialogVisible: boolean;

  @Output("onDialogVisibilityChange")
  public onDialogVisibilityChangeEvent: EventEmitter<void>;

  @Output('onSaveAdjust')
  public onSaveAdjustEvent: EventEmitter<AddAdjustData>;

  constructor() {
    this._addFormGroup = new FormGroup<AddFormGroupFields>({
      name: new FormControl(null, [Validators.required]),
      cost: new FormControl(null, [Validators.required]),
    });
    this.onSaveAdjustEvent = new EventEmitter();
    this.onDialogVisibilityChangeEvent = new EventEmitter();

    this.isDialogVisible = false;
  }

  get addFormGroup() {
    return this._addFormGroup;
  }

  onCloseModal() {
    this.onDialogVisibilityChangeEvent.emit();
  }

  onSaveAdjust() {
    this.onSaveAdjustEvent.emit({
      name: this._addFormGroup.value.name || null,
      cost: this._addFormGroup.value.cost || null,
    });
  }
}

export interface AddAdjustData {
  name: string | null;
  cost: number | null;
}

interface AddFormGroupFields {
  name: FormControl<string | null>;
  cost: FormControl<number | null>;
}

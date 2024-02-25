import { Component, EventEmitter, Input, Output } from '@angular/core';
import {
  FormControl,
  FormGroup,
  Validators,
  ReactiveFormsModule,
} from '@angular/forms';

import { DialogModule } from 'primeng/dialog';

import { InputComponent } from '../../../../shared/components/input/input.component';
import { ButtonComponent } from '../../../../shared/components/button/button.component';

@Component({
  selector: 'app-add-measure-dialog',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    DialogModule,
    InputComponent,
    ButtonComponent,
  ],
  templateUrl: './add-measure-dialog.component.html',
  styleUrls: [
    './add-measure-dialog.component.css',
    '/src/app/shared/styles/dialog.css',
  ]
})
export class AddMeasureDialogComponent {
  private _addFormGroup: FormGroup<AddFormGroupFields>;

  @Input()
  public isDialogVisible: boolean;

  @Output("onDialogVisibilityChange")
  public onDialogVisibilityChangeEvent: EventEmitter<void>;

  @Output('onSaveMeasure')
  public onSaveMeasureEvent: EventEmitter<AddMeasureData>;

  constructor() {
    this._addFormGroup = new FormGroup<AddFormGroupFields>({
      name: new FormControl(null, [Validators.required]),
    });
    this.onSaveMeasureEvent = new EventEmitter();
    this.onDialogVisibilityChangeEvent = new EventEmitter();

    this.isDialogVisible = false;
  }

  get addFormGroup() {
    return this._addFormGroup;
  }

  onCloseModal() {
    this.onDialogVisibilityChangeEvent.emit();
  }

  onSaveMeasure() {
    this.onSaveMeasureEvent.emit({
      name: this._addFormGroup.value.name || null
    });
  }
}

export interface AddMeasureData {
  name: string | null;
}

interface AddFormGroupFields {
  name: FormControl<string | null>;
}

import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import {
  FormControl,
  FormGroup,
  Validators,
  ReactiveFormsModule,
} from '@angular/forms';

import { DialogModule } from 'primeng/dialog';
import { InputMaskModule } from 'primeng/inputmask';

import { normalizePhone } from '../../../../shared/utils/normalize-phone';
import { InputComponent } from '../../../../shared/components/input/input.component';
import { ButtonComponent } from '../../../../shared/components/button/button.component';

@Component({
  selector: 'app-add-customer-dialog',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    DialogModule,
    InputComponent,
    ButtonComponent,
    InputMaskModule,
  ],
  templateUrl: './add-customer-dialog.component.html',
  styleUrls: [
    './add-customer-dialog.component.css',
    '/src/app/shared/styles/dialog.css'
  ],
})
export class AddCustomerDialogComponent implements OnInit {
  private _addFormGroup: FormGroup<AddFormGroupFields>;

  @Input()
  public isDialogVisible: boolean;

  @Output("onDialogVisibilityChange")
  public onDialogVisibilityChangeEvent: EventEmitter<void>;

  @Output('onSaveCustomer')
  public onSaveCustomerEvent: EventEmitter<AddCustomerData>;

  constructor() {
    this._addFormGroup = new FormGroup<AddFormGroupFields>({
      name: new FormControl(null, [Validators.required]),
      email: new FormControl(null, [Validators.required, Validators.email]),
      phone: new FormControl(null),
    });
    this.onSaveCustomerEvent = new EventEmitter();
    this.onDialogVisibilityChangeEvent = new EventEmitter();

    this.isDialogVisible = false;
  }

  get addFormGroup() {
    return this._addFormGroup;
  }

  ngOnInit(): void {
    this.applyValidatorToCustomerPhone();
  }

  onSaveCustomer() {
    let normalizedPhoneNumber = null;

    if (this._addFormGroup.value.phone) {
      normalizedPhoneNumber = normalizePhone(this._addFormGroup.value.phone);
    }

    this.onSaveCustomerEvent.emit({
      name: this._addFormGroup.value.name || null,
      email: this._addFormGroup.value.email || null,
      phone: normalizedPhoneNumber,
    });
  }

  onCloseModal() {
    this.onDialogVisibilityChangeEvent.emit();
  }

  private applyValidatorToCustomerPhone() {
    this._addFormGroup.controls.phone.valueChanges.subscribe({
      next: (phone) => {
        if (phone) {
          let normalizedPhone = normalizePhone(phone);

          if (normalizedPhone.length === 0) {
            this._addFormGroup.controls.phone.clearValidators();
            this._addFormGroup.controls.phone.setValue(null);
            return;
          }

          this._addFormGroup.controls.phone.setValidators(Validators.pattern(/^\(\d{2}\) \d \d{4}-\d{4}$/g));
        }
      },
      complete: () => {
        this._addFormGroup.controls.phone.updateValueAndValidity({ emitEvent: false, onlySelf: true });
      },
    });
  }
}

export interface AddCustomerData {
  name: string | null;
  email: string | null;
  phone: string | null;
}

interface AddFormGroupFields {
  name: FormControl<string | null>;
  email: FormControl<string | null>;
  phone: FormControl<string | null>;
}

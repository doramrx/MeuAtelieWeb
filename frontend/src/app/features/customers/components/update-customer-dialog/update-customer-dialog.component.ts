import { CustomerService } from '../../../../services/customer.service';

import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Component, OnInit, EventEmitter, Input, OnChanges, Output, SimpleChanges, inject } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';

import { DialogModule } from 'primeng/dialog';
import { Message } from 'primeng/api';
import { InputMaskModule } from 'primeng/inputmask';

import { InputComponent } from '../../../../shared/components/input/input.component';
import { ButtonComponent } from '../../../../shared/components/button/button.component';
import { normalizePhone } from '../../../../shared/utils/normalize-phone';

@Component({
  selector: 'app-update-customer-dialog',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    DialogModule,
    InputComponent,
    ButtonComponent,
    InputMaskModule,
  ],
  templateUrl: './update-customer-dialog.component.html',
  styleUrls: [
    './update-customer-dialog.component.css',
    '/src/app/shared/styles/dialog.css'
  ]
})
export class UpdateCustomerDialogComponent implements OnInit, OnChanges {
  private customerService: CustomerService = inject(CustomerService);
  private _updateFormGroup: FormGroup<UpdateFormGroupFields>;

  @Input()
  public isDialogVisible: boolean;

  @Input()
  public customerId?: string;

  @Output('onDialogVisibilityChange')
  public onDialogVisibilityChangeEvent: EventEmitter<void>;

  @Output('onUpdateCustomer')
  public onUpdateCustomerEvent: EventEmitter<UpdateCustomerData>;

  @Output('onFetchCustomerError')
  public onFetchCustomerErrorEvent: EventEmitter<Message>;

  constructor() {
    this._updateFormGroup = new FormGroup<UpdateFormGroupFields>({
      id: new FormControl(null),
      name: new FormControl(null, [Validators.required]),
      phone: new FormControl(null),
    });

    this.onUpdateCustomerEvent = new EventEmitter();
    this.onDialogVisibilityChangeEvent = new EventEmitter();
    this.onFetchCustomerErrorEvent = new EventEmitter();

    this.isDialogVisible = false;
  }

  get updateFormGroup() {
    return this._updateFormGroup;
  }

  ngOnInit(): void {
    this.applyValidatorToCustomerPhone();
  }

  ngOnChanges(changes: SimpleChanges): void {
    const customerIdSimpleChange = changes['customerId'];

    if (customerIdSimpleChange && customerIdSimpleChange.currentValue) {
      this.fetchCustomer(customerIdSimpleChange.currentValue);
    }
  }

  onUpdateCustomer() {
    let normalizedPhoneNumber = null;

    if (this._updateFormGroup.value.phone) {
      normalizedPhoneNumber = normalizePhone(this._updateFormGroup.value.phone);
    }

    this.onUpdateCustomerEvent.emit({
      id: this._updateFormGroup.value.id || null,
      name: this._updateFormGroup.value.name || null,
      phone: normalizedPhoneNumber,
    });
  }

  onCloseModal() {
    this.onDialogVisibilityChangeEvent.emit();
  }

  private applyValidatorToCustomerPhone() {
    this._updateFormGroup.controls.phone.valueChanges.subscribe({
      next: (phone) => {
        if (phone) {
          let normalizedPhone = normalizePhone(phone);

          if (normalizedPhone.length === 0) {
            this._updateFormGroup.controls.phone.clearValidators();
            this._updateFormGroup.controls.phone.setValue(null);
            return;
          }

          this._updateFormGroup.controls.phone.setValidators(
            Validators.pattern(/^\(\d{2}\) \d \d{4}-\d{4}$/g)
          );
        }
      },
      complete: () => {
        this._updateFormGroup.controls.phone.updateValueAndValidity({
          emitEvent: false,
          onlySelf: true,
        });
      },
    });
  }

  private fetchCustomer(id: string) {
    this.customerService.findById(id).subscribe({
      next: (customerDTO) => {
        this._updateFormGroup.setValue({
          id: customerDTO.id,
          name: customerDTO.name,
          phone: customerDTO.phone,
        });
      },
      error: (error: HttpErrorResponse) => {
        this.onFetchCustomerErrorEvent.emit({
          severity: 'error',
          summary: 'Erro',
          detail: error.error.details,
        });
      },
    });
  }
}

export interface UpdateCustomerData {
  id: string | null;
  name: string | null;
  phone: string | null;
}

interface UpdateFormGroupFields {
  id: FormControl<string | null>;
  name: FormControl<string | null>;
  phone: FormControl<string | null>;
}

import { Component, EventEmitter, Input, Output } from '@angular/core';
import { ButtonComponent } from '../../../../shared/components/button/button.component';
import { DialogModule } from 'primeng/dialog';

@Component({
  selector: 'app-inactivate-customer-dialog',
  standalone: true,
  imports: [
    DialogModule,
    ButtonComponent
  ],
  templateUrl: './inactivate-customer-dialog.component.html',
  styleUrls: [
    './inactivate-customer-dialog.component.css',
    '/src/app/shared/styles/dialog.css'
  ]
})
export class InactivateCustomerDialogComponent {

  @Input()
  public isDialogVisible: boolean;

  @Output()
  public onDeleteConfirmationEvent: EventEmitter<void>;

  @Output("onDialogVisibilityChange")
  public onDialogVisibilityChangeEvent: EventEmitter<void>;

  constructor() {
    this.onDeleteConfirmationEvent = new EventEmitter();
    this.onDialogVisibilityChangeEvent = new EventEmitter();
    this.isDialogVisible = false;
  }

  onCloseModal() {
    this.onDialogVisibilityChangeEvent.emit();
  }

  onDeleteConfirmation() {
    this.onDeleteConfirmationEvent.emit();
  }

}

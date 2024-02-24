import { Component, EventEmitter, Input, Output } from '@angular/core';
import { ButtonComponent } from '../../../../shared/components/button/button.component';
import { DialogModule } from 'primeng/dialog';

@Component({
  selector: 'app-inactivate-adjust-dialog',
  standalone: true,
  imports: [
    DialogModule,
    ButtonComponent
  ],
  templateUrl: './inactivate-adjust-dialog.component.html',
  styleUrls: [
    './inactivate-adjust-dialog.component.css',
    '/src/app/shared/styles/dialog.css',
  ]
})
export class InactivateAdjustDialogComponent {

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

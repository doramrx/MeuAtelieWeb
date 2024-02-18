import { Component, Input } from '@angular/core';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';

@Component({
  selector: 'app-input',
  standalone: true,
  imports: [ReactiveFormsModule],
  templateUrl: './input.component.html',
  styleUrl: './input.component.css'
})
export class InputComponent {
  @Input()
  public formGroup!: FormGroup<any>;
  @Input()
  public inputId?: string;
  @Input()
  public inputLabel?: string;
  @Input()
  public inputType?: string;
  @Input()
  public inputFormControlName: string | null;

  constructor() {
    this.inputFormControlName = null;
  }
}

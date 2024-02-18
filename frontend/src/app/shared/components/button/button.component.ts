import { Component, EventEmitter, Input, Output } from '@angular/core';

@Component({
  selector: 'app-button',
  standalone: true,
  imports: [],
  templateUrl: './button.component.html',
  styleUrl: './button.component.css'
})
export class ButtonComponent {

  @Input()
  public buttonText?: string;

  @Output("onClick")
  public onClickEvent: EventEmitter<void>;

  @Input()
  public type?: string;

  @Input()
  public disabled: boolean;

  @Input()
  public buttonClass?: string;

  constructor() {
    this.onClickEvent = new EventEmitter();
    this.disabled = false;
  }

  onClick() {
    this.onClickEvent.emit();
  }

}

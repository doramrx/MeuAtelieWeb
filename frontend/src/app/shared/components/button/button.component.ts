import { Component, Input } from '@angular/core';

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

  @Input()
  public onClick: () => void;

  @Input()
  public type?: string;

  constructor() {
    this.onClick = () => { };
  }

}

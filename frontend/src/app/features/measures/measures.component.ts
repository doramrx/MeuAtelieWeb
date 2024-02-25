import { Component } from '@angular/core';
import { HeaderComponent } from '../../shared/components/header/header.component';

@Component({
  selector: 'app-measures',
  standalone: true,
  imports: [
    HeaderComponent
  ],
  templateUrl: './measures.component.html',
  styleUrl: './measures.component.css'
})
export class MeasuresComponent {

}

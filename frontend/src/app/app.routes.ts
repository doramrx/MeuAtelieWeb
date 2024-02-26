import { Routes } from '@angular/router';
import { CustomersComponent } from './features/customers/customers.component';
import { AdjustsComponent } from './features/adjusts/adjusts.component';
import { MeasuresComponent } from './features/measures/measures.component';

export const routes: Routes = [
  {
    path: 'customers',
    component: CustomersComponent
  },
  {
    path: 'adjusts',
    component: AdjustsComponent
  },
  {
    path: 'measures',
    component: MeasuresComponent
  },
];

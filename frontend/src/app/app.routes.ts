import { Routes } from '@angular/router';
import { CustomersComponent } from './features/customers/customers.component';
import { AdjustsComponent } from './features/adjusts/adjusts.component';

export const routes: Routes = [
  {
    path: 'customers',
    component: CustomersComponent
  },
  {
    path: 'adjusts',
    component: AdjustsComponent
  }
];

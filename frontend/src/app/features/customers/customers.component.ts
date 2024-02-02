import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-customers',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './customers.component.html',
  styleUrl: './customers.component.css',
})
export class CustomersComponent {
  private _selectedMenuOption: MenuOptions;
  private _isHamburgerMenuExpanded: boolean;

  constructor() {
    this._selectedMenuOption = 'ORDERS';
    this._isHamburgerMenuExpanded = false;
  }

  get selectedMenuOption() {
    return this._selectedMenuOption;
  }

  get isHamburgerMenuExpanded() {
    return this._isHamburgerMenuExpanded;
  }

  changeSelectedMenuOption(menuOption: MenuOptions) {
    this._selectedMenuOption = menuOption;
  }

  toggleHamburguerMenu() {
    this._isHamburgerMenuExpanded = !this._isHamburgerMenuExpanded;
  }
}

type MenuOptions = 'ORDERS' | 'CUSTOMERS' | 'ADJUSTS' | 'MEASURES';

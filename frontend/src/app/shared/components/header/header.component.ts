import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './header.component.html',
  styleUrl: './header.component.css'
})
export class HeaderComponent {
  private _isHamburgerMenuExpanded: boolean;

  @Input()
  public selectedMenuOption?: MenuOptions;
  public matchMedia?: boolean;

  constructor() {
    this._isHamburgerMenuExpanded = false;
    window.matchMedia("(min-width: 570px)").addEventListener("change", (event) => {
      this.matchMedia = event.matches;

      if (!event.matches) {
        this._isHamburgerMenuExpanded = false;
      }
    });
  }

  get isHamburgerMenuExpanded() {
    return this._isHamburgerMenuExpanded;
  }

  changeSelectedMenuOption(menuOption: MenuOptions) {
    this.selectedMenuOption = menuOption;
    this._isHamburgerMenuExpanded = false;
  }

  toggleHamburguerMenu() {
    this._isHamburgerMenuExpanded = !this._isHamburgerMenuExpanded;
  }
}

type MenuOptions = 'ORDERS' | 'CUSTOMERS' | 'ADJUSTS' | 'MEASURES';

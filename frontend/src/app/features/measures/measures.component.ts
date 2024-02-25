import {
  MeasureService,
  MeasurePage,
  QueryParams
} from '../../services/measure.service';

import { CommonModule } from '@angular/common';
import { Component, OnInit, inject } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';

import { PaginatorModule, PaginatorState } from 'primeng/paginator';

import { HeaderComponent } from '../../shared/components/header/header.component';

import { AvailableFilters, FilterComponent } from './components/filter/filter.component';

@Component({
  selector: 'app-measures',
  standalone: true,
  imports: [
    CommonModule,
    HeaderComponent,
    PaginatorModule,
    FilterComponent,
  ],
  templateUrl: './measures.component.html',
  styleUrl: './measures.component.css'
})
export class MeasuresComponent implements OnInit {
  private measureService: MeasureService = inject(MeasureService);

  private _activeFilters: AvailableFilters;
  private _measurePage?: MeasurePage
  private _currentPage: number;

  constructor() {
    this._activeFilters = {
      name: null,
      status: null
    };
    this._currentPage = 0;
  }

  ngOnInit(): void {
    this.fetchMeasures();
  }

  existMeasures() {
    return this._measurePage && this._measurePage.content.length > 0;
  }

  getMeasures() {
    if (this._measurePage) {
      return this._measurePage.content;
    }
    return [];
  }

  onPageChange(paginatorState: PaginatorState) {
    this._currentPage = paginatorState.page || 0;
    this.fetchMeasures();
  }

  getPageSize() {
    if (this._measurePage) {
      return this._measurePage.pageable.pageSize;
    }
    return 0;
  }

  getTotalRecords() {
    if (this._measurePage) {
      return this._measurePage.totalElements;
    }
    return 0;
  }

  applyFilters(applyedFilter: AvailableFilters) {
    this._activeFilters = applyedFilter;
    this.fetchMeasures();
  }

  private fetchMeasures() {
    const params: QueryParams = {
      page: this._currentPage,
      name: this._activeFilters.name,
      isActive: this._activeFilters.status,
    };

    this.measureService.findAll(params).subscribe({
      next: (measurePage) => {
        this._measurePage = measurePage;
      },
      error: (error: HttpErrorResponse) => {
        console.log(error);
      }
    })
  }

}

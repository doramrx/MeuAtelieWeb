import {
  MeasureService,
  MeasurePage,
  QueryParams,
  SaveMeasureDTO
} from '../../services/measure.service';

import { CommonModule } from '@angular/common';
import { Component, OnInit, inject } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';

import { PaginatorModule, PaginatorState } from 'primeng/paginator';
import { Message, MessageService } from 'primeng/api';
import { ToastModule } from 'primeng/toast';

import { HeaderComponent } from '../../shared/components/header/header.component';
import { ButtonComponent } from '../../shared/components/button/button.component';

import { AvailableFilters, FilterComponent } from './components/filter/filter.component';
import { AddMeasureData, AddMeasureDialogComponent } from './components/add-measure-dialog/add-measure-dialog.component';

@Component({
  selector: 'app-measures',
  standalone: true,
  imports: [
    CommonModule,
    HeaderComponent,
    PaginatorModule,
    ToastModule,
    FilterComponent,
    ButtonComponent,
    AddMeasureDialogComponent
  ],
  providers: [MessageService],
  templateUrl: './measures.component.html',
  styleUrl: './measures.component.css'
})
export class MeasuresComponent implements OnInit {
  private measureService: MeasureService = inject(MeasureService);
  private messageService: MessageService = inject(MessageService);

  private _activeFilters: AvailableFilters;
  private _measurePage?: MeasurePage
  private _currentPage: number;

  public activeModal: AvailableModalsType | null;

  constructor() {
    this._activeFilters = {
      name: null,
      status: null
    };
    this._currentPage = 0;
    this.activeModal = null;
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

  showToast(message: Message) {
    this.messageService.add(message);
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

  isModalVisible(type: AvailableModalsType) {
    return this.activeModal !== null && this.activeModal === type
      ? true
      : false;
  }

  closeModal() {
    this.activeModal = null;
  }

  showAddMeasureDialog() {
    this.activeModal = 'ADD';
  }

  saveMeasure(measureData: AddMeasureData) {
    const saveMeasure: SaveMeasureDTO = {
      name: measureData.name,
    };

    this.measureService.addMeasure(saveMeasure).subscribe({
      next: () => {
        this.closeModal();
        this.showToast({
          severity: 'success',
          summary: 'Sucesso',
          detail: 'Medida cadastrada com sucesso',
        });
        this.fetchMeasures();
      },
      error: (error: HttpErrorResponse) => {
        console.error(error);
        this.showToast({
          severity: 'error',
          summary: 'Erro',
          detail: error.error.details,
        });
      },
    });
  }

}

enum AvailableModals {
  ADD,
  UPDATE,
  DELETE,
}

type AvailableModalsType = keyof typeof AvailableModals;

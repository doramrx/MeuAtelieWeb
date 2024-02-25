import {
  AdjustService,
  AdjustPage,
  QueryParams,
  SaveAdjustDTO,
  UpdateAdjustDTO
} from '../../services/adjust.service';

import { CommonModule, CurrencyPipe } from '@angular/common';
import { Component, OnInit, inject } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';

import { Message, MessageService } from 'primeng/api';
import { ToastModule } from 'primeng/toast';
import { PaginatorModule, PaginatorState } from 'primeng/paginator';

import { HeaderComponent } from '../../shared/components/header/header.component';
import { ButtonComponent } from '../../shared/components/button/button.component';

import { AvailableFilters, FilterComponent } from './components/filter/filter.component';
import { AddAdjustData, AddAdjustDialogComponent } from './components/add-adjust-dialog/add-adjust-dialog.component';
import { UpdateAdjustDialogComponent, UpdateAdjustData } from './components/update-adjust-dialog/update-adjust-dialog.component';
import { InactivateAdjustDialogComponent } from './components/inactivate-adjust-dialog/inactivate-adjust-dialog.component';

@Component({
  selector: 'app-adjusts',
  standalone: true,
  imports: [
    CommonModule,
    CurrencyPipe,
    HeaderComponent,
    FilterComponent,
    ButtonComponent,
    PaginatorModule,
    ToastModule,
    AddAdjustDialogComponent,
    UpdateAdjustDialogComponent,
    InactivateAdjustDialogComponent,
  ],
  providers: [MessageService],
  templateUrl: './adjusts.component.html',
  styleUrl: './adjusts.component.css'
})
export class AdjustsComponent implements OnInit {
  private adjustService: AdjustService = inject(AdjustService);
  private messageService: MessageService = inject(MessageService);

  private _activeFilters: AvailableFilters;
  private _adjustPage?: AdjustPage;
  private _currentPage: number;

  public activeModal: AvailableModalsType | null;
  public adjustId?: string;

  constructor() {
    this._activeFilters = {
      name: null,
      status: null
    };
    this._currentPage = 0;
    this.activeModal = null;
  }

  ngOnInit(): void {
    this.fetchAdjusts();
  }

  existAdjusts() {
    return this._adjustPage && this._adjustPage.content.length > 0;
  }

  getAdjusts() {
    if (this._adjustPage) {
      return this._adjustPage.content;
    }
    return [];
  }

  onPageChange(paginatorState: PaginatorState) {
    this._currentPage = paginatorState.page || 0;
    this.fetchAdjusts();
  }

  getPageSize() {
    if (this._adjustPage) {
      return this._adjustPage.pageable.pageSize;
    }
    return 0;
  }

  getTotalRecords() {
    if (this._adjustPage) {
      return this._adjustPage.totalElements;
    }
    return 0;
  }

  showToast(message: Message) {
    this.messageService.add(message);
  }

  applyFilters(applyedFilter: AvailableFilters) {
    this._activeFilters = applyedFilter;
    this.fetchAdjusts();
  }

  private fetchAdjusts() {
    const params: QueryParams = {
      page: this._currentPage,
      name: this._activeFilters.name,
      isActive: this._activeFilters.status,
    };

    this.adjustService.findAll(params).subscribe({
      next: (adjustPage) => {
        this._adjustPage = adjustPage;
      },
      error: (error: HttpErrorResponse) => {
        this.showToast({
          severity: 'error',
          summary: 'Erro',
          detail: error.error.details,
        });
      },
    });
  }

  isModalVisible(type: AvailableModalsType) {
    return this.activeModal !== null && this.activeModal === type
      ? true
      : false;
  }

  closeModal() {
    this.activeModal = null;
  }

  showAddAdjustDialog() {
    this.activeModal = 'ADD';
  }

  saveAdjust(adjustData: AddAdjustData) {
    const saveAdjust: SaveAdjustDTO = {
      name: adjustData.name,
      cost: adjustData.cost,
    };

    this.adjustService.addAdjust(saveAdjust).subscribe({
      next: () => {
        this.closeModal();
        this.showToast({
          severity: 'success',
          summary: 'Sucesso',
          detail: 'Ajuste cadastrado com sucesso',
        });
        this.fetchAdjusts();
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

  showUpdateAdjustDialog(id: string) {
    this.activeModal = 'UPDATE';
    this.adjustId = id;
  }

  updateAdjust(adjustData: UpdateAdjustData) {
    if (!adjustData.id) {
      this.showToast({
        severity: 'error',
        summary: 'Erro',
        detail: 'Não foi possível atualizar os dados do ajuste',
      });
      return;
    }

    const updateAdjust: UpdateAdjustDTO = {
      name: adjustData.name || '',
      cost: adjustData.cost || 0,
    };

    this.adjustService.updateAdjust(adjustData.id, updateAdjust).subscribe({
      next: () => {
        this.closeModal();
        this.showToast({
          severity: 'success',
          summary: 'Sucesso',
          detail: 'Ajuste atualizado com sucesso',
        });
        this.fetchAdjusts();
      },
      error: (error: HttpErrorResponse) => {
        this.showToast({
          severity: 'error',
          summary: 'Erro',
          detail: error.error.details,
        });
      },
    });
  }

  showDeleteAdjustDialog(id: string) {
    this.activeModal = 'DELETE';
    this.adjustId = id;
  }

  deleteAdjust() {
    if (!this.adjustId) {
      this.showToast({
        severity: 'error',
        summary: 'Erro',
        detail: 'Não foi possível inativar o ajuste',
      });
      return;
    }

    this.adjustService
      .deleteAdjust(this.adjustId)
      .subscribe({
        next: () => {
          this.closeModal();
          this.showToast({
            severity: 'success',
            summary: 'Sucesso',
            detail: 'Ajuste inativado com sucesso',
          });
          this.fetchAdjusts();
        },
        error: (error: HttpErrorResponse) => {
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

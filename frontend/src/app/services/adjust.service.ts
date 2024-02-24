import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AdjustService {
  private readonly BASE_URL = 'http://localhost:8080/adjusts';

  private http: HttpClient = inject(HttpClient);

  constructor() { }

  findAll(queryParams: QueryParams): Observable<AdjustPage> {
    let httpParams: HttpParams = new HttpParams();

    httpParams = httpParams.append('page', queryParams.page);

    if (queryParams.name) {
      httpParams = httpParams.append('name', queryParams.name);
    }

    if (typeof queryParams.isActive === "boolean") {
      httpParams = httpParams.append('isActive', queryParams.isActive);
    }

    return this.http.get<AdjustPage>(this.BASE_URL, { params: httpParams });
  }

  findById(id: string): Observable<AdjustDTO> {
    return this.http.get<AdjustDTO>(`${this.BASE_URL}/${id}`);
  }

  addAdjust(dto: SaveAdjustDTO): Observable<AdjustDTO> {
    return this.http.post<AdjustDTO>(this.BASE_URL, dto);
  }

  updateAdjust(id: string, dto: UpdateAdjustDTO): Observable<AdjustDTO> {
    return this.http.put<AdjustDTO>(`${this.BASE_URL}/${id}`, dto);
  }

  deleteAdjust(id: string): Observable<any> {
    return this.http.delete(`${this.BASE_URL}/${id}`);
  }

}

interface AdjustDTO {
  id: string;
  name: string;
  cost: number;
  isActive: boolean;
}

export interface SaveAdjustDTO {
  name: string | null;
  cost: number | null;
}

export interface UpdateAdjustDTO {
  name: string;
  cost: number;
}

export interface AdjustPage {
  totalElements: number;
  pageable: {
    pageSize: number;
  };
  content: AdjustDTO[]
}

export interface QueryParams {
  page: number;
  name: string | null;
  isActive?: boolean | null;
}

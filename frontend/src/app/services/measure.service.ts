import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class MeasureService {
  private readonly BASE_URL = 'http://localhost:8080/measures';

  private http: HttpClient = inject(HttpClient);

  constructor() { }

  findAll(queryParams: QueryParams): Observable<MeasurePage> {
    let httpParams: HttpParams = new HttpParams();

    httpParams = httpParams.append('page', queryParams.page);

    if (queryParams.name) {
      httpParams = httpParams.append('name', queryParams.name);
    }

    if (typeof queryParams.isActive === "boolean") {
      httpParams = httpParams.append('isActive', queryParams.isActive);
    }

    return this.http.get<MeasurePage>(this.BASE_URL, { params: httpParams });
  }

  findById(id: string): Observable<MeasureDTO> {
    return this.http.get<MeasureDTO>(`${this.BASE_URL}/${id}`);
  }

  addMeasure(dto: SaveMeasureDTO): Observable<MeasureDTO> {
    return this.http.post<MeasureDTO>(this.BASE_URL, dto);
  }

  updateMeasure(id: string, dto: UpdateMeasureDTO): Observable<MeasureDTO> {
    return this.http.put<MeasureDTO>(`${this.BASE_URL}/${id}`, dto);
  }

  deleteMeasure(id: string): Observable<any> {
    return this.http.delete(`${this.BASE_URL}/${id}`);
  }

}

interface MeasureDTO {
  id: string;
  name: string;
  isActive: boolean;
}

export interface SaveMeasureDTO {
  name: string | null;
}

export interface UpdateMeasureDTO {
  name: string;
}

export interface MeasurePage {
  totalElements: number;
  pageable: {
    pageSize: number;
  };
  content: MeasureDTO[];
}

export interface QueryParams {
  page: number;
  name: string | null;
  isActive?: boolean | null;
}



import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class CustomerService {
  private readonly BASE_URL = 'http://localhost:8080/customers';

  private http: HttpClient = inject(HttpClient);

  constructor() { }

  findAll(queryParams: QueryParams): Observable<CustomerPage> {
    let httpParams: HttpParams = new HttpParams();

    httpParams = httpParams.append('page', queryParams.page);

    if (queryParams.name) {
      httpParams = httpParams.append('name', queryParams.name);
    }

    if (queryParams.email) {
      httpParams = httpParams.append('email', queryParams.email);
    }

    if (queryParams.phone) {
      httpParams = httpParams.append('phone', queryParams.phone);
    }

    if (typeof queryParams.isActive === "boolean") {
      httpParams = httpParams.append('isActive', queryParams.isActive);
    }

    return this.http.get<CustomerPage>(this.BASE_URL, { params: httpParams });
  }
}

interface Customer {
  id: string;
  name: string;
  email: string;
  phone: string;
  isActive: boolean;
}

export interface CustomerPage {
  totalElements: number;
  pageable: {
    pageSize: number;
  };
  content: Customer[];
}

export interface QueryParams {
  page: number;
  name: string | null;
  email: string | null;
  phone: string | null;
  isActive?: boolean | null;
}

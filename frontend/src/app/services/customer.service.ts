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

  findById(id: string): Observable<CustomerDTO> {
    return this.http.get<CustomerDTO>(`${this.BASE_URL}/${id}`);
  }

  addCustomer(dto: SaveCustomerDTO): Observable<CustomerDTO> {
    return this.http.post<CustomerDTO>(this.BASE_URL, dto);
  }

  updateCustomer(id: string, dto: UpdateCustomerDTO): Observable<CustomerDTO> {
    return this.http.put<CustomerDTO>(`${this.BASE_URL}/${id}`, dto);
  }

  deleteCustomer(id: string): Observable<any> {
    return this.http.delete(`${this.BASE_URL}/${id}`);
  }
}

interface CustomerDTO {
  id: string;
  name: string;
  email: string;
  phone: string;
  isActive: boolean;
}

export interface SaveCustomerDTO {
  name: string | null;
  email: string | null;
  phone: string | null;
}

export interface UpdateCustomerDTO {
  name: string;
  phone: string | null;
}

export interface CustomerPage {
  totalElements: number;
  pageable: {
    pageSize: number;
  };
  content: CustomerDTO[];
}

export interface QueryParams {
  page: number;
  name: string | null;
  email: string | null;
  phone: string | null;
  isActive?: boolean | null;
}

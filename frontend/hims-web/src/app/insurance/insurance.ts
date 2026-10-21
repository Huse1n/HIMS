import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export type InsuranceType =
  | 'LIABILITY'
  | 'PARTIAL_COVERAGE'
  | 'FULL_COVERAGE';

export type PaymentFrequency =
  | 'YEARLY'
  | 'HALF_YEARLY'
  | 'QUARTERLY';

export type PolicyStatus =
  | 'DRAFT'
  | 'ACTIVE'
  | 'CANCELLED';

export interface PremiumQuoteRequest {
  insuranceType: InsuranceType;
  roadsideAssistance: boolean;
  paymentFrequency: PaymentFrequency;
}

export interface PremiumQuote {
  basePremium: number;
  annualPremium: number;
  installmentAmount: number;
  installmentsPerYear: number;
}

export interface PolicyRequest extends PremiumQuoteRequest {
  customerId: number;
  vehicleManufacturer: string;
  vehicleModel: string;
  licensePlate: string;
  firstRegistrationYear: number;
  startDate: string;
}

export interface InsurancePolicy extends PolicyRequest {
  id: number;
  policyNumber: string;
  endDate: string;
  annualPremium: number;
  installmentAmount: number;
  status: PolicyStatus;
}

@Injectable({
  providedIn: 'root'
})
export class InsuranceService {

  private readonly http = inject(HttpClient);
  private readonly apiUrl = '/api/policies';

  // Beitrag berechnen
  calculatePremium(
    request: PremiumQuoteRequest
  ): Observable<PremiumQuote> {
    return this.http.post<PremiumQuote>(
      `${this.apiUrl}/quote`,
      request
    );
  }

  // Alle Verträge
  getPolicies(): Observable<InsurancePolicy[]> {
    return this.http.get<InsurancePolicy[]>(this.apiUrl);
  }

  // Einzelnen Vertrag
  getPolicyById(id: number): Observable<InsurancePolicy> {
    return this.http.get<InsurancePolicy>(
      `${this.apiUrl}/${id}`
    );
  }

  // Vertrag erstellen
  createPolicy(
    request: PolicyRequest
  ): Observable<InsurancePolicy> {
    return this.http.post<InsurancePolicy>(
      this.apiUrl,
      request
    );
  }

  // Vertrag bearbeiten
  updatePolicy(
    id: number,
    request: PolicyRequest
  ): Observable<InsurancePolicy> {
    return this.http.put<InsurancePolicy>(
      `${this.apiUrl}/${id}`,
      request
    );
  }

  // Vertrag löschen
  deletePolicy(id: number): Observable<void> {
    return this.http.delete<void>(
      `${this.apiUrl}/${id}`
    );
  }

  // Verträge eines Kunden
  getPoliciesByCustomer(
    customerId: number
  ): Observable<InsurancePolicy[]> {
    return this.http.get<InsurancePolicy[]>(
      `${this.apiUrl}/customer/${customerId}`
    );
  }

  downloadPolicyPdf(id: number): Observable<Blob> {
    return this.http.get(`${this.apiUrl}/${id}/pdf`, {
      responseType: 'blob'
    });
  }
}

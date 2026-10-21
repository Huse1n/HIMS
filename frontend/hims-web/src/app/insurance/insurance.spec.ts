import { TestBed } from '@angular/core/testing';
import {
  HttpTestingController,
  provideHttpClientTesting
} from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { InsuranceService } from './insurance';

describe('InsuranceService', () => {
  let service: InsuranceService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        InsuranceService,
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    });

    service = TestBed.inject(InsuranceService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should request all insurance policies', () => {
    service.getPolicies().subscribe(policies => {
      expect(policies).toEqual([]);
    });

    const request = httpMock.expectOne('/api/policies');

    expect(request.request.method).toBe('GET');

    request.flush([]);
  });

  it('should request a premium calculation', () => {
    const quoteRequest = {
      insuranceType: 'LIABILITY' as const,
      roadsideAssistance: false,
      paymentFrequency: 'YEARLY' as const
    };

    service.calculatePremium(quoteRequest).subscribe(quote => {
      expect(quote.annualPremium).toBe(300);
    });

    const request = httpMock.expectOne('/api/policies/quote');

    expect(request.request.method).toBe('POST');
    expect(request.request.body).toEqual(quoteRequest);

    request.flush({
      basePremium: 300,
      annualPremium: 300,
      installmentAmount: 300,
      installmentsPerYear: 1
    });
  });
});

import { TestBed } from '@angular/core/testing';
import {
  provideHttpClient
} from '@angular/common/http';

import {
  HttpTestingController,
  provideHttpClientTesting
} from '@angular/common/http/testing';

import { CustomerService } from './customer';
import { Customer } from '../models/customer';

describe('CustomerService', () => {

  let service: CustomerService;
  let httpMock: HttpTestingController;

  const apiUrl = 'http://localhost:8081/api/customers';

  const mockCustomer: Customer = {
    id: 1,
    firstName: 'Max',
    lastName: 'Mustermann',
    email: 'max@example.com'
  };

  beforeEach(() => {

    TestBed.configureTestingModule({
      providers: [
        CustomerService,
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    });

    service = TestBed.inject(CustomerService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  // TEST 1: CustomerService should be created
  it('should be created', () => {
    expect(service).toBeTruthy();
  });


  // TEST 2: GET all customers
  it('should get all customers', () => {

    const mockCustomers: Customer[] = [mockCustomer];

    service.getAllCustomers().subscribe(customers => {
      expect(customers).toEqual(mockCustomers);
    });

    const req = httpMock.expectOne(apiUrl);

    expect(req.request.method).toBe('GET');

    req.flush(mockCustomers);
  });


  // TEST 3: GET customer by ID
  it('should get customer by ID', () => {

    service.getCustomerById(1).subscribe(customer => {
      expect(customer).toEqual(mockCustomer);
    });

    const req = httpMock.expectOne(`${apiUrl}/1`);

    expect(req.request.method).toBe('GET');

    req.flush(mockCustomer);
  });


  // TEST 4: POST create customer
  it('should create a customer', () => {

    service.createCustomer(mockCustomer).subscribe(customer => {
      expect(customer).toEqual(mockCustomer);
    });

    const req = httpMock.expectOne(apiUrl);

    expect(req.request.method).toBe('POST');

    expect(req.request.body).toEqual(mockCustomer);

    req.flush(mockCustomer);
  });


  // TEST 5: PUT update customer
  it('should update a customer', () => {

    const updatedCustomer: Customer = {
      ...mockCustomer,
      firstName: 'Anna'
    };

    service.updateCustomer(1, updatedCustomer).subscribe(customer => {
      expect(customer).toEqual(updatedCustomer);
    });

    const req = httpMock.expectOne(`${apiUrl}/1`);

    expect(req.request.method).toBe('PUT');

    expect(req.request.body).toEqual(updatedCustomer);

    req.flush(updatedCustomer);
  });


  // TEST 6: DELETE customer
  it('should delete a customer', () => {

    service.deleteCustomer(1).subscribe();

    const req = httpMock.expectOne(`${apiUrl}/1`);

    expect(req.request.method).toBe('DELETE');

    req.flush(null);
  });


  // TEST 7: Handle 404 - Customer not found
  it('should handle 404 error', () => {

    service.getCustomerById(999).subscribe({
      next: () => {
        throw new Error('Expected 404 error');
      },
      error: (error) => {
        expect(error.status).toBe(404);
      }
    });

    const req = httpMock.expectOne(`${apiUrl}/999`);

    expect(req.request.method).toBe('GET');

    req.flush(
      { message: 'Customer not found' },
      { status: 404, statusText: 'Not Found' }
    );
  });


// TEST 8: Handle 409 - Duplicate email
  it('should handle 409 duplicate email error', () => {

    service.createCustomer(mockCustomer).subscribe({
      next: () => {
        throw new Error('Expected 409 error');
      },
      error: (error) => {
        expect(error.status).toBe(409);
      }
    });

    const req = httpMock.expectOne(apiUrl);

    expect(req.request.method).toBe('POST');

    req.flush(
      { message: 'Email already exists' },
      { status: 409, statusText: 'Conflict' }
    );
  });


// TEST 9: Handle 400 - Bad request
  it('should handle 400 bad request error', () => {

    service.createCustomer(mockCustomer).subscribe({
      next: () => {
        throw new Error('Expected 400 error');
      },
      error: (error) => {
        expect(error.status).toBe(400);
      }
    });

    const req = httpMock.expectOne(apiUrl);

    req.flush(
      { message: 'Invalid customer data' },
      { status: 400, statusText: 'Bad Request' }
    );
  });


// TEST 10: Handle 500 - Server error
  it('should handle 500 server error', () => {

    service.getAllCustomers().subscribe({
      next: () => {
        throw new Error('Expected 500 error');
      },
      error: (error) => {
        expect(error.status).toBe(500);
      }
    });

    const req = httpMock.expectOne(apiUrl);

    req.flush(
      { message: 'Internal server error' },
      { status: 500, statusText: 'Internal Server Error' }
    );
  });

});

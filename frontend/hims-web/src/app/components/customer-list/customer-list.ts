import { Component, inject, OnInit, signal } from '@angular/core';
import { CustomerService } from '../../services/customer';
import { Customer } from '../../models/customer';
import { CustomerForm } from '../customer-form/customer-form';
import { AuthService } from '../../services/auth';
@Component({
  selector: 'app-customer-list',
  imports: [CustomerForm],
  templateUrl: './customer-list.html',
  styleUrl: './customer-list.scss'
})
export class CustomerList implements OnInit {

  private authService = inject(AuthService);

  isAdmin(): boolean {
    return this.authService.isAdmin();
  }
  private customerService = inject(CustomerService);

  customers = signal<Customer[]>([]);
  loading = signal(true);
  error = signal('');

  showForm = signal(false);
  selectedCustomer = signal<Customer | null>(null);

  ngOnInit(): void {
    this.loadCustomers();
  }

  loadCustomers(): void {
    this.loading.set(true);
    this.error.set('');

    this.customerService.getAllCustomers().subscribe({
      next: (data) => {
        this.customers.set(data);
        this.loading.set(false);
      },
      error: (err) => {
        console.error(err);
        this.error.set('Customers could not be loaded.');
        this.loading.set(false);
      }
    });
  }

  addCustomer(): void {
    this.selectedCustomer.set(null);
    this.showForm.set(true);
  }

  editCustomer(customer: Customer): void {
    this.selectedCustomer.set(customer);
    this.showForm.set(true);
  }

  closeForm(): void {
    this.showForm.set(false);
    this.selectedCustomer.set(null);
  }

  onCustomerSaved(): void {
    this.closeForm();
    this.loadCustomers();
  }

  deleteCustomer(customer: Customer): void {
    if (customer.id == null) return;

    const confirmed = confirm(
      `Are you sure you want to delete ${customer.firstName} ${customer.lastName}?`
    );

    if (!confirmed) return;

    this.customerService.deleteCustomer(customer.id).subscribe({
      next: () => this.loadCustomers(),
      error: (err) => {
        console.error(err);
        this.error.set('Customer could not be deleted.');
      }
    });
  }
}

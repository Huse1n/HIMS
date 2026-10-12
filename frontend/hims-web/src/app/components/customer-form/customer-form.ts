import {
  Component, EventEmitter, Input, Output,
  OnChanges, SimpleChanges, inject, signal
} from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CustomerService } from '../../services/customer';
import { Customer } from '../../models/customer';

@Component({
  selector: 'app-customer-form',
  imports: [FormsModule],
  templateUrl: './customer-form.html',
  styleUrl: './customer-form.scss'
})
export class CustomerForm implements OnChanges {

  private customerService = inject(CustomerService);

  @Input() customer: Customer | null = null;

  @Output() customerCreated = new EventEmitter<void>();
  @Output() cancelled = new EventEmitter<void>();

  firstName = '';
  lastName = '';
  email = '';

  saving = signal(false);
  error = signal('');

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['customer']) {
      this.firstName = this.customer?.firstName ?? '';
      this.lastName = this.customer?.lastName ?? '';
      this.email = this.customer?.email ?? '';
    }
  }

  saveCustomer(): void {
    this.saving.set(true);
    this.error.set('');

    const customerData: Customer = {
      firstName: this.firstName,
      lastName: this.lastName,
      email: this.email
    };

    const request = this.customer?.id != null
      ? this.customerService.updateCustomer(this.customer.id, customerData)
      : this.customerService.createCustomer(customerData);

    request.subscribe({
      next: () => {
        this.saving.set(false);
        this.customerCreated.emit();
      },
      error: (err) => {
        console.error(err);

        if (err.status === 409) {
          this.error.set('This email address is already registered.');
        } else if (err.status === 400) {
          this.error.set('Please check the entered information.');
        } else if (err.status === 404) {
          this.error.set('Customer not found.');
        } else {
          this.error.set('Something went wrong. Please try again.');
        }

        this.saving.set(false);
      }
    });
  }
}

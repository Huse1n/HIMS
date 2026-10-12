import { Component, inject, OnInit, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { CustomerService } from '../../services/customer';

@Component({
  selector: 'app-dashboard',
  imports: [RouterLink],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.scss'
})
export class Dashboard implements OnInit {

  private customerService = inject(CustomerService);

  totalCustomers = signal(0);
  loading = signal(true);
  backendOnline = signal(false);

  ngOnInit(): void {
    this.customerService.getAllCustomers().subscribe({
      next: (customers) => {
        this.totalCustomers.set(customers.length);
        this.backendOnline.set(true);
        this.loading.set(false);
      },
      error: (err) => {
        console.error(err);
        this.backendOnline.set(false);
        this.loading.set(false);
      }
    });
  }
}

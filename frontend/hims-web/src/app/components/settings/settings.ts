import { Component, inject, OnInit, signal } from '@angular/core';
import { CustomerService } from '../../services/customer';

@Component({
  selector: 'app-settings',
  imports: [],
  templateUrl: './settings.html',
  styleUrl: './settings.scss'
})
export class Settings implements OnInit {

  private customerService = inject(CustomerService);

  backendOnline = signal(false);
  checking = signal(true);

  ngOnInit(): void {
    this.checkConnection();
  }

  checkConnection(): void {
    this.checking.set(true);

    this.customerService.getAllCustomers().subscribe({
      next: () => {
        this.backendOnline.set(true);
        this.checking.set(false);
      },
      error: () => {
        this.backendOnline.set(false);
        this.checking.set(false);
      }
    });
  }
}

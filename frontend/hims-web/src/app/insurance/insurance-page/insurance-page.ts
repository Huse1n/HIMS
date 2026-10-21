import { Component, OnInit, inject, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { CustomerService } from '../../services/customer';
import { Customer } from '../../models/customer';

import {
  InsuranceService,
  InsurancePolicy,
  InsuranceType,
  PaymentFrequency,
  PremiumQuote,
  PolicyRequest
} from '../insurance';

@Component({
  selector: 'app-insurance-page',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './insurance-page.html',
  styleUrl: './insurance-page.scss'
})
export class InsurancePage implements OnInit {

  private readonly cdr = inject(ChangeDetectorRef);
  private readonly insuranceService = inject(InsuranceService);
  private readonly customerService = inject(CustomerService);

  // Kunden und Verträge
  customers: Customer[] = [];
  policies: InsurancePolicy[] = [];

  // Versicherungsprodukt
  productName = 'HUK-COBURG Kfz-Versicherung';

  // Formular
  customerId: number | null = null;
  editingPolicyId: number | null = null;

  insuranceType: InsuranceType = 'LIABILITY';
  roadsideAssistance = false;
  paymentFrequency: PaymentFrequency = 'YEARLY';

  vehicleManufacturer = '';
  vehicleModel = '';
  licensePlate = '';
  firstRegistrationYear = 2021;

  startDate = '2026-10-23';

  // Beitragsberechnung
  premiumQuote: PremiumQuote | null = null;

  // Status
  loading = false;
  saving = false;
  calculating = false;

  errorMessage = '';
  successMessage = '';

  ngOnInit(): void {
    this.loadCustomers();
    this.loadPolicies();
    this.calculatePremium();
  }

  // ==============================
  // KUNDEN LADEN
  // ==============================

  loadCustomers(): void {
    this.customerService.getAllCustomers().subscribe({
      next: (customers) => {
        this.customers = customers;
        this.cdr.detectChanges();
      },
      error: () => {
        this.errorMessage = 'Kunden konnten nicht geladen werden.';
        this.cdr.detectChanges();
      }
    });
  }

  // ==============================
  // VERTRÄGE LADEN
  // ==============================

  loadPolicies(): void {
    this.loading = true;

    this.insuranceService.getPolicies().subscribe({
      next: (policies) => {
        this.policies = policies;
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: () => {
        this.errorMessage = 'Verträge konnten nicht geladen werden.';
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }

  // ==============================
  // BEITRAG BERECHNEN
  // ==============================

  calculatePremium(): void {

    this.calculating = true;
    this.premiumQuote = null;
    this.errorMessage = '';

    this.insuranceService.calculatePremium({
      insuranceType: this.insuranceType,
      roadsideAssistance: this.roadsideAssistance,
      paymentFrequency: this.paymentFrequency
    }).subscribe({
      next: (quote) => {
        this.premiumQuote = quote;
        this.calculating = false;
        this.cdr.detectChanges();
      },
      error: () => {
        this.errorMessage = 'Beitrag konnte nicht berechnet werden.';
        this.calculating = false;
        this.cdr.detectChanges();
      }
    });
  }

  // ==============================
  // VERTRAG BEARBEITEN
  // ==============================

  editPolicy(policy: InsurancePolicy): void {

    this.editingPolicyId = policy.id;

    // Bestehende Vertragsdaten ins Formular laden
    this.customerId = policy.customerId;
    this.insuranceType = policy.insuranceType;
    this.roadsideAssistance = policy.roadsideAssistance;
    this.paymentFrequency = policy.paymentFrequency;

    this.vehicleManufacturer = policy.vehicleManufacturer;
    this.vehicleModel = policy.vehicleModel;
    this.licensePlate = policy.licensePlate;
    this.firstRegistrationYear = policy.firstRegistrationYear;

    this.startDate = policy.startDate;

    this.errorMessage = '';
    this.successMessage = '';

    // Beitrag neu berechnen
    this.calculatePremium();

    // Zum Formular scrollen
    window.scrollTo({
      top: 0,
      behavior: 'smooth'
    });
  }

  // ==============================
  // VERTRAG ERSTELLEN / AKTUALISIEREN
  // ==============================

  savePolicy(): void {

    this.errorMessage = '';
    this.successMessage = '';

    // Validierung
    if (!this.customerId) {
      this.errorMessage = 'Bitte einen Kunden auswählen.';
      return;
    }

    if (
      !this.vehicleManufacturer.trim() ||
      !this.vehicleModel.trim() ||
      !this.licensePlate.trim()
    ) {
      this.errorMessage = 'Bitte alle Fahrzeugdaten eingeben.';
      return;
    }

    if (
      !Number.isInteger(this.firstRegistrationYear) ||
      this.firstRegistrationYear < 1900
    ) {
      this.errorMessage = 'Bitte ein gültiges Erstzulassungsjahr eingeben.';
      return;
    }

    if (!this.startDate) {
      this.errorMessage = 'Bitte den Versicherungsbeginn eingeben.';
      return;
    }

    // Request erstellen
    const request: PolicyRequest = {
      customerId: this.customerId,
      insuranceType: this.insuranceType,
      roadsideAssistance: this.roadsideAssistance,
      paymentFrequency: this.paymentFrequency,
      vehicleManufacturer: this.vehicleManufacturer,
      vehicleModel: this.vehicleModel,
      licensePlate: this.licensePlate,
      firstRegistrationYear: this.firstRegistrationYear,
      startDate: this.startDate
    };

    this.saving = true;

    // CREATE oder UPDATE?
    const isEditing = this.editingPolicyId !== null;

    const operation$ = isEditing
      ? this.insuranceService.updatePolicy(this.editingPolicyId!, request)
      : this.insuranceService.createPolicy(request);

    operation$.subscribe({
      next: (policy) => {

        this.successMessage = isEditing
          ? `Vertrag ${policy.policyNumber} wurde erfolgreich aktualisiert.`
          : `Vertrag ${policy.policyNumber} wurde erfolgreich erstellt.`;

        this.editingPolicyId = null;
        this.saving = false;

        // Vertragsübersicht aktualisieren
        this.loadPolicies();

        this.cdr.detectChanges();
      },
      error: (err) => {

        console.error('Vertrag konnte nicht gespeichert werden:', err);

        this.errorMessage =
          'Versicherungsvertrag konnte nicht gespeichert werden.';

        this.saving = false;
        this.cdr.detectChanges();
      }
    });
  }

  // ==============================
  // BEARBEITUNG ABBRECHEN
  // ==============================

  cancelEdit(): void {
    this.editingPolicyId = null;
    this.resetForm();
  }

  // ==============================
  // FORMULAR ZURÜCKSETZEN
  // ==============================

  resetForm(): void {

    this.customerId = null;

    this.insuranceType = 'LIABILITY';
    this.roadsideAssistance = false;
    this.paymentFrequency = 'YEARLY';

    this.vehicleManufacturer = '';
    this.vehicleModel = '';
    this.licensePlate = '';
    this.firstRegistrationYear = 2021;

    this.startDate = '2026-10-23';

    this.calculatePremium();
  }

  downloadPdf(policy: InsurancePolicy): void {

    this.errorMessage = '';

    this.insuranceService.downloadPolicyPdf(policy.id).subscribe({
      next: (pdfBlob) => {

        const url = window.URL.createObjectURL(pdfBlob);

        const link = document.createElement('a');
        link.href = url;
        link.download = `${policy.policyNumber}.pdf`;

        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);

        window.setTimeout(() => {
          window.URL.revokeObjectURL(url);
        }, 1000);
      },
      error: (err) => {
        console.error('PDF-Download fehlgeschlagen:', err);
        this.errorMessage = 'PDF konnte nicht heruntergeladen werden.';
        this.cdr.detectChanges();
      }
    });
  }

  // ==============================
  // VERTRAG LÖSCHEN
  // ==============================

  deletePolicy(policy: InsurancePolicy): void {

    if (!confirm(`Vertrag ${policy.policyNumber} wirklich löschen?`)) {
      return;
    }

    this.insuranceService.deletePolicy(policy.id).subscribe({
      next: () => {
        this.successMessage = 'Vertrag wurde erfolgreich gelöscht.';
        this.loadPolicies();
        this.cdr.detectChanges();
      },
      error: () => {
        this.errorMessage = 'Vertrag konnte nicht gelöscht werden.';
        this.cdr.detectChanges();
      }
    });
  }

  // ==============================
  // ANZEIGENAMEN
  // ==============================

  getInsuranceTypeLabel(type: InsuranceType): string {
    switch (type) {
      case 'LIABILITY':
        return 'Haftpflicht';

      case 'PARTIAL_COVERAGE':
        return 'Teilkasko';

      case 'FULL_COVERAGE':
        return 'Vollkasko';
    }
  }

  getPaymentFrequencyLabel(frequency: PaymentFrequency): string {
    switch (frequency) {
      case 'YEARLY':
        return 'Jährlich';

      case 'HALF_YEARLY':
        return 'Halbjährlich';

      case 'QUARTERLY':
        return 'Vierteljährlich';
    }
  }
}

import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, tap } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private currentRole: 'ADMIN' | 'USER' | null = null;
  private http = inject(HttpClient);
  private apiUrl = '/api/customers';

  private credentials: string | null = null;

  login(username: string, password: string): Observable<unknown> {
    const credentials = btoa(`${username}:${password}`);

    return this.http.get(this.apiUrl, {
      headers: new HttpHeaders({
        Authorization: `Basic ${credentials}`
      })
    }).pipe(
      tap(() => {
        this.credentials = credentials;
        this.currentRole = username === 'admin' ? 'ADMIN' : 'USER';
      })
    );
  }

  getAuthorizationHeader(): string | null {
    return this.credentials
      ? `Basic ${this.credentials}`
      : null;
  }

  isLoggedIn(): boolean {
    return this.credentials !== null;
  }

  logout(): void {
    this.credentials = null;
    this.currentRole = null;
  }

  isAdmin(): boolean {
    return this.isLoggedIn() && this.currentRole === 'ADMIN';
  }
}

import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { AuthService } from '../services/auth';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);
  const authorization = authService.getAuthorizationHeader();

  // Authorization-Header hinzufügen
  if (authorization && req.url.startsWith('/api/')) {
    req = req.clone({
      setHeaders: {
        Authorization: authorization
      }
    });
  }

  // CSRF-Token aus dem Cookie lesen
  const csrfToken = document.cookie
    .split('; ')
    .find(cookie => cookie.startsWith('XSRF-TOKEN='))
    ?.split('=')[1];

  // CSRF-Token bei schreibenden API-Anfragen mitsenden
  if (
    csrfToken &&
    req.url.startsWith('/api/') &&
    !['GET', 'HEAD', 'OPTIONS'].includes(req.method)
  ) {
    req = req.clone({
      setHeaders: {
        'X-XSRF-TOKEN': decodeURIComponent(csrfToken)
      }
    });
  }

  return next(req);
};

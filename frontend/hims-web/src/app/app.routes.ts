import { Routes } from '@angular/router';

import { CustomerList } from './components/customer-list/customer-list';
import { Dashboard } from './components/dashboard/dashboard';
import { Settings } from './components/settings/settings';
import { Login } from './pages/login/login';

import { authGuard } from './guards/auth.guard';

export const routes: Routes = [
  {
    path: '',
    redirectTo: 'dashboard',
    pathMatch: 'full'
  },
  {
    path: 'login',
    component: Login
  },
  {
    path: 'dashboard',
    component: Dashboard,
    canActivate: [authGuard]
  },
  {
    path: 'customers',
    component: CustomerList,
    canActivate: [authGuard]
  },
  {
    path: 'settings',
    component: Settings,
    canActivate: [authGuard]
  },
  {
    path: '**',
    redirectTo: 'dashboard'
  }
];

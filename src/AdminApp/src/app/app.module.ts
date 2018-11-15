import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import {ButtonsModule, CardsFreeModule, MDBBootstrapModule, WavesModule} from 'angular-bootstrap-md';
import { RouterModule, Routes } from '@angular/router';
const appRoutes: Routes = [
  {
    path: 'doel',
    component: DoelComponent
  },
  {
    path: 'login',
    component: LoginComponent
  },
  { path: '',
    component: DoelComponent
  },
  { path: '**', component: DoelComponent }
];

import { AppComponent } from './app.component';
import { LoginComponent } from './login/login.component';
import { DoelComponent } from './doel/doel.component';
import { FormsModule } from '@angular/forms';

@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    DoelComponent
  ],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    RouterModule.forRoot(
      appRoutes
      ),
    MDBBootstrapModule.forRoot(),
    FormsModule,
    WavesModule,
    CardsFreeModule,
    ButtonsModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }

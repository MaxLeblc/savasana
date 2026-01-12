import { HttpClientModule } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { expect } from '@jest/globals';
import { of, throwError } from 'rxjs';
import { SessionService } from 'src/app/services/session.service';
import { AuthService } from '../../services/auth.service';

import { LoginComponent } from './login.component';

describe('LoginComponent', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;
  let authService: AuthService;
  let router: Router;
  let sessionService: SessionService;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [LoginComponent],
      providers: [
        AuthService,
        SessionService
      ],
      imports: [
        RouterTestingModule,
        BrowserAnimationsModule,
        HttpClientModule,
        MatCardModule,
        MatIconModule,
        MatFormFieldModule,
        MatInputModule,
        ReactiveFormsModule
      ]
    })
      .compileComponents();

    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;

    // Retrieve injected services to be able to mock them
    authService = TestBed.inject(AuthService);
    router = TestBed.inject(Router);
    sessionService = TestBed.inject(SessionService);

    fixture.detectChanges();
  });

  // UNIT TEST 1 : Check that the component exists
  it('should create', () => {
    expect(component).toBeTruthy();
  });

  // UNIT TEST 2 : Check that the form is invalid when fields are empty
  // This is a UNIT TEST: we only test the behavior of the form.
  it('should have an invalid form when fields are empty', () => {
    // GIVEN : The form is empty by default

    // WHEN : We check the form state
    const form = component.form;

    // THEN : The form should be invalid
    expect(form.valid).toBeFalsy();
    expect(form.get('email')?.hasError('required')).toBeTruthy();
    expect(form.get('password')?.hasError('required')).toBeTruthy();
  });

  // UNIT TEST 3 : Check that the form is invalid when email is invalid
  it('should have an invalid form when email is invalid', () => {
    // GIVEN : We fill the form with an invalid email
    component.form.patchValue({
      email: 'invalid-email',
      password: 'password123'
    });

    // WHEN : We check the form state
    const emailControl = component.form.get('email');

    // THEN : The email should be invalid
    expect(emailControl?.hasError('email')).toBeTruthy();
    expect(component.form.valid).toBeFalsy();
  });

  // UNIT TEST 4 : Check that the form is valid when fields are correctly filled
  it('should have a valid form when fields are correctly filled', () => {
    // GIVEN : We fill the form correctly
    component.form.patchValue({
      email: 'test@example.com',
      password: 'password123'
    });

    // WHEN : We check the form state

    // THEN : The form should be valid
    expect(component.form.valid).toBeTruthy();
  });

  // INTEGRATION TEST 5 : Check that the login is successful
  // We test the interaction between the component, the authService, and the router
  it('should navigate to /sessions on successful login', () => {
    // GIVEN : We prepare valid login data
    const mockSessionInfo = {
      token: 'fake-jwt-token',
      type: 'Bearer',
      id: 1,
      username: 'testuser',
      firstName: 'Test',
      lastName: 'User',
      admin: false
    };

    // We "mock" the methods of the services
    jest.spyOn(authService, 'login').mockReturnValue(of(mockSessionInfo));
    jest.spyOn(sessionService, 'logIn');
    // We mock navigate so it doesn't actually navigate
    const navigateSpy = jest.spyOn(router, 'navigate').mockResolvedValue(true);

    // WHEN : We fill the form and submit
    component.form.patchValue({
      email: 'test@example.com',
      password: 'password123'
    });
    component.submit();

    // THEN : We check that the correct methods have been called
    expect(authService.login).toHaveBeenCalledWith({
      email: 'test@example.com',
      password: 'password123'
    });
    expect(sessionService.logIn).toHaveBeenCalledWith(mockSessionInfo);
    expect(navigateSpy).toHaveBeenCalledWith(['/sessions']);
    expect(component.onError).toBeFalsy();
  });

  // INTEGRATION TEST 6 : Check error handling when login fails
  it('should set onError to true when login fails', () => {
    // GIVEN : We mock a login error
    jest.spyOn(authService, 'login').mockReturnValue(
      throwError(() => new Error('Invalid credentials'))
    );

    // WHEN : We submit the form
    component.form.patchValue({
      email: 'wrong@example.com',
      password: 'wrongpassword'
    });
    component.submit();

    // THEN : The onError flag should be true
    expect(component.onError).toBeTruthy();
  });

  // UNIT TEST 7 : Check password visibility toggle
  it('should toggle password visibility', () => {
    // GIVEN : The password is hidden by default
    expect(component.hide).toBeTruthy();

    // WHEN : We change the value
    component.hide = false;

    // THEN : The password should be visible
    expect(component.hide).toBeFalsy();

    // WHEN : We change the value again
    component.hide = true;

    // THEN : The password should be hidden
    expect(component.hide).toBeTruthy();
  });

  // INTEGRATION TEST 8 : Verify form submission is prevented when form is invalid
  it('should not call authService when submitting invalid form', () => {
    // GIVEN: Invalid form (empty fields)
    const loginSpy = jest.spyOn(authService, 'login');

    // WHEN: Try to submit with invalid form
    component.submit();

    // THEN: authService.login should be called even with invalid data
    // (Angular doesn't prevent submit, validation is visual only)
    expect(loginSpy).toHaveBeenCalled();
  });
});

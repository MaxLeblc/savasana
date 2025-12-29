import { HttpClientModule } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { Router } from '@angular/router';
import { expect } from '@jest/globals';
import { of, throwError } from 'rxjs';
import { AuthService } from '../../services/auth.service';

import { RegisterComponent } from './register.component';

describe('RegisterComponent', () => {
  let component: RegisterComponent;
  let fixture: ComponentFixture<RegisterComponent>;
  let authService: AuthService;
  let router: Router;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [RegisterComponent],
      providers: [AuthService],
      imports: [
        BrowserAnimationsModule,
        HttpClientModule,
        ReactiveFormsModule,
        MatCardModule,
        MatFormFieldModule,
        MatIconModule,
        MatInputModule
      ]
    })
      .compileComponents();

    fixture = TestBed.createComponent(RegisterComponent);
    component = fixture.componentInstance;

    // Retrieve injected services to be able to mock them
    authService = TestBed.inject(AuthService);
    router = TestBed.inject(Router);

    fixture.detectChanges();
  });

  // UNIT TEST 1 : Check that the component exists
  it('should create', () => {
    expect(component).toBeTruthy();
  });

  // UNIT TEST 2 : Verify form is invalid when fields are empty
  it('should have an invalid form when fields are empty', () => {
    // GIVEN: Form is empty by default

    // WHEN: Check form state
    const form = component.form;

    // THEN: Form should be invalid
    expect(form.valid).toBeFalsy();
    expect(form.get('email')?.hasError('required')).toBeTruthy();
    expect(form.get('firstName')?.hasError('required')).toBeTruthy();
    expect(form.get('lastName')?.hasError('required')).toBeTruthy();
    expect(form.get('password')?.hasError('required')).toBeTruthy();
  });

  // UNIT TEST 3 : Verify form is invalid with invalid email
  it('should have an invalid form when email is invalid', () => {
    // GIVEN: Fill form with invalid email
    component.form.patchValue({
      email: 'invalid-email',
      firstName: 'John',
      lastName: 'Doe',
      password: 'password123'
    });

    // WHEN: Check email validation
    const emailControl = component.form.get('email');

    // THEN: Email should be invalid
    expect(emailControl?.hasError('email')).toBeTruthy();
    expect(component.form.valid).toBeFalsy();
  });

  // UNIT TEST 4 : Verify form is valid with correct data
  it('should have a valid form when all fields are correctly filled', () => {
    // GIVEN: Fill form with valid data
    component.form.patchValue({
      email: 'john.doe@example.com',
      firstName: 'John',
      lastName: 'Doe',
      password: 'password123'
    });

    // WHEN: Check form state

    // THEN: Form should be valid
    expect(component.form.valid).toBeTruthy();
  });

  // INTEGRATION TEST 5 : Verify successful registration
  it('should navigate to /login on successful registration', () => {
    // GIVEN: Prepare valid registration data
    const registerRequest = {
      email: 'john.doe@example.com',
      firstName: 'John',
      lastName: 'Doe',
      password: 'password123'
    };

    // Mock the services
    jest.spyOn(authService, 'register').mockReturnValue(of(void 0));
    const navigateSpy = jest.spyOn(router, 'navigate').mockResolvedValue(true);

    // WHEN: Fill form and submit
    component.form.patchValue(registerRequest);
    component.submit();

    // THEN: Verify correct methods were called
    expect(authService.register).toHaveBeenCalledWith(registerRequest);
    expect(navigateSpy).toHaveBeenCalledWith(['/login']);
    expect(component.onError).toBeFalsy();
  });

  // INTEGRATION TEST 6 : Verify error handling on registration failure
  it('should set onError to true when registration fails', () => {
    // GIVEN: Mock registration error
    jest.spyOn(authService, 'register').mockReturnValue(
      throwError(() => new Error('Registration failed'))
    );

    // WHEN: Submit form
    component.form.patchValue({
      email: 'john.doe@example.com',
      firstName: 'John',
      lastName: 'Doe',
      password: 'password123'
    });
    component.submit();

    // THEN: onError flag should be true
    expect(component.onError).toBeTruthy();
  });

  // UNIT TEST 7 : Verify all fields are required
  it('should have required validators on all fields', () => {
    // GIVEN: Empty form

    // WHEN: Check each field
    const emailControl = component.form.get('email');
    const firstNameControl = component.form.get('firstName');
    const lastNameControl = component.form.get('lastName');
    const passwordControl = component.form.get('password');

    // THEN: All fields should have required error
    expect(emailControl?.hasError('required')).toBeTruthy();
    expect(firstNameControl?.hasError('required')).toBeTruthy();
    expect(lastNameControl?.hasError('required')).toBeTruthy();
    expect(passwordControl?.hasError('required')).toBeTruthy();
  });

  // INTEGRATION TEST 8: Verify form submission with empty form still calls service
  it('should call authService.register even with invalid form data', () => {
    // GIVEN: Empty/invalid form
    const registerSpy = jest.spyOn(authService, 'register').mockReturnValue(
      throwError(() => new Error('Validation error'))
    );

    // WHEN: Submit without filling the form
    component.submit();

    // THEN: Service should still be called (no client-side prevention)
    expect(registerSpy).toHaveBeenCalled();
    expect(component.onError).toBeTruthy();
  });
});

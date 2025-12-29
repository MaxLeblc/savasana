import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { expect } from '@jest/globals';

import { AuthService } from '../../app/features/auth/services/auth.service';

describe('AuthService', () => {
  let service: AuthService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule
      ]
    });
    service = TestBed.inject(AuthService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  // UNIT TEST 1: Verify service creation
  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  // INTEGRATION TEST 2: Verify register calls correct endpoint
  it('should register new user', () => {
    // GIVEN: Registration request data
    const registerRequest = {
      email: 'test@example.com',
      firstName: 'John',
      lastName: 'Doe',
      password: 'password123'
    };

    // WHEN: Call register
    service.register(registerRequest).subscribe();

    const req = httpMock.expectOne('api/auth/register');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(registerRequest);
    req.flush(null);
  });

  // INTEGRATION TEST 3: Verify login calls correct endpoint
  it('should login user', () => {
    // GIVEN: Login request data
    const loginRequest = {
      email: 'test@example.com',
      password: 'password123'
    };

    const mockResponse = {
      token: 'fake-token',
      type: 'Bearer',
      id: 1,
      username: 'test@example.com',
      firstName: 'John',
      lastName: 'Doe',
      admin: false
    };

    // WHEN: Call login
    service.login(loginRequest).subscribe(response => {
      // THEN: Should return session information
      expect(response).toEqual(mockResponse);
    });

    const req = httpMock.expectOne('api/auth/login');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(loginRequest);
    req.flush(mockResponse);
  });
});

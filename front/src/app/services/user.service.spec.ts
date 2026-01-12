import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { expect } from '@jest/globals';

import { UserService } from './user.service';

describe('UserService', () => {
  let service: UserService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule
      ]
    });
    service = TestBed.inject(UserService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  // UNIT TEST 1: Verify service creation
  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  // INTEGRATION TEST 2: Verify getById calls correct endpoint
  it('should fetch user by id', () => {
    // GIVEN: Mock user data
    const mockUser = {
      id: 1,
      email: 'test@example.com',
      firstName: 'John',
      lastName: 'Doe',
      admin: false,
      password: 'password',
      createdAt: new Date(),
      updatedAt: new Date()
    };

    // WHEN: Call getById
    service.getById('1').subscribe(user => {
      // THEN: Should return user
      expect(user).toEqual(mockUser);
    });

    const req = httpMock.expectOne('api/user/1');
    expect(req.request.method).toBe('GET');
    req.flush(mockUser);
  });

  // INTEGRATION TEST 3: Verify delete calls correct endpoint
  it('should delete user by id', () => {
    // WHEN: Call delete
    service.delete('1').subscribe(response => {
      // THEN: Should complete successfully
      expect(response).toEqual({});
    });

    const req = httpMock.expectOne('api/user/1');
    expect(req.request.method).toBe('DELETE');
    req.flush({});
  });
});

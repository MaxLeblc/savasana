import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { expect } from '@jest/globals';

import { TeacherService } from './teacher.service';

describe('TeacherService', () => {
  let service: TeacherService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule
      ]
    });
    service = TestBed.inject(TeacherService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  // UNIT TEST 1: Verify service creation
  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  // INTEGRATION TEST 2: Verify all() calls correct endpoint
  it('should fetch all teachers', () => {
    // GIVEN: Mock teachers data
    const mockTeachers = [
      {
        id: 1,
        firstName: 'John',
        lastName: 'Doe',
        createdAt: new Date(),
        updatedAt: new Date()
      },
      {
        id: 2,
        firstName: 'Jane',
        lastName: 'Smith',
        createdAt: new Date(),
        updatedAt: new Date()
      }
    ];

    // WHEN: Call all
    service.all().subscribe(teachers => {
      // THEN: Should return all teachers
      expect(teachers).toEqual(mockTeachers);
      expect(teachers.length).toBe(2);
    });

    const req = httpMock.expectOne('api/teacher');
    expect(req.request.method).toBe('GET');
    req.flush(mockTeachers);
  });

  // INTEGRATION TEST 3: Verify detail() calls correct endpoint
  it('should fetch teacher by id', () => {
    // GIVEN: Mock teacher data
    const mockTeacher = {
      id: 1,
      firstName: 'John',
      lastName: 'Doe',
      createdAt: new Date(),
      updatedAt: new Date()
    };

    // WHEN: Call detail
    service.detail('1').subscribe(teacher => {
      // THEN: Should return teacher
      expect(teacher).toEqual(mockTeacher);
    });

    const req = httpMock.expectOne('api/teacher/1');
    expect(req.request.method).toBe('GET');
    req.flush(mockTeacher);
  });
});

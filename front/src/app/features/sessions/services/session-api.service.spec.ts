import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { expect } from '@jest/globals';

import { SessionApiService } from './session-api.service';

describe('SessionsService', () => {
  let service: SessionApiService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule
      ]
    });
    service = TestBed.inject(SessionApiService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  // UNIT TEST 1: Verify service creation
  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  // INTEGRATION TEST 2: Verify all() method
  it('should fetch all sessions', () => {
    const mockSessions = [{ id: 1, name: 'Yoga', description: 'Test', date: new Date(), teacher_id: 1, users: [], createdAt: new Date(), updatedAt: new Date() }];

    service.all().subscribe(sessions => {
      expect(sessions).toEqual(mockSessions);
    });

    const req = httpMock.expectOne('api/session');
    expect(req.request.method).toBe('GET');
    req.flush(mockSessions);
  });

  // INTEGRATION TEST 3: Verify detail() method
  it('should fetch session by id', () => {
    const mockSession = { id: 1, name: 'Yoga', description: 'Test', date: new Date(), teacher_id: 1, users: [], createdAt: new Date(), updatedAt: new Date() };

    service.detail('1').subscribe(session => {
      expect(session).toEqual(mockSession);
    });

    const req = httpMock.expectOne('api/session/1');
    expect(req.request.method).toBe('GET');
    req.flush(mockSession);
  });

  // INTEGRATION TEST 4: Verify delete() method
  it('should delete session', () => {
    service.delete('1').subscribe(response => {
      expect(response).toEqual({});
    });

    const req = httpMock.expectOne('api/session/1');
    expect(req.request.method).toBe('DELETE');
    req.flush({});
  });

  // INTEGRATION TEST 5: Verify create() method
  it('should create session', () => {
    const newSession = { name: 'New Yoga', description: 'Test', date: new Date(), teacher_id: 1, users: [] } as any;

    service.create(newSession).subscribe(session => {
      expect(session).toEqual(newSession);
    });

    const req = httpMock.expectOne('api/session');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(newSession);
    req.flush(newSession);
  });

  // INTEGRATION TEST 6: Verify update() method
  it('should update session', () => {
    const updatedSession = { id: 1, name: 'Updated Yoga', description: 'Test', date: new Date(), teacher_id: 1, users: [], createdAt: new Date(), updatedAt: new Date() };

    service.update('1', updatedSession).subscribe(session => {
      expect(session).toEqual(updatedSession);
    });

    const req = httpMock.expectOne('api/session/1');
    expect(req.request.method).toBe('PUT');
    expect(req.request.body).toEqual(updatedSession);
    req.flush(updatedSession);
  });

  // INTEGRATION TEST 7: Verify participate() method
  it('should participate in session', () => {
    service.participate('1', '2').subscribe();

    const req = httpMock.expectOne('api/session/1/participate/2');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toBeNull();
    req.flush(null);
  });

  // INTEGRATION TEST 8: Verify unParticipate() method
  it('should unparticipate from session', () => {
    service.unParticipate('1', '2').subscribe();

    const req = httpMock.expectOne('api/session/1/participate/2');
    expect(req.request.method).toBe('DELETE');
    req.flush(null);
  });
});

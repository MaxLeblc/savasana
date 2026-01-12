import { HttpClientModule } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { expect } from '@jest/globals';
import { of } from 'rxjs';
import { SessionService } from 'src/app/services/session.service';
import { SessionApiService } from '../../services/session-api.service';

import { ListComponent } from './list.component';

describe('ListComponent', () => {
  let component: ListComponent;
  let fixture: ComponentFixture<ListComponent>;
  let sessionApiService: SessionApiService;

  const mockSessionService = {
    sessionInformation: {
      admin: true,
      id: 1
    }
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ListComponent],
      imports: [HttpClientModule, MatCardModule, MatIconModule],
      providers: [
        { provide: SessionService, useValue: mockSessionService },
        SessionApiService
      ]
    })
      .compileComponents();

    fixture = TestBed.createComponent(ListComponent);
    component = fixture.componentInstance;
    sessionApiService = TestBed.inject(SessionApiService);
    fixture.detectChanges();
  });

  // UNIT TEST 1 : Check that the component exists
  it('should create', () => {
    expect(component).toBeTruthy();
  });

  // INTEGRATION TEST 2 : Verify sessions$ observable is initialized with all sessions
  it('should fetch all sessions on initialization', () => {
    // GIVEN: Mock sessions data
    const mockSessions = [
      {
        id: 1,
        name: 'Yoga Flow',
        description: 'A relaxing yoga session',
        date: new Date(),
        teacher_id: 1,
        users: [1, 2],
        createdAt: new Date(),
        updatedAt: new Date()
      },
      {
        id: 2,
        name: 'Power Yoga',
        description: 'An intense yoga session',
        date: new Date(),
        teacher_id: 2,
        users: [1],
        createdAt: new Date(),
        updatedAt: new Date()
      }
    ];

    jest.spyOn(sessionApiService, 'all').mockReturnValue(of(mockSessions));

    // WHEN: Component is initialized
    const newComponent = new ListComponent(
      TestBed.inject(SessionService),
      sessionApiService
    );

    // THEN: sessions$ should contain the mock data
    newComponent.sessions$.subscribe(sessions => {
      expect(sessions).toEqual(mockSessions);
      expect(sessions.length).toBe(2);
    });
  });

  // UNIT TEST 3 : Verify user getter returns session information
  it('should return user from sessionService', () => {
    // WHEN: Access user property
    const user = component.user;

    // THEN: Should return sessionInformation
    expect(user).toEqual(mockSessionService.sessionInformation);
    expect(user?.admin).toBe(true);
    expect(user?.id).toBe(1);
  });

  // UNIT TEST 4 : Verify user getter returns undefined when no session
  it('should return undefined when no session information', () => {
    // GIVEN: SessionService with no sessionInformation
    const emptySessionService = { sessionInformation: undefined };
    const newComponent = new ListComponent(
      emptySessionService as any,
      sessionApiService
    );

    // WHEN: Access user property
    const user = newComponent.user;

    // THEN: Should return undefined
    expect(user).toBeUndefined();
  });

  // INTEGRATION TEST 5 : Verify sessions$ is properly set during component initialization
  it('should have sessions$ observable set on component creation', () => {
    // THEN: sessions$ should be defined
    expect(component.sessions$).toBeDefined();
  });

  // UNIT TEST 6 : Verify admin status from user getter
  it('should identify user as admin', () => {
    // WHEN: Check if user is admin
    const isAdmin = component.user?.admin;

    // THEN: Should be true
    expect(isAdmin).toBe(true);
  });
});

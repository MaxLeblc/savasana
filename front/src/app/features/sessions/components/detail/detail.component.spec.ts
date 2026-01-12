import { HttpClientModule } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { expect } from '@jest/globals';
import { of } from 'rxjs';
import { SessionService } from '../../../../services/session.service';
import { TeacherService } from '../../../../services/teacher.service';
import { SessionApiService } from '../../services/session-api.service';

import { DetailComponent } from './detail.component';

describe('DetailComponent', () => {
  let component: DetailComponent;
  let fixture: ComponentFixture<DetailComponent>;
  let sessionApiService: SessionApiService;
  let teacherService: TeacherService;
  let matSnackBar: MatSnackBar;
  let router: Router;

  const mockSessionService = {
    sessionInformation: {
      admin: true,
      id: 1
    }
  };

  const mockActivatedRoute = {
    snapshot: {
      paramMap: {
        get: jest.fn().mockReturnValue('1')
      }
    }
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        RouterTestingModule,
        HttpClientModule,
        MatSnackBarModule,
        ReactiveFormsModule
      ],
      declarations: [DetailComponent],
      providers: [
        { provide: SessionService, useValue: mockSessionService },
        { provide: ActivatedRoute, useValue: mockActivatedRoute },
        SessionApiService,
        TeacherService
      ],
    })
      .compileComponents();

    sessionApiService = TestBed.inject(SessionApiService);
    teacherService = TestBed.inject(TeacherService);
    matSnackBar = TestBed.inject(MatSnackBar);
    router = TestBed.inject(Router);

    fixture = TestBed.createComponent(DetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  // UNIT TEST 1 : Check that the component exists
  it('should create', () => {
    expect(component).toBeTruthy();
  });

  // UNIT TEST 2 : Verify session ID is extracted from route params
  it('should extract session ID from route params', () => {
    // THEN: sessionId should be set from route
    expect(component.sessionId).toBe('1');
  });

  // UNIT TEST 3 : Verify admin status is set from sessionService
  it('should set isAdmin from sessionService', () => {
    // THEN: isAdmin should be true
    expect(component.isAdmin).toBe(true);
  });

  // UNIT TEST 4 : Verify userId is set from sessionService
  it('should set userId from sessionService', () => {
    // THEN: userId should be '1'
    expect(component.userId).toBe('1');
  });

  // UNIT TEST 5 : Verify back navigation
  it('should navigate back when back() is called', () => {
    // GIVEN: Spy on window.history.back
    const backSpy = jest.spyOn(window.history, 'back');

    // WHEN: Call back
    component.back();

    // THEN: Should call window.history.back
    expect(backSpy).toHaveBeenCalled();
  });

  // INTEGRATION TEST 6 : Verify delete session flow
  it('should delete session, show message and navigate to sessions', (done) => {
    // GIVEN: Mock delete response
    jest.spyOn(sessionApiService, 'delete').mockReturnValue(of({}));
    const snackBarSpy = jest.spyOn(matSnackBar, 'open').mockReturnValue({} as any);
    const navigateSpy = jest.spyOn(router, 'navigate').mockResolvedValue(true);

    // WHEN: Call delete
    component.delete();

    // THEN: Should delete, show message and navigate
    setTimeout(() => {
      expect(sessionApiService.delete).toHaveBeenCalledWith('1');
      expect(snackBarSpy).toHaveBeenCalledWith('Session deleted !', 'Close', { duration: 3000 });
      expect(navigateSpy).toHaveBeenCalledWith(['sessions']);
      done();
    }, 0);
  });

  // INTEGRATION TEST 7 : Verify participate flow
  it('should call participate and refresh session', () => {
    // GIVEN: Mock session data
    const mockSession = {
      id: 1,
      name: 'Yoga Session',
      description: 'Test',
      date: new Date(),
      teacher_id: 1,
      users: [1],
      createdAt: new Date(),
      updatedAt: new Date()
    };

    const mockTeacher = {
      id: 1,
      firstName: 'John',
      lastName: 'Doe',
      createdAt: new Date(),
      updatedAt: new Date()
    };

    jest.spyOn(sessionApiService, 'participate').mockReturnValue(of(void 0));
    jest.spyOn(sessionApiService, 'detail').mockReturnValue(of(mockSession));
    jest.spyOn(teacherService, 'detail').mockReturnValue(of(mockTeacher));

    // WHEN: Call participate
    component.participate();

    // THEN: Should call participate and refresh
    expect(sessionApiService.participate).toHaveBeenCalledWith('1', '1');
    expect(sessionApiService.detail).toHaveBeenCalledWith('1');
  });

  // INTEGRATION TEST 8 : Verify unParticipate flow
  it('should call unParticipate and refresh session', () => {
    // GIVEN: Mock session data
    const mockSession = {
      id: 1,
      name: 'Yoga Session',
      description: 'Test',
      date: new Date(),
      teacher_id: 1,
      users: [],
      createdAt: new Date(),
      updatedAt: new Date()
    };

    const mockTeacher = {
      id: 1,
      firstName: 'John',
      lastName: 'Doe',
      createdAt: new Date(),
      updatedAt: new Date()
    };

    jest.spyOn(sessionApiService, 'unParticipate').mockReturnValue(of(void 0));
    jest.spyOn(sessionApiService, 'detail').mockReturnValue(of(mockSession));
    jest.spyOn(teacherService, 'detail').mockReturnValue(of(mockTeacher));

    // WHEN: Call unParticipate
    component.unParticipate();

    // THEN: Should call unParticipate and refresh
    expect(sessionApiService.unParticipate).toHaveBeenCalledWith('1', '1');
    expect(sessionApiService.detail).toHaveBeenCalledWith('1');
  });

  // INTEGRATION TEST 9 : Verify fetchSession sets isParticipate correctly
  it('should set isParticipate to true when user is in session', (done) => {
    // GIVEN: Mock session with user included
    const mockSession = {
      id: 1,
      name: 'Yoga Session',
      description: 'Test',
      date: new Date(),
      teacher_id: 1,
      users: [1, 2, 3],
      createdAt: new Date(),
      updatedAt: new Date()
    };

    const mockTeacher = {
      id: 1,
      firstName: 'John',
      lastName: 'Doe',
      createdAt: new Date(),
      updatedAt: new Date()
    };

    jest.spyOn(sessionApiService, 'detail').mockReturnValue(of(mockSession));
    jest.spyOn(teacherService, 'detail').mockReturnValue(of(mockTeacher));

    // WHEN: ngOnInit is called
    component.ngOnInit();

    // THEN: isParticipate should be true
    setTimeout(() => {
      expect(component.isParticipate).toBe(true);
      expect(component.session).toEqual(mockSession);
      expect(component.teacher).toEqual(mockTeacher);
      done();
    }, 0);
  });
});


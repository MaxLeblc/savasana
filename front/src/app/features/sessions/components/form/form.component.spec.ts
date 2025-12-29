import { HttpClientModule } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { expect } from '@jest/globals';
import { of } from 'rxjs';
import { SessionService } from 'src/app/services/session.service';
import { TeacherService } from 'src/app/services/teacher.service';
import { SessionApiService } from '../../services/session-api.service';

import { FormComponent } from './form.component';

describe('FormComponent', () => {
  let component: FormComponent;
  let fixture: ComponentFixture<FormComponent>;
  let sessionApiService: SessionApiService;
  let teacherService: TeacherService;
  let router: Router;
  let matSnackBar: MatSnackBar;

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
        MatCardModule,
        MatIconModule,
        MatFormFieldModule,
        MatInputModule,
        ReactiveFormsModule,
        MatSnackBarModule,
        MatSelectModule,
        BrowserAnimationsModule
      ],
      providers: [
        { provide: SessionService, useValue: mockSessionService },
        { provide: ActivatedRoute, useValue: mockActivatedRoute },
        SessionApiService,
        TeacherService
      ],
      declarations: [FormComponent]
    })
      .compileComponents();

    sessionApiService = TestBed.inject(SessionApiService);
    teacherService = TestBed.inject(TeacherService);
    router = TestBed.inject(Router);
    matSnackBar = TestBed.inject(MatSnackBar);

    // Mock teacherService.all() to prevent real HTTP call
    jest.spyOn(teacherService, 'all').mockReturnValue(of([]));

    fixture = TestBed.createComponent(FormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  // UNIT TEST 1 : Check that the component exists
  it('should create', () => {
    expect(component).toBeTruthy();
  });

  // INTEGRATION TEST 2 : Verify form initialization for create mode
  it('should initialize empty form when not in update mode', () => {
    // GIVEN: Router URL without 'update'
    jest.spyOn(router, 'url', 'get').mockReturnValue('/sessions/create');

    // WHEN: ngOnInit is called
    component.ngOnInit();

    // THEN: Form should be initialized with empty values
    expect(component.onUpdate).toBe(false);
    expect(component.sessionForm).toBeDefined();
    expect(component.sessionForm?.get('name')?.value).toBe('');
    expect(component.sessionForm?.get('description')?.value).toBe('');
  });

  // INTEGRATION TEST 3 : Verify form initialization for update mode
  it('should initialize form with session data when in update mode', (done) => {
    // GIVEN: Router URL with 'update' and mock session
    jest.spyOn(router, 'url', 'get').mockReturnValue('/sessions/update/1');
    const mockSession = {
      id: 1,
      name: 'Yoga Flow',
      description: 'Relaxing session',
      date: new Date('2024-01-15'),
      teacher_id: 2,
      users: [],
      createdAt: new Date(),
      updatedAt: new Date()
    };
    jest.spyOn(sessionApiService, 'detail').mockReturnValue(of(mockSession));

    // WHEN: ngOnInit is called
    component.ngOnInit();

    // THEN: Form should be initialized with session data
    setTimeout(() => {
      expect(component.onUpdate).toBe(true);
      expect(component.sessionForm?.get('name')?.value).toBe('Yoga Flow');
      expect(component.sessionForm?.get('description')?.value).toBe('Relaxing session');
      expect(component.sessionForm?.get('teacher_id')?.value).toBe(2);
      done();
    }, 0);
  });

  // INTEGRATION TEST 4 : Verify create session flow
  it('should create session and navigate on submit in create mode', (done) => {
    // GIVEN: Form in create mode
    jest.spyOn(router, 'url', 'get').mockReturnValue('/sessions/create');
    component.ngOnInit();

    const newSession = {
      name: 'Power Yoga',
      description: 'Intense workout',
      date: '2024-02-20',
      teacher_id: 3
    };

    component.sessionForm?.patchValue(newSession);

    jest.spyOn(sessionApiService, 'create').mockReturnValue(of({} as any));
    const snackBarSpy = jest.spyOn(matSnackBar, 'open').mockReturnValue({} as any);
    const navigateSpy = jest.spyOn(router, 'navigate').mockResolvedValue(true);

    // WHEN: Submit form
    component.submit();

    // THEN: Should create session, show message and navigate
    setTimeout(() => {
      expect(sessionApiService.create).toHaveBeenCalled();
      expect(snackBarSpy).toHaveBeenCalledWith('Session created !', 'Close', { duration: 3000 });
      expect(navigateSpy).toHaveBeenCalledWith(['sessions']);
      done();
    }, 0);
  });

  // INTEGRATION TEST 5 : Verify update session flow
  it('should update session and navigate on submit in update mode', (done) => {
    // GIVEN: Form in update mode
    jest.spyOn(router, 'url', 'get').mockReturnValue('/sessions/update/1');
    const mockSession = {
      id: 1,
      name: 'Yoga Flow',
      description: 'Relaxing session',
      date: new Date('2024-01-15'),
      teacher_id: 2,
      users: [],
      createdAt: new Date(),
      updatedAt: new Date()
    };
    jest.spyOn(sessionApiService, 'detail').mockReturnValue(of(mockSession));
    jest.spyOn(sessionApiService, 'update').mockReturnValue(of({} as any));
    const snackBarSpy = jest.spyOn(matSnackBar, 'open').mockReturnValue({} as any);
    const navigateSpy = jest.spyOn(router, 'navigate').mockResolvedValue(true);

    component.ngOnInit();

    // WHEN: Submit form after modification
    setTimeout(() => {
      component.sessionForm?.patchValue({ name: 'Updated Yoga Flow' });
      component.submit();

      setTimeout(() => {
        expect(sessionApiService.update).toHaveBeenCalledWith('1', expect.any(Object));
        expect(snackBarSpy).toHaveBeenCalledWith('Session updated !', 'Close', { duration: 3000 });
        expect(navigateSpy).toHaveBeenCalledWith(['sessions']);
        done();
      }, 0);
    }, 0);
  });

  // UNIT TEST 6 : Verify form has required validators
  it('should have required validators on form fields', () => {
    // GIVEN: Form initialized
    jest.spyOn(router, 'url', 'get').mockReturnValue('/sessions/create');
    component.ngOnInit();

    // WHEN: Check validators
    const nameControl = component.sessionForm?.get('name');
    const dateControl = component.sessionForm?.get('date');
    const teacherControl = component.sessionForm?.get('teacher_id');
    const descriptionControl = component.sessionForm?.get('description');

    // THEN: All fields should be required
    expect(nameControl?.hasError('required')).toBeTruthy();
    expect(dateControl?.hasError('required')).toBeTruthy();
    expect(teacherControl?.hasError('required')).toBeTruthy();
    expect(descriptionControl?.hasError('required')).toBeTruthy();
  });

  // INTEGRATION TEST 7 : Verify non-admin users are redirected
  it('should redirect non-admin users to sessions list', () => {
    // GIVEN: User is not admin
    mockSessionService.sessionInformation.admin = false;
    const navigateSpy = jest.spyOn(router, 'navigate').mockResolvedValue(true);

    // WHEN: ngOnInit is called
    component.ngOnInit();

    // THEN: Should redirect to /sessions
    expect(navigateSpy).toHaveBeenCalledWith(['/sessions']);

    // Reset admin status
    mockSessionService.sessionInformation.admin = true;
  });

  // UNIT TEST 8 : Verify onUpdate flag behavior
  it('should set onUpdate to false for create mode', () => {
    // GIVEN: Create mode URL
    jest.spyOn(router, 'url', 'get').mockReturnValue('/sessions/create');

    // WHEN: ngOnInit is called
    component.ngOnInit();

    // THEN: onUpdate should be false
    expect(component.onUpdate).toBe(false);
  });

  // UNIT TEST 9 : Verify onUpdate flag behavior for update mode
  it('should set onUpdate to true for update mode', () => {
    // GIVEN: Update mode URL
    jest.spyOn(router, 'url', 'get').mockReturnValue('/sessions/update/1');
    jest.spyOn(sessionApiService, 'detail').mockReturnValue(of({
      id: 1,
      name: 'Test',
      description: 'Test',
      date: new Date(),
      teacher_id: 1,
      users: [],
      createdAt: new Date(),
      updatedAt: new Date()
    }));

    // WHEN: ngOnInit is called
    component.ngOnInit();

    // THEN: onUpdate should be true
    expect(component.onUpdate).toBe(true);
  });
});

import { HttpClientModule } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { Router } from '@angular/router';
import { expect } from '@jest/globals';
import { of } from 'rxjs';
import { SessionService } from 'src/app/services/session.service';
import { UserService } from 'src/app/services/user.service';

import { MeComponent } from './me.component';

describe('MeComponent', () => {
  let component: MeComponent;
  let fixture: ComponentFixture<MeComponent>;
  let userService: UserService;
  let sessionService: SessionService;
  let router: Router;
  let matSnackBar: MatSnackBar;

  const mockSessionService = {
    sessionInformation: {
      admin: true,
      id: 1
    },
    logOut: jest.fn()
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [MeComponent],
      imports: [
        MatSnackBarModule,
        HttpClientModule,
        MatCardModule,
        MatFormFieldModule,
        MatIconModule,
        MatInputModule
      ],
      providers: [
        { provide: SessionService, useValue: mockSessionService },
        UserService
      ],
    })
      .compileComponents();

    fixture = TestBed.createComponent(MeComponent);
    component = fixture.componentInstance;

    userService = TestBed.inject(UserService);
    sessionService = TestBed.inject(SessionService);
    router = TestBed.inject(Router);
    matSnackBar = TestBed.inject(MatSnackBar);

    fixture.detectChanges();
  });

  // UNIT TEST 1: Check that the component exists
  it('should create', () => {
    expect(component).toBeTruthy();
  });

  // INTEGRATION TEST 2: Verify user data is loaded on init
  it('should fetch user information on component initialization', () => {
    // GIVEN: Mock user data
    const mockUser = {
      id: 1,
      email: 'test@example.com',
      firstName: 'John',
      lastName: 'Doe',
      admin: true,
      password: 'password123',
      createdAt: new Date(),
      updatedAt: new Date()
    };

    const getUserSpy = jest.spyOn(userService, 'getById').mockReturnValue(of(mockUser));

    // WHEN: ngOnInit is called
    component.ngOnInit();

    // THEN: User data should be fetched and assigned
    expect(getUserSpy).toHaveBeenCalledWith('1');
    expect(component.user).toEqual(mockUser);
  });

  // UNIT TEST 3: Verify back navigation
  it('should navigate back when back() is called', () => {
    // GIVEN: Spy on window.history.back
    const backSpy = jest.spyOn(window.history, 'back');

    // WHEN: Call back method
    component.back();

    // THEN: window.history.back should be called
    expect(backSpy).toHaveBeenCalled();
  });

  // INTEGRATION TEST 4: Verify delete account flow
  it('should delete account, show snackbar, logout and navigate to home', (done) => {
    // GIVEN: Mock delete response and snackbar
    jest.spyOn(userService, 'delete').mockReturnValue(of(null));
    const snackBarSpy = jest.spyOn(matSnackBar, 'open').mockReturnValue({} as any);
    const navigateSpy = jest.spyOn(router, 'navigate').mockResolvedValue(true);

    // WHEN: Call delete method
    component.delete();

    // THEN: Wait for observable to complete
    setTimeout(() => {
      expect(userService.delete).toHaveBeenCalledWith('1');
      expect(snackBarSpy).toHaveBeenCalledWith(
        'Your account has been deleted !',
        'Close',
        { duration: 3000 }
      );
      expect(sessionService.logOut).toHaveBeenCalled();
      expect(navigateSpy).toHaveBeenCalledWith(['/']);
      done();
    }, 0);
  });

  // UNIT TEST 5: Verify user property is undefined initially
  it('should have undefined user before ngOnInit', () => {
    // GIVEN: Component before initialization
    const newComponent = new MeComponent(router, sessionService, matSnackBar, userService);

    // THEN: user should be undefined
    expect(newComponent.user).toBeUndefined();
  });

  // INTEGRATION TEST 6: Verify correct user ID is retrieved from session
  it('should use session ID to fetch user data', () => {
    // GIVEN: Mock session with specific ID
    mockSessionService.sessionInformation.id = 42;
    const mockUser = {
      id: 42,
      email: 'user42@example.com',
      firstName: 'Jane',
      lastName: 'Smith',
      admin: false,
      password: 'securepass',
      createdAt: new Date(),
      updatedAt: new Date()
    };

    const getUserSpy = jest.spyOn(userService, 'getById').mockReturnValue(of(mockUser));

    // WHEN: ngOnInit is called
    component.ngOnInit();

    // THEN: Should fetch user with session ID
    expect(getUserSpy).toHaveBeenCalledWith('42');
    expect(component.user?.id).toBe(42);
  });
});

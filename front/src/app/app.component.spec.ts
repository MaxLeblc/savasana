import { HttpClientModule } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { MatToolbarModule } from '@angular/material/toolbar';
import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { expect } from '@jest/globals';
import { of } from 'rxjs';
import { SessionService } from './services/session.service';

import { AppComponent } from './app.component';

describe('AppComponent', () => {
  let component: AppComponent;
  let sessionService: SessionService;
  let router: Router;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        RouterTestingModule,
        HttpClientModule,
        MatToolbarModule
      ],
      declarations: [
        AppComponent
      ],
    }).compileComponents();

    const fixture = TestBed.createComponent(AppComponent);
    component = fixture.componentInstance;
    sessionService = TestBed.inject(SessionService);
    router = TestBed.inject(Router);
  });

  // UNIT TEST 1 : Check that the component exists
  it('should create the app', () => {
    expect(component).toBeTruthy();
  });

  // INTEGRATION TEST 2 : Verify $isLogged observable returns session state
  it('should return observable from sessionService.$isLogged()', () => {
    // GIVEN: Mock isLogged observable
    const mockIsLogged$ = of(true);
    jest.spyOn(sessionService, '$isLogged').mockReturnValue(mockIsLogged$);

    // WHEN: Call $isLogged
    const result = component.$isLogged();

    // THEN: Should return the observable from sessionService
    expect(result).toBe(mockIsLogged$);
    expect(sessionService.$isLogged).toHaveBeenCalled();
  });

  // INTEGRATION TEST 3 : Verify logout calls sessionService and navigates
  it('should logout and navigate to home', () => {
    // GIVEN: Mock logout and navigation
    jest.spyOn(sessionService, 'logOut');
    const navigateSpy = jest.spyOn(router, 'navigate').mockResolvedValue(true);

    // WHEN: Call logout
    component.logout();

    // THEN: Should logout and navigate to home
    expect(sessionService.logOut).toHaveBeenCalled();
    expect(navigateSpy).toHaveBeenCalledWith(['']);
  });

  // UNIT TEST 4 : Verify component has access to sessionService
  it('should have sessionService injected', () => {
    // THEN: sessionService should be defined
    expect(sessionService).toBeDefined();
  });

  // UNIT TEST 5 : Verify component has access to router
  it('should have router injected', () => {
    // THEN: router should be defined
    expect(router).toBeDefined();
  });

  // INTEGRATION TEST 6 : Verify logout flow when user is logged in
  it('should complete logout flow from logged state', () => {
    // GIVEN: User is logged in
    jest.spyOn(sessionService, '$isLogged').mockReturnValue(of(true));
    jest.spyOn(sessionService, 'logOut');
    const navigateSpy = jest.spyOn(router, 'navigate').mockResolvedValue(true);

    // WHEN: User logs out
    component.logout();

    // THEN: Should perform complete logout
    expect(sessionService.logOut).toHaveBeenCalled();
    expect(navigateSpy).toHaveBeenCalledWith(['']);
  });
});

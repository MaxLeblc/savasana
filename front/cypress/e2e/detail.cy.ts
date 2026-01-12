describe('Session Detail Component', () => {
  const mockSession = {
    id: 1,
    name: 'Yoga Flow',
    description: 'A dynamic yoga session for all levels',
    date: '2024-06-15',
    teacher_id: 1,
    users: [2, 3],
    createdAt: new Date(),
    updatedAt: new Date()
  };

  const mockTeacher = {
    id: 1,
    firstName: 'Sophie',
    lastName: 'Laurent',
    createdAt: new Date(),
    updatedAt: new Date()
  };

  beforeEach(() => {
    // Mock teacher API for all tests
    cy.intercept('GET', '/api/teacher/1', {
      body: mockTeacher
    });
  });

  it('Should allow user to participate in a session', () => {
    // Mock session without current user
    const sessionWithoutUser = { ...mockSession, users: [2, 3] };

    cy.intercept('GET', '/api/session/1', {
      body: sessionWithoutUser
    });

    // Mock sessions list BEFORE login
    cy.intercept('GET', '/api/session', [sessionWithoutUser]).as('sessions');

    // Login as regular user
    cy.visit('/login');
    cy.intercept('POST', '/api/auth/login', {
      body: {
        id: 1,
        username: 'user@test.com',
        firstName: 'User',
        lastName: 'Test',
        admin: false,
        token: 'fake-jwt-token'
      },
    });
    cy.get('input[formControlName=email]').type('user@test.com');
    cy.get('input[formControlName=password]').type('password{enter}{enter}');
    cy.url().should('include', '/sessions');

    // Navigate to session detail
    cy.contains('button', 'Detail').click();

    // Verify Participate button is visible
    cy.contains('button', 'Participate').should('be.visible');

    // Mock participate API call
    cy.intercept('POST', '/api/session/1/participate/1', {
      statusCode: 200
    }).as('participate');

    // Mock updated session after participation
    cy.intercept('GET', '/api/session/1', {
      body: { ...mockSession, users: [1, 2, 3] }
    }).as('sessionAfterParticipate');

    // Click participate button
    cy.contains('button', 'Participate').click();

    // Wait for API calls
    cy.wait('@participate');
    cy.wait('@sessionAfterParticipate');

    // Verify button changed to "Do not participate"
    cy.contains('button', 'Do not participate').should('be.visible');
  });

  it('Should allow user to unparticipate from a session', () => {
    // Mock session with current user already participating
    const sessionWithUser = { ...mockSession, users: [1, 2, 3] };

    cy.intercept('GET', '/api/session/1', {
      body: sessionWithUser
    });

    // Mock sessions list BEFORE login
    cy.intercept('GET', '/api/session', [sessionWithUser]).as('sessions');

    // Login as regular user
    cy.visit('/login');
    cy.intercept('POST', '/api/auth/login', {
      body: {
        id: 1,
        username: 'user@test.com',
        firstName: 'User',
        lastName: 'Test',
        admin: false,
        token: 'fake-jwt-token'
      },
    });
    cy.get('input[formControlName=email]').type('user@test.com');
    cy.get('input[formControlName=password]').type('password{enter}{enter}');
    cy.url().should('include', '/sessions');

    // Navigate to session detail
    cy.contains('button', 'Detail').click();

    // Verify "Do not participate" button is visible
    cy.contains('button', 'Do not participate').should('be.visible');

    // Mock unparticipate API call
    cy.intercept('DELETE', '/api/session/1/participate/1', {
      statusCode: 200
    }).as('unparticipate');

    // Mock updated session after unparticipation
    cy.intercept('GET', '/api/session/1', {
      body: { ...mockSession, users: [2, 3] }
    }).as('sessionAfterUnparticipate');

    // Click unparticipate button
    cy.contains('button', 'Do not participate').click();

    // Wait for API calls
    cy.wait('@unparticipate');
    cy.wait('@sessionAfterUnparticipate');

    // Verify button changed back to "Participate"
    cy.contains('button', 'Participate').should('be.visible');
  });

  it('Should allow admin to delete a session', () => {
    cy.intercept('GET', '/api/session/1', {
      body: mockSession
    });

    // Mock sessions list BEFORE login
    cy.intercept('GET', '/api/session', [mockSession]).as('sessions');

    // Login as admin
    cy.visit('/login');
    cy.intercept('POST', '/api/auth/login', {
      body: {
        id: 1,
        username: 'yoga@studio.com',
        firstName: 'Admin',
        lastName: 'Admin',
        admin: true,
        token: 'fake-jwt-token'
      },
    });
    cy.get('input[formControlName=email]').type('yoga@studio.com');
    cy.get('input[formControlName=password]').type('test!1234{enter}{enter}');
    cy.url().should('include', '/sessions');

    // Navigate to session detail
    cy.contains('button', 'Detail').click();

    // Verify Delete button is visible for admin
    cy.contains('button', 'Delete').should('be.visible');

    // Mock delete API call
    cy.intercept('DELETE', '/api/session/1', {
      statusCode: 200
    }).as('deleteSession');

    // Click delete button
    cy.contains('button', 'Delete').click();

    // Wait for delete API call
    cy.wait('@deleteSession');

    // Verify snackbar message appears
    cy.contains('Session deleted !').should('be.visible');

    // Verify redirect to sessions list
    cy.url().should('include', '/sessions');
  });

  it('Should not display Delete button for regular user', () => {
    cy.intercept('GET', '/api/session/1', {
      body: mockSession
    });

    // Mock sessions list BEFORE login
    cy.intercept('GET', '/api/session', [mockSession]).as('sessions');

    // Login as regular user
    cy.visit('/login');
    cy.intercept('POST', '/api/auth/login', {
      body: {
        id: 1,
        username: 'user@test.com',
        firstName: 'User',
        lastName: 'Test',
        admin: false,
        token: 'fake-jwt-token'
      },
    });
    cy.get('input[formControlName=email]').type('user@test.com');
    cy.get('input[formControlName=password]').type('password{enter}{enter}');
    cy.url().should('include', '/sessions');

    // Navigate to session detail
    cy.contains('button', 'Detail').click();

    // Verify Delete button does not exist
    cy.contains('button', 'Delete').should('not.exist');
  });

  it('Should navigate back to sessions list', () => {
    cy.intercept('GET', '/api/session/1', {
      body: mockSession
    });

    // Mock sessions list BEFORE login
    cy.intercept('GET', '/api/session', [mockSession]).as('sessions');

    // Login
    cy.visit('/login');
    cy.intercept('POST', '/api/auth/login', {
      body: {
        id: 1,
        username: 'yoga@studio.com',
        firstName: 'Admin',
        lastName: 'Admin',
        admin: true,
        token: 'fake-jwt-token'
      },
    });
    cy.get('input[formControlName=email]').type('yoga@studio.com');
    cy.get('input[formControlName=password]').type('test!1234{enter}{enter}');
    cy.url().should('include', '/sessions');

    // Navigate to session detail
    cy.contains('button', 'Detail').click();

    // Verify we are on detail page
    cy.url().should('include', '/sessions/detail/1');

    // Click back button
    cy.get('button[mat-icon-button]').first().click();

    // Verify we're back on sessions list
    cy.url().should('include', '/sessions');
    cy.url().should('not.include', '/detail');
  });

  it('Should display session information correctly', () => {
    cy.intercept('GET', '/api/session/1', {
      body: mockSession
    });

    // Mock sessions list BEFORE login
    cy.intercept('GET', '/api/session', [mockSession]).as('sessions');

    // Login
    cy.visit('/login');
    cy.intercept('POST', '/api/auth/login', {
      body: {
        id: 1,
        username: 'yoga@studio.com',
        firstName: 'Admin',
        lastName: 'Admin',
        admin: true,
        token: 'fake-jwt-token'
      },
    });
    cy.get('input[formControlName=email]').type('yoga@studio.com');
    cy.get('input[formControlName=password]').type('test!1234{enter}{enter}');
    cy.url().should('include', '/sessions');

    // Navigate to session detail
    cy.contains('button', 'Detail').click();

    // Verify all session information is displayed
    cy.contains('Yoga Flow').should('be.visible');
    cy.contains('A dynamic yoga session for all levels').should('be.visible');
    cy.contains('Sophie LAURENT').should('be.visible');
    cy.contains('2 attendees').should('be.visible');
    cy.contains('June 15, 2024').should('be.visible');
  });

  it('Should update attendees count after participation', () => {
    // Mock session with 2 attendees
    const sessionBefore = { ...mockSession, users: [2, 3] };

    cy.intercept('GET', '/api/session/1', {
      body: sessionBefore
    });

    // Mock sessions list BEFORE login
    cy.intercept('GET', '/api/session', [sessionBefore]).as('sessions');

    // Login as regular user
    cy.visit('/login');
    cy.intercept('POST', '/api/auth/login', {
      body: {
        id: 1,
        username: 'user@test.com',
        firstName: 'User',
        lastName: 'Test',
        admin: false,
        token: 'fake-jwt-token'
      },
    });
    cy.get('input[formControlName=email]').type('user@test.com');
    cy.get('input[formControlName=password]').type('password{enter}{enter}');
    cy.url().should('include', '/sessions');

    // Navigate to session detail
    cy.contains('button', 'Detail').click();

    // Verify initial attendees count
    cy.contains('2 attendees').should('be.visible');

    // Mock participate API
    cy.intercept('POST', '/api/session/1/participate/1', {
      statusCode: 200
    });

    // Mock updated session with 3 attendees
    cy.intercept('GET', '/api/session/1', {
      body: { ...mockSession, users: [1, 2, 3] }
    }).as('updatedSession');

    // Participate
    cy.contains('button', 'Participate').click();

    cy.wait('@updatedSession');

    // Verify attendees count increased
    cy.contains('3 attendees').should('be.visible');
  });
});

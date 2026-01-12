describe('User Complete Journey', () => {
  const mockSession = {
    id: 1,
    name: 'Yoga Flow',
    description: 'A dynamic yoga session for all levels',
    date: '2024-06-15',
    teacher_id: 1,
    users: [2, 3], // Other users already participating
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

  const mockUser = {
    id: 1,
    email: 'jones@studio.com',
    firstName: 'Jones',
    lastName: 'Test',
    admin: false,
    createdAt: '2023-01-15',
    updatedAt: '2023-01-15'
  };

  it('User should login, participate in a rental, then delete their account', () => {
    // Step 1: Login as regular user (Jones)
    // Note: Cannot use cy.login() here because we need to display sessions
    cy.visit('/login');

    cy.intercept('POST', '/api/auth/login', {
      body: {
        id: 1,
        username: 'jones@studio.com',
        firstName: 'Jones',
        lastName: 'Test',
        admin: false,
        token: 'fake-jwt-token'
      },
    });

    cy.intercept('GET', '/api/session', [mockSession]).as('sessions');

    cy.get('input[formControlName=email]').type('jones@studio.com');
    cy.get('input[formControlName=password]').type('jonesjones{enter}{enter}');
    cy.url().should('include', '/sessions');

    // Verify user is not admin - no Create button
    cy.contains('button', 'Create').should('not.exist');

    // Step 2: View rental details
    cy.intercept('GET', '/api/session/1', {
      body: mockSession
    });

    cy.intercept('GET', '/api/teacher/1', {
      body: mockTeacher
    });

    // Verify session exists in list
    cy.contains('Yoga Flow').should('be.visible');

    // Click Detail button to navigate to detail page
    cy.contains('button', 'Detail').click();

    // Now we're on detail page
    cy.contains('Yoga Flow').should('be.visible');
    cy.contains('A dynamic yoga session for all levels').should('be.visible');

    // User should see Participate button (not Delete or Edit)
    cy.contains('button', 'Participate').should('be.visible');
    cy.contains('button', 'Edit').should('not.exist');
    cy.contains('button', 'Delete').should('not.exist');

    // Step 3: Participate in the rental
    cy.intercept('POST', '/api/session/1/participate/1', {
      statusCode: 200
    }).as('participate');

    cy.intercept('GET', '/api/session/1', {
      body: { ...mockSession, users: [1, 2, 3] }
    }).as('getUpdatedSession');

    cy.contains('button', 'Participate').click();

    cy.wait('@participate');
    cy.wait('@getUpdatedSession');

    // Verify button changed to "Do not participate"
    cy.contains('button', 'Do not participate').should('be.visible');

    // Step 4: Navigate to account/profile page
    cy.get('button[mat-icon-button]').first().click();
    cy.url().should('include', '/sessions');

    cy.intercept('GET', '/api/user/1', {
      body: mockUser
    }).as('getUser');

    cy.contains('span', 'Account').click();

    cy.wait('@getUser');
    cy.url().should('include', '/me');

    // Verify user information
    cy.contains('User information').should('be.visible');
    cy.contains('Name: Jones TEST').should('be.visible');
    cy.contains('Email: jones@studio.com').should('be.visible');

    // Verify admin badge is NOT displayed
    cy.contains('You are admin').should('not.exist');

    // Step 5: Delete user account
    cy.intercept('DELETE', '/api/user/1', {
      statusCode: 200
    }).as('deleteUser');

    cy.get('button[color="warn"]').should('be.visible').click();

    cy.wait('@deleteUser');

    // Verify redirect to home page after deletion
    cy.url().should('eq', Cypress.config().baseUrl + '/');
  });
});

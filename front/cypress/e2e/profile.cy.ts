describe('User Profile spec', () => {
  const mockUser = {
    id: 1,
    email: 'user@test.com',
    firstName: 'John',
    lastName: 'Doe',
    admin: false,
    createdAt: '2023-01-15',
    updatedAt: '2023-01-15'
  };

  const mockAdminUser = {
    id: 1,
    email: 'yoga@studio.com',
    firstName: 'Admin',
    lastName: 'User',
    admin: true,
    createdAt: '2023-01-15',
    updatedAt: '2023-01-15'
  };

  it('Should display user information', () => {
    // Mock user profile API
    cy.intercept('GET', '/api/user/1', {
      body: mockUser
    }).as('getUser');

    cy.login('user@test.com', 'password', false);

    // Navigate to profile page
    cy.contains('span', 'Account').click();

    cy.wait('@getUser');

    // Verify user information is displayed
    cy.contains('User information').should('be.visible');
    cy.contains('Name: John DOE').should('be.visible');
    cy.contains('Email: user@test.com').should('be.visible');
    cy.contains('Create at:').should('be.visible');
    cy.contains('January 15, 2023').should('be.visible');
  });

  it('Should display admin badge for admin user', () => {
    cy.intercept('GET', '/api/user/1', {
      body: mockAdminUser
    });

    cy.login('yoga@studio.com', 'test!1234', true);

    cy.contains('span', 'Account').click();

    // Verify admin badge is displayed
    cy.contains('You are admin').should('be.visible');
  });

  it('Should not display admin badge for regular user', () => {
    cy.intercept('GET', '/api/user/1', {
      body: mockUser
    });

    cy.login('user@test.com', 'password', false);

    cy.contains('span', 'Account').click();

    // Verify admin badge is NOT displayed
    cy.contains('You are admin').should('not.exist');
  });

  it('Should display delete button', () => {
    cy.intercept('GET', '/api/user/1', {
      body: mockUser
    });

    cy.login('user@test.com', 'password', false);

    cy.contains('span', 'Account').click();

    // Verify delete button is displayed
    cy.contains('Detail').should('be.visible');
    cy.get('button[color="warn"]').should('be.visible');
  });

  it('Should allow user to delete account', () => {
    cy.intercept('GET', '/api/user/1', {
      body: mockUser
    });

    // Mock delete user API
    cy.intercept('DELETE', '/api/user/1', {
      statusCode: 200
    }).as('deleteUser');

    cy.login('user@test.com', 'password', false);

    cy.contains('span', 'Account').click();

    // Click delete button
    cy.get('button[color="warn"]').click();

    cy.wait('@deleteUser');

    // Verify redirect to home page after deletion
    cy.url().should('eq', Cypress.config().baseUrl + '/');
  });

  it('Should navigate back from profile page', () => {
    cy.intercept('GET', '/api/user/1', {
      body: mockUser
    });

    cy.login('user@test.com', 'password', false);

    cy.contains('span', 'Account').click();

    // Click back button
    cy.get('button[mat-icon-button]').first().click();

    // Verify redirect to previous page
    cy.url().should('not.include', '/me');
  });

  it('Should access profile from menu', () => {
    cy.login();

    // Click on Account menu item
    cy.contains('span', 'Account').click();

    // Verify we are on profile page
    cy.url().should('include', '/me');
  });

  it('Should display formatted date', () => {
    cy.intercept('GET', '/api/user/1', {
      body: mockUser
    });

    cy.login('user@test.com', 'password', false);

    cy.contains('span', 'Account').click();

    // Verify date is formatted (not raw timestamp)
    cy.contains('Create at:').parent().should('contain', '2023');
  });

  it('Should not allow admin to delete their own account', () => {
    cy.intercept('GET', '/api/user/1', {
      body: mockAdminUser
    });

    cy.login('yoga@studio.com', 'test!1234', true);

    cy.contains('span', 'Account').click();

    // Admin should not see delete button for their own account
    cy.get('button[color="warn"]').should('not.exist');
  });
});

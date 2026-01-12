describe('Sessions spec', () => {
  it('Should display sessions list after login', () => {
    // Use the custom login command
    cy.login();

    // Verify we are on the sessions page
    cy.url().should('include', '/sessions');

    // Verify page title
    cy.contains('Rentals available').should('be.visible');
  });

  it('Should display empty sessions list for regular user', () => {
    // Login as regular user
    cy.login('yoga@studio.com', 'test!1234', false);

    // Verify we are on the sessions page
    cy.url().should('include', '/sessions');

    // Page should load without errors
    cy.contains('Rentals available').should('be.visible');
  });

  it('Should display Create button for admin users', () => {
    // Login as admin
    cy.login('yoga@studio.com', 'test!1234', true);

    // Admin should see Create button
    cy.contains('button', 'Create').should('be.visible');
  });

  it('Should not display Create button for regular users', () => {
    // Login as regular user (not admin)
    cy.login('user@test.com', 'password', false);

    // Regular user should not see Create button
    cy.contains('button', 'Create').should('not.exist');
  });
});

// ***********************************************
// Custom command declarations for TypeScript
// ***********************************************
declare namespace Cypress {
  interface Chainable {
    /**
     * Custom command to log in a user
     * @param email - User email (default: yoga@studio.com)
     * @param password - User password (default: test!1234)
     * @param admin - Whether user is admin (default: true)
     * @example cy.login()
     * @example cy.login('user@test.com', 'password', false)
     */
    login(email?: string, password?: string, admin?: boolean): Chainable<void>;
  }
}

// -- Login command --
Cypress.Commands.add('login', (email = 'yoga@studio.com', password = 'test!1234', admin = true) => {
  cy.visit('/login');

  // Mock the login API call
  cy.intercept('POST', '/api/auth/login', {
    body: {
      id: 1,
      username: email,
      firstName: admin ? 'Admin' : 'User',
      lastName: admin ? 'Admin' : 'Test',
      admin: admin,
      token: 'fake-jwt-token'
    },
  });

  // Mock the sessions API call (called after login redirect)
  cy.intercept('GET', '/api/session', []).as('session');

  // Fill in the login form
  cy.get('input[formControlName=email]').type(email);
  cy.get('input[formControlName=password]').type(`${password}{enter}{enter}`);

  // Wait for redirect to sessions page
  cy.url().should('include', '/sessions');
});

// ***********************************************
// This example commands.js shows you how to
// create various custom commands and overwrite
// existing commands.
//
// For more comprehensive examples of custom
// commands please read more here:
// https://on.cypress.io/custom-commands
// ***********************************************
//
//
// -- This is a parent command --
// Cypress.Commands.add("login", (email, password) => { ... })
//
//
// -- This is a child command --
// Cypress.Commands.add("drag", { prevSubject: 'element'}, (subject, options) => { ... })
//
//
// -- This is a dual command --
// Cypress.Commands.add("dismiss", { prevSubject: 'optional'}, (subject, options) => { ... })
//
//
// -- This will overwrite an existing command --
// Cypress.Commands.overwrite("visit", (originalFn, url, options) => { ... })

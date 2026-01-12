describe('Register spec', () => {
  beforeEach(() => {
    // Visit the registration page before each test
    cy.visit('/register');
  });

  it('Should register successfully', () => {
    // Mock the POST /api/auth/register API call
    // Simulate a successful response (empty)
    cy.intercept('POST', '/api/auth/register', {
      statusCode: 200,
      body: null
    }).as('registerRequest');

    // Fill in the registration form
    cy.get('input[formControlName=firstName]').type('John');
    cy.get('input[formControlName=lastName]').type('Doe');
    cy.get('input[formControlName=email]').type('john.doe@test.com');
    cy.get('input[formControlName=password]').type('password123');

    // Submit the form
    cy.get('button[type=submit]').click();

    // Verify the API call was made
    cy.wait('@registerRequest');

    // Verify redirection to login page
    cy.url().should('include', '/login');
  });

  it('Should display error on registration failure', () => {
    // Mock an error during registration (e.g., email already exists)
    cy.intercept('POST', '/api/auth/register', {
      statusCode: 400,
      body: { message: 'Email already exists' }
    }).as('registerRequest');

    // Fill in the form
    cy.get('input[formControlName=firstName]').type('John');
    cy.get('input[formControlName=lastName]').type('Doe');
    cy.get('input[formControlName=email]').type('existing@test.com');
    cy.get('input[formControlName=password]').type('password123');

    // Submit the form
    cy.get('button[type=submit]').click();

    // Wait for the response
    cy.wait('@registerRequest');

    // Verify that the error message is displayed
    cy.get('.error').should('be.visible');
    cy.get('.error').should('contain', 'An error occurred');
  });

  it('Should disable submit button when form is invalid', () => {
    // Button should be disabled initially (empty form)
    cy.get('button[type=submit]').should('be.disabled');

    // Fill in the form partially (email missing)
    cy.get('input[formControlName=firstName]').type('John');
    cy.get('input[formControlName=lastName]').type('Doe');
    cy.get('input[formControlName=password]').type('password123');

    // Button should still be disabled
    cy.get('button[type=submit]').should('be.disabled');

    // Add the email
    cy.get('input[formControlName=email]').type('john@test.com');

    // Now the button should be enabled
    cy.get('button[type=submit]').should('not.be.disabled');
  });

  it('Should require a valid email format', () => {
    // Enter an invalid email
    cy.get('input[formControlName=email]').type('invalid-email');
    cy.get('input[formControlName=firstName]').type('John');
    cy.get('input[formControlName=lastName]').type('Doe');
    cy.get('input[formControlName=password]').type('password123');

    // Button should be disabled (email validation)
    cy.get('button[type=submit]').should('be.disabled');

    // Correct the email
    cy.get('input[formControlName=email]').clear().type('john@test.com');

    // Button should be enabled
    cy.get('button[type=submit]').should('not.be.disabled');
  });
});

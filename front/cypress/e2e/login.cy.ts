describe('Login spec', () => {
  it('Login successfully', () => {
    cy.visit('/login')

    // Mock the login API call
    cy.intercept('POST', '/api/auth/login', {
      body: {
        id: 1,
        username: 'userName',
        firstName: 'firstName',
        lastName: 'lastName',
        admin: true
      },
    })

    // Mock the sessions API call
    cy.intercept(
      {
        method: 'GET',
        url: '/api/session',
      },
      []).as('session')

    // Fill in the login form with valid credentials
    cy.get('input[formControlName=email]').type("yoga@studio.com")
    cy.get('input[formControlName=password]').type(`${"test!1234"}{enter}{enter}`)

    // Verify redirection to sessions page
    cy.url().should('include', '/sessions')
  })
});

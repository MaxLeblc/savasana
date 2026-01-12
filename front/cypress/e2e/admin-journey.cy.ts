describe('Admin Complete Journey', () => {
  const mockTeachers = [
    {
      id: 1,
      firstName: 'Sophie',
      lastName: 'Laurent',
      createdAt: new Date(),
      updatedAt: new Date()
    },
    {
      id: 2,
      firstName: 'Marie',
      lastName: 'Dupont',
      createdAt: new Date(),
      updatedAt: new Date()
    }
  ];

  const mockSession = {
    id: 1,
    name: 'Morning Yoga',
    description: 'Start your day with energy',
    date: '2024-06-20',
    teacher_id: 1,
    users: [],
    createdAt: new Date(),
    updatedAt: new Date()
  };

  it('Admin should login, create, edit and delete a rental session', () => {
    // Step 1: Login as admin using custom command
    cy.login('yoga@studio.com', 'test!1234', true);

    // Step 2: Navigate to create new rental
    cy.intercept('GET', '/api/teacher', {
      body: mockTeachers
    }).as('getTeachers');

    cy.contains('button', 'Create').should('be.visible').click();

    cy.wait('@getTeachers');
    cy.url().should('include', '/sessions/create');
    cy.contains('Create session').should('be.visible');

    // Step 3: Fill form and create new rental
    cy.get('input[formControlName="name"]').type('Evening Relaxation');
    cy.get('input[formControlName="date"]').type('2024-07-01');
    cy.get('mat-select[formControlName="teacher_id"]').click();
    cy.get('mat-option').contains('Sophie Laurent').click();
    cy.get('textarea[formControlName="description"]').type('Relax and unwind after a long day');

    cy.intercept('POST', '/api/session', {
      statusCode: 200,
      body: {
        id: 2,
        name: 'Evening Relaxation',
        description: 'Relax and unwind after a long day',
        date: '2024-07-01',
        teacher_id: 1,
        users: [],
        createdAt: new Date(),
        updatedAt: new Date()
      }
    }).as('createSession');

    cy.intercept('GET', '/api/session', [
      {
        id: 2,
        name: 'Evening Relaxation',
        description: 'Relax and unwind after a long day',
        date: '2024-07-01',
        teacher_id: 1,
        users: [],
        createdAt: new Date(),
        updatedAt: new Date()
      }
    ]);

    cy.get('button[type="submit"]').click();

    cy.wait('@createSession');
    cy.url().should('include', '/sessions');
    cy.contains('Session created !').should('be.visible');

    // Step 4: View rental details
    cy.intercept('GET', '/api/session/2', {
      body: {
        id: 2,
        name: 'Evening Relaxation',
        description: 'Relax and unwind after a long day',
        date: '2024-07-01',
        teacher_id: 1,
        users: [],
        createdAt: new Date(),
        updatedAt: new Date()
      }
    });

    cy.intercept('GET', '/api/teacher/1', {
      body: mockTeachers[0]
    });

    // Click Detail button to navigate to detail page
    cy.contains('button', 'Detail').click();

    cy.contains('Evening Relaxation').should('be.visible');
    cy.contains('Relax and unwind after a long day').should('be.visible');
    cy.contains('button', 'Delete').should('be.visible');

    // Go back to sessions list
    cy.get('button[mat-icon-button]').first().click();
    cy.url().should('include', '/sessions');

    // Step 5: Edit the rental from the list
    cy.intercept('GET', '/api/teacher', {
      body: mockTeachers
    });

    // Click Edit button on the session card
    cy.contains('button', 'Edit').click();

    cy.url().should('include', '/sessions/update/2');
    cy.contains('Update session').should('be.visible');

    cy.get('input[formControlName="name"]').clear().type('Morning Flow Updated');

    cy.intercept('PUT', '/api/session/2', {
      statusCode: 200,
      body: {
        id: 2,
        name: 'Morning Flow Updated',
        description: 'Relax and unwind after a long day',
        date: '2024-07-01',
        teacher_id: 1,
        users: [],
        createdAt: new Date(),
        updatedAt: new Date()
      }
    }).as('updateSession');

    cy.get('button[type="submit"]').click();

    cy.wait('@updateSession');
    cy.url().should('include', '/sessions');
    cy.contains('Session updated !').should('be.visible');

    // Step 6: Delete the rental
    cy.intercept('GET', '/api/session/2', {
      body: {
        id: 2,
        name: 'Morning Flow Updated',
        description: 'Relax and unwind after a long day',
        date: '2024-07-01',
        teacher_id: 1,
        users: [],
        createdAt: new Date(),
        updatedAt: new Date()
      }
    });

    cy.intercept('GET', '/api/teacher/1', {
      body: mockTeachers[0]
    });

    // Click Detail button to navigate to detail page
    cy.contains('button', 'Detail').click();

    cy.intercept('DELETE', '/api/session/2', {
      statusCode: 200
    }).as('deleteSession');

    cy.intercept('GET', '/api/session', []);

    cy.contains('button', 'Delete').click();

    cy.wait('@deleteSession');
    cy.url().should('include', '/sessions');
    cy.contains('Session deleted !').should('be.visible');
  });
});

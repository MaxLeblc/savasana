#!/bin/bash

# Script to run Cypress tests with coverage
# Requires the backend and frontend to be already running

cd "$(dirname "$0")/front"

# Use Node 16 directly
export PATH="$HOME/.config/nvm/versions/node/v16.20.2/bin:$PATH"

echo "Using Node $(node --version)"
echo "Running Cypress tests..."
echo "Make sure backend (port 8080) and frontend (port 4200) are running!"
echo ""

# Check if services are running
if ! curl -s http://localhost:4200 > /dev/null; then
  echo "❌ Frontend not running on port 4200!"
  exit 1
fi

if ! curl -s http://localhost:8080/api/auth/register > /dev/null 2>&1; then
  echo "❌ Backend not running on port 8080!"
  exit 1
fi

echo "✅ Services are running"
echo ""

# Run Cypress tests in headless mode with Electron (built-in browser)
npx cypress run --headless --browser electron

CYPRESS_EXIT_CODE=$?

if [ $CYPRESS_EXIT_CODE -eq 0 ]; then
  echo ""
  echo "✅ Cypress tests passed!"
  echo ""
  echo "Generating coverage report..."
  npm run e2e:coverage
  echo ""
  echo "✅ Cypress coverage report generated in coverage/lcov-report/"
  echo "   Open coverage/lcov-report/index.html in a browser to view"
else
  echo ""
  echo "❌ Cypress tests failed with exit code $CYPRESS_EXIT_CODE"
fi

exit $CYPRESS_EXIT_CODE

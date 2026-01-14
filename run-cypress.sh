#!/bin/bash

# Script to run Cypress tests with coverage
# Starts frontend with instrumentation automatically if needed

cd "$(dirname "$0")/front"

# Use Node 16 directly
export PATH="$HOME/.config/nvm/versions/node/v16.20.2/bin:$PATH"

echo "Using Node $(node --version)"
echo "Running Cypress tests with coverage..."
echo ""

# Check if backend is running
if ! curl -s http://localhost:8080/api/auth/register > /dev/null 2>&1; then
  echo "Backend not running on port 8080!"
  echo "Start it with: ./start-back.sh"
  exit 1
fi

echo "✅ Backend is running"

# Check if frontend is already running
FRONTEND_RUNNING=false
if curl -s http://localhost:4200 > /dev/null 2>&1; then
  echo "Frontend already running on port 4200"
  echo "Note: For coverage, it should be started with instrumentation"
  echo "Continuing with existing frontend..."
  FRONTEND_RUNNING=true
else
  echo "Starting frontend with instrumentation for coverage..."
  rm -rf .nyc_output coverage
  mkdir -p .nyc_output
  
  npm run start:e2e > /tmp/ng-serve-e2e.log 2>&1 &
  FRONTEND_PID=$!
  
  echo "Waiting for frontend to be ready (max 90 seconds)..."
  for i in {1..90}; do
    if curl -s http://localhost:4200 > /dev/null 2>&1; then
      echo "Frontend ready with instrumentation!"
      sleep 3
      break
    fi
    if [ $i -eq 90 ]; then
      echo "Frontend failed to start after 90 seconds"
      echo "Check logs at: /tmp/ng-serve-e2e.log"
      kill $FRONTEND_PID 2>/dev/null
      exit 1
    fi
    sleep 1
  done
fi

echo ""

# Run Cypress tests in headless mode
npx cypress run --headless --browser electron

CYPRESS_EXIT_CODE=$?

# Stop frontend if we started it
if [ "$FRONTEND_RUNNING" = false ]; then
  echo ""
  echo "Stopping frontend..."
  kill $FRONTEND_PID 2>/dev/null
  wait $FRONTEND_PID 2>/dev/null
fi

if [ $CYPRESS_EXIT_CODE -eq 0 ]; then
  echo ""
  echo "Cypress tests passed!"
  echo ""
  echo "Coverage summary:"
  echo "===================="
  npm run e2e:coverage
  echo ""
  echo "Full coverage report: front/coverage/lcov-report/index.html"
else
  echo ""
  echo "Cypress tests failed with exit code $CYPRESS_EXIT_CODE"
fi

exit $CYPRESS_EXIT_CODE

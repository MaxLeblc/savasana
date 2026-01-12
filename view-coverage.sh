#!/bin/bash

# Script to view coverage reports

cd "$(dirname "$0")/front"

echo "📊 Coverage Reports"
echo "==================="
echo ""

# Check Jest coverage
if [ -d "coverage/jest/lcov-report" ]; then
  echo "✅ Jest coverage available:"
  echo "   file://$(pwd)/coverage/jest/lcov-report/index.html"
  
  # Display summary if available
  if [ -f "coverage/jest/coverage-summary.json" ]; then
    echo ""
    echo "   Quick summary:"
    grep -A 5 "total" coverage/jest/coverage-summary.json | head -6 || true
  fi
else
  echo "❌ Jest coverage not found. Run ./run-jest.sh first"
fi

echo ""

# Check Cypress coverage
if [ -d "coverage/lcov-report" ]; then
  echo "✅ Cypress coverage available:"
  echo "   file://$(pwd)/coverage/lcov-report/index.html"
else
  echo "❌ Cypress coverage not found. Run ./run-cypress.sh first"
fi

echo ""
echo "💡 Tip: Open the URLs above in your browser to see detailed reports"

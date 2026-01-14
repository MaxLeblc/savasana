#!/bin/bash

# Script to run Jest tests with coverage

cd "$(dirname "$0")/front"

# Use Node 16 directly
export PATH="$HOME/.config/nvm/versions/node/v16.20.2/bin:$PATH"

echo "Using Node $(node --version)"
echo "Running Jest tests with coverage..."

# Run Jest tests with coverage
npm test -- --coverage --coverageDirectory=coverage/jest

echo ""
echo "✅ Jest coverage report generated in coverage/jest/"
echo "   Open coverage/jest/lcov-report/index.html in a browser to view"

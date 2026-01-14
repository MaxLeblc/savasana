#!/bin/bash

# Script for frontend with instrumentation (for Cypress coverage)

cd "$(dirname "$0")/front"

# Use Node 16 directly
export PATH="$HOME/.config/nvm/versions/node/v16.20.2/bin:$PATH"

echo "Using Node $(node --version)"
echo "Starting frontend with instrumentation for E2E coverage..."
echo "Proxy: API calls to /api/* → http://localhost:8080"
echo ""

# Start Angular with instrumentation using local CLI
npx ng run yoga:serve-coverage

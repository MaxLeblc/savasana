#!/bin/bash

# Script for frontend with good Node version

cd "$(dirname "$0")/front"

# Use Node 16 directly
export PATH="$HOME/.config/nvm/versions/node/v16.20.2/bin:$PATH"

echo "Using Node $(node --version)"
echo "Starting frontend..."
echo "Proxy: API calls to /api/* → http://localhost:8080"
echo ""

# Start Angular with the proxy using local CLI
npx ng serve --proxy-config src/proxy.config.json

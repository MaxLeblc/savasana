#!/bin/bash

# Script for frontend with good Node version

cd "$(dirname "$0")/front"

# Configuration of nvm
export NVM_DIR="$HOME/.config/nvm"
[ -s "$NVM_DIR/nvm.sh" ] && \. "$NVM_DIR/nvm.sh"

# Use Node 16
nvm use 16

# Start Angular with the proxy
npm run start -- --proxy-config src/proxy.config.json

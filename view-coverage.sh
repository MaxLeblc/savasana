#!/bin/bash

# Script to view coverage reports

SCRIPT_DIR="$(dirname "$0")"

echo "📊 Coverage Reports"
echo "==================="
echo ""

# Check Jacoco (Backend) coverage
if [ -f "$SCRIPT_DIR/back/target/site/jacoco/index.html" ]; then
  echo "✅ Backend (Jacoco) coverage available:"
  echo "   file://$SCRIPT_DIR/back/target/site/jacoco/index.html"
  
  # Display summary if available
  if [ -f "$SCRIPT_DIR/back/target/site/jacoco/jacoco.csv" ]; then
    echo ""
    echo "   Quick summary:"
    tail -1 "$SCRIPT_DIR/back/target/site/jacoco/jacoco.csv" | awk -F',' '{
      missed_instr=$4; covered_instr=$5;
      missed_branch=$6; covered_branch=$7;
      total_instr=missed_instr+covered_instr;
      total_branch=missed_branch+covered_branch;
      if(total_instr>0) instr_cov=(covered_instr/total_instr)*100; else instr_cov=0;
      if(total_branch>0) branch_cov=(covered_branch/total_branch)*100; else branch_cov=0;
      printf "   Instruction coverage: %.1f%%\n   Branch coverage: %.1f%%\n", instr_cov, branch_cov;
    }'
  fi
else
  echo "❌ Backend (Jacoco) coverage not found. Run: cd back && JAVA_HOME=/usr/lib/jvm/java-17-openjdk mvn clean test"
fi

echo ""

cd "$SCRIPT_DIR/front"

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

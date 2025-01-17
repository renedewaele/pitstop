#!/bin/sh
set -e

ORIG_DIR="$PWD"

for DIR in deploy-application generate-jwt; do
  cd "$DIR"
  npm install
  npm run build
  cd "${ORIG_DIR}"
done

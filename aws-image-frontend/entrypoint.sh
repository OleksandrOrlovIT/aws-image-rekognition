#!/bin/sh

echo "Replacing API URL in JS files..."
find /usr/share/nginx/html -name "*.js" -exec sed -i "s|REACT_APP_API_URL_PLACEHOLDER|$REACT_APP_API_URL|g" {} \;

exec "$@"

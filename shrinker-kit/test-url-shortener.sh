#!/bin/bash

# Colors for output
GREEN='\033[0;32m'
RED='\033[0;31m'
NC='\033[0m' # No Color

echo "Testing URL Shortener API"
echo "------------------------"

# Test 1: Shorten URL
echo -e "\n${GREEN}Test 1: Shortening URL${NC}"
RESPONSE=$(curl -s -X POST http://localhost:8080/api/shorten \
     -H "Content-Type: application/json" \
     -d '{"url":"https://github.com/contiki-os/contiki/releases/tag/3.0"}')

# Extract short URL from response
SHORT_URL=$(echo $RESPONSE | grep -o '"shortUrl":"[^"]*' | grep -o '[^"]*$')

if [ ! -z "$SHORT_URL" ]; then
    echo "✓ Successfully created short URL: $SHORT_URL"
else
    echo -e "${RED}✗ Failed to create short URL${NC}"
    exit 1
fi

# Test 2: Retrieve Original URL
echo -e "\n${GREEN}Test 2: Retrieving Original URL${NC}"
HTTP_CODE=$(curl -s -o /dev/null -w "%{http_code}" -i http://localhost:8080/api/$SHORT_URL)

if [ "$HTTP_CODE" = "302" ]; then
    echo "✓ Successfully retrieved original URL (HTTP 302): $HTTP_CODE"
else
    echo -e "${RED}✗ Failed to retrieve URL (HTTP $HTTP_CODE)${NC}"
fi

# Test 3: Test Invalid Short URL
echo -e "\n${GREEN}Test 3: Testing Invalid Short URL${NC}"
HTTP_CODE=$(curl -s -o /dev/null -w "%{http_code}" -i http://localhost:8080/api/invalid123)

if [ "$HTTP_CODE" = "500" ]; then
    echo "✓ Correctly handled invalid URL (HTTP 500)"
else
    echo -e "${RED}✗ Unexpected response for invalid URL (HTTP $HTTP_CODE)${NC}"
fi

echo -e "\nTests completed!"
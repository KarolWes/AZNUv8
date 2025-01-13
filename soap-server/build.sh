#!/bin/bash

set -e
DOCKER_IMAGE="aznu/soap-server"

echo "Running Maven clean and package..."
mvn clean package -Dmaven.test.skip

# Build Docker image
echo "Building Docker image..."
docker build -t $DOCKER_IMAGE .
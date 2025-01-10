#!/bin/bash

# Exit on error
set -e

# Define variables
DOCKER_IMAGE="aznu/booking"

# Maven clean and package
echo "Running Maven clean and package..."
mvn clean package

# Build Docker image
echo "Building Docker image..."
docker build -t $DOCKER_IMAGE .

# Run Docker Compose
echo "Starting Docker Compose..."
docker-compose up -d

echo "Process completed. Docker image tagged as $DOCKER_IMAGE and Docker Compose is up."

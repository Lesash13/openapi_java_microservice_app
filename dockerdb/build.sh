#!/bin/bash

PG_VERSION="12.6-alpine"
IMAGE_BASE_NAME="pg573-image"
IMAGE_TAG="$PG_VERSION"

source ../utils/build-image.sh

# script main
BUILD_TYPE="$1" # local

if [ "$BUILD_TYPE" != "local" ]; then
  setupDocker
fi

setupGitlabUrl
printBuildInfo
buildImage
if [ "$BUILD_TYPE" != "local" ]; then
  pushImages
  removeLocalImages
fi

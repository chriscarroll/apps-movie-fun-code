#!/bin/bash

apt-get update && apt-get install -y curl

set -ex

if [ -z $MOVIE_FUN_URL ]; then
  echo "MOVIE_FUN_URL not set"
  exit 1
fi

pushd movie-fun-service-source
  echo "Running smoke tests for Movie Fun Service deployed at $MOVIE_FUN_URL"
  ./mvnw test
popd

exit 0

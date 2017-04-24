#!/usr/bin/env bash

if [ "$TRAVIS_BRANCH" = 'master' ] && [ "$TRAVIS_PULL_REQUEST" == 'false' ]; then
    openssl aes-256-cbc -K $encrypted_0165d7629ef9_key -iv $encrypted_0165d7629ef9_iv -in .travis/codesigning.asc.enc -out codesigning.asc -d
    gpg --fast-import codesigning.asc
fi

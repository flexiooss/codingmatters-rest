#!/usr/bin/env bash

if [ "$TRAVIS_BRANCH" = 'master' ] && [ "$TRAVIS_PULL_REQUEST" == 'false' ]; then
    openssl aes-256-cbc -K $encrypted_544db893dbcc_key -iv $encrypted_544db893dbcc_iv -in .travis/codesigning.asc.enc -out codesigning.asc -d
    gpg --fast-import codesigning.asc
fi

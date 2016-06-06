#!/bin/bash

if ! which 7z > /dev/null; then
  sudo apt-get install p7zip-full
fi

rm dist/ -r
mkdir dist

7z a dist/classes-only.zip src/ch/ethz/sae/*
7z a dist/all-sources.zip src/*

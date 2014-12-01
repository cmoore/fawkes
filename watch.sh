#!/bin/bash

while inotifywait -q -r -e modify --exclude '#$' ./src; do
    clear && mvn -q install && echo "Ok"
done


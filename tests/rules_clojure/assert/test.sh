#!/usr/bin/env bash
OUTPUT=$($1 $2)
if ! grep -q "$3" <<< "$OUTPUT"; then
    echo "Test output:"
    echo "$OUTPUT"
    echo "Does not contain:"
    echo "$3"
    exit 1
fi
exit 0

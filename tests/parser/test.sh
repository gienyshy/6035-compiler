#!/bin/sh

runparser() {
  java -jar `dirname $0`/../../dist/Compiler.jar -target parse $1
}

fail=0

for file in `dirname $0`/illegal/*; do
  if runparser $file 2> /dev/null; then
    echo "Illegal file $file parsed successfully.";
    fail=1
  fi
done

for file in `dirname $0`/legal/*; do
  if ! runparser $file; then
    echo "Legal file $file failed to parse.";
    fail=1
  fi
done

exit $fail;

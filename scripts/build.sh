#!/bin/sh
# Adapted from https://github.com/swannodette/mori/blob/master/scripts/build.sh

set -e

echo "Finalizing dds.js"

(cat resources/amd/wrapper.prefix;
 cat resources/public/js/release/dds.intermediate.js;
 cat resources/amd/wrapper.suffix) > resources/public/js/release/dds.min.js

echo "Build finished."

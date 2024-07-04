#!/bin/bash

usage() {
	echo "$(basename $0) -v version" >&2
	echo "" >&2
	echo "where:" >&2
	echo "     -v version to update to" >&2
	echo "" >&2
	echo "eg:" >&2
	echo "    $(basename $0) -v 2.1.0" >&2
	exit 1
}

VERSION=$1

while getopts ":v:" opt; do
  case ${opt} in
    v)
      VERSION=$OPTARG
      ;;
    \? ) usage "Invalid option"
      exit 1
      ;;
    : ) 
      echo "Option -$OPTARG requires an argument." 1>&2; exit 1;;
  esac
done
shift $((OPTIND -1))

if [ -z "$VERSION" ]
then
	usage
fi


mvnd versions:update-parent -DparentVersion=[$VERSION,$VERSION] -Dskip.nightly  
mvnd versions:set -DnewVersion=$VERSION

git add pom.xml
git add module-simple-tests/pom.xml
git add module-simple/pom.xml
git add webapp-tests/pom.xml
git add webapp/pom.xml

git commit -m "updates pom.xml files to $VERSION"

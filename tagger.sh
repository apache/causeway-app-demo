#!/bin/bash

usage() {
	echo "$(basename $0) -b base -v version -x" >&2
	echo "" >&2
	echo "where:" >&2
	echo "     -b base branch, either v2 or v3" >&2
	echo "     -v version to create new tag under" >&2
	echo "     -x execute (otherwise, is a dry run)" >&2
	echo "" >&2
	echo "eg:" >&2
	echo "    $(basename $0) -b v2 -v 2.1.0" >&2
	echo "    $(basename $0) -b v3 -v 3.1.0 -x" >&2
	echo "" >&2
	exit 1
}

BASE=""
VERSION=""
EXECUTE=""

while getopts ":b:v:x" opt; do
  case ${opt} in
    b)
      BASE=$OPTARG
      ;;
    v)
      VERSION=$OPTARG
      ;;
    x)
      EXECUTE="true"
      ;;
    \? ) usage "Invalid option"
      exit 1
      ;;
    : ) 
      echo "Option -$OPTARG requires an argument." 1>&2; exit 1;;
  esac
done
shift $((OPTIND -1))

if [ "$BASE" != "v2" -a "$BASE" != "v3" ]
then
	usage
fi

if [ -z "$VERSION" ]
then
	usage
fi


echo "BASE   : $BASE"
echo "VERSION: $VERSION"
echo "EXECUTE: $EXECUTE"


PREV_TAG=""
for TAG in $(git tag -l | grep "tags/$BASE/")
do
	NEW_TAG="tags/$VERSION/$(echo $TAG | cut -c9-)"

	if [ -n "$PREV_TAG" ]
	then
		echo "git cherry-pick $PREV_TAG..$TAG"

    if [ "$EXECUTE" = "true" ]
    then
		  git cherry-pick $PREV_TAG..$TAG
    fi
	fi

  echo "git tag $NEW_TAG"

  if [ "$EXECUTE" = "true" ]
  then
	  git tag $NEW_TAG
  fi

	PREV_TAG=$TAG
done

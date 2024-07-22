#!/bin/bash

usage() {
	echo "$(basename $0) -b base_branch -p previous_version -v new_version -x" >&2
	echo "" >&2
	echo "where:" >&2
	echo "     -b base branch, either v2 or v3" >&2
	echo "     -p previous version" >&2
	echo "     -v new version" >&2
	echo "     -x execute (otherwise, is a dry run)" >&2
	echo "" >&2
	echo "eg:" >&2
	echo "    $(basename $0) -b v2 -p 2.0.0 -v 2.1.0" >&2
	echo "    $(basename $0) -b v3 -p 3.0.0 -v 3.1.0 -x" >&2
	echo "" >&2
	exit 1
}

BASE_BRANCH=""
PREV_VERSION=""
NEW_VERSION=""
EXECUTE=""

while getopts ":b:p:v:x" opt; do
  case ${opt} in
    b)
      BASE_BRANCH=$OPTARG
      ;;
    p)
      PREV_VERSION=$OPTARG
      ;;
    v)
      NEW_VERSION=$OPTARG
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

if [ "$BASE_BRANCH" != "v2" -a "$BASE_BRANCH" != "v3" ]
then
	usage
fi

if [ -z "$PREV_VERSION" -o -z "$NEW_VERSION" ]
then
	usage
fi


echo "-b BASE_BRANCH  : $BASE_BRANCH"
echo "-p PREV_VERSION : $PREV_VERSION"
echo "-v NEW_VERSION  : $NEW_VERSION"
echo "-x EXECUTE      : $EXECUTE"


PREV_TAG=""
for TAG in $(git tag -l | grep "tags/$BASE_BRANCH/")
do
	NEW_TAG="tags/$NEW_VERSION/$(echo $TAG | cut -c9-)"

	if [ -n "$PREV_TAG" ]
	then
	  for COMMIT in $(git log $PREV_TAG..$TAG --pretty=format:"%H")
	  do

      echo "git cherry-pick $COMMIT"
      if [ "$EXECUTE" = "true" ]
      then
        git cherry-pick $COMMIT

        if [ $? -ne 0 ]
        then
            echo "Cherry-pick failed; aborting."
            exit 1
        fi
      fi

      if [ "$EXECUTE" = "true" ]
      then
        for POM_XML in $(find . -name "pom.xml")
        do
            sed -i "s/<version>$PREV_VERSION<\/version>/<version>$NEW_VERION<\/version>/g" "$POM_XML"
        done
        if [ -n "$(git status --porcelain)" ]
        then
          git add .
          git commit --amend --no-edit
        fi
      fi
    done
	fi

  echo "git tag -f $NEW_TAG"

  if [ "$EXECUTE" = "true" ]
  then
	  git tag -f $NEW_TAG
  fi

	PREV_TAG=$TAG
done

#!/usr/bin/env bash
usage() {
  echo "$(basename $0): [-x] -P [ -f tag]" >&2
  echo "  -x : execute; otherwise acts as a dry-run" >&2
  echo "  -P : skip push; otherwise will push all changes to origin" >&2
  echo "  -f : from tag" >&2
  echo "" >&2
}

EXECUTE="no"
PUSH="yes"
FROM_TAG=""

while getopts ":hPf:x" arg; do
  case $arg in
    h)
      usage
      exit 0
      ;;
    x)
      EXECUTE="yes"
      ;;
    f)
      FROM_TAG=$(echo $OPTARG | sed s/v3/v2/)
      shift
      ;;
    P)
      PUSH="no"
      ;;
    *)
      usage
      exit 1
  esac
done

shift $((OPTIND-1))

echo "-x : EXECUTE : ${EXECUTE}"
echo "-P : PUSH    : ${PUSH}"
echo "-f : FROM_TAG: ${FROM_TAG}"

TAG_NAMES=/tmp/$(basename $0).tag_names.$$

if [ -n $FROM_TAG ]
then
  git tag | grep v2 | awk -v pt="$FROM_TAG" '$0 > pt' > $TAG_NAMES
  PREV=$FROM_TAG
else
  git tag | grep v2 > $TAG_NAMES
  PREV=""
fi

for TAGV2 in $(cat $TAG_NAMES)
do
  TAGV3=$(echo $TAGV2 | sed s/v2/v3/)

  if [ -n "$PREV" ]
  then
    echo git cherry-pick $PREV..$TAGV2
    if [ "$EXECUTE" = "yes" ]
    then
      git cherry-pick $PREV..$TAGV2
      read -p "continue?"
    fi
  fi

  echo git tag $TAGV3
  if [ "$EXECUTE" = "yes" ]
  then
    git tag $TAGV3
    read -p "continue?"
  fi

  PREV=$TAGV2
done


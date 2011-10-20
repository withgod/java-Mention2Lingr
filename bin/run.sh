#!/bin/sh

base=`dirname $0`

classpath=$base/../target/classes
for i in `find $base/../target/dependency -type f -name *.jar`; do
	classpath="$i:$classpath"
done

java -classpath $classpath withgod.lingr.Mention2Lingr $*


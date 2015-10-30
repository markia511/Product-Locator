#!/bin/bash

BASE="$HOME/geocoder"
echo $BASE

export CLASSPATH="$BASE/Geocoder.jar:$BASE/lib/commons-compress-1.4.1.jar:$BASE/lib/commons-codec-1.6.jar:$BASE/lib/dom4j-1.6.1.jar:$BASE/lib/mail-1.4.jar:$BASE/lib/ojdbc6.jar:$BASE/lib/poi-3.8.jar:$BASE/lib/poi-ooxml-3.8.jar:$BASE/lib/poi-ooxml-schemas-3.8.jar:$BASE/lib/poi-scratchpad-3.8.jar:$BASE/lib/xmlbeans-2.3.0.jar:$BASE"
echo $CLASSPATH

LOG=$BASE/logs/load_`date +"%Y_%m_%d_%H_%M_%S"`.log
echo $LOG

/usr/java/jdk1.6.0_33/bin/java -Xmx3000M -Xms3000M -Djava.security.egd=file:///dev/urandom -Xloggc:./loggc com.ko.lct.job.geocoding.GeoCodingProcess parse >> $LOG 2>&1

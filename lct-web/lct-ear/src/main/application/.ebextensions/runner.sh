#!/bin/bash

fileName=/etc/httpd/conf.d/elasticbeanstalk.conf
logFile=/home/ec2-user/post.deploy.status

# Function to log the messages to the file
function logMessage() {
     echo "$(date) $1" >>$logFile;
}

# Web application name to be secured
# Could be specified within the environment configuration with the SECUREDWEBAPP name

defaultSecureApp="lct-web";
if [ -n "$SECUREDWEBAPP" ]; then
    securedApp=$SECUREDWEBAPP;
    logMessage "Secured web application name provided within environment config $SECUREDWEBAPP";
else 
    securedApp=$defaultSecureApp;
    logMessage "Default secured web application name is used $defaultSecureApp";
fi

# The list of IPs that allowed to access the secured application.
# Could be either provided via environment variable ALLOWEDIPS or here

defaultAllowedIPs=(213.184.243.* 217.21.56.* 217.21.63.* 213.184.231.* 86.57.255.* 161.162.*.* 205.160.*.* 216.52.207.* 204.124.196.* 208.100.40.*);
if [ -n "$ALLOWEDIPS" ]; then
    allowedIPs=$ALLOWEDIPS;
    logMessage "Allowed IPs provided within environment config $ALLOWEDIPS"; 
else 
    allowedIPs=${defaultAllowedIPs[*]};
    logMessage "Default set of IPs is used ${defaultAllowedIPs[*]}";
fi


tmpStr=
for item in ${allowedIPs[*]}
do
    tmp=$(echo $item | sed 's/\*/\[0-9\]\{1,3\}/g' | sed 's/\./\\\\\./g');
    tmpStr+=$(echo "		SetEnvIF X-Forwarded-For \"(,| |^)$tmp(,| |$)\" allowedIP \\");
    tmpStr+=$'\n';
done

if grep -q $securedApp $fileName; then
    logMessage "$securedApp exists within $fileName";
else
	sed "/Proxy>/ a\
    <Location /$securedApp>\n\n$tmpStr \
		Order deny,allow\n \
		Allow from env=allowedIP\n \
		Deny from all\n \
	</Location>" $fileName >$fileName.tmp;
	  
	mv -f $fileName.tmp $fileName;
    logMessage "$securedApp location added to $fileName";
fi;


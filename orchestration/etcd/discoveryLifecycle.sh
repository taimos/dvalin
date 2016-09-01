#!/usr/bin/env bash

INSTANCE=$(uuidgen)
BASEURL=http://localhost:2379/v2/keys/dvalin/discovery

curl -L -X PUT -d 'value={"host":"testhost","status":"STARTING"}' -d ttl=15 ${BASEURL}/testservice/${INSTANCE}
curl -L ${BASEURL}/testservice/${INSTANCE}

sleep 5
curl -L -X PUT -d 'value={"host":"testhost","status":"STARTED","properties":{"port":8080}}' -d ttl=15 ${BASEURL}/testservice/${INSTANCE}
curl -L ${BASEURL}/testservice/${INSTANCE}

#sleep 5
#curl -L -X DELETE ${BASEURL}/testservice/${INSTANCE}




#!/usr/bin/env bash

BASEURL=http://localhost:2379/v2/keys/dvalin/config

curl -L -X PUT -d 'value=bar' ${BASEURL}/foo
curl -L ${BASEURL}/foo

sleep 3

curl -L -X PUT -d 'value=blubb' ${BASEURL}/foo
curl -L ${BASEURL}/foo

sleep 3

#curl -L -X DELETE ${BASEURL}/foo
#curl -L ${BASEURL}/foo

sleep 3

curl -L -X PUT -d 'value=baz' -d ttl=5 ${BASEURL}/baz
curl -L ${BASEURL}/baz




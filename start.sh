#!/usr/bin/env bash

# URLs
export KNIME_ADAPTER_URL=http://localhost:8080/knime
export NIFI_ADAPTER_URL=http://localhost:9090/nifi-api

# Credentials
export JOBMANAGER_USERNAME=admin
export JOBMANAGER_PASSWORD=saagie2018!

# Start command
java -jar $*

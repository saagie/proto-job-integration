#!/usr/bin/env bash

# URLs
export KNIME_ADAPTER_URL=http://localhost:8080/knime
export NIFI_ADAPTER_URL=http://localhost:9090/nifi-api

# AWS
export AWS_SERVICE=glue
export AWS_REGION=us-east-1
export AWS_URL=https://glue.us-east-1.amazonaws.com
export AWS_PUBLIC_KEY=AKIAJUEB6C3GIVV3EESA
export AWS_SECRET_KEY=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

# Credentials
export JOBMANAGER_USERNAME=admin
export JOBMANAGER_PASSWORD=saagie2018!

# Start command
java -Dspring.profiles.active=glue,demo,none -jar $*

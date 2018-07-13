#!/usr/bin/env bash

export KNIME_ADAPTER_URL=http://localhost:8080/knime
export KNIME_ADAPTER_USERNAME=admin
export KNIME_ADAPTER_PASSWORD=saagie2018!

java -jar $*

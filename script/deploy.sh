#!/usr/bin/env bash
mvn clean deploy -P release -Dmaven.test.skip=true -Darguments=gpg.passphrase=""
#!/usr/bin/env bash
mvn clean deploy -Dmaven.test.skip=true -Darguments=gpg.passphrase=""
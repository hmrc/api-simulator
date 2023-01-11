#!/usr/bin/env bash
sbt clean compile scalafmtAll scalafixAll coverage test it:test component:test coverageOff coverageReport

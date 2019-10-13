// SPDX-FileCopyrightText: ©  Basil Peace
// SPDX-License-Identifier: Apache-2.0
//noinspection GroovyUnusedAssignment
@SuppressWarnings(['UnusedVariable', 'NoDef', 'VariableTypeRequired'])
@Library('jenkins-pipeline-shared-library@v3.0.1') dummy

defaultJVMPipeline(
  publicReleases: Boolean.TRUE,
  tests: [
    'test',
    'functionalTest',
  ].toSet(),
  // TODO gradleTest:
  gradlePlugin: Boolean.TRUE
)

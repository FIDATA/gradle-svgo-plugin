// SPDX-Copyright: ©  Basil Peace
// SPDX-License-Identifier: Apache-2.0
package org.fidata.gradle.svgo

import groovy.transform.CompileStatic
import org.gradle.api.Project
import org.ysb33r.grolifant.api.exec.generic.AbstractToolExecSpec

@CompileStatic
final class SvgoExecSpec extends AbstractToolExecSpec {
  SvgoExecSpec(Project project) {
    super(project)
    executable 'svgo'
  }
}

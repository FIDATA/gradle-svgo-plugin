// SPDX-Copyright: ©  Basil Peace
// SPDX-License-Identifier: Apache-2.0
package org.fidata.gradle.svgo

import groovy.transform.CompileStatic
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.Input
import org.gradle.internal.os.OperatingSystem
import org.ysb33r.grolifant.api.exec.AbstractToolExtension
import org.ysb33r.grolifant.api.exec.ResolvableExecutable

@CompileStatic
final class SvgoExtension extends AbstractToolExtension {
  /**
   * Name of svgo project extension
   */
  static final String NAME = 'svgo'

  protected SvgoExtension(Project project) {
    super(project)
    executable path: OperatingSystem.current().findInPath('svgo')
  }

  protected SvgoExtension(Task task, String projectExtName) {
    super(task, projectExtName)
  }

  @Override
  @Input
  ResolvableExecutable getResolvableExecutable() {
    super.resolvableExecutable
  }
}

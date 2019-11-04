package org.fidata.gradle.svgo

import groovy.transform.CompileStatic
import javax.annotation.Nullable
import org.gradle.api.Plugin
import org.gradle.api.Project

@CompileStatic
class SvgoBasePlugin implements Plugin<Project> {
  @Override
  void apply(Project project) {
    @Nullable SvgoExtension parentExtension = project.parent?.extensions?.findByType(SvgoExtension)
    project.extensions.create(SvgoExtension.NAME, SvgoExtension, project, parentExtension)
    project.extensions.extraProperties.set(SvgoTask.simpleName, SvgoTask)
  }
}

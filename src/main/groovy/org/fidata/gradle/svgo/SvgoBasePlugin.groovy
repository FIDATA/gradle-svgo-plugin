package org.fidata.gradle.svgo

import groovy.transform.CompileStatic
import org.gradle.api.Plugin
import org.gradle.api.Project

@CompileStatic
class SvgoBasePlugin implements Plugin<Project> {
  @Override
  void apply(Project project) {
    project.extensions.create(MyExtension.NAME,MyExtension,project)


    project.extensions.extraProperties.set(SvgoTask.simpleName, SvgoTask)
  }
}

// SPDX-Copyright: ©  Basil Peace
// SPDX-License-Identifier: Apache-2.0
package org.fidata.gradle.svgo

import javax.inject.Inject
import org.gradle.api.file.ConfigurableFileTree
import org.gradle.api.file.FileCollection
import org.gradle.api.file.ProjectLayout
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Provider
import groovy.transform.CompileStatic
import org.gradle.api.logging.LogLevel
import org.gradle.api.provider.Property
import org.gradle.api.provider.ProviderFactory
import org.gradle.api.tasks.Console
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.OutputFiles
import org.ysb33r.grolifant.api.exec.AbstractExecWrapperTask

@CompileStatic
final class SvgoTask extends AbstractExecWrapperTask<SvgoExecSpec, SvgoExtension> {
  @Delegate(interfaces = false, excludeTypes = [Provider<SvgoExecSpec>])
  private final SvgoSpec svgoSpec

  private final SvgoExtension toolExtension

  @Internal
  final Property<File> input

  @Internal
  final Property<String> string

  @Internal
  final Property<Boolean> recursive

  @Internal
  final Property<File> output

  // Note that this returned collection is mutable, but private
  private FileCollection getFilesInInputDir(File inputFile) {
    project.fileTree(inputFile) { ConfigurableFileTree configurableFileTree ->
      configurableFileTree.include((recursive.get() ? '**/' : '') + '*.svg')
    }
  }

  @InputFiles
  final FileCollection inputFiles

  @OutputFiles
  final FileCollection outputFiles

  @Inject
  SvgoTask(ProviderFactory providerFactory, ObjectFactory objectFactory, ProjectLayout projectLayout) {
    this.toolExtension = new SvgoExtension(this, SvgoExtension.NAME /* TODO */)
    this.extensions.add 'toolConfig', toolExtension
    this.svgoSpec = new SvgoSpec(project, providerFactory, objectFactory)
    this.input = objectFactory.property(File)
    this.output = objectFactory.property(File)
    this.string = objectFactory.property(String)
    this.recursive = objectFactory.property(Boolean).convention(Boolean.FALSE)
    this.inputFiles = projectLayout.files {
      File inputFile = input.get()
      if (inputFile.directory) {
        getFilesInInputDir(inputFile)
      } else {
        inputFile
      }
    }
    this.outputFiles = projectLayout.files {
      File outputFile = output.get()
      File inputFile = input.get()
      if (inputFile.directory) {
        getFilesInInputDir(inputFile).collect { File singleInputFile ->
          new File(outputFile, inputFile.relativePath(singleInputFile))
        }
      } else {
        outputFile
      }
    }
  }

  @Override
  protected final SvgoExecSpec createExecSpec() {
    new SvgoExecSpec(project, toolExtension.getResolver())
  }

  @Override
  protected SvgoExecSpec configureExecSpec(SvgoExecSpec execSpec) {
    // Run in temporary dir so that log files don't pollute project root dir
    execSpec.workingDir this.temporaryDir
    if (input.present) {
      if (string.present) {
        throw new IllegalStateException('Both input and string are set')
      }
      File inputFile = input.get()
      if (inputFile.directory) {
        execSpec.scriptArgs '-f', inputFile
        if (recursive.get()) {
          execSpec.scriptArgs '--recursive'
        }
      } else {
        execSpec.scriptArgs '-i', inputFile
      }
    } else {
      if (!string.present) {
        throw new IllegalStateException('One of input or string should be set')
      }
      execSpec.scriptArgs '-s', string.get()
    }
    execSpec.scriptArgs '-o', output.get()
    if (precision.present) {
      execSpec.scriptArgs "--precision=${ precision.get() }"
    }
    // config
    if (disable.present) {
      execSpec.scriptArgs "--disable=${ disable.get().join(',') }"
    }
    if (enable.present) {
      execSpec.scriptArgs "--enable=${ enable.get().join(',') }"
    }
    // datauri
    if (multipass.get()) {
      execSpec.scriptArgs '--multipass'
    }
    if (pretty.get()) {
      execSpec.scriptArgs '--pretty'
      if (indent.present) {
        execSpec.scriptArgs "--indent=${ indent.get() }"
      }
    }
    if (quiet.get()) {
      execSpec.scriptArgs '--quiet'
    }
    execSpec
  }
}

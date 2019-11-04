// SPDX-Copyright: ©  Basil Peace
// SPDX-License-Identifier: Apache-2.0
package org.fidata.gradle.svgo

import groovy.json.JsonOutput
import groovy.transform.CompileStatic
import javax.inject.Inject
import org.gradle.api.file.ConfigurableFileTree
import org.gradle.api.file.FileCollection
import org.gradle.api.provider.Property
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.OutputFiles
import org.ysb33r.grolifant.api.exec.specific.AbstractExecWrapperTask

@CompileStatic
final class SvgoTask extends AbstractExecWrapperTask<SvgoExtension, SvgoExecSpec> {
  @Delegate(methodAnnotations = true)
  private final SvgoExtension toolExtension

  public static final Class<DataUri> DataUri = org.fidata.gradle.svgo.DataUri

  @Override
  protected SvgoExtension getToolExtension() {
    this.@toolExtension
  }

  @Internal
  final Property<File> input

  @Internal
  final Property<String> string

  @Internal
  final Property<Boolean> recursive

  @Internal
  final Property<File> output

  // Note that returned collection is mutable, but private TOTEST
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
  SvgoTask() {
    this.@toolExtension = new SvgoExtension(project, project.extensions.findByType(SvgoExtension))
    this.@input = objectFactory.property(File)
    this.@output = objectFactory.property(File)
    this.@string = objectFactory.property(String)
    this.@recursive = objectFactory.property(Boolean).convention(Boolean.FALSE)
    this.@inputFiles = projectLayout.files {
      File inputFile = input.get()
      if (inputFile.directory) {
        getFilesInInputDir(inputFile)
      } else {
        inputFile
      }
    }
    this.@outputFiles = projectLayout.files {
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
    SvgoExecSpec execSpec = new SvgoExecSpec(project)
    // Run in temporary dir so that log files don't pollute project's root dir
    execSpec.workingDir this.temporaryDir
    if (input.present) {
      if (string.present) {
        throw new IllegalStateException('Both input and string are set')
      }
      File inputFile = input.get()
      if (inputFile.directory) {
        execSpec.exeArgs "--folder=$inputFile"
        if (recursive.get()) {
          execSpec.exeArgs '--recursive'
        }
      } else {
        execSpec.exeArgs "--input=$inputFile"
      }
    } else {
      if (!string.present) {
        throw new IllegalStateException('One of input or string should be set')
      }
      execSpec.exeArgs "--string=${ string.get() }"
    }
    execSpec.exeArgs "--output=${ output.get() }"
    if (precision.present) {
      execSpec.exeArgs "--precision=${ precision.get() }"
    }
    if (configFile.present) {
      execSpec.exeArgs "--config=${ configFile.get().asFile }"
    } else if (config.present) {
      execSpec.exeArgs "--config=${ JsonOutput.toJson(config.get()) }"
    }
    if (disable.present) {
      execSpec.exeArgs "--disable=${ disable.get().join(',') }"
    }
    if (enable.present) {
      execSpec.exeArgs "--enable=${ enable.get().join(',') }"
    }
    if (dataUri.present) {
      execSpec.exeArgs "--datauri=${ dataUri.get() }"
    }
    if (multipass.get()) {
      execSpec.exeArgs '--multipass'
    }
    if (pretty.get()) {
      execSpec.exeArgs '--pretty'
      if (indent.present) {
        execSpec.exeArgs "--indent=${ indent.get() }"
      }
    }
    if (quiet.get()) {
      execSpec.exeArgs '--quiet'
    }
    execSpec
  }
}

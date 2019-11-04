// SPDX-Copyright: ©  Basil Peace
// SPDX-License-Identifier: Apache-2.0
package org.fidata.gradle.svgo

import groovy.json.JsonSlurper
import groovy.transform.CompileStatic
import javax.annotation.Nullable
import org.gradle.api.Project
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.logging.LogLevel
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Console
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.ysb33r.grolifant.api.exec.specific.AbstractToolExtension

@CompileStatic
final class SvgoExtension extends AbstractToolExtension<SvgoExtension> {
  /**
   * Name of svgo project extension
   */
  static final String NAME = 'svgo'

  @Input
  @Optional
  final Property<Integer> precision

  @Internal
  final RegularFileProperty configFile

  @Internal
  final Property<ConfigObject> config
  
  void setConfig(Closure obj) {
    ConfigObject configObject = new ConfigObject()
    obj.delegate = configObject
    obj.resolveStrategy = Closure.DELEGATE_FIRST 
    obj.call(configObject)
    config.set configObject
  }
  
  @Input
  @Optional
  protected final Provider<Map<?, ?>> configValue

  @Input
  @Optional
  final ListProperty<String> enable

  @Input
  @Optional
  final ListProperty<String> disable

  public static final Class<DataUri> DataUri = org.fidata.gradle.svgo.DataUri

  @Input
  @Optional
  final Property<DataUri> dataUri

  @Input
  final Property<Boolean> multipass

  @Input
  final Property<Boolean> pretty

  @Internal
  final Property<Integer> indent

  @Input
  @Optional
  protected final Provider<Integer> indentIfPretty

  @Console
  final Property<Boolean> quiet

  protected SvgoExtension(Project project, @Nullable SvgoExtension parentExtension) {
    super(project, parentExtension)
    executable.convention 'svgo'

    this.@precision = objectFactory.property(Integer)
    this.@configFile = objectFactory.fileProperty()
    this.@config = objectFactory.property(ConfigObject)
    this.@enable = objectFactory.listProperty(String) // empty by default since https://github.com/gradle/gradle/pull/7963
    this.@disable = objectFactory.listProperty(String)
    this.@multipass = objectFactory.property(Boolean)
    this.@dataUri = objectFactory.property(DataUri)
    this.@pretty = objectFactory.property(Boolean)
    this.@indent = objectFactory.property(Integer)
    this.@quiet = objectFactory.property(Boolean)

    if (parentExtension != null) {
      this.@precision.convention parentExtension.precision
      this.@configFile.convention parentExtension.configFile
      this.@config.convention parentExtension.config
      this.@enable.convention parentExtension.enable
      this.@disable.convention parentExtension.disable
      this.@dataUri.convention parentExtension.dataUri
      this.@multipass.convention parentExtension.multipass
      this.@pretty.convention parentExtension.pretty
      this.@indent.convention parentExtension.indent
      this.@quiet.convention parentExtension.quiet
    } else {
      this.@multipass.convention(Boolean.FALSE)
      this.@pretty.convention(Boolean.FALSE)
      this.@quiet.convention(providerFactory.provider { (project.logging.level ?: project.gradle.startParameter.logLevel) >= LogLevel.QUIET })
    }
    
    this.@configValue = providerFactory.provider {
      if (configFile.present) {
        if (config.present) {
          throw new IllegalStateException('Both configFile and config are set')
        }
        (Map<?, ?>)new JsonSlurper().parse(configFile.get().asFile)
      } else if (config.present) {
        config.get()
      } else {
        null
      }
    }

    this.@indentIfPretty = providerFactory.provider {
      pretty.get() ? this.indent.getOrNull() : null
    }
  }
}

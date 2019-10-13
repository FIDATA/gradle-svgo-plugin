package org.fidata.gradle.svgo

import groovy.transform.CompileStatic
import javax.inject.Inject
import org.gradle.api.Project
import org.gradle.api.Transformer
import org.gradle.api.file.ConfigurableFileTree
import org.gradle.api.file.FileCollection
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.provider.ProviderFactory
import org.gradle.api.tasks.Console
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Optional
import org.ysb33r.grolifant.api.exec.ExternalExecutable

@CompileStatic
final class SvgoSpec implements Provider<SvgoExecSpec> {
  private final Project project
  private final ProviderFactory providerFactory
  private final ExternalExecutable registry

  @Input
  @Optional
  final Property<Integer> precision

  @Input
  @Optional
  final RegularFileProperty configFile

  @Input
  @Optional
  private final MapProperty<String, Object> config

  @Input
  @Optional
  final ListProperty<String> enable

  @Input
  @Optional
  final ListProperty<String> disable

  @Input
  final Property<Boolean> multipass

  @Input
  final Property<Boolean> pretty

  @Internal
  final Property<Integer> indent

  @Input
  @Optional
  final Provider<Integer> indentIfPretty

  @Console
  final Property<Boolean> quiet

  @Inject
  SvgoSpec(Project project /* TODO: Gradle 6.0 */, ProviderFactory providerFactory, ObjectFactory objectFactory) {
    this.project = project
    this.providerFactory = providerFactory
    this.registry = registry
    this.precision = objectFactory.property(Integer)
    this.configFile = objectFactory.fileProperty()
    this.config = objectFactory.mapProperty(String, Object) // empty by default since https://github.com/gradle/gradle/pull/7963
    this.enable = objectFactory.listProperty(String)
    this.disable = objectFactory.listProperty(String)
    this.multipass = objectFactory.property(Boolean).convention(Boolean.FALSE)
    this.pretty = objectFactory.property(Boolean).convention(Boolean.FALSE)
    this.indent = objectFactory.property(Integer)
    this.indentIfPretty = providerFactory.provider {
      pretty.get() ? this.indent.getOrNull() : null
    }
    this.quiet = objectFactory.property(Boolean).convention(Boolean.FALSE)
    // TODO final Property<Boolean> verbose = project.objects.property(Boolean).convention project.provider { (project.logging.level ?: project.gradle.startParameter.logLevel) <= LogLevel.DEBUG }
  }

  @Override
  SvgoExecSpec get() {
  }

  @Override
  SvgoExecSpec getOrNull() {
    get()
  }

  @Override
  SvgoExecSpec getOrElse(SvgoExecSpec svgoExecSpec) {
    get()
  }

  @Override
  <S> Provider<S> map(Transformer<? extends S, ? super SvgoExecSpec> transformer) {
    providerFactory.provider {
      transformer.transform(get())
    }
  }

  @Override
  <S> Provider<S> flatMap(Transformer<? extends Provider<? extends S>, ? super SvgoExecSpec> transformer) {
    (Provider<S>)transformer.transform(get())
  }

  @Override
  boolean isPresent() {
    true
  }
}

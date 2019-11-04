package org.fidata.gradle.svgo

enum DataUri {
  BASE_64('base64'),
  ENCODED('enc'),
  UNENCODED('unenc')

  final String code

  DataUri(String code) {
    this.@code = code
  }

  @Override
  String toString() {
    code
  }
}

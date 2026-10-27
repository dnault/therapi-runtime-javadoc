/*
 * Copyright 2015 David Nault and contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.therapi.runtimejavadoc.repack.com.eclipsesource.json;

import java.io.IOException;


@SuppressWarnings("serial") // use default serial UID
class JsonNumber extends JsonValue {

  private final String string;

  JsonNumber(String string) {
    if (string == null) {
      throw new NullPointerException("string is null");
    }
    this.string = string;
  }

  @Override
  public String toString() {
    return string;
  }

  @Override
  void write(JsonWriter writer) throws IOException {
    writer.writeNumber(string);
  }

  @Override
  public boolean isNumber() {
    return true;
  }

  @Override
  public int asInt() {
    return Integer.parseInt(string, 10);
  }

  @Override
  public long asLong() {
    return Long.parseLong(string, 10);
  }

  @Override
  public float asFloat() {
    return Float.parseFloat(string);
  }

  @Override
  public double asDouble() {
    return Double.parseDouble(string);
  }

  @Override
  public int hashCode() {
    return string.hashCode();
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    if (object == null) {
      return false;
    }
    if (getClass() != object.getClass()) {
      return false;
    }
    JsonNumber other = (JsonNumber)object;
    return string.equals(other.string);
  }

}

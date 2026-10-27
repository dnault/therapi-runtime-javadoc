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

/**
 * An unchecked exception to indicate that an input does not qualify as valid JSON.
 */
@SuppressWarnings("serial") // use default serial UID
public class ParseException extends RuntimeException {

  private final Location location;

  ParseException(String message, Location location) {
    super(message + " at " + location);
    this.location = location;
  }

  /**
   * Returns the location at which the error occurred.
   *
   * @return the error location
   */
  public Location getLocation() {
    return location;
  }

  /**
   * Returns the absolute character index at which the error occurred. The offset of the first
   * character of a document is 0.
   *
   * @return the character offset at which the error occurred, will be &gt;= 0
   * @deprecated Use {@link #getLocation()} instead
   */
  @Deprecated
  public int getOffset() {
    return location.offset;
  }

  /**
   * Returns the line number in which the error occurred. The number of the first line is 1.
   *
   * @return the line in which the error occurred, will be &gt;= 1
   * @deprecated Use {@link #getLocation()} instead
   */
  @Deprecated
  public int getLine() {
    return location.line;
  }

  /**
   * Returns the column number at which the error occurred, i.e. the number of the character in its
   * line. The number of the first character of a line is 1.
   *
   * @return the column in which the error occurred, will be &gt;= 1
   * @deprecated Use {@link #getLocation()} instead
   */
  @Deprecated
  public int getColumn() {
    return location.column;
  }

}

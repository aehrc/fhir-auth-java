/*
 * Copyright 2026 Commonwealth Scientific and Industrial Research
 * Organisation (CSIRO) ABN 41 687 119 230.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package au.csiro.fhir.auth;

import java.net.URI;
import javax.annotation.Nonnull;
import lombok.experimental.UtilityClass;

/**
 * Utility methods for working with web resources.
 *
 * @author Piotr Szul
 */
@UtilityClass
public class WebUtils {

  /**
   * Ensures that the URI ends with a slash.
   *
   * @param uri the URI to ensure ends with a slash.
   * @return the URI with a trailing slash.
   */
  @Nonnull
  public static URI ensurePathEndsWithSlash(@Nonnull final URI uri) {
    return uri.getPath().endsWith("/") ? uri : URI.create(uri + "/");
  }
}

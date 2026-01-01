/*
 * Copyright 2023 Commonwealth Scientific and Industrial Research
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

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import javax.annotation.Nonnull;
import lombok.experimental.UtilityClass;
import org.apache.commons.io.IOUtils;
import org.apache.http.client.ResponseHandler;
import org.mockito.Mockito;

/**
 * Utility methods for testing.
 *
 * @author Piotr Szul
 */
@UtilityClass
public class TestUtils {

  /**
   * Gets the contents of a resource as a string using UTF-8 encoding.
   *
   * @param resourcePath the path to the resource
   * @return the contents of the resource as a string
   */
  public static String getResourceAsString(@Nonnull final String resourcePath) {
    try {
      return IOUtils.resourceToString(
          resourcePath, StandardCharsets.UTF_8, TestUtils.class.getClassLoader());
    } catch (final IOException ex) {
      throw new RuntimeException("Cannot read resource", ex);
    }
  }

  /**
   * Creates a Mockito argument matcher for any ResponseHandler.
   *
   * @param <T> the type of the response
   * @return a Mockito argument matcher
   */
  @SuppressWarnings("unchecked")
  public static <T> ResponseHandler<T> anyResponseHandler() {
    return Mockito.any(ResponseHandler.class);
  }
}

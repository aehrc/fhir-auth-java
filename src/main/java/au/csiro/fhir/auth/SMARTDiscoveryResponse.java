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
import java.net.URI;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnull;
import lombok.Builder;
import lombok.Value;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;

/**
 * Response from SMART configuration discovery endpoint.
 *
 * @author Piotr Szul
 */
@Value
@Builder
public class SMARTDiscoveryResponse {

  /** The path to the SMART configuration discovery endpoint. */
  public static String SMART_WELL_KNOWN_CONFIGURATION_PATH = ".well-known/smart-configuration";

  @Nonnull String tokenEndpoint;

  @Nonnull @Builder.Default List<String> grantTypesSupported = Collections.emptyList();

  @Nonnull @Builder.Default
  List<String> tokenEndpointAuthMethodsSupported = Collections.emptyList();

  @Nonnull @Builder.Default
  List<String> tokenEndpointAuthSigningAlgValuesSupported = Collections.emptyList();

  @Nonnull @Builder.Default List<String> capabilities = Collections.emptyList();

  /**
   * Gets the SMART configuration discovery response from the FHIR endpoint. This method appends the
   * well-known SMART configuration path to the provided URI.
   *
   * @param fhirEndpointURI the FHIR endpoint URI
   * @param httpClient the HTTP client
   * @return the SMART configuration discovery response
   * @throws IOException if an error occurs
   */
  @Nonnull
  public static SMARTDiscoveryResponse get(
      @Nonnull final URI fhirEndpointURI, @Nonnull final HttpClient httpClient) throws IOException {
    final URI wellKnownUri =
        WebUtils.ensurePathEndsWithSlash(fhirEndpointURI)
            .resolve(SMART_WELL_KNOWN_CONFIGURATION_PATH);
    return getFromUrl(wellKnownUri, httpClient);
  }

  /**
   * Gets the SMART configuration discovery response from an explicit URL. This method fetches
   * directly from the provided URL without appending any well-known path. Use this when you have an
   * explicit OAuth metadata URL that points directly to the configuration document.
   *
   * @param metadataUrl the explicit URL to the OAuth metadata document
   * @param httpClient the HTTP client
   * @return the SMART configuration discovery response
   * @throws IOException if an error occurs
   */
  @Nonnull
  public static SMARTDiscoveryResponse getFromUrl(
      @Nonnull final URI metadataUrl, @Nonnull final HttpClient httpClient) throws IOException {
    final HttpGet request = new HttpGet(metadataUrl);
    request.addHeader("Accept", "application/json");
    return httpClient.execute(
        request, JsonResponseHandler.lowerCaseWithUnderscore(SMARTDiscoveryResponse.class));
  }
}

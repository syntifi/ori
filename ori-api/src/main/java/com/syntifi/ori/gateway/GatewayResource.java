package com.syntifi.ori.gateway;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.util.Map;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import com.syntifi.ori.rest.RestApiResource;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Path("/")
public class GatewayResource {

  private static final String FALLBACK_RESOURCE = "/frontend/index.html";
  private static final Map<String, String> EXTENSION_TYPES = Map.of(
      "svg", "image/svg+xml");
  private final RestApiResource apiResource;

  @Inject
  public GatewayResource(RestApiResource apiResource) {
    this.apiResource = apiResource;
  }

  @Path("/api/v2")
  public RestApiResource getApiResource() {
    return apiResource;
  }

  @GET
  @Path("/")
  @Schema(hidden = true)
  @Operation(hidden = true)
  public Response getFrontendRoot() throws IOException {
    return getFrontendStaticFile("/frontend/index.html");
  }

  @GET
  @Path("/{fileName:.+}")
  @Schema(hidden = true)
  @Operation(hidden = true)
  public Response getFrontendStaticFile(@PathParam("fileName") String fileName) throws IOException {
    final InputStream requestedFileStream = GatewayResource.class.getResourceAsStream("/frontend/" + fileName);
    final InputStream inputStream;
    final String fileToServe;
    if (requestedFileStream != null) {
      fileToServe = fileName;
      inputStream = requestedFileStream;
    } else {
      fileToServe = FALLBACK_RESOURCE;
      inputStream = GatewayResource.class.getResourceAsStream(FALLBACK_RESOURCE);
    }

    final StreamingOutput streamingOutput = outputStream -> IOUtils.copy(inputStream, outputStream);

    return Response
        .ok(streamingOutput)
        .cacheControl(CacheControl.valueOf("max-age=900"))
        .type(contentType(inputStream, fileToServe))
        .build();
  }

  private String contentType(InputStream inputStream, String file) throws IOException {
    return EXTENSION_TYPES.getOrDefault(
        FilenameUtils.getExtension(file),
        URLConnection.guessContentTypeFromStream(inputStream));
  }
}

package io.quarkiverse.langchain4j.ovhai;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import dev.langchain4j.model.ovhai.internal.api.EmbeddingRequest;
import dev.langchain4j.model.ovhai.internal.api.EmbeddingResponse;

@Path("api/text2vec")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface OvhAiRestApi {
    String API_KEY_HEADER = "Bearer";

    @Path("embeddings")
    @POST
    EmbeddingResponse embedding(EmbeddingRequest request, @HeaderParam("Authorization") String authorizationHeader);
}

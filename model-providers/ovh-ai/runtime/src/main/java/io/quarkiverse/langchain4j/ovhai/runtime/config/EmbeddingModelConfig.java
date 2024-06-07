package io.quarkiverse.langchain4j.ovhai.runtime.config;

import java.util.Optional;

import io.quarkus.runtime.annotations.ConfigDocDefault;
import io.quarkus.runtime.annotations.ConfigGroup;
import io.smallrye.config.WithDefault;

@ConfigGroup
public interface EmbeddingModelConfig {

    /**
     * The maximum number of times to retry. 1 means exactly one attempt, with retrying disabled.
     *
     * @deprecated Using the fault tolerance mechanisms built in Langchain4j is not recommended. If possible,
     *             use MicroProfile Fault Tolerance instead.
     */
    @WithDefault("1")
    Integer maxRetries();

    /**
     * Whether chat model requests should be logged
     */
    @ConfigDocDefault("false")
    @WithDefault("${quarkus.langchain4j.ovhai.log-requests}")
    Optional<Boolean> logRequests();

    /**
     * Whether chat model responses should be logged
     */
    @ConfigDocDefault("false")
    @WithDefault("${quarkus.langchain4j.ovhai.log-responses}")
    Optional<Boolean> logResponses();
}

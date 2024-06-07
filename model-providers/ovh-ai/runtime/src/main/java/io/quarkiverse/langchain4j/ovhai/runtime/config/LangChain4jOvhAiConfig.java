package io.quarkiverse.langchain4j.ovhai.runtime.config;

import static io.quarkus.runtime.annotations.ConfigPhase.RUN_TIME;

import java.time.Duration;
import java.util.Optional;

import io.quarkus.runtime.annotations.ConfigDocDefault;
import io.quarkus.runtime.annotations.ConfigGroup;
import io.quarkus.runtime.annotations.ConfigRoot;
import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;
import io.smallrye.config.WithParentName;

@ConfigRoot(phase = RUN_TIME)
@ConfigMapping(prefix = "quarkus.langchain4j.ovhai")
public interface LangChain4jOvhAiConfig {
    /**
     * Default model config
     */
    @WithParentName
    OvhAiConfig defaultConfig();

    @ConfigGroup
    interface OvhAiConfig {
        /**
         * Base URL of the Anthropic API
         */
        @WithDefault("https://multilingual-e5-base.endpoints.kepler.ai.cloud.ovh.net")
        String baseUrl();

        /**
         * Anthropic API key
         */
        @WithDefault("dummy") // TODO: this should be optional but Smallrye Config doesn't like it
        String apiKey();

        /**
         * Timeout for Anthropic calls
         */
        @ConfigDocDefault("10s")
        @WithDefault("${quarkus.langchain4j.timeout}")
        Optional<Duration> timeout();

        /**
         * Whether the Anthropic client should log requests
         */
        @ConfigDocDefault("false")
        @WithDefault("${quarkus.langchain4j.log-requests}")
        Optional<Boolean> logRequests();

        /**
         * Whether the Anthropic client should log responses
         */
        @ConfigDocDefault("false")
        @WithDefault("${quarkus.langchain4j.log-responses}")
        Optional<Boolean> logResponses();

        /**
         * Whether to enable the integration. Defaults to {@code true}, which means requests are made to the Anthropic
         * provider.
         * Set to {@code false} to disable all requests.
         */
        @WithDefault("true")
        Boolean enableIntegration();

        /**
         * Chat model related settings
         */
        EmbeddingModelConfig embeddingModel();
    }
}

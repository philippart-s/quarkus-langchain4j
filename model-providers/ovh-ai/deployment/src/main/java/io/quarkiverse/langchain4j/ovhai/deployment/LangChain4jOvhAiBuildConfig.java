package io.quarkiverse.langchain4j.ovhai.deployment;

import static io.quarkus.runtime.annotations.ConfigPhase.BUILD_TIME;

import io.quarkus.runtime.annotations.ConfigRoot;
import io.smallrye.config.ConfigMapping;

@ConfigRoot(phase = BUILD_TIME)
@ConfigMapping(prefix = "quarkus.langchain4j.ovhai")
public interface LangChain4jOvhAiBuildConfig {
    /**
     * Chat model related settings
     */
    EmbbedingModelBuildConfig embeddingModel();
}

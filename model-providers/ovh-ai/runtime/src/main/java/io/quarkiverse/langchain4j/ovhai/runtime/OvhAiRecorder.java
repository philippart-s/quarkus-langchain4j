package io.quarkiverse.langchain4j.ovhai.runtime;

import java.time.Duration;
import java.util.function.Supplier;

import dev.langchain4j.model.embedding.DisabledEmbeddingModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.ovhai.OvhAiEmbeddingModel;
import io.quarkiverse.langchain4j.ovhai.runtime.config.LangChain4jOvhAiConfig;
import io.quarkiverse.langchain4j.runtime.NamedConfigUtil;
import io.quarkus.runtime.annotations.Recorder;
import io.smallrye.config.ConfigValidationException;

@Recorder
public class OvhAiRecorder {
    private static final String DUMMY_KEY = "dummy";

    public Supplier<EmbeddingModel> embeddingModel(LangChain4jOvhAiConfig runtimeConfig, String configName) {
        var ovhAiConfig = correspondingOvhAiConfig(runtimeConfig, configName);

        if (ovhAiConfig.enableIntegration()) {
            var embeddingModelConfig = ovhAiConfig.embeddingModel();
            var apiKey = ovhAiConfig.apiKey();

            if (DUMMY_KEY.equals(apiKey)) {
                throw new ConfigValidationException(createApiKeyConfigProblem(configName));
            }

            var builder = OvhAiEmbeddingModel.builder()
                    .baseUrl(ovhAiConfig.baseUrl())
                    .apiKey(apiKey)
                    .logRequests(embeddingModelConfig.logRequests().orElse(false))
                    .logResponses(embeddingModelConfig.logResponses().orElse(false))
                    .timeout(ovhAiConfig.timeout().orElse(Duration.ofSeconds(10)))
                    .maxRetries(embeddingModelConfig.maxRetries());

            return new Supplier<>() {
                @Override
                public EmbeddingModel get() {
                    return builder.build();
                }
            };
        } else {
            return new Supplier<>() {
                @Override
                public EmbeddingModel get() {
                    return new DisabledEmbeddingModel();
                }
            };
        }
    }

    private LangChain4jOvhAiConfig.OvhAiConfig correspondingOvhAiConfig(
            LangChain4jOvhAiConfig runtimeConfig, String configName) {

        return NamedConfigUtil.isDefault(configName) ? runtimeConfig.defaultConfig()
                : runtimeConfig.defaultConfig();
    }

    private static ConfigValidationException.Problem[] createApiKeyConfigProblem(String configName) {
        return createConfigProblems("api-key", configName);
    }

    private static ConfigValidationException.Problem[] createConfigProblems(String key, String configName) {
        return new ConfigValidationException.Problem[] { createConfigProblem(key, configName) };
    }

    private static ConfigValidationException.Problem createConfigProblem(String key, String configName) {
        return new ConfigValidationException.Problem(
                "SRCFG00014: The config property quarkus.langchain4j.ovhai%s%s is required but it could not be found in any config source"
                        .formatted(
                                NamedConfigUtil.isDefault(configName) ? "." : ("." + configName + "."), key));
    }
}

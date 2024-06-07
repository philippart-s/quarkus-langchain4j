package io.quarkiverse.langchain4j.ovhai.runtime.graalvm;

import com.oracle.svm.core.annotate.Substitute;
import com.oracle.svm.core.annotate.TargetClass;

import dev.langchain4j.model.ovhai.internal.client.OvhAiClient;
import io.quarkiverse.langchain4j.ovhai.QuarkusOvhAiClient;

public class Substitutions {
    @TargetClass(OvhAiClient.class)
    static final class Target_AnthropicClient {
        @Substitute
        public static OvhAiClient.Builder builder() {
            return new QuarkusOvhAiClient.Builder();
        }
    }
}

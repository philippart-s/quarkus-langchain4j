package io.quarkiverse.langchain4j.ovhai;

import static java.util.stream.Collectors.joining;
import static java.util.stream.StreamSupport.stream;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.TimeUnit;

import org.jboss.logging.Logger;
import org.jboss.resteasy.reactive.client.api.ClientLogger;
import org.jboss.resteasy.reactive.client.api.LoggingScope;

import dev.langchain4j.model.ovhai.internal.client.OvhAiClient;
import dev.langchain4j.model.ovhai.internal.client.OvhAiClientBuilderFactory;
import io.quarkus.rest.client.reactive.QuarkusRestClientBuilder;
import io.vertx.core.Handler;
import io.vertx.core.MultiMap;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpClientRequest;
import io.vertx.core.http.HttpClientResponse;

public class QuarkusOvhAiClient extends OvhAiClient {
    private final String apiKey;
    private final OvhAiRestApi restApi;

    public QuarkusOvhAiClient(Builder builder) {
        this.apiKey = builder.apiKey;

        try {
            var restApiBuilder = QuarkusRestClientBuilder.newBuilder().baseUri(new URI(builder.baseUrl))
                    .connectTimeout(builder.timeout.toSeconds(), TimeUnit.SECONDS)
                    .readTimeout(builder.timeout.toSeconds(), TimeUnit.SECONDS);

            if (builder.logRequests || builder.logResponses) {
                restApiBuilder.loggingScope(LoggingScope.REQUEST_RESPONSE).clientLogger(
                        new QuarkusOvhAiClient.OvhAiClientLogger(builder.logRequests, builder.logResponses));
            }

            this.restApi = restApiBuilder.build(OvhAiRestApi.class);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public static class QuarkusOvhAiClientBuilderFactory implements OvhAiClientBuilderFactory {
        @Override
        public OvhAiClient.Builder get() {
            return new Builder();
        }
    }

    public static class Builder extends OvhAiClient.Builder<QuarkusOvhAiClient, Builder> {
        @Override
        public QuarkusOvhAiClient build() {
            return new QuarkusOvhAiClient(this);
        }
    }

    /**
     * Introduce a custom logger as the stock one logs at the DEBUG level by default...
     */
    static class OvhAiClientLogger implements ClientLogger {
        private static final Logger log = Logger.getLogger(OvhAiClientLogger.class);

        private final boolean logRequests;
        private final boolean logResponses;

        public OvhAiClientLogger(boolean logRequests, boolean logResponses) {
            this.logRequests = logRequests;
            this.logResponses = logResponses;
        }

        @Override
        public void setBodySize(int bodySize) {
            // ignore
        }

        @Override
        public void logRequest(HttpClientRequest request, Buffer body, boolean omitBody) {
            if (logRequests && log.isInfoEnabled()) {
                try {
                    log.infof("Request:\n- method: %s\n- url: %s\n- headers: %s\n- body: %s", request.getMethod(),
                            request.absoluteURI(), inOneLine(request.headers()), bodyToString(body));
                } catch (Exception e) {
                    log.warn("Failed to log request", e);
                }
            }
        }

        @Override
        public void logResponse(HttpClientResponse response, boolean redirect) {
            if (logResponses && log.isInfoEnabled()) {
                response.bodyHandler(new Handler<>() {
                    @Override
                    public void handle(Buffer body) {
                        try {
                            log.infof("Response:\n- status code: %s\n- headers: %s\n- body: %s", response.statusCode(),
                                    inOneLine(response.headers()), bodyToString(body));
                        } catch (Exception e) {
                            log.warn("Failed to log response", e);
                        }
                    }
                });
            }
        }

        private String bodyToString(Buffer body) {
            return (body != null) ? body.toString() : "";
        }

        private String inOneLine(MultiMap headers) {
            return stream(headers.spliterator(), false)
                    .map(header -> {
                        var headerKey = header.getKey();
                        var headerValue = header.getValue();

                        if (headerKey.equals(OvhAiRestApi.API_KEY_HEADER)) {
                            headerValue = maskApiKeyHeaderValue(headerValue);
                        }

                        return "[%s: %s]".formatted(headerKey, headerValue);
                    })
                    .collect(joining(", "));
        }

        private static String maskApiKeyHeaderValue(String apiKeyHeaderValue) {
            try {
                if (apiKeyHeaderValue.length() <= 4) {
                    return apiKeyHeaderValue;
                }
                return apiKeyHeaderValue.substring(0, 2)
                        + "..."
                        + apiKeyHeaderValue.substring(apiKeyHeaderValue.length() - 2);
            } catch (Exception e) {
                return "Failed to mask the API key.";
            }
        }
    }
}

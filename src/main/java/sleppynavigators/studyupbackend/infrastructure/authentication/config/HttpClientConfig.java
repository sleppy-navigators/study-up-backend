package sleppynavigators.studyupbackend.infrastructure.authentication.config;

import java.io.File;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class HttpClientConfig {

    private static final int CACHE_SIZE_BYTES = 10 * 1024 * 1024;
    private static final String CACHE_DIRECTORY = "src/main/resources/cache";

    @Bean
    public OkHttpClient okHttpClient() {
        return new OkHttpClient.Builder()
                .cache(cache())
                .build();
    }

    private Cache cache() {
        File cacheDirectory = new File(CACHE_DIRECTORY);
        return new Cache(cacheDirectory, CACHE_SIZE_BYTES);
    }
}

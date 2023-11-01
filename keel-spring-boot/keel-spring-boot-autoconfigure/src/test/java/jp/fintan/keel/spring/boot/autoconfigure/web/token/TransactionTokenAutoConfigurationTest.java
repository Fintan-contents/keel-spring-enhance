package jp.fintan.keel.spring.boot.autoconfigure.web.token;

import static org.assertj.core.api.Assertions.assertThat;

import jp.fintan.keel.spring.web.token.transaction.TransactionTokenInterceptor;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.FilteredClassLoader;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import org.junit.jupiter.api.Test;

class TransactionTokenAutoConfigurationTest {

    private WebApplicationContextRunner contextRunner = new WebApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(TransactionTokenAutoConfiguration.class));

    @Test
    void contextShouldNotConfigureTransactionTokenInterceptorWhenTransactionTokenInterceptorAlreadyExists() {
        contextRunner
                .withUserConfiguration(TestConfiguration.class)
                .run((context) -> assertThat(context)
                        .hasSingleBean(TransactionTokenInterceptor.class));
    }

    @Test
    void contextShouldNotConfigureTransactionTokenInterceptorWhenFilteringWebMvcConfigurer() {
        contextRunner
                .withClassLoader(new FilteredClassLoader(WebMvcConfigurer.class))
                .run((context) -> assertThat(context)
                        .doesNotHaveBean(TransactionTokenInterceptor.class));
    }

    @Test
    void contextShouldConfigureTransactionTokenInterceptorWhenTransactionTokenPropertiesIsEnabled() {
        contextRunner
                .withPropertyValues("keel.transaction.token.enabled=true")
                .run((context) -> assertThat(context)
                .hasSingleBean(TransactionTokenInterceptor.class));
    }

    @Test
    void contextShouldNotConfigureTransactionTokenInterceptorWhenTransactionTokenPropertiesIsNotEnabled() {
        contextRunner
                .withPropertyValues("keel.transaction.token.enabled=false")
                .run((context) -> assertThat(context)
                        .doesNotHaveBean(TransactionTokenInterceptor.class));
    }

    @Test
    void contextShouldConfigureTransactionTokenInterceptorWhenTransactionTokenPropertiesMatchIfMissing() {
        contextRunner
                .run((context) -> assertThat(context)
                        .hasSingleBean(TransactionTokenInterceptor.class));
    }

    @Configuration
    static class TestConfiguration {
        @Bean
        public TransactionTokenInterceptor transactionTokenInterceptor() {
            return new TransactionTokenInterceptor();
        }
    }

}

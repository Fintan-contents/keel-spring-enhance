package jp.fintan.keel.spring.boot.autoconfigure.web.token;

import jp.fintan.keel.spring.web.token.transaction.TransactionTokenInterceptor;
import jp.fintan.keel.spring.web.token.transaction.TransactionTokenRequestDataValueProcessor;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.support.RequestDataValueProcessor;

/**
 * トランサクショントークン機能のAuto-configuration。
 */
@AutoConfiguration
@ConditionalOnWebApplication(type = Type.SERVLET)
@ConditionalOnClass({WebMvcConfigurer.class})
@ConditionalOnMissingBean(TransactionTokenInterceptor.class)
@EnableConfigurationProperties(TransactionTokenProperties.class)
@ConditionalOnProperty(prefix = TransactionTokenProperties.TRANSACTION_TOKEN_PREFIX, name = "enabled", havingValue = "true", matchIfMissing = true)
public class TransactionTokenAutoConfiguration implements WebMvcConfigurer {

    private final TransactionTokenProperties transactionTokenProperties;

    public TransactionTokenAutoConfiguration(TransactionTokenProperties transactionTokenProperties) {
        this.transactionTokenProperties = transactionTokenProperties;
    }

    @Bean
    public RequestDataValueProcessor requestDataValueProcessor() {
        return new TransactionTokenRequestDataValueProcessor();
    }

    @Bean
    public TransactionTokenInterceptor transactionTokenInterceptor() {
        return new TransactionTokenInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry
                .addInterceptor(transactionTokenInterceptor())
                .addPathPatterns(transactionTokenProperties.getPathPatterns())
                .excludePathPatterns(transactionTokenProperties.getExcludePathPatterns())
                .order(Ordered.LOWEST_PRECEDENCE - 10);
    }

}

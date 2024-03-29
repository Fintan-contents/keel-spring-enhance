/*
 * Originally distributed by NTT DATA Corporation under Apache License, Version 2.0.
 *    Library: TERASOLUNA Server Framework for Java (5.x) Common Library
 *    Source:  https://github.com/terasolunaorg/terasoluna-gfw/blob/5.4.1.RELEASE/terasoluna-gfw-common-libraries/terasoluna-gfw-web/src/test/java/org/terasoluna/gfw/web/token/transaction/TransactionTokenContextHandlerMethodArgumentResolverTest.java
 *
 * Modified by TIS Inc.
 */
/*
 * Copyright (C) 2013-2017 NTT DATA Corporation
 * Copyright (C) 2018 TIS Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package jp.fintan.keel.spring.web.token.transaction;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.mockito.Mockito;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.method.support.ModelAndViewContainer;

import org.junit.jupiter.api.Test;

/**
 * Test class for TransactionTokenContextHandlerMethodArgumentResolver
 */
class TransactionTokenContextHandlerMethodArgumentResolverTest {

    /**
     * case of supportsParameter returns true
     */
    @Test
    void testSupportsParameter01() {
        TransactionTokenContextHandlerMethodArgumentResolver resolver = new TransactionTokenContextHandlerMethodArgumentResolver();
        MethodParameter parameter = mock(MethodParameter.class);

        Mockito.<Class<?>> when(parameter.getParameterType()).thenReturn(
                TransactionTokenContext.class);

        boolean result = resolver.supportsParameter(parameter);

        assertThat(result, is(true));
    }

    /**
     * case of supportsParameter returns false
     */
    @Test
    void testSupportsParameter02() {
        TransactionTokenContextHandlerMethodArgumentResolver resolver = new TransactionTokenContextHandlerMethodArgumentResolver();
        MethodParameter parameter = mock(MethodParameter.class);

        Mockito.<Class<?>> when(parameter.getParameterType()).thenReturn(
                Object.class);

        boolean result = resolver.supportsParameter(parameter);

        assertThat(result, is(false));
    }

    /**
     * case of resolveArgument
     */
    @Test
    void testResolveArgument01() {
        TransactionTokenContextHandlerMethodArgumentResolver resolver = new TransactionTokenContextHandlerMethodArgumentResolver();
        MethodParameter parameter = mock(MethodParameter.class);
        ModelAndViewContainer mavContainer = mock(ModelAndViewContainer.class);
        NativeWebRequest webRequest = mock(NativeWebRequest.class);
        WebDataBinderFactory binderFactory = mock(WebDataBinderFactory.class);

        String str = "testResolveArgument01";
        when(webRequest.getAttribute(
                TransactionTokenInterceptor.TOKEN_CONTEXT_REQUEST_ATTRIBUTE_NAME,
                RequestAttributes.SCOPE_REQUEST)).thenReturn(str);

        Object result = null;
        try {
            result = resolver.resolveArgument(parameter, mavContainer,
                    webRequest, binderFactory);
        } catch (Exception e) {
            e.printStackTrace();
        }

        assertThat(result, is(instanceOf(String.class)));
    }
}

/*
 * Originally distributed by NTT DATA Corporation under Apache License, Version 2.0.
 *    Library: TERASOLUNA Server Framework for Java (5.x) Common Library
 *    Source:  https://github.com/terasolunaorg/terasoluna-gfw/blob/5.4.1.RELEASE/terasoluna-gfw-common-libraries/terasoluna-gfw-web/src/test/java/org/terasoluna/gfw/web/token/transaction/TransactionTokenRequestDataValueProcessorTest.java
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
/**
 * @(#)TransactionTokenRequestDataValueProcessorTest.java
 */
package jp.fintan.keel.spring.web.token.transaction;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;

import org.junit.jupiter.api.Test;

/**
 * Test class for TransactionTokenRequestDataValueProcessor
 */
class TransactionTokenRequestDataValueProcessorTest {

    /**
     * Test method for
     * {@link TransactionTokenRequestDataValueProcessor#getExtraHiddenFields(jakarta.servlet.http.HttpServletRequest)}
     * request has no next token.
     */
    @Test
    void testGetExtraHiddenFields01() {
        // setup arguments
        TransactionTokenRequestDataValueProcessor processor = new TransactionTokenRequestDataValueProcessor();
        HttpServletRequest request = mock(HttpServletRequest.class);

        // mock behavior
        when((TransactionToken) request.getAttribute(
                TransactionTokenInterceptor.NEXT_TOKEN_REQUEST_ATTRIBUTE_NAME))
                        .thenReturn(null);

        // run
        Map<String, String> result = processor.getExtraHiddenFields(request);

        // assert
        assertThat(result, nullValue());
    }

    /**
     * Test method for
     * {@link TransactionTokenRequestDataValueProcessor#getExtraHiddenFields(jakarta.servlet.http.HttpServletRequest)}
     * request has next token.
     */
    @Test
    void testGetExtraHiddenFields02() {
        // setup arguments
        TransactionTokenRequestDataValueProcessor processor = new TransactionTokenRequestDataValueProcessor();
        HttpServletRequest request = mock(HttpServletRequest.class);
        TransactionToken token = new TransactionToken("tokenName", "tokenkey", "tokenValue");

        // mock behavior
        when((TransactionToken) request.getAttribute(
                TransactionTokenInterceptor.NEXT_TOKEN_REQUEST_ATTRIBUTE_NAME))
                        .thenReturn(token);

        // run
        Map<String, String> result = processor.getExtraHiddenFields(request);

        // capture
        Map<String, String> expected = new HashMap<String, String>();
        expected.put("_TRANSACTION_TOKEN", token.getTokenString());

        // assert
        assertThat(result, is(expected));
    }

}

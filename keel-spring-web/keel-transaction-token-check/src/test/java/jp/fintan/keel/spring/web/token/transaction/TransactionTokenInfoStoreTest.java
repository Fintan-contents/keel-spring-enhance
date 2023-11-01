/*
 * Originally distributed by NTT DATA Corporation under Apache License, Version 2.0.
 *    Library: TERASOLUNA Server Framework for Java (5.x) Common Library
 *    Source:  https://github.com/terasolunaorg/terasoluna-gfw/blob/5.4.1.RELEASE/terasoluna-gfw-common-libraries/terasoluna-gfw-web/src/test/java/org/terasoluna/gfw/web/token/transaction/TransactionTokenInfoStoreTest.java
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
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.springframework.ui.Model;
import org.springframework.web.method.HandlerMethod;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TransactionTokenInfoStoreTest {

    TransactionTokenInfoStore store;

    @BeforeEach
    void setup() {
        store = new TransactionTokenInfoStore();
    }

    @Test
    void testCreateTransactionTokenInfo01() throws Exception {

        HandlerMethod handlerMethod = new HandlerMethod(new TransactionTokenSampleController(), TransactionTokenSampleController.class
                .getDeclaredMethod("fourth", SampleForm.class, Model.class));

        TransactionTokenInfo tokenInfo = store.createTransactionTokenInfo(
                handlerMethod);
        assertNotNull(tokenInfo);

    }

    @Test
    void testCreateTransactionTokenInfo02() throws Exception {

        HandlerMethod handlerMethod = new HandlerMethod(new TransactionTokenSampleController(), TransactionTokenSampleController.class
                .getDeclaredMethod("first", SampleForm.class, Model.class));

        TransactionTokenInfo tokenInfo = store.createTransactionTokenInfo(
                handlerMethod);
        assertNotNull(tokenInfo);

    }

    @Test
    void testCreateTokenInfoName01() throws Exception {

        TransactionTokenCheck methodAnnotation = mock(
                TransactionTokenCheck.class);
        TransactionTokenCheck classAnnotation = mock(
                TransactionTokenCheck.class);

        when(methodAnnotation.value()).thenReturn("test");
        when(classAnnotation.value()).thenReturn("test");

        assertNotNull(store.createTokenName(classAnnotation, methodAnnotation));

    }

    @Test
    void testCreateTokenInfoName02() throws Exception {

        TransactionTokenCheck methodAnnotation = mock(
                TransactionTokenCheck.class);
        TransactionTokenCheck classAnnotation = null;

        when(methodAnnotation.value()).thenReturn("test");

        assertNotNull(store.createTokenName(classAnnotation, methodAnnotation));

    }

    @Test
    void testCreateTokenInfoName03() throws Exception {

        TransactionTokenCheck methodAnnotation = mock(
                TransactionTokenCheck.class);
        TransactionTokenCheck classAnnotation = null;

        when(methodAnnotation.value()).thenReturn("");

        assertNotNull(store.createTokenName(classAnnotation, methodAnnotation));

    }

    @Test
    void testCreateTokenInfoName04() throws Exception {

        TransactionTokenCheck methodAnnotation = mock(
                TransactionTokenCheck.class);
        TransactionTokenCheck classAnnotation = null;

        when(methodAnnotation.value()).thenReturn(null);

        assertNotNull(store.createTokenName(classAnnotation, methodAnnotation));

    }

    @Test
    void testCreateTokenInfoName05() throws Exception {

        TransactionTokenCheck methodAnnotation = mock(
                TransactionTokenCheck.class);
        TransactionTokenCheck classAnnotation = mock(
                TransactionTokenCheck.class);

        when(methodAnnotation.value()).thenReturn("test");
        when(classAnnotation.value()).thenReturn("");

        assertNotNull(store.createTokenName(classAnnotation, methodAnnotation));
    }

    @Test
    void testCreateTokenInfoName06() throws Exception {

        TransactionTokenCheck methodAnnotation = mock(
                TransactionTokenCheck.class);
        TransactionTokenCheck classAnnotation = mock(
                TransactionTokenCheck.class);

        when(methodAnnotation.value()).thenReturn("test");
        when(classAnnotation.value()).thenReturn(null);

        assertNotNull(store.createTokenName(classAnnotation, methodAnnotation));
    }

    @Test
    void testGetTransactionTokenInfo() throws NoSuchMethodException, SecurityException {

        HandlerMethod handlerMethod = new HandlerMethod(new TransactionTokenSampleController(), TransactionTokenSampleController.class
                .getDeclaredMethod("first", SampleForm.class, Model.class));

        assertNotNull(store.getTransactionTokenInfo(handlerMethod));

    }

    @Test
    void testNamespaceCreateTransactionTokenInfo() throws Exception {

        HandlerMethod handlerMethod = new HandlerMethod(new TransactionTokenSampleNamespaceController(), TransactionTokenSampleNamespaceController.class
                .getDeclaredMethod("first"));

        TransactionTokenInfo tokenInfo = store.createTransactionTokenInfo(
                handlerMethod);
        assertThat(tokenInfo.getTokenName(), containsString(
                "testTokenAttrByNameSpace"));

    }
}

/*
 * Originally distributed by NTT DATA Corporation under Apache License, Version 2.0.
 *    Library: TERASOLUNA Server Framework for Java (5.x) Common Library
 *    Source:  https://github.com/terasolunaorg/terasoluna-gfw/blob/5.4.1.RELEASE/terasoluna-gfw-common-libraries/terasoluna-gfw-web/src/test/java/org/terasoluna/gfw/web/token/transaction/TransactionTokenInterceptorTest.java
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
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import jp.fintan.keel.spring.web.token.TokenStringGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.ui.Model;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.method.HandlerMethod;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@SpringJUnitConfig(classes = TestConfig.class)
class TransactionTokenInterceptorTest {

    @Autowired
    ConfigurableApplicationContext context;

    TransactionTokenInterceptor interceptor;

    MockHttpServletRequest request;

    MockHttpServletResponse response;

    @BeforeEach
    void setUp() throws Exception {

        // prepare request object
        request = new MockHttpServletRequest();
        request.setRequestURI("/token/first");
        request.setMethod("GET");

        // prepare response object
        response = new MockHttpServletResponse();

        // set ServletRequestAttributes to RequestContextHolder
        ServletRequestAttributes attributes = new ServletRequestAttributes(request);
        RequestContextHolder.setRequestAttributes(attributes);

        // prepare intercepter instance
        interceptor = new TransactionTokenInterceptor();
    }

    @AfterEach
    void tearDown() throws Exception {
    }

    @Test
    void testPreHandleIncorrectHandler() throws Exception {
        boolean result = interceptor.preHandle(request, response, null);
        assertTrue(result);
    }

    @Test
    void testPreHandleWithoutTokenValidate() throws Exception {
        boolean result = interceptor.preHandle(request, response,
                new HandlerMethod(new TransactionTokenSampleController(), TransactionTokenSampleController.class
                        .getDeclaredMethod("first", SampleForm.class,
                                Model.class)));
        assertTrue(result);
    }

    @Test
    void testPreHandleWithTokenValidate() throws Exception {

        HttpSessionTransactionTokenStore tokenStore = new HttpSessionTransactionTokenStore();
        TransactionToken inputToken = new TransactionToken("testTokenAttr", "111", "222");
        tokenStore.store(inputToken);

        request.setParameter(
                TransactionTokenInterceptor.TOKEN_REQUEST_PARAMETER,
                "testTokenAttr~111~222");

        interceptor = new TransactionTokenInterceptor(new TokenStringGenerator(), new TransactionTokenInfoStore(), tokenStore);

        boolean result = interceptor.preHandle(request, response,
                new HandlerMethod(new TransactionTokenSampleController(), TransactionTokenSampleController.class
                        .getDeclaredMethod("third", SampleForm.class,
                                Model.class)));

        // Confirm return value
        assertTrue(result);
        // Confirm that TokenContext is stored in the request
        assertNotNull(request.getAttribute(
                TransactionTokenInterceptor.TOKEN_CONTEXT_REQUEST_ATTRIBUTE_NAME));
    }

    @Test
    void testPreHandleWithTokenValidateFail() throws Exception {
        assertThrows(InvalidTransactionTokenException.class, () -> {

            HttpSessionTransactionTokenStore tokenStore = new HttpSessionTransactionTokenStore();

            interceptor = new TransactionTokenInterceptor(new TokenStringGenerator(), new TransactionTokenInfoStore(), tokenStore);

            interceptor.preHandle(request, response,
                    new HandlerMethod(new TransactionTokenSampleController(), TransactionTokenSampleController.class
                            .getDeclaredMethod("third", SampleForm.class,
                                    Model.class)));

        });

    }

    @Test
    void testPreHandleTokenIsSentInHiddenFieldAtBeginPhase() throws Exception {

        HttpSessionTransactionTokenStore tokenStore = new HttpSessionTransactionTokenStore();
        TransactionToken inputToken = new TransactionToken("tokenName1", "111", "222");
        tokenStore.store(inputToken);

        assertThat(tokenStore.getSession().getAttribute(tokenStore
                .createSessionAttributeName(inputToken)), is(notNullValue()));

        request.setParameter(
                TransactionTokenInterceptor.TOKEN_REQUEST_PARAMETER,
                "tokenName1~111~222");

        interceptor = new TransactionTokenInterceptor(new TokenStringGenerator(), new TransactionTokenInfoStore(), tokenStore);

        interceptor.preHandle(request, response,
                new HandlerMethod(new TransactionTokenSampleController(), TransactionTokenSampleController.class
                        .getDeclaredMethod("first", SampleForm.class,
                                Model.class)));

        // check if inputToken is removed
        assertThat(tokenStore.getSession().getAttribute(tokenStore
                .createSessionAttributeName(inputToken)), is(nullValue()));
    }

    @Test
    void testPreHandleValidTokenOnCheck() throws Exception {

        HttpSessionTransactionTokenStore tokenStore = new HttpSessionTransactionTokenStore();
        TransactionToken inputToken = new TransactionToken("testTokenAttr", "111", "222");
        tokenStore.store(inputToken);

        assertThat(tokenStore.getSession().getAttribute(tokenStore
                .createSessionAttributeName(inputToken)), is(notNullValue()));

        request.setParameter(
                TransactionTokenInterceptor.TOKEN_REQUEST_PARAMETER,
                "testTokenAttr~111~222");

        interceptor = new TransactionTokenInterceptor(new TokenStringGenerator(), new TransactionTokenInfoStore(), tokenStore);

        boolean result = interceptor.preHandle(request, response,
                new HandlerMethod(new TransactionTokenSampleController(), TransactionTokenSampleController.class
                        .getDeclaredMethod("fifth", SampleForm.class,
                                Model.class)));

        assertTrue(result);

        TransactionTokenContext transactionTokenCtx = (TransactionTokenContext) request
                .getAttribute(
                        TransactionTokenInterceptor.TOKEN_CONTEXT_REQUEST_ATTRIBUTE_NAME);
        TransactionToken token = transactionTokenCtx.getReceivedToken();
        assertNotNull(token);
        assertThat(token.getTokenName(), is("testTokenAttr"));
        assertThat(token.getTokenKey(), is("111"));
        assertThat(token.getTokenValue(), is("222"));
    }

    @Test
    void testValidateToken01() {
        HttpSessionTransactionTokenStore tokenStore = new HttpSessionTransactionTokenStore();
        TransactionToken inputToken = new TransactionToken("tokenName1", "111", "222");
        tokenStore.store(inputToken);

        TransactionTokenInfo tokenInfo = new TransactionTokenInfo("tokenName1", TransactionTokenType.IN);

        boolean result = interceptor.validateToken(inputToken, tokenStore,
                tokenInfo);

        assertTrue(result);

        result = interceptor.validateToken(inputToken, tokenStore, tokenInfo);

        assertFalse(result);

    }

    @Test
    void testValidateToken02() {

        HttpSessionTransactionTokenStore tokenStore = mock(
                HttpSessionTransactionTokenStore.class);
        TransactionToken inputToken = new TransactionToken("tokenName1", "111", "222");

        TransactionTokenInfo tokenInfo = new TransactionTokenInfo("tokenName1", TransactionTokenType.IN);

        // Set mock behavior
        when(tokenStore.getAndClear(any())).thenReturn(
                "differentValue");

        boolean result = interceptor.validateToken(inputToken, tokenStore,
                tokenInfo);
        assertFalse(result);
    }

    // ---------------Constructor related---------------

    @Test
    void testNonParameterizedConstructor() {
        // Use parameterless constructor
        interceptor = new TransactionTokenInterceptor();
        assertNotNull(interceptor);
    }

    @Test
    void testIntConstructor() {
        // Use int constructor
        interceptor = new TransactionTokenInterceptor(10);
        assertNotNull(interceptor);

    }

    // ------------------------------

    @Test
    void testCreateReceivedToken() {
        request.setParameter(
                TransactionTokenInterceptor.TOKEN_REQUEST_PARAMETER, "a~b~c");
        TransactionToken token = interceptor.createReceivedToken(request);
        assertThat(token.getTokenName(), is("a"));
    }

    @Test
    void testRemoveEmptyToken() {
        // This test case should always pass
        // This will ensure that neither valid/invalid token removal generates
        // exception
        assertDoesNotThrow(() -> {
            interceptor.removeToken(new TransactionToken(""));
            interceptor.removeToken(new TransactionToken("a~b~c"));
        });

    }

    @Test
    void testPostHandleIncorrectHandler() throws Exception {

        assertDoesNotThrow(() -> {
            interceptor.postHandle(request, response, null, null);
        });
    }

    @Test
    void testPostHandleWithRemoveToken() throws Exception {

        HttpSessionTransactionTokenStore tokenStore = new HttpSessionTransactionTokenStore();
        TransactionToken inputToken = new TransactionToken("testTokenAttr", "111", "222");
        tokenStore.store(inputToken);

        request.setParameter(
                TransactionTokenInterceptor.TOKEN_REQUEST_PARAMETER,
                "testTokenAttr~111~222");

        interceptor = new TransactionTokenInterceptor(new TokenStringGenerator(), new TransactionTokenInfoStore(), tokenStore);

        interceptor.preHandle(request, response,
                new HandlerMethod(new TransactionTokenSampleController(), TransactionTokenSampleController.class
                        .getDeclaredMethod("third", SampleForm.class,
                                Model.class)));

        interceptor.postHandle(request, response,
                new HandlerMethod(new TransactionTokenSampleController(), TransactionTokenSampleController.class
                        .getDeclaredMethod("third", SampleForm.class,
                                Model.class)), null);

        // Confirm that token is removed from session
        assertNull(tokenStore.getSession().getAttribute(
                HttpSessionTransactionTokenStore.TOKEN_HOLDER_SESSION_ATTRIBUTE_PREFIX
                        + inputToken.getTokenName() + "~" + inputToken
                                .getTokenKey()));
    }

    @Test
    void testPostHandleWithCreateToken() throws Exception {

        HttpSessionTransactionTokenStore tokenStore = new HttpSessionTransactionTokenStore();
        TransactionToken inputToken = new TransactionToken("tokenName1", "111", "222");
        tokenStore.store(inputToken);

        request.setParameter(
                TransactionTokenInterceptor.TOKEN_REQUEST_PARAMETER,
                "tokenName1~111~222");

        interceptor = new TransactionTokenInterceptor(new TokenStringGenerator(), new TransactionTokenInfoStore(), tokenStore);

        interceptor.preHandle(request, response,
                new HandlerMethod(new TransactionTokenSampleController(), TransactionTokenSampleController.class
                        .getDeclaredMethod("first", SampleForm.class,
                                Model.class)));

        interceptor.postHandle(request, response,
                new HandlerMethod(new TransactionTokenSampleController(), TransactionTokenSampleController.class
                        .getDeclaredMethod("third", SampleForm.class,
                                Model.class)), null);

        // Next token is stored in request object
        assertNotNull(request.getAttribute(
                TransactionTokenInterceptor.NEXT_TOKEN_REQUEST_ATTRIBUTE_NAME));

        TransactionToken nextToken = (TransactionToken) request.getAttribute(
                TransactionTokenInterceptor.NEXT_TOKEN_REQUEST_ATTRIBUTE_NAME);

        // Confirm that next token is present in the session
        assertNotNull(tokenStore.getSession().getAttribute(
                HttpSessionTransactionTokenStore.TOKEN_HOLDER_SESSION_ATTRIBUTE_PREFIX
                        + nextToken.getTokenName() + "~" + nextToken
                                .getTokenKey()));
    }

    @Test
    void testPostHandleWithUpdateToken() throws Exception {

        HttpSessionTransactionTokenStore tokenStore = new HttpSessionTransactionTokenStore();
        TransactionToken inputToken = new TransactionToken("testTokenAttr", "111", "222");
        tokenStore.store(inputToken);

        request.setParameter(
                TransactionTokenInterceptor.TOKEN_REQUEST_PARAMETER,
                "testTokenAttr~111~222");

        interceptor = new TransactionTokenInterceptor(new TokenStringGenerator(), new TransactionTokenInfoStore(), tokenStore);

        interceptor.preHandle(request, response,
                new HandlerMethod(new TransactionTokenSampleController(), TransactionTokenSampleController.class
                        .getDeclaredMethod("second", SampleForm.class,
                                Model.class, TransactionTokenContext.class)));

        interceptor.postHandle(request, response,
                new HandlerMethod(new TransactionTokenSampleController(), TransactionTokenSampleController.class
                        .getDeclaredMethod("third", SampleForm.class,
                                Model.class)), null);

        // Confirm that token is still present in the session
        assertNotNull(tokenStore.getSession().getAttribute(
                HttpSessionTransactionTokenStore.TOKEN_HOLDER_SESSION_ATTRIBUTE_PREFIX
                        + inputToken.getTokenName() + "~" + inputToken
                                .getTokenKey()));
        // Next token is also stored in request object
        assertNotNull(request.getAttribute(
                TransactionTokenInterceptor.NEXT_TOKEN_REQUEST_ATTRIBUTE_NAME));
    }

    @Test
    void testPostHandleWithKeepToken() throws Exception {

        HttpSessionTransactionTokenStore tokenStore = new HttpSessionTransactionTokenStore();
        TransactionToken inputToken = new TransactionToken("testTokenAttr", "111", "222");
        tokenStore.store(inputToken);

        request.setParameter(
                TransactionTokenInterceptor.TOKEN_REQUEST_PARAMETER,
                "testTokenAttr~111~222");

        interceptor = new TransactionTokenInterceptor(new TokenStringGenerator(), new TransactionTokenInfoStore(), tokenStore);

        interceptor.preHandle(request, response,
                new HandlerMethod(new TransactionTokenSampleController(), TransactionTokenSampleController.class
                        .getDeclaredMethod("fifth", SampleForm.class,
                                Model.class)));

        interceptor.postHandle(request, response,
                new HandlerMethod(new TransactionTokenSampleController(), TransactionTokenSampleController.class
                        .getDeclaredMethod("fifth", SampleForm.class,
                                Model.class)), null);

        TransactionToken nextToken = (TransactionToken) request.getAttribute(
                TransactionTokenInterceptor.NEXT_TOKEN_REQUEST_ATTRIBUTE_NAME);
        assertNotNull(nextToken);
        assertThat(nextToken.getTokenName(), is("testTokenAttr"));
        assertThat(nextToken.getTokenKey(), is("111"));
        assertThat(nextToken.getTokenValue(), is("222"));
        assertThat(tokenStore.getAndClear(nextToken), is("222"));
    }

    @Test
    void testPostHandleWithNoneOperation() throws Exception {

        TransactionTokenContextImpl context = mock(
                TransactionTokenContextImpl.class);

        request.setAttribute(
                TransactionTokenInterceptor.TOKEN_CONTEXT_REQUEST_ATTRIBUTE_NAME,
                context);

        when(context.getReserveCommand()).thenReturn(
                TransactionTokenContextImpl.ReserveCommand.NONE);

        assertDoesNotThrow(() -> {
            interceptor.postHandle(request, response,
                    new HandlerMethod(new TransactionTokenSampleController(), TransactionTokenSampleController.class
                            .getDeclaredMethod("third", SampleForm.class,
                                    Model.class)), null);
        });

    }

    @Test
    void testAfterCompletionWithoutException() {
        assertDoesNotThrow(() -> {
            interceptor.afterCompletion(request, response, null, null);
        });
    }

    @Test
    void testAfterCompletionWithException() {
        assertDoesNotThrow(() -> {

            HttpSessionTransactionTokenStore tokenStore = new HttpSessionTransactionTokenStore();
            TransactionToken inputToken = new TransactionToken("testTokenAttr", "111", "222");
            tokenStore.store(inputToken);

            request.setParameter(
                    TransactionTokenInterceptor.TOKEN_REQUEST_PARAMETER,
                    "testTokenAttr~111~222");

            interceptor = new TransactionTokenInterceptor(new TokenStringGenerator(), new TransactionTokenInfoStore(), tokenStore);

            interceptor.preHandle(request, response,
                    new HandlerMethod(new TransactionTokenSampleController(), TransactionTokenSampleController.class
                            .getDeclaredMethod("third", SampleForm.class,
                                    Model.class)));

            // Confirm that token is stored in session
            assertNotNull(tokenStore.getSession().getAttribute(
                    HttpSessionTransactionTokenStore.TOKEN_HOLDER_SESSION_ATTRIBUTE_PREFIX
                            + inputToken.getTokenName() + "~" + inputToken
                            .getTokenKey()));

            // Consider that exception has occured and call afterCompletion()
            // method
            // This call should remove the token from the store
            Exception ex = new InvalidTransactionTokenException();
            interceptor.afterCompletion(request, response, null, ex);

            // Confirm that token is removed from session
            assertNull(tokenStore.getSession().getAttribute(
                    HttpSessionTransactionTokenStore.TOKEN_HOLDER_SESSION_ATTRIBUTE_PREFIX
                            + inputToken.getTokenName() + "~" + inputToken
                            .getTokenKey()));

        });
    }

    @Test
    void testAfterCompletionWithExceptionHasNoTransactionTokenContextImpl() {
        assertDoesNotThrow(() -> {
            interceptor.afterCompletion(request, response, null,
                    new Exception());
        });
    }

    /*
     * @Test public void testCreateTokenSynchronization() throws Exception { int size = 2000; Thread arrThreads[] = new
     * Thread[size]; for (int i = 0; i <size ; i++) { Thread thread = new Thread(new Runnable() {
     * @Override public void run() { try { interceptor.createToken(request, session1, tokenInfo1, generator1, tokenStore1); }
     * catch (Exception ex) { ex.printStackTrace(); } } }, "Thread_" + (i+1)); arrThreads[i] = thread; } for (Thread thread :
     * arrThreads) { try { thread.start(); thread.join(); } catch (Exception e) { // TODO Auto-generated catch block
     * e.printStackTrace(); } } }
     */
}

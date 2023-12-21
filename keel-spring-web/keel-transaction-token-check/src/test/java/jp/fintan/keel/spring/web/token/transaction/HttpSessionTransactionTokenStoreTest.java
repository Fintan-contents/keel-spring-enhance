/*
 * Originally distributed by NTT DATA Corporation under Apache License, Version 2.0.
 *    Library: TERASOLUNA Server Framework for Java (5.x) Common Library
 *    Source:  https://github.com/terasolunaorg/terasoluna-gfw/blob/5.4.1.RELEASE/terasoluna-gfw-common-libraries/terasoluna-gfw-web/src/test/java/org/terasoluna/gfw/web/token/transaction/HttpSessionTransactionTokenStoreTest.java
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
import static org.junit.jupiter.api.Assertions.*;

import java.util.Enumeration;
import java.util.concurrent.TimeUnit;

import jakarta.servlet.http.HttpSession;

import jp.fintan.keel.spring.web.token.TokenStringGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test class for HttpSessionTransactionTokenStore
 */
@SpringJUnitConfig(classes = TestConfig.class)
class HttpSessionTransactionTokenStoreTest {

    MockHttpServletRequest request;

    MockHttpServletResponse response;

    @Autowired
    ApplicationContext applicationContext;

    @Autowired
    ConfigurableApplicationContext context;

    HttpSessionTransactionTokenStore store;

    @BeforeEach
    void setUp() throws Exception {
        // prepare request object
        request = new MockHttpServletRequest();

        // prepare response object
        response = new MockHttpServletResponse();

        // set ServletRequestAttributes to RequestContextHolder
        ServletRequestAttributes attributes = new ServletRequestAttributes(request);
        RequestContextHolder.setRequestAttributes(attributes);
    }

    /**
     * tokenHolder is null
     */
    @Test
    void testGetAndClear01() {
        // setup parameters
        HttpSession session = new MockHttpSession();
        request.setSession(session);
        TransactionToken token = new TransactionToken("TransactionToken");

        // prepare store instance
        store = new HttpSessionTransactionTokenStore();

        // run
        String actuals = store.getAndClear(token);

        // assert
        assertNull(actuals);
    }

    /**
     * tokenHolder is not null
     */
    @Test
    void testGetAndClear02() {
        // setup parameters
        HttpSession session = new MockHttpSession();
        TransactionToken token = new TransactionToken("tokenName", "tokenKey", "tokenValue");
        session.setAttribute(
                HttpSessionTransactionTokenStore.TOKEN_HOLDER_SESSION_ATTRIBUTE_PREFIX
                        + token.getTokenName() + token.getTokenKey(), token);
        request.setSession(session);

        // prepare store instance
        store = new HttpSessionTransactionTokenStore();
        store.store(token);

        // run
        String actuals = store.getAndClear(token);

        // assert
        assertThat(actuals, is(token.getTokenValue()));
        assertNotNull(session.getAttribute(
                HttpSessionTransactionTokenStore.TOKEN_HOLDER_SESSION_ATTRIBUTE_PREFIX
                        + token.getTokenName() + token.getTokenKey()));
    }

    @Test
    void testGetAndClear_tokenIsNull() throws Exception {
        assertThrows(IllegalArgumentException.class, () -> {
            try {
                store = new HttpSessionTransactionTokenStore();
                store.getAndClear(null);
            } catch (Exception e) {
                assertThat(e.getMessage(), is("token must not be null"));
                throw e;
            }
        });
    }

    @Test
    void testRemove() {
        // setup parameters
        HttpSession session = new MockHttpSession();
        request.setSession(session);
        TransactionToken token = new TransactionToken("tokenName", "tokenKey", "tokenValue");

        // prepare store instance
        store = new HttpSessionTransactionTokenStore();
        store.store(token);

        // run
        store.remove(token);

        // assert
        assertNull(session.getAttribute(
                HttpSessionTransactionTokenStore.TOKEN_HOLDER_SESSION_ATTRIBUTE_PREFIX
                        + token.getTokenName() + token.getTokenKey()));
    }

    @Test
    void testRemove_tokenIsNull() throws Exception {
        assertThrows(IllegalArgumentException.class, () -> {
            try {
                store = new HttpSessionTransactionTokenStore();
                store.remove(null);
            } catch (Exception e) {
                assertThat(e.getMessage(), is("token must not be null"));
                throw e;
            }
        });
    }

    /**
     * create a new Token key (stored token is as it is the same as sizePerTokenName)
     */
    @Test
    void testCreateAndReserveTokenKey_storedToken_is_as_same_as_sizePerTokenName() throws InterruptedException {
        // prepare store instance
        store = new HttpSessionTransactionTokenStore(4, 4);

        // setup parameters
        HttpSession session = new MockHttpSession();
        request.setSession(session);

        TransactionToken tokenA = new TransactionToken("tokenName", "tokenKeyA", "tokenValueA");
        store.store(tokenA);
        TimeUnit.MILLISECONDS.sleep(1);
        TransactionToken tokenB = new TransactionToken("tokenName", "tokenKeyB", "tokenValueB");
        store.store(tokenB);
        TimeUnit.MILLISECONDS.sleep(1);
        TransactionToken tokenC = new TransactionToken("tokenName", "tokenKeyC", "tokenValueC");
        store.store(tokenC);
        TimeUnit.MILLISECONDS.sleep(1);
        TransactionToken tokenD = new TransactionToken("tokenName", "tokenKeyD", "tokenValueD");
        store.store(tokenD);
        TimeUnit.MILLISECONDS.sleep(1);
        TransactionToken token1 = new TransactionToken("tokenName1", "tokenKey1", "tokenValue1");
        store.store(token1);
        TimeUnit.MILLISECONDS.sleep(1);
        TransactionToken token2 = new TransactionToken("tokenName2", "tokenKey2", "tokenValue2");
        store.store(token2);
        TimeUnit.MILLISECONDS.sleep(1);
        TransactionToken token3 = new TransactionToken("tokenName3", "tokenKey3", "tokenValue3");
        store.store(token3);

        // run
        String actual = store.createAndReserveTokenKey(tokenA.getTokenName());
        Enumeration<String> enumeration = session.getAttributeNames();
        while (enumeration.hasMoreElements()) {
            System.out.println(enumeration.nextElement());
        }

        // String expected = "";

        // assert
        assertNotNull(actual);
        assertThat(session.getAttribute(store.createSessionAttributeName(
                tokenA)), is(nullValue()));
        assertThat(session.getAttribute(store.createSessionAttributeName(
                tokenB)), is(notNullValue()));
        assertThat(session.getAttribute(store.createSessionAttributeName(
                tokenC)), is(notNullValue()));
        assertThat(session.getAttribute(store.createSessionAttributeName(
                tokenD)), is(notNullValue()));
        assertThat(session.getAttribute(store.createSessionAttributeName(
                token1)), is(notNullValue()));
        assertThat(session.getAttribute(store.createSessionAttributeName(
                token2)), is(notNullValue()));
        assertThat(session.getAttribute(store.createSessionAttributeName(
                token3)), is(notNullValue()));
    }

    /**
     * create a new Token key (stored token is as it is the same as sizePerTokenName)
     */
    @Test
    void testCreateAndReserveTokenKey_storedToken_is_greater_than_sizePerTokenName() throws InterruptedException {
        // prepare store instance
        store = new HttpSessionTransactionTokenStore(4, 4);

        // setup parameters
        HttpSession session = new MockHttpSession();
        request.setSession(session);

        TransactionToken tokenA = new TransactionToken("tokenName", "tokenKeyA", "tokenValueA");
        store.store(tokenA);
        TimeUnit.MILLISECONDS.sleep(1);
        TransactionToken tokenB = new TransactionToken("tokenName", "tokenKeyB", "tokenValueB");
        store.store(tokenB);
        TimeUnit.MILLISECONDS.sleep(1);
        TransactionToken tokenC = new TransactionToken("tokenName", "tokenKeyC", "tokenValueC");
        store.store(tokenC);
        TimeUnit.MILLISECONDS.sleep(1);
        TransactionToken tokenD = new TransactionToken("tokenName", "tokenKeyD", "tokenValueE");
        store.store(tokenD);
        TimeUnit.MILLISECONDS.sleep(1);
        TransactionToken tokenE = new TransactionToken("tokenName", "tokenKeyE", "tokenValueD");
        store.store(tokenE);
        TimeUnit.MILLISECONDS.sleep(1);
        TransactionToken token1 = new TransactionToken("tokenName1", "tokenKey1", "tokenValue1");
        store.store(token1);
        TimeUnit.MILLISECONDS.sleep(1);
        TransactionToken token2 = new TransactionToken("tokenName2", "tokenKey2", "tokenValue2");
        store.store(token2);
        TimeUnit.MILLISECONDS.sleep(1);
        TransactionToken token3 = new TransactionToken("tokenName3", "tokenKey3", "tokenValue3");
        store.store(token3);

        // run
        String actual = store.createAndReserveTokenKey(tokenA.getTokenName());
        Enumeration<String> enumeration = session.getAttributeNames();
        while (enumeration.hasMoreElements()) {
            System.out.println(enumeration.nextElement());
        }

        // String expected = "";

        // assert
        assertNotNull(actual);
        assertThat(session.getAttribute(store.createSessionAttributeName(
                tokenA)), is(nullValue())); // check
        assertThat(session.getAttribute(store.createSessionAttributeName(
                tokenB)), is(nullValue())); // check
        assertThat(session.getAttribute(store.createSessionAttributeName(
                tokenC)), is(notNullValue()));
        assertThat(session.getAttribute(store.createSessionAttributeName(
                tokenD)), is(notNullValue()));
        assertThat(session.getAttribute(store.createSessionAttributeName(
                tokenE)), is(notNullValue()));
        assertThat(session.getAttribute(store.createSessionAttributeName(
                token1)), is(notNullValue()));
        assertThat(session.getAttribute(store.createSessionAttributeName(
                token2)), is(notNullValue()));
        assertThat(session.getAttribute(store.createSessionAttributeName(
                token3)), is(notNullValue()));
    }

    /**
     * create a new Token key (stored token is as it is the same as sizePerTokenName)
     */
    @Test
    void testCreateAndReserveTokenKey_storedToken_is_greater_than_sizePerTokenName2() throws InterruptedException {
        // prepare store instance
        store = new HttpSessionTransactionTokenStore(4, 4);

        // setup parameters
        HttpSession session = new MockHttpSession();
        request.setSession(session);

        TransactionToken tokenA = new TransactionToken("tokenName", "tokenKeyA", "tokenValueA");
        store.store(tokenA);
        TimeUnit.MILLISECONDS.sleep(1);
        TransactionToken tokenB = new TransactionToken("tokenName", "tokenKeyB", "tokenValueB");
        store.store(tokenB);
        TimeUnit.MILLISECONDS.sleep(1);
        TransactionToken tokenC = new TransactionToken("tokenName", "tokenKeyC", "tokenValueC");
        store.store(tokenC);
        TimeUnit.MILLISECONDS.sleep(1);
        TransactionToken tokenD = new TransactionToken("tokenName", "tokenKeyD", "tokenValueD");
        store.store(tokenD);
        TimeUnit.MILLISECONDS.sleep(1);
        TransactionToken tokenE = new TransactionToken("tokenName", "tokenKeyE", "tokenValueE");
        store.store(tokenE);
        TimeUnit.MILLISECONDS.sleep(1);
        TransactionToken tokenF = new TransactionToken("tokenName", "tokenKeyF", "tokenValueF");
        store.store(tokenF);
        TimeUnit.MILLISECONDS.sleep(1);
        TransactionToken token1 = new TransactionToken("tokenName1", "tokenKey1", "tokenValue1");
        store.store(token1);
        TimeUnit.MILLISECONDS.sleep(1);
        TransactionToken token2 = new TransactionToken("tokenName2", "tokenKey2", "tokenValue2");
        store.store(token2);
        TimeUnit.MILLISECONDS.sleep(1);
        TransactionToken token3 = new TransactionToken("tokenName3", "tokenKey3", "tokenValue3");
        store.store(token3);

        // run
        String actual = store.createAndReserveTokenKey(tokenA.getTokenName());
        Enumeration<String> enumeration = session.getAttributeNames();
        while (enumeration.hasMoreElements()) {
            System.out.println(enumeration.nextElement());
        }

        // String expected = "";

        // assert
        assertNotNull(actual);
        assertThat(session.getAttribute(store.createSessionAttributeName(
                tokenA)), is(nullValue())); // check
        assertThat(session.getAttribute(store.createSessionAttributeName(
                tokenB)), is(nullValue())); // check
        assertThat(session.getAttribute(store.createSessionAttributeName(
                tokenC)), is(nullValue())); // check
        assertThat(session.getAttribute(store.createSessionAttributeName(
                tokenD)), is(notNullValue()));
        assertThat(session.getAttribute(store.createSessionAttributeName(
                tokenE)), is(notNullValue()));
        assertThat(session.getAttribute(store.createSessionAttributeName(
                tokenF)), is(notNullValue()));
        assertThat(session.getAttribute(store.createSessionAttributeName(
                token1)), is(notNullValue()));
        assertThat(session.getAttribute(store.createSessionAttributeName(
                token2)), is(notNullValue()));
        assertThat(session.getAttribute(store.createSessionAttributeName(
                token3)), is(notNullValue()));
    }

    /**
     * create a new Token key (stored token is less than sizePerTokenName)
     */
    @Test
    void testCreateAndReserveTokenKey_storedToken_isLessThan_sizePerTokenName() {
        // prepare store instance
        store = new HttpSessionTransactionTokenStore(5);

        // setup parameters
        HttpSession session = new MockHttpSession();
        request.setSession(session);

        TransactionToken tokenA = new TransactionToken("tokenName", "tokenKeyA", "tokenValueA");
        store.store(tokenA);
        TransactionToken tokenB = new TransactionToken("tokenName", "tokenKeyB", "tokenValueB");
        store.store(tokenB);
        TransactionToken tokenC = new TransactionToken("tokenName", "tokenKeyC", "tokenValueC");
        store.store(tokenC);
        TransactionToken token1 = new TransactionToken("tokenName1", "tokenKey1", "tokenValue1");
        store.store(token1);
        TransactionToken token2 = new TransactionToken("tokenName2", "tokenKey2", "tokenValue2");
        store.store(token2);
        TransactionToken token3 = new TransactionToken("tokenName3", "tokenKey3", "tokenValue3");
        store.store(token3);

        // run
        String actual = store.createAndReserveTokenKey(tokenA.getTokenName());

        // String expected = "";

        // assert
        assertNotNull(actual);
        assertThat(session.getAttribute(store.createSessionAttributeName(
                tokenA)), is(notNullValue()));
        assertThat(session.getAttribute(store.createSessionAttributeName(
                tokenB)), is(notNullValue()));
        assertThat(session.getAttribute(store.createSessionAttributeName(
                tokenC)), is(notNullValue()));
        assertThat(session.getAttribute(store.createSessionAttributeName(
                token1)), is(notNullValue()));
        assertThat(session.getAttribute(store.createSessionAttributeName(
                token2)), is(notNullValue()));
        assertThat(session.getAttribute(store.createSessionAttributeName(
                token3)), is(notNullValue()));
    }

    @Test
    void testCreateAndReserveTokenKey_generate_failed() throws Exception {
        assertThrows(IllegalStateException.class, () -> {
            // prepare store instance
            store = new HttpSessionTransactionTokenStore(new TokenStringGenerator() {
                @Override
                public String generate(String seed) {
                    // return always same value
                    return "xxxxx";
                }
            }, 5, 5);

            // setup parameters
            MockHttpSession session = new MockHttpSession();
            request.setSession(session);

            session.setAttribute(
                    HttpSessionTransactionTokenStore.TOKEN_HOLDER_SESSION_ATTRIBUTE_PREFIX
                            + "foo" + TransactionToken.TOKEN_STRING_SEPARATOR
                            + "xxxxx", "already in!");
            try {
                store.createAndReserveTokenKey("foo");
            } catch (Exception e) {
                assertThat(e.getMessage(), is(
                        "token key generation failed within retry count 5"));
                throw e;
            }
        });
    }

    @Test
    void testStore_token_isNull() throws Exception {
        assertThrows(IllegalArgumentException.class, () -> {
            try {
                store = new HttpSessionTransactionTokenStore(5);
                store.store(null);
            } catch (Exception e) {
                assertThat(e.getMessage(), is("token must not be null"));
                throw e;
            }
        });
    }

    @Test
    void testConstructor_generator_isNull() throws Exception {
        assertThrows(IllegalArgumentException.class, () -> {
            try {
                new HttpSessionTransactionTokenStore(null, 10, 10);
            } catch (Exception e) {
                assertThat(e.getMessage(), is("generator must not be null"));
                throw e;
            }
        });
    }

    @Test
    void testConstructor_transactionTokensPerTokenName_isZero() throws Exception {
        assertThrows(IllegalArgumentException.class, () -> {
            try {
                new HttpSessionTransactionTokenStore(new TokenStringGenerator(), 0, 1);
            } catch (Exception e) {
                assertThat(e.getMessage(), is(
                        "transactionTokenSizePerTokenName must be greater than 0"));
                throw e;
            }

        });

    }

    @Test
    void testConstructor_transactionTokensPerTokenName_isNegative() throws Exception {
        assertThrows(IllegalArgumentException.class, () -> {
            try {
                new HttpSessionTransactionTokenStore(new TokenStringGenerator(), -1, 0);
            } catch (Exception e) {
                assertThat(e.getMessage(), is(
                        "transactionTokenSizePerTokenName must be greater than 0"));
                throw e;
            }

        });

    }

    @Test
    void testConstructor_retryCreateTokenName_isZero() throws Exception {
        assertThrows(IllegalArgumentException.class, () -> {
            try {
                new HttpSessionTransactionTokenStore(new TokenStringGenerator(), 1, 0);
            } catch (Exception e) {
                assertThat(e.getMessage(), is(
                        "retryCreateTokenName must be greater than 0"));
                throw e;
            }

        });

    }

    @Test
    void testConstructor_retryCreateTokenName_isNegative() throws Exception {
        assertThrows(IllegalArgumentException.class, () -> {
            try {
                new HttpSessionTransactionTokenStore(new TokenStringGenerator(), 1, -1);
            } catch (Exception e) {
                assertThat(e.getMessage(), is(
                        "retryCreateTokenName must be greater than 0"));
                throw e;
            }

        });

    }
}

/*
 * Originally distributed by NTT DATA Corporation under Apache License, Version 2.0.
 *    Library: TERASOLUNA Server Framework for Java (5.x) Common Library
 *    Source:  https://github.com/terasolunaorg/terasoluna-gfw/blob/5.4.1.RELEASE/terasoluna-gfw-common-libraries/terasoluna-gfw-web/src/test/java/org/terasoluna/gfw/web/token/transaction/TransactionTokenContextImplTest.java
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

import java.lang.reflect.Field;

import org.springframework.util.ReflectionUtils;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TransactionTokenContextImplTest {

    @BeforeEach
    void setUp() throws Exception {
    }

    @AfterEach
    void tearDown() throws Exception {
    }

    @Test
    void TestTransactionTokenContextImpl01() {

        // setup parameters
        TransactionTokenInfo beginTransactionToken = new TransactionTokenInfo("testTokenAttribute1", TransactionTokenType.BEGIN);
        TransactionToken receivedToken = new TransactionToken("aaa");

        // setup up expected result
        TransactionTokenContextImpl.ReserveCommand expectedCommand = TransactionTokenContextImpl.ReserveCommand.CREATE_TOKEN;

        // run
        TransactionTokenContextImpl contextImpl = new TransactionTokenContextImpl(beginTransactionToken, receivedToken);

        // test
        TransactionTokenContextImpl.ReserveCommand resultCommand = contextImpl.getReserveCommand();

        assertThat(resultCommand, is(expectedCommand));
        assertThat(contextImpl.getReceivedToken(), is(receivedToken));
        assertThat(contextImpl.getTokenInfo(), is(beginTransactionToken));
    }

    @Test
    void TestTransactionTokenContextImpl02() {

        // setup parameters
        TransactionTokenInfo beginTransactionToken = new TransactionTokenInfo("testTokenAttribute1", TransactionTokenType.BEGIN);
        TransactionToken receivedToken = new TransactionToken("aaa", "key", "value");

        // setup up expected result
        TransactionTokenContextImpl.ReserveCommand expectedCommand = TransactionTokenContextImpl.ReserveCommand.UPDATE_TOKEN;

        // run
        TransactionTokenContextImpl contextImpl = new TransactionTokenContextImpl(beginTransactionToken, receivedToken);

        // test
        TransactionTokenContextImpl.ReserveCommand resultCommand = contextImpl.getReserveCommand();

        assertThat(resultCommand, is(expectedCommand));
        assertThat(contextImpl.getReceivedToken(), is(receivedToken));
        assertThat(contextImpl.getTokenInfo(), is(beginTransactionToken));
    }

    @Test
    void TestTransactionTokenContextImpl03() {

        // setup parameters
        TransactionTokenInfo inTransactionToken = new TransactionTokenInfo("testTokenAttribute2", TransactionTokenType.IN);
        TransactionToken receivedToken = new TransactionToken("bbb");

        // setup up expected result
        TransactionTokenContextImpl.ReserveCommand expectedCommand = TransactionTokenContextImpl.ReserveCommand.CREATE_TOKEN;

        // run
        TransactionTokenContextImpl contextImpl = new TransactionTokenContextImpl(inTransactionToken, receivedToken);
        TransactionTokenContextImpl.ReserveCommand resultCommand = contextImpl.getReserveCommand();

        // test
        assertThat(resultCommand, is(expectedCommand));
        assertThat(contextImpl.getReceivedToken(), is(receivedToken));
        assertThat(contextImpl.getTokenInfo(), is(inTransactionToken));

    }

    @Test
    void TestTransactionTokenContextImpl04() {

        // setup parameters
        TransactionTokenInfo inTransactionToken = new TransactionTokenInfo("testTokenAttribute2", TransactionTokenType.IN);
        TransactionToken receivedToken = new TransactionToken("bbb", "key", "value");

        // setup up expected result
        TransactionTokenContextImpl.ReserveCommand expectedCommand = TransactionTokenContextImpl.ReserveCommand.UPDATE_TOKEN;

        // run
        TransactionTokenContextImpl contextImpl = new TransactionTokenContextImpl(inTransactionToken, receivedToken);
        TransactionTokenContextImpl.ReserveCommand resultCommand = contextImpl.getReserveCommand();

        // test
        assertThat(resultCommand, is(expectedCommand));
        assertThat(contextImpl.getReceivedToken(), is(receivedToken));
        assertThat(contextImpl.getTokenInfo(), is(inTransactionToken));

    }

    @Test
    void TestTransactionTokenContextImpl05() {

        // setup parameters
        TransactionTokenInfo endTransactionToken = new TransactionTokenInfo("testTokenAttribute3", TransactionTokenType.END);
        TransactionToken receivedToken = new TransactionToken("ccc");

        // setup up expected result
        TransactionTokenContextImpl.ReserveCommand expectedCommand = TransactionTokenContextImpl.ReserveCommand.NONE;

        // run
        TransactionTokenContextImpl contextImpl = new TransactionTokenContextImpl(endTransactionToken, receivedToken);

        // test
        TransactionTokenContextImpl.ReserveCommand resultCommand = contextImpl.getReserveCommand();
        assertThat(resultCommand, is(expectedCommand));
        assertThat(contextImpl.getReceivedToken(), is(receivedToken));
        assertThat(contextImpl.getTokenInfo(), is(endTransactionToken));
    }

    @Test
    void TestTransactionTokenContextImpl06() {

        // setup parameters
        TransactionTokenInfo endTransactionToken = new TransactionTokenInfo("testTokenAttribute3", TransactionTokenType.END);
        TransactionToken receivedToken = new TransactionToken("ccc", "key", "value");

        // setup up expected result
        TransactionTokenContextImpl.ReserveCommand expectedCommand = TransactionTokenContextImpl.ReserveCommand.REMOVE_TOKEN;

        // run
        TransactionTokenContextImpl contextImpl = new TransactionTokenContextImpl(endTransactionToken, receivedToken);

        // test
        TransactionTokenContextImpl.ReserveCommand resultCommand = contextImpl.getReserveCommand();
        assertThat(resultCommand, is(expectedCommand));
        assertThat(contextImpl.getReceivedToken(), is(receivedToken));
        assertThat(contextImpl.getTokenInfo(), is(endTransactionToken));
    }

    @Test
    void testTransactionTokenContextImpl07() {

        // setup parameters
        TransactionTokenInfo checkTransactionToken = new TransactionTokenInfo("checkToken", TransactionTokenType.CHECK);
        TransactionToken receivedToken = new TransactionToken("namespace", "key", "value");

        // setup up expected result
        TransactionTokenContextImpl.ReserveCommand expectedCommand = TransactionTokenContextImpl.ReserveCommand.KEEP_TOKEN;

        // run
        TransactionTokenContextImpl contextImpl = new TransactionTokenContextImpl(checkTransactionToken, receivedToken);

        // test
        TransactionTokenContextImpl.ReserveCommand resultCommand = contextImpl.getReserveCommand();
        assertThat(resultCommand, is(expectedCommand));
        assertThat(contextImpl.getReceivedToken(), is(receivedToken));
        assertThat(contextImpl.getTokenInfo(), is(checkTransactionToken));
    }

    @Test
    void testTransactionTokenContextImpl08() {

        // setup parameters
        TransactionTokenInfo updateTransactionToken = new TransactionTokenInfo("updateToken", TransactionTokenType.CHECK);
        TransactionToken receivedToken = new TransactionToken("namespace", "key", "");

        // setup up expected result
        TransactionTokenContextImpl.ReserveCommand expectedCommand = TransactionTokenContextImpl.ReserveCommand.KEEP_TOKEN;

        // run
        TransactionTokenContextImpl contextImpl = new TransactionTokenContextImpl(updateTransactionToken, receivedToken);

        // test
        TransactionTokenContextImpl.ReserveCommand resultCommand = contextImpl.getReserveCommand();
        assertThat(resultCommand, is(expectedCommand));
        assertThat(contextImpl.getReceivedToken(), is(receivedToken));
        assertThat(contextImpl.getTokenInfo(), is(updateTransactionToken));
    }

    @Test
    void TestCreateToken01() {
        // setup parameters
        TransactionTokenInfo beginTransactionToken = new TransactionTokenInfo("testTokenAttribute1", TransactionTokenType.BEGIN);
        TransactionToken receivedToken = new TransactionToken("aaa", "key", "value");

        // setup target
        TransactionTokenContextImpl contextImpl = new TransactionTokenContextImpl(beginTransactionToken, receivedToken);

        // setup up expected result
        TransactionTokenContextImpl.ReserveCommand expectedCommand = TransactionTokenContextImpl.ReserveCommand.REMOVE_TOKEN;

        // run
        contextImpl.createToken();

        // test
        TransactionTokenContextImpl.ReserveCommand resultCommand = contextImpl.getReserveCommand();
        assertThat(resultCommand, is(expectedCommand));

    }

    @Test
    void TestCreateToken02() {
        // setup parameters
        TransactionTokenInfo beginTransactionToken = new TransactionTokenInfo("testTokenAttribute1", TransactionTokenType.BEGIN);
        TransactionToken receivedToken = new TransactionToken("aaa");

        // setup target
        TransactionTokenContextImpl contextImpl = new TransactionTokenContextImpl(beginTransactionToken, receivedToken);

        // setup up expected result
        TransactionTokenContextImpl.ReserveCommand expectedCommand = TransactionTokenContextImpl.ReserveCommand.NONE;

        // run
        contextImpl.createToken();

        // test
        TransactionTokenContextImpl.ReserveCommand resultCommand = contextImpl.getReserveCommand();
        assertThat(resultCommand, is(expectedCommand));
    }

    @Test
    void TestRemoveToken01() {
        // setup parameters
        TransactionTokenInfo beginTransactionToken = new TransactionTokenInfo("testTokenAttribute1", TransactionTokenType.BEGIN);
        TransactionToken receivedToken = new TransactionToken("aaa", "key", "value");

        // setup target
        TransactionTokenContextImpl contextImpl = new TransactionTokenContextImpl(beginTransactionToken, receivedToken);

        // setup up expected result
        TransactionTokenContextImpl.ReserveCommand expectedCommand = TransactionTokenContextImpl.ReserveCommand.REMOVE_TOKEN;

        // run
        contextImpl.removeToken();

        // test
        TransactionTokenContextImpl.ReserveCommand resultCommand = contextImpl.getReserveCommand();
        assertThat(resultCommand, is(expectedCommand));

    }

    @Test
    void TestCancelReservation01() throws IllegalArgumentException, IllegalAccessException {
        // setup parameters
        TransactionTokenInfo beginTransactionToken = new TransactionTokenInfo("testTokenAttribute1", TransactionTokenType.BEGIN);
        TransactionToken receivedToken = new TransactionToken("aaa");

        // setup target
        TransactionTokenContextImpl contextImpl = new TransactionTokenContextImpl(beginTransactionToken, receivedToken);

        // setup up expected result
        TransactionTokenContextImpl.ReserveCommand expectedCommand = TransactionTokenContextImpl.ReserveCommand.CREATE_TOKEN;

        // run
        contextImpl.cancelReservation();

        // test
        Field field = ReflectionUtils.findField(
                TransactionTokenContextImpl.class, "defaultCommand");
        ReflectionUtils.makeAccessible(field);
        TransactionTokenContextImpl.ReserveCommand resultCommand = (TransactionTokenContextImpl.ReserveCommand) field.get(contextImpl);
        assertThat(resultCommand, is(expectedCommand));

    }

    @Test
    void TestCancelReservation02() throws IllegalArgumentException, IllegalAccessException {

        // setup parameters
        TransactionTokenInfo beginTransactionToken = new TransactionTokenInfo("testTokenAttribute1", TransactionTokenType.BEGIN);
        TransactionToken receivedToken = new TransactionToken("aaa", "key", "value");

        // setup up expected result
        TransactionTokenContextImpl.ReserveCommand expectedCommand = TransactionTokenContextImpl.ReserveCommand.UPDATE_TOKEN;

        // run
        TransactionTokenContextImpl contextImpl = new TransactionTokenContextImpl(beginTransactionToken, receivedToken);

        contextImpl.cancelReservation();

        // test
        Field field = ReflectionUtils.findField(
                TransactionTokenContextImpl.class, "defaultCommand");
        ReflectionUtils.makeAccessible(field);
        TransactionTokenContextImpl.ReserveCommand resultCommand = (TransactionTokenContextImpl.ReserveCommand) field.get(contextImpl);
        assertThat(resultCommand, is(expectedCommand));
    }

    @Test
    void TestCancelReservation03() throws IllegalArgumentException, IllegalAccessException {

        // setup parameters
        TransactionTokenInfo inTransactionToken = new TransactionTokenInfo("testTokenAttribute2", TransactionTokenType.IN);
        TransactionToken receivedToken = new TransactionToken("bbb");

        // setup up expected result
        TransactionTokenContextImpl.ReserveCommand expectedCommand = TransactionTokenContextImpl.ReserveCommand.CREATE_TOKEN;

        // run
        TransactionTokenContextImpl contextImpl = new TransactionTokenContextImpl(inTransactionToken, receivedToken);
        contextImpl.cancelReservation();

        // test
        Field field = ReflectionUtils.findField(
                TransactionTokenContextImpl.class, "defaultCommand");
        ReflectionUtils.makeAccessible(field);
        TransactionTokenContextImpl.ReserveCommand resultCommand = (TransactionTokenContextImpl.ReserveCommand) field.get(contextImpl);
        assertThat(resultCommand, is(expectedCommand));

    }

    @Test
    void TestCancelReservation04() throws IllegalArgumentException, IllegalAccessException {

        // setup parameters
        TransactionTokenInfo inTransactionToken = new TransactionTokenInfo("testTokenAttribute2", TransactionTokenType.IN);
        TransactionToken receivedToken = new TransactionToken("bbb", "key", "value");

        // setup up expected result
        TransactionTokenContextImpl.ReserveCommand expectedCommand = TransactionTokenContextImpl.ReserveCommand.UPDATE_TOKEN;

        // run
        TransactionTokenContextImpl contextImpl = new TransactionTokenContextImpl(inTransactionToken, receivedToken);
        contextImpl.cancelReservation();

        // test
        Field field = ReflectionUtils.findField(
                TransactionTokenContextImpl.class, "defaultCommand");
        ReflectionUtils.makeAccessible(field);
        TransactionTokenContextImpl.ReserveCommand resultCommand = (TransactionTokenContextImpl.ReserveCommand) field.get(contextImpl);
        assertThat(resultCommand, is(expectedCommand));

    }

    @Test
    void TestCancelReservation05() throws IllegalArgumentException, IllegalAccessException {

        // setup parameters
        TransactionTokenInfo endTransactionToken = new TransactionTokenInfo("testTokenAttribute3", TransactionTokenType.END);
        TransactionToken receivedToken = new TransactionToken("ccc");

        // setup up expected result
        TransactionTokenContextImpl.ReserveCommand expectedCommand = TransactionTokenContextImpl.ReserveCommand.NONE;

        // run
        TransactionTokenContextImpl contextImpl = new TransactionTokenContextImpl(endTransactionToken, receivedToken);

        contextImpl.cancelReservation();

        // test
        Field field = ReflectionUtils.findField(
                TransactionTokenContextImpl.class, "defaultCommand");
        ReflectionUtils.makeAccessible(field);
        TransactionTokenContextImpl.ReserveCommand resultCommand = (TransactionTokenContextImpl.ReserveCommand) field.get(contextImpl);
        assertThat(resultCommand, is(expectedCommand));
    }

    @Test
    void TestCancelReservation06() throws IllegalArgumentException, IllegalAccessException {

        // setup parameters
        TransactionTokenInfo endTransactionToken = new TransactionTokenInfo("testTokenAttribute3", TransactionTokenType.END);
        TransactionToken receivedToken = new TransactionToken("ccc", "key", "value");

        // setup up expected result
        TransactionTokenContextImpl.ReserveCommand expectedCommand = TransactionTokenContextImpl.ReserveCommand.REMOVE_TOKEN;

        // run
        TransactionTokenContextImpl contextImpl = new TransactionTokenContextImpl(endTransactionToken, receivedToken);

        contextImpl.cancelReservation();

        // test
        Field field = ReflectionUtils.findField(
                TransactionTokenContextImpl.class, "defaultCommand");
        ReflectionUtils.makeAccessible(field);
        TransactionTokenContextImpl.ReserveCommand resultCommand = (TransactionTokenContextImpl.ReserveCommand) field.get(contextImpl);
        assertThat(resultCommand, is(expectedCommand));
    }
}

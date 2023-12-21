/*
 * Originally distributed by NTT DATA Corporation under Apache License, Version 2.0.
 *    Library: TERASOLUNA Server Framework for Java (5.x) Common Library
 *    Source:  https://github.com/terasolunaorg/terasoluna-gfw/blob/5.4.1.RELEASE/terasoluna-gfw-common-libraries/terasoluna-gfw-web/src/test/java/org/terasoluna/gfw/web/token/transaction/TransactionTokenInfoTest.java
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

import org.junit.jupiter.api.Test;

class TransactionTokenInfoTest {

    @Test
    void TestTransactionTokenInfo01() {
        // setup input parameters

        String tokenName = "aaa";

        TransactionTokenType beginToken = TransactionTokenType.BEGIN;

        // run
        TransactionTokenInfo info = new TransactionTokenInfo(tokenName, beginToken);

        // assert
        assertThat(tokenName, is(info.getTokenName()));
        assertThat(beginToken, is(info.getTransactionTokenType()));
    }

    @Test
    void TestNeedCreate01() {
        // setup input parameters

        String tokenName = "aaa";

        TransactionTokenType beginToken = TransactionTokenType.BEGIN;

        // run
        TransactionTokenInfo info = new TransactionTokenInfo(tokenName, beginToken);
        boolean output = info.needCreate();
        // assert
        assertThat(output, is(true));
    }

    @Test
    void TestNeedCreate02() {
        // setup input parameters

        String tokenName = "aaa";

        TransactionTokenType beginToken = TransactionTokenType.IN;

        // run
        TransactionTokenInfo info = new TransactionTokenInfo(tokenName, beginToken);
        boolean output = info.needCreate();
        // assert
        assertThat(output, is(true));
    }

    @Test
    void TestNeedCreate03() {
        // setup input parameters

        String tokenName = "aaa";

        TransactionTokenType beginToken = TransactionTokenType.END;

        // run
        TransactionTokenInfo info = new TransactionTokenInfo(tokenName, beginToken);
        boolean output = info.needCreate();
        // assert
        assertThat(output, is(false));
    }

    @Test
    void TestNeedValidate01() {
        // setup input parameters

        String tokenName = "aaa";

        TransactionTokenType beginToken = TransactionTokenType.BEGIN;

        // run
        TransactionTokenInfo info = new TransactionTokenInfo(tokenName, beginToken);
        boolean output = info.needValidate();
        // assert
        assertThat(output, is(false));
    }

    @Test
    void TestNeedValidate02() {
        // setup input parameters

        String tokenName = "aaa";

        TransactionTokenType beginToken = TransactionTokenType.IN;

        // run
        TransactionTokenInfo info = new TransactionTokenInfo(tokenName, beginToken);
        boolean output = info.needValidate();
        // assert
        assertThat(output, is(true));
    }

    @Test
    void TestNeedValidate03() {
        // setup input parameters

        String tokenName = "aaa";

        TransactionTokenType beginToken = TransactionTokenType.END;

        // run
        TransactionTokenInfo info = new TransactionTokenInfo(tokenName, beginToken);
        boolean output = info.needValidate();
        // assert
        assertThat(output, is(true));
    }

    @Test
    void TestNeedCreate04() {
        // setup input parameters

        String tokenName = "aaa";

        TransactionTokenType beginToken = TransactionTokenType.END;

        // run
        TransactionTokenInfo info = new TransactionTokenInfo(tokenName, beginToken);
        boolean output = info.needCreate();
        // assert
        assertThat(output, is(false));
    }

    @Test
    void TestNeedValidate04() {
        // setup input parameters

        String tokenName = "aaa";

        TransactionTokenType beginToken = TransactionTokenType.END;

        // run
        TransactionTokenInfo info = new TransactionTokenInfo(tokenName, beginToken);
        boolean output = info.needCreate();
        // assert
        assertThat(output, is(false));
    }

    @Test
    void testNeedCreate05() {
        // setup input parameters

        String tokenName = "aaa";

        TransactionTokenType tokenType = TransactionTokenType.CHECK;

        // run
        TransactionTokenInfo tokenInfo = new TransactionTokenInfo(tokenName, tokenType);
        boolean output = tokenInfo.needCreate();
        // assert
        assertThat(output, is(false));
    }

    @Test
    void testNeedValidate05() {
        // setup input parameters

        String tokenName = "aaa";

        TransactionTokenType tokenType = TransactionTokenType.CHECK;

        // run
        TransactionTokenInfo info = new TransactionTokenInfo(tokenName, tokenType);
        boolean output = info.needValidate();
        // assert
        assertThat(output, is(true));
    }

    @Test
    void testNeedKeep01() {
        // setup input parameters

        String tokenName = "aaa";

        TransactionTokenType tokenType = TransactionTokenType.NONE;

        // run
        TransactionTokenInfo info = new TransactionTokenInfo(tokenName, tokenType);
        boolean output = info.needKeep();
        // assert
        assertThat(output, is(false));
    }

    @Test
    void testNeedKeep02() {
        // setup input parameters

        String tokenName = "aaa";

        TransactionTokenType tokenType = TransactionTokenType.BEGIN;

        // run
        TransactionTokenInfo info = new TransactionTokenInfo(tokenName, tokenType);
        boolean output = info.needKeep();
        // assert
        assertThat(output, is(false));
    }

    @Test
    void testNeedKeep03() {
        // setup input parameters

        String tokenName = "aaa";

        TransactionTokenType tokenType = TransactionTokenType.IN;

        // run
        TransactionTokenInfo info = new TransactionTokenInfo(tokenName, tokenType);
        boolean output = info.needKeep();
        // assert
        assertThat(output, is(false));
    }

    @Test
    void testNeedKeep04() {
        // setup input parameters

        String tokenName = "aaa";

        TransactionTokenType tokenType = TransactionTokenType.END;

        // run
        TransactionTokenInfo info = new TransactionTokenInfo(tokenName, tokenType);
        boolean output = info.needKeep();
        // assert
        assertThat(output, is(false));
    }

    @Test
    void testNeedKeep05() {
        // setup input parameters

        String tokenName = "aaa";

        TransactionTokenType tokenType = TransactionTokenType.CHECK;

        // run
        TransactionTokenInfo info = new TransactionTokenInfo(tokenName, tokenType);
        boolean output = info.needKeep();
        // assert
        assertThat(output, is(true));
    }

    @Test
    void TestToString() {
        // setup input parameters

        String tokenName = "tokenName";

        TransactionTokenType beginToken = TransactionTokenType.END;

        // run
        TransactionTokenInfo info = new TransactionTokenInfo(tokenName, beginToken);
        String output = info.toString();
        // assert
        assertThat(output, is("TransactionTokenInfo [tokenName=" + tokenName
                + ", transitionType=" + TransactionTokenType.END + "]"));
    }

}

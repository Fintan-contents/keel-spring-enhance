/*
 * Originally distributed by NTT DATA Corporation under Apache License, Version 2.0.
 *    Library: TERASOLUNA Server Framework for Java (5.x) Common Library
 *    Source:  https://github.com/terasolunaorg/terasoluna-gfw/blob/5.4.1.RELEASE/terasoluna-gfw-common-libraries/terasoluna-gfw-web/src/test/java/org/terasoluna/gfw/web/token/transaction/InvalidTransactionTokenExceptionTest.java
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

class InvalidTransactionTokenExceptionTest {

    @Test
    void testExceptionConstructor() {

        try {
            process();
        } catch (InvalidTransactionTokenException e) {
            assertThat(e.getMessage(), is(
                    "Invalid Transaction Token Exception !!!"));
        }
    }

    @Test
    void testExceptionConstructorWithCustomMessage() {

        try {
            processOther();
        } catch (InvalidTransactionTokenException e) {
            assertThat(e.getMessage(), is("Custom Message"));
        }
    }

    private void processOther() {
        throw new InvalidTransactionTokenException("Custom Message");
    }

    private void process() {
        throw new InvalidTransactionTokenException();
    }

}

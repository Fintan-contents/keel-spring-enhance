/*
 * Originally distributed by NTT DATA Corporation under Apache License, Version 2.0.
 *    Library: TERASOLUNA Server Framework for Java (5.x) Common Library
 *    Source:  https://github.com/terasolunaorg/terasoluna-gfw/blob/5.4.1.RELEASE/terasoluna-gfw-common-libraries/terasoluna-gfw-web/src/test/java/org/terasoluna/gfw/web/mvc/support/RequestDataValueProcessorAdaptorTest.java
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
package jp.fintan.keel.spring.web.mvc.support;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.mock.web.MockHttpServletRequest;

import org.junit.jupiter.api.Test;

class RequestDataValueProcessorAdaptorTest {

    RequestDataValueProcessorAdaptor requestDataValueProcessorAdaptor = new RequestDataValueProcessorAdaptor();

    HttpServletRequest request = new MockHttpServletRequest();

    @Test
    void testProcessAction01() {
        String action = requestDataValueProcessorAdaptor.processAction(request,
                "action");
        assertThat(action, is("action"));
    }

    @Test
    void testProcessAction02() {
        // set up
        String action = requestDataValueProcessorAdaptor.processAction(request,
                "action", "method");

        // assert
        assertThat(action, is("action"));
    }

    @Test
    void testProcessFormFieldValue() {
        String value = requestDataValueProcessorAdaptor.processFormFieldValue(
                request, "", "value", "");
        assertThat(value, is("value"));
    }

    @Test
    void testGetExtraHiddenFields() {
        Map<String, String> map = requestDataValueProcessorAdaptor
                .getExtraHiddenFields(request);
        assertNull(map);
    }

    @Test
    void testProcessUrl() {
        String url = requestDataValueProcessorAdaptor.processUrl(request,
                "http://localhost:8080/test");
        assertThat(url, is("http://localhost:8080/test"));
    }

}

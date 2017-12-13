/*
 * Copyright (c) 2017 seleniumQuery authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package endtoend.fluentfunctions.evaluators.is;

import static endtoend.fluentfunctions.evaluators.EvaluatorsExceptionTestUtils.assertThrowsAssertionError;
import static endtoend.fluentfunctions.evaluators.EvaluatorsExceptionTestUtils.assertThrowsTimeoutException;
import static io.github.seleniumquery.SeleniumQuery.$;
import static org.junit.Assert.assertEquals;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import testinfrastructure.junitrule.SetUpAndTearDownDriver;

public class IsNotEmptyEvaluatorTest {

    @ClassRule @Rule public static SetUpAndTearDownDriver setUpAndTearDownDriverRule = new SetUpAndTearDownDriver();

    @Test
    public void success() {
        assertEquals(3, $("div").waitUntil().isNotEmpty().then().size());
        assertEquals(3, $("div").assertThat().isNotEmpty().then().size());
    }

    @Test
    public void isNotEmpty_fails_waitUntil() {
        assertThrowsTimeoutException(
            __ ->
                $("span").waitUntil(100).isNotEmpty()
            ,
            "Timeout while waiting for $(\"span\").waitUntil().isNotEmpty().\n\n" +
                "expected: <matched element set to be not empty>\n" +
                "but: <last matched element set was empty>"
        );
    }

    @Test
    public void isNotEmpty_fails_assertThat() {
        assertThrowsAssertionError(
            __ ->
                $("span").assertThat().isNotEmpty()
            ,
            "Failed assertion $(\"span\").assertThat().isNotEmpty().\n\n" +
                "expected: <matched element set to be not empty>\n" +
                "but: <matched element set was empty>"
        );
    }

}

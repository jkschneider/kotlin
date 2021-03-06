/*
 * Copyright 2010-2015 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jetbrains.kotlin.idea.completion.test.handlers;

import com.intellij.codeInsight.completion.CompletionType;
import org.jetbrains.kotlin.idea.completion.test.KotlinCompletionTestCase;
import org.jetbrains.kotlin.idea.completion.test.TestPackage;

import java.io.File;

public class SmartCompletionMultifileHandlerTest extends KotlinCompletionTestCase {
    public void testImportExtensionFunction() throws Exception {
        doTest();
    }

    public void testImportExtensionProperty() throws Exception {
        doTest();
    }

    @Override
    protected void setUp() throws Exception {
        setType(CompletionType.SMART);
        super.setUp();
    }

    public void doTest() throws Exception {
        String fileName = getTestName(false);

        configureByFiles(null, fileName + "-1.kt", fileName + "-2.kt");
        complete(1);
        checkResultByFile(fileName + ".kt.after");
    }

    @Override
    protected String getTestDataPath() {
        return new File(TestPackage.getCOMPLETION_TEST_DATA_BASE_PATH(), "/handlers/multifile/smart/").getPath() + File.separator;
    }
}

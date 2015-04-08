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

package org.jetbrains.kotlin.lang.resolve.android.test;

import com.intellij.testFramework.TestDataPath;
import org.jetbrains.kotlin.test.JUnit3RunnerWithInners;
import org.jetbrains.kotlin.test.JetTestUtils;
import org.jetbrains.kotlin.test.TestMetadata;
import org.junit.runner.RunWith;

import java.io.File;
import java.util.regex.Pattern;

/** This class is generated by {@link org.jetbrains.kotlin.generators.tests.TestsPackage}. DO NOT MODIFY MANUALLY */
@SuppressWarnings("all")
@RunWith(JUnit3RunnerWithInners.class)
public class AndroidXml2KConversionTestGenerated extends AbstractAndroidXml2KConversionTest {
    @TestMetadata("plugins/android-compiler-plugin/testData/android/converter/simple")
    @TestDataPath("$PROJECT_ROOT")
    @RunWith(JUnit3RunnerWithInners.class)
    public static class Simple extends AbstractAndroidXml2KConversionTest {
        public void testAllFilesPresentInSimple() throws Exception {
            JetTestUtils.assertAllTestsPresentByMetadata(this.getClass(), new File("plugins/android-compiler-plugin/testData/android/converter/simple"), Pattern.compile("^([^\\.]+)$"), false);
        }

        @TestMetadata("escapedLayoutName")
        public void testEscapedLayoutName() throws Exception {
            String fileName = JetTestUtils.navigationMetadata("plugins/android-compiler-plugin/testData/android/converter/simple/escapedLayoutName/");
            doTest(fileName);
        }

        @TestMetadata("fqNameInAttr")
        public void testFqNameInAttr() throws Exception {
            String fileName = JetTestUtils.navigationMetadata("plugins/android-compiler-plugin/testData/android/converter/simple/fqNameInAttr/");
            doTest(fileName);
        }

        @TestMetadata("fqNameInTag")
        public void testFqNameInTag() throws Exception {
            String fileName = JetTestUtils.navigationMetadata("plugins/android-compiler-plugin/testData/android/converter/simple/fqNameInTag/");
            doTest(fileName);
        }

        @TestMetadata("layoutVariants")
        public void testLayoutVariants() throws Exception {
            String fileName = JetTestUtils.navigationMetadata("plugins/android-compiler-plugin/testData/android/converter/simple/layoutVariants/");
            doTest(fileName);
        }

        @TestMetadata("multiFile")
        public void testMultiFile() throws Exception {
            String fileName = JetTestUtils.navigationMetadata("plugins/android-compiler-plugin/testData/android/converter/simple/multiFile/");
            doTest(fileName);
        }

        @TestMetadata("noIds")
        public void testNoIds() throws Exception {
            String fileName = JetTestUtils.navigationMetadata("plugins/android-compiler-plugin/testData/android/converter/simple/noIds/");
            doTest(fileName);
        }

        @TestMetadata("sameIds")
        public void testSameIds() throws Exception {
            String fileName = JetTestUtils.navigationMetadata("plugins/android-compiler-plugin/testData/android/converter/simple/sameIds/");
            doTest(fileName);
        }

        @TestMetadata("singleFile")
        public void testSingleFile() throws Exception {
            String fileName = JetTestUtils.navigationMetadata("plugins/android-compiler-plugin/testData/android/converter/simple/singleFile/");
            doTest(fileName);
        }

        @TestMetadata("specialTags")
        public void testSpecialTags() throws Exception {
            String fileName = JetTestUtils.navigationMetadata("plugins/android-compiler-plugin/testData/android/converter/simple/specialTags/");
            doTest(fileName);
        }

        @TestMetadata("supportSingleFile")
        public void testSupportSingleFile() throws Exception {
            String fileName = JetTestUtils.navigationMetadata("plugins/android-compiler-plugin/testData/android/converter/simple/supportSingleFile/");
            doTest(fileName);
        }
    }

    @TestMetadata("plugins/android-compiler-plugin/testData/android/converter/exceptions")
    @TestDataPath("$PROJECT_ROOT")
    @RunWith(JUnit3RunnerWithInners.class)
    public static class Exceptions extends AbstractAndroidXml2KConversionTest {
        public void testAllFilesPresentInExceptions() throws Exception {
            JetTestUtils.assertAllTestsPresentByMetadata(this.getClass(), new File("plugins/android-compiler-plugin/testData/android/converter/exceptions"), Pattern.compile("^([^\\.]+)$"), false);
        }

        @TestMetadata("noManifest")
        public void testNoManifest() throws Exception {
            String fileName = JetTestUtils.navigationMetadata("plugins/android-compiler-plugin/testData/android/converter/exceptions/noManifest/");
            doNoManifestTest(fileName);
        }
    }
}

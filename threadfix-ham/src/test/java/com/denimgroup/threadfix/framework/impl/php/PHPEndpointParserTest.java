package com.denimgroup.threadfix.framework.impl.php;

import com.denimgroup.threadfix.framework.TestConstants;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PHPEndpointParserTest {

    @Test
    public void testFunctionLibrary(){
        String file = TestConstants.THREADFIX_SOURCE_ROOT + "threadfix-ham/target/test-classes/code.php/library-test.php";
        boolean result = PHPEndpointParser.parse(new File(file));

        assertFalse("The file should NOT be considered an endpoint", result);
    }

    @Test
    public void testFunctionCallInCode(){
        String file = TestConstants.THREADFIX_SOURCE_ROOT + "threadfix-ham/target/test-classes/code.php/function-call.php";
        boolean result = PHPEndpointParser.parse(new File(file));

        assertTrue("The file should be considered an endpoint", result);
    }

    @Test
    public void testMarkupOutsideOfCode() {
        String file = TestConstants.THREADFIX_SOURCE_ROOT + "threadfix-ham/target/test-classes/code.php/markup-file.php";
        boolean result = PHPEndpointParser.parse(new File(file));

        assertTrue("The file should be considered an endpoint", result);
    }
}

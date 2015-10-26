package com.denimgroup.threadfix.framework.impl.php;

import com.denimgroup.threadfix.framework.TestConstants;
import org.junit.Test;

import javax.annotation.Nonnull;
import java.io.File;
import java.util.List;
import java.util.Map;

import static com.denimgroup.threadfix.CollectionUtils.list;
import static com.denimgroup.threadfix.CollectionUtils.listFrom;
import static com.denimgroup.threadfix.CollectionUtils.map;
import static org.junit.Assert.assertTrue;

public class PHPParameterParserTest {

    @Test
    public void testFindParameters(){
        String file = TestConstants.THREADFIX_SOURCE_ROOT + "threadfix-ham/target/test-classes/code.php/parameter-test.php";
        Map<Integer,List<String>> expected = map(18, list("ip"), 16, list("Submit"));

        Map<Integer,List<String>> actual = PHPParameterParser.parse(new File(file));

        compare(expected, actual);
    }

    private void compare(Map<Integer,List<String>> expected, Map<Integer,List<String>> actual){
        boolean equal = true;

        for(Integer key : expected.keySet()) {
            if(actual.containsKey(key)){
                equal = compare(expected.get(key),actual.get(key));
            }else {
                equal = false;
            }
        }

        for(Integer key: actual.keySet()) {
            if(expected.containsKey(key)){
                equal = compare(expected.get(key),actual.get(key));
            }else {
                equal = false;
            }
        }

        assertTrue("The parameter maps are not equal" + actual, equal);
    }

    private boolean compare(@Nonnull List<String> expected, @Nonnull List<String> actual){
        List<String> actualCopy = listFrom(actual);
        List<String> expectedCopy = listFrom(expected);

        expectedCopy.removeAll(actualCopy);
        actualCopy.removeAll(expected);

        return actualCopy.isEmpty() && expectedCopy.isEmpty();
    }


}

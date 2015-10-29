package com.denimgroup.threadfix.framework.impl.php;

import com.denimgroup.threadfix.framework.engine.CodePoint;
import com.denimgroup.threadfix.framework.engine.full.EndpointQuery;
import com.denimgroup.threadfix.framework.engine.parameter.ParameterParser;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.regex.Pattern;
import static com.denimgroup.threadfix.framework.util.RegexUtils.getRegexResult;

public class PHPDataFlowParser implements ParameterParser {

    Pattern getPattern = Pattern.compile("\\$_GET\\[\\s*'([^']+)'\\s*\\]"),
            postPattern = Pattern.compile("\\$_POST\\[\\s*'([^']+)'\\s*\\]"),
            requestPattern = Pattern.compile("\\$_REQUEST\\[\\s*'([^']+)'\\s*\\]");

    @Nullable
    @Override
    public String parse(@Nonnull EndpointQuery query) {
        String parameter = null;

        List<CodePoint> codePoints = query.getCodePoints();
        if(codePoints != null) {
            for(CodePoint codePoint : codePoints) {
                parameter = findMatch(codePoint);

                if(parameter != null) {
                    break;
                }
            }
        }

        return parameter;
    }

    private String findMatch(CodePoint codePoint) {
        String line = codePoint.getLineText();

        if(line == null) {
            return null;
        }

        String getResult = getRegexResult(line, getPattern);

        if(getResult != null){
            return getResult;
        }else {
            String postResult = getRegexResult(line, postPattern);
            if(postResult != null){
                return postResult;
            }
        }

        return getRegexResult(line, requestPattern);
    }

}

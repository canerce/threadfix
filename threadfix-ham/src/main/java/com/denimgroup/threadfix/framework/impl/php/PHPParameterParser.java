package com.denimgroup.threadfix.framework.impl.php;

import com.denimgroup.threadfix.framework.util.EventBasedTokenizer;
import com.denimgroup.threadfix.framework.util.EventBasedTokenizerRunner;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.denimgroup.threadfix.CollectionUtils.map;


public class PHPParameterParser implements EventBasedTokenizer {

    @Nonnull
    private State state = State.START;
    @Nonnull
    private PageState pageState = PageState.START;
    @Nonnull
    private Map<Integer,List<String>> lineNumberToParameterMap = map();

    private enum State {
        START, DOLLAR_SIGN, UNDERSCORE, POST, GET, OPEN_BRACKET
    }

    private enum PageState {
        START, OPEN_ANGLE_BRACKET, QUESTION_MARK, IN_PHP
    }

    @Nonnull
    public static Map<Integer,List<String>> parse(File file) {
        PHPParameterParser parser = new PHPParameterParser();
        EventBasedTokenizerRunner.run(file, false, parser);
        return parser.lineNumberToParameterMap;
    }

    @Override
    public boolean shouldContinue() {
        return true;
    }

    @Override
    public void processToken(int type, int lineNumber, String stringValue) {
        switch(pageState) {
            case START:
                if(type == OPEN_ANGLE_BRACKET){
                    pageState = PageState.OPEN_ANGLE_BRACKET;
                }
                break;
            case OPEN_ANGLE_BRACKET:
                if(type == QUESTION_MARK){
                    pageState = PageState.QUESTION_MARK;
                }
                break;
            case QUESTION_MARK:
                if(type == CLOSE_ANGLE_BACKET){
                    pageState = PageState.START;
                }else if(stringValue != null && stringValue.equals("php")){
                    pageState = PageState.IN_PHP;
                }
                break;
            case IN_PHP:
                if(type == QUESTION_MARK) {
                    pageState = PageState.QUESTION_MARK;
                }else{
                    processCodeToken(type, lineNumber, stringValue);
                }

        }
    }

    public void processCodeToken(int type, int lineNumber, @Nullable String stringValue) {
        switch(state) {
            case START:
                if(type == DOLLAR_SIGN) {
                    state = State.DOLLAR_SIGN;
                }
                break;
            case DOLLAR_SIGN:
                if(type == UNDERSCORE) {
                    state = State.UNDERSCORE;
                }
                break;
            case UNDERSCORE:
                if(stringValue != null){
                    if(stringValue.equals("GET") || stringValue.equals("REQUEST")){
                        state = State.GET;
                    }else if(stringValue.equals("POST")){
                        state = State.POST;
                    }else{
                        state = State.START;
                    }
                }else{
                    state = State.START;
                }
                break;
            case GET:
            case POST:
                if(type == OPEN_SQUARE_BRACKET)
                {
                    state = State.OPEN_BRACKET;
                }
                break;
            case OPEN_BRACKET:
                if(stringValue != null){
                    if(!lineNumberToParameterMap.containsKey(lineNumber)){
                        lineNumberToParameterMap.put(lineNumber, new ArrayList<String>());
                    }
                    lineNumberToParameterMap.get(lineNumber).add(stringValue);
                    state = State.START;
                }
                break;
        }

    }

}

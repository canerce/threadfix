package com.denimgroup.threadfix.framework.impl.php;


import com.denimgroup.threadfix.framework.util.EventBasedTokenizer;
import com.denimgroup.threadfix.framework.util.EventBasedTokenizerRunner;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;

public class PHPEndpointParser implements EventBasedTokenizer{

    @Nonnull
    private State state = State.START;
    @Nonnull
    private PageState pageState = PageState.START;

    private enum PageState {
        START, OPEN_ANGLE_BRACKET, QUESTION_MARK, IN_PHP
    }

    private enum State {
        START, IN_CLASS, IN_FUNCTION, IN_DEFINE
    }

    public boolean isEndpoint = false;
    private int unclosedCurlyCount = 0;

    public static boolean parse(File file){
        PHPEndpointParser parser = new PHPEndpointParser();
        EventBasedTokenizerRunner.run(file, true, parser);
        return parser.isEndpoint;
    }

    @Override
    public boolean shouldContinue() {
        return !isEndpoint;
    }

    @Override
    public void processToken(int type, int lineNumber, String stringValue) {
        switch(pageState) {
            case START:
                if(type == OPEN_ANGLE_BRACKET){
                    pageState = PageState.OPEN_ANGLE_BRACKET;
                }else {
                    pageState = PageState.START;
                    isEndpoint = true;
                }
                break;
            case OPEN_ANGLE_BRACKET:
                if(type == QUESTION_MARK){
                    pageState = PageState.QUESTION_MARK;
                }else {
                    pageState = PageState.START;
                    isEndpoint = true;
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
                    processCodeToken(type, stringValue);
                }
                break;
        }
    }

    private void processCodeToken(int type, @Nullable String stringValue) {
        switch(state) {
            case START:
                if(stringValue != null) {
                    if(stringValue.equals("function")){
                        state = State.IN_FUNCTION;
                    }else if(stringValue.equals("define")){
                        state = State.IN_DEFINE;
                    }else if(stringValue.equals("class")){
                        state = State.IN_CLASS;
                    }else{
                        isEndpoint = true;
                    }
                }
                break;
            case IN_CLASS:
            case IN_FUNCTION:
                if(type == OPEN_CURLY){
                    unclosedCurlyCount++;
                }else if(type == CLOSE_CURLY){
                    unclosedCurlyCount--;
                    if(unclosedCurlyCount == 0){
                        state = State.START;
                    }
                }
                break;
            case IN_DEFINE:
                if(type == SEMICOLON){
                    state = State.START;
                }
                break;
        }
    }
}

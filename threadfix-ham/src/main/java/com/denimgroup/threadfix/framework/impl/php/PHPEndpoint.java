package com.denimgroup.threadfix.framework.impl.php;

import com.denimgroup.threadfix.framework.engine.AbstractEndpoint;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.denimgroup.threadfix.CollectionUtils.list;
import static com.denimgroup.threadfix.CollectionUtils.set;

public class PHPEndpoint extends AbstractEndpoint{

    private Set<String> parameters = set(), methods;
    private String dynamicPath, staticPath;


    public PHPEndpoint(@Nonnull String staticPath,
                       @Nonnull String dynamicPath,
                       @Nonnull Set<String> methods,
                       @Nonnull Map<Integer,List<String>> parameterMap) {
        this.methods = methods;
        this.staticPath = staticPath;

        String path = dynamicPath;
        if(path != null && path.indexOf("/index.php") != -1){
            this.dynamicPath = path.replace("/index.php","/");
        }

        for (List<String> value : parameterMap.values()) {
            parameters.addAll(value);
        }
    }

    @Nonnull
    @Override
    protected List<String> getLintLine() { return list(); }

    @Nonnull
    @Override
    public Set<String> getParameters() { return parameters; }


    @Nonnull
    @Override
    public Set<String> getHttpMethods() { return methods; }

    @Nonnull
    @Override
    public String getUrlPath() { return dynamicPath; }

    @Nonnull
    @Override
    public String getFilePath() { return staticPath; }

    @Override
    public int getStartingLineNumber() { return -1; }

    @Override
    public int getLineNumberForParameter(String parameter) { return 0; }

    @Override
    public boolean matchesLineNumber(int lineNumber) { return true; }
}

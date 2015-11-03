package com.denimgroup.threadfix.framework.impl.php;

import org.apache.commons.io.filefilter.IOFileFilter;

import javax.annotation.Nonnull;
import java.io.File;

public class PHPFileFilter implements IOFileFilter {

    public static final PHPFileFilter INSTANCE = new PHPFileFilter();
    private PHPFileFilter(){}

    @Override
    public boolean accept(@Nonnull File file) { return  isPHPEndpoint(file.getName());}

    @Override
    public boolean accept(File dir, @Nonnull String name) { return isPHPEndpoint(name); }

    private boolean isPHPEndpoint(@Nonnull String filename) {
        return filename.endsWith(".php") && !filename.endsWith(".inc.php");
    }

}


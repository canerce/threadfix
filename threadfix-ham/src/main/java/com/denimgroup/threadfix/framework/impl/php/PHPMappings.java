////////////////////////////////////////////////////////////////////////
//
//     Copyright (c) 2009-2015 Denim Group, Ltd.
//
//     The contents of this file are subject to the Mozilla Public License
//     Version 2.0 (the "License"); you may not use this file except in
//     compliance with the License. You may obtain a copy of the License at
//     http://www.mozilla.org/MPL/
//
//     Software distributed under the License is distributed on an "AS IS"
//     basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
//     License for the specific language governing rights and limitations
//     under the License.
//
//     The Original Code is ThreadFix.
//
//     The Initial Developer of the Original Code is Denim Group, Ltd.
//     Portions created by Denim Group, Ltd. are Copyright (C)
//     Denim Group, Ltd. All Rights Reserved.
//
//     Contributor(s): Denim Group, Ltd.
//
////////////////////////////////////////////////////////////////////////
package com.denimgroup.threadfix.framework.impl.php;

import com.denimgroup.threadfix.data.interfaces.Endpoint;
import com.denimgroup.threadfix.framework.engine.full.EndpointGenerator;
import com.denimgroup.threadfix.framework.filefilter.FileExtensionFileFilter;
import com.denimgroup.threadfix.framework.util.EventBasedTokenizerRunner;
import com.denimgroup.threadfix.framework.util.FilePathUtils;
import com.denimgroup.threadfix.logging.SanitizedLogger;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.denimgroup.threadfix.CollectionUtils.list;
import static com.denimgroup.threadfix.CollectionUtils.set;

public class PHPMappings implements EndpointGenerator {

    private static final SanitizedLogger LOG = new SanitizedLogger("PHPMappings");

    private List<Endpoint> endpoints = list();
    @Nullable
    private final File projectRoot;

    public PHPMappings(@Nonnull File rootFile) {
        if(rootFile.exists()) {
            this.projectRoot = rootFile;

            Collection<File> phpFiles = FileUtils.listFiles(rootFile, PHPFileFilter.INSTANCE, TrueFileFilter.INSTANCE);

            for (File file : phpFiles) {
                parseFile(file);
            }

        } else {
            LOG.error("Root file didn't exist. Exiting.");
            projectRoot = null;
        }
    }

    private void parseFile(File file) {

        if (projectRoot != null) {

            PHPParameterParser parameterParser = new PHPParameterParser();
            PHPEndpointParser endpointParser = new PHPEndpointParser();
            EventBasedTokenizerRunner.run(file, true, parameterParser, endpointParser);

            if(endpointParser.isEndpoint) {
                String staticPath = FilePathUtils.getRelativePath(file, projectRoot);
                String dynamicPath = (staticPath == null) ? "" : staticPath;
                createEndpoint(staticPath, dynamicPath, parameterParser.lineNumberToParameterMap);
            }
        }

    }

    private void createEndpoint(String staticPath, String dynamicPath, Map<Integer,List<String>> parameters) {

        PHPEndpoint endpoint = new PHPEndpoint(
                "webroot" + staticPath,
                dynamicPath,
                set("GET", "POST"),
                parameters);

        endpoints.add(endpoint);
    }



    @Nonnull
    @Override
    public List<Endpoint> generateEndpoints() { return endpoints; }

    @Override
    public Iterator<Endpoint> iterator() { return endpoints.iterator(); }
}

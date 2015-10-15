#!/bin/bash
difflist="$(git diff --name-only --diff-filter=MDCRTUXB dev..dev-qa)"

# This declares a blacklist of modified files
declare -a blacklist=(".gitignore"
	"pom.xml"
	"threadfix-cli/src/test/java/com/denimgroup/threadfix/cli/HttpRestUtilsTests.java"
	"threadfix-cli/src/test/java/com/denimgroup/threadfix/cli/ThreadFixRestClientIT.java"
	"threadfix-entities/pom.xml"
	"threadfix-ide-plugin/eclipse/pom.xml"
	"threadfix-ide-plugin/pom.xml"
	"threadfix-importers/pom.xml"
	"threadfix-main/pom-selenium.xml"
	"threadfix-main/pom.xml"
	"threadfix-main/src/main/resources/jdbc.properties.mysql"
	"threadfix-main/src/test/java/com/denimgroup/threadfix/service/defects/BugzillaTests.java"
	"threadfix-main/src/test/java/com/denimgroup/threadfix/service/defects/JiraTests.java"
	"threadfix-main/src/test/java/com/denimgroup/threadfix/service/defects/TFSTests.java"
	"threadfix-main/src/test/java/com/denimgroup/threadfix/service/defects/util/DefectUtils.java"
	"threadfix-main/src/test/java/com/denimgroup/threadfix/service/defects/util/TestConstants.java"
	"threadfix-main/threadfix.log"
	"threadfix-service-interfaces/pom.xml"
	"threadfix-upgrade/mysql-upgrade/src/resources/fabfile.py")

for url in "${blacklist[@]}"
do
	difflist=$(echo "$difflist" | grep -v $url)
done

# Formatting
printf "\n============================================================"
printf "\n  Differing Files"
printf "\n============================================================\n"

# Echo Final List
echo "$difflist"

# Formatting
printf "============================================================"
printf "\n  End List"
printf "\n============================================================\n\n"

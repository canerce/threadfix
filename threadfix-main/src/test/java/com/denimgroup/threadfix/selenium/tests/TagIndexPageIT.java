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
package com.denimgroup.threadfix.selenium.tests;

import com.denimgroup.threadfix.CommunityTests;
import com.denimgroup.threadfix.data.enums.TagType;
import com.denimgroup.threadfix.selenium.pages.*;
import com.denimgroup.threadfix.selenium.utils.DatabaseUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.openqa.selenium.By;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@Category(CommunityTests.class)
public class TagIndexPageIT extends BaseDataTest{

    @Test
    public void testCreateTag() {
        String tagName = getName();

        TagIndexPage tagIndexPage = loginPage.defaultLogin()
                .clickTagsLink()
                .createNewTag(tagName);

        assertTrue("Tag was not created properly", tagIndexPage.isAppTagNameLinkPresent(tagName));
    }

    @Test
    public void testDeleteTag() {
        String tagName = getName();
        createTag(tagName);

        TagIndexPage tagIndexPage = loginPage.defaultLogin()
                .clickTagsLink()
                .deleteTag(tagName);

        assertTrue("Tag was not deleted properly", !tagIndexPage.isAppTagNameLinkPresent(tagName));
    }

    @Test
    public void testEditTag() {
        String tagName = getName();
        createTag(tagName);
        String newName = getName();

        TagIndexPage tagIndexPage = loginPage.defaultLogin()
                .clickTagsLink()
                .editTagName(tagName, newName);

        assertTrue("Old tag name was not deleted properly", !tagIndexPage.isAppTagNameLinkPresent(tagName));
        assertTrue("New tag name was no added properly ", tagIndexPage.isAppTagNameLinkPresent(newName));
    }

    @Test
    public void testTagNameNavigation() {
        String tagName = getName();
        createTag(tagName);

        loginPage.defaultLogin()
                .clickTagsLink()
                .clickTagName(tagName);

        assertTrue("Tag name did not navigate correctly",
                driver.findElement(By.linkText("Back to Tags Page")).isEnabled());
    }

    @Test
    public void testCreateVulnerabilityTag() {
        String tagName = getName();
        TagIndexPage tagIndexPage = loginPage.defaultLogin()
                .clickTagsLink()
                .createNewVulnerabilityTag(tagName);
        assertTrue("Tag was not created properly", tagIndexPage.isVulnerabilityTagNameLinkPresent(tagName));
    }

    @Test
    public void testDeleteVulnerabilityTag() {
        String tagName = getName();
        createTag(tagName, TagType.VULNERABILITY.getDisplayName());

        TagIndexPage tagIndexPage = loginPage.defaultLogin()
                .clickTagsLink()
                .deleteVulnerabilityTag(tagName);

        assertTrue("Vulnerability Tag was not deleted properly", !tagIndexPage.isVulnerabilityTagNameLinkPresent(tagName));
    }

    @Test
    public void testEditVulnerabilityTag() {
        String tagName = getName();
        createTag(tagName, TagType.VULNERABILITY.getDisplayName());
        String newName = getName();

        TagIndexPage tagIndexPage = loginPage.defaultLogin()
                .clickTagsLink()
                .editVulnerabilityTagName(tagName, newName);

        assertTrue("Old vulnerability tag name was not deleted properly", !tagIndexPage.isVulnerabilityTagNameLinkPresent(tagName));
        assertTrue("New vulnerability tag name was not added properly ", tagIndexPage.isVulnerabilityTagNameLinkPresent(newName));
    }

    @Test
    public void testVulnerabilityTagNameNavigation() {
        String tagName = getName();
        createTag(tagName, TagType.VULNERABILITY.getDisplayName());

        loginPage.defaultLogin()
                .clickTagsLink()
                .clickVulnerabilityTagName(tagName);

        assertTrue("Vulnerability Tag name did not navigate correctly",
                driver.findElement(By.linkText("Back to Tags Page")).isEnabled());
    }
    @Test
    public void testCreateCommentTag() {
        String tagName = getName();

        TagIndexPage tagIndexPage = loginPage.defaultLogin()
                .clickTagsLink()
                .createNewCommentTag(tagName);

        assertTrue("Comment Tag was not created properly", tagIndexPage.isCommentTagNameLinkPresent(tagName));
    }

    @Test
    public void testDeleteCommentTag() {
        String tagName = getName();
        createTag(tagName, TagType.COMMENT.getDisplayName());

        TagIndexPage tagIndexPage = loginPage.defaultLogin()
                .clickTagsLink()
                .deleteCommentTag(tagName);

        assertTrue("Comment Tag was not deleted properly", !tagIndexPage.isCommentTagNameLinkPresent(tagName));
    }

    @Test
    public void testEditCommentTag() {
        String tagName = getName();
        createTag(tagName, TagType.COMMENT.getDisplayName());
        String newName = getName();

        TagIndexPage tagIndexPage = loginPage.defaultLogin()
                .clickTagsLink()
                .editCommentTagName(tagName, newName);

        assertTrue("Old comment tag name was not deleted properly", !tagIndexPage.isCommentTagNameLinkPresent(tagName));
        assertTrue("New comment tag name was not added properly ", tagIndexPage.isCommentTagNameLinkPresent(newName));
    }

    @Test
    public void testCommentTagNameNavigation() {
        String tagName = getName();
        createTag(tagName, TagType.COMMENT.getDisplayName());

        loginPage.defaultLogin()
                .clickTagsLink()
                .clickCommentTagName(tagName);

        assertTrue("Comment Tag name did not navigate correctly",
                driver.findElement(By.linkText("Back to Tags Page")).isEnabled());
    }
}

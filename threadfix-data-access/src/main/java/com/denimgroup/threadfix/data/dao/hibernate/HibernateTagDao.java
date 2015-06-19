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
package com.denimgroup.threadfix.data.dao.hibernate;

import com.denimgroup.threadfix.data.dao.AbstractNamedObjectDao;
import com.denimgroup.threadfix.data.dao.TagDao;
import com.denimgroup.threadfix.data.entities.Tag;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Hibernate Tag DAO implementation. Most basic methods are implemented in the
 * AbstractGenericDao
 * 
 * @author stran
 * @see com.denimgroup.threadfix.data.dao.AbstractNamedObjectDao
 */
@Repository
public class HibernateTagDao
        extends AbstractNamedObjectDao<Tag>
        implements TagDao {

    @Override
    protected Order getOrder() {
        return Order.asc("name");
    }

    @Autowired
    public HibernateTagDao(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    @Override
    protected Class<Tag> getClassReference() {
        return Tag.class;
    }

    @Override
    public Tag retrieveByName(String name) {
        return (Tag) getSession()
                .createCriteria(getClassReference())
                .add(Restrictions.eq("active", true))
                .add(Restrictions.eq("name", name))
                .uniqueResult();
    }


    @Override
    public Tag retrieveAppTagByName(String name) {
        return (Tag) getSession()
                .createCriteria(getClassReference())
                .add(Restrictions.eq("active", true))
                .add(Restrictions.eq("name", name))
                .add(Restrictions.or(Restrictions.isNull("tagForComment"), Restrictions.eq("tagForComment", false)))
                .uniqueResult();
    }

    @Override
    public Tag retrieveCommentTagByName(String name) {
        return (Tag) getSession()
                .createCriteria(getClassReference())
                .add(Restrictions.eq("active", true))
                .add(Restrictions.eq("name", name))
                .add(Restrictions.eq("tagForComment", true))
                .uniqueResult();
    }

    @Override
    public List<Tag> retrieveAllCommentTags() {
        return getSession()
                .createCriteria(getClassReference())
                .add(Restrictions.eq("active", true))
                .add(Restrictions.eq("tagForComment", true))
                .addOrder(getOrder())
                .list();

    }

    @Override
    public List<Tag> retrieveTagsByName(String name) {
        return getSession()
                .createCriteria(getClassReference())
                .add(Restrictions.eq("active", true))
                .add(Restrictions.eq("name", name))
                .addOrder(getOrder())
                .list();
    }

    @Override
    public List<Tag> retrieveAllApplicationTags() {
        return getSession()
                .createCriteria(getClassReference())
                .add(Restrictions.eq("active", true))
                .add(Restrictions.or(Restrictions.isNull("tagForComment"), Restrictions.eq("tagForComment", false)))
                .addOrder(getOrder())
                .list();
    }
}

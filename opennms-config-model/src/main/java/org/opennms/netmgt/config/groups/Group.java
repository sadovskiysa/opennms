/*******************************************************************************
 * This file is part of OpenNMS(R).
 * 
 * Copyright (C) 2017-2017 The OpenNMS Group, Inc.
 * OpenNMS(R) is Copyright (C) 1999-2017 The OpenNMS Group, Inc.
 * 
 * OpenNMS(R) is a registered trademark of The OpenNMS Group, Inc.
 * 
 * OpenNMS(R) is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * 
 * OpenNMS(R) is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with OpenNMS(R).  If not, see:
 *     http://www.gnu.org/licenses/
 * 
 * For more information contact:
 *     OpenNMS(R) Licensing <license@opennms.org>
 *     http://www.opennms.org/
 *     http://www.opennms.com/
 *******************************************************************************/

package org.opennms.netmgt.config.groups;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.opennms.core.xml.ValidateUsing;

@XmlRootElement(name = "group")
@XmlAccessorType(XmlAccessType.FIELD)
@ValidateUsing("groups.xsd")
public class Group implements Serializable {
    private static final long serialVersionUID = 1L;

    @XmlElement(name = "name", required = true)
    private String m_name;

    @XmlElement(name = "default-map")
    private String m_defaultMap;

    @XmlElement(name = "comments")
    private String m_comments;

    @XmlElement(name = "user")
    private List<String> m_users = new ArrayList<>();

    @XmlElement(name = "duty-schedule")
    private List<String> m_dutySchedules = new ArrayList<>();

    public Group() {
    }

    public String getName() {
        return m_name;
    }

    public void setName(final String name) {
        m_name = name;
    }

    public String getDefaultMap() {
        return m_defaultMap;
    }

    public void setDefaultMap(final String defaultMap) {
        m_defaultMap = defaultMap;
    }

    public String getComments() {
        return m_comments;
    }

    public void setComments(final String comments) {
        m_comments = comments;
    }

    public List<String> getUsers() {
        return m_users;
    }

    public void setUsers(final List<String> users) {
        m_users.clear();
        m_users.addAll(users);
    }

    public void addUser(final String user) {
        m_users.add(user);
    }

    public boolean removeUser(final String user) {
        return m_users.remove(user);
    }

    public void clearUsers() {
        m_users.clear();
    }

    public List<String> getDutySchedules() {
        return m_dutySchedules;
    }

    public void setDutySchedules(final List<String> dutySchedules) {
        m_dutySchedules.clear();
        m_dutySchedules.addAll(dutySchedules);
    }

    public void addDutySchedule(final String dutySchedule) {
        m_dutySchedules.add(dutySchedule);
    }

    public void clearDutySchedules() {
        m_dutySchedules.clear();
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            m_name, 
            m_defaultMap, 
            m_comments, 
            m_users, 
            m_dutySchedules);
    }

    @Override
    public boolean equals(final Object obj) {
        if ( this == obj ) {
            return true;
        }
        
        if (obj instanceof Group) {
            final Group temp = (Group)obj;
            return Objects.equals(temp.m_name, m_name)
                && Objects.equals(temp.m_defaultMap, m_defaultMap)
                && Objects.equals(temp.m_comments, m_comments)
                && Objects.equals(temp.m_users, m_users)
                && Objects.equals(temp.m_dutySchedules, m_dutySchedules);
        }
        return false;
    }

    @Override
    public String toString() {
        return "Group [name=" + m_name + ", defaultMap=" + m_defaultMap
                + ", comments=" + m_comments + ", users=" + m_users
                + ", dutySchedules=" + m_dutySchedules + "]";
    }

}

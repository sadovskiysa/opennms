/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2008-2011 The OpenNMS Group, Inc.
 * OpenNMS(R) is Copyright (C) 1999-2011 The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is a registered trademark of The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published
 * by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * OpenNMS(R) is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenNMS(R).  If not, see:
 *      http://www.gnu.org/licenses/
 *
 * For more information contact:
 *     OpenNMS(R) Licensing <license@opennms.org>
 *     http://www.opennms.org/
 *     http://www.opennms.com/
 *******************************************************************************/

package org.opennms.web.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.opennms.netmgt.dao.CategoryDao;
import org.opennms.netmgt.dao.NodeDao;
import org.opennms.netmgt.model.OnmsCategory;
import org.opennms.netmgt.model.OnmsCategoryCollection;
import org.opennms.netmgt.model.OnmsNode;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.sun.jersey.spi.resource.PerRequest;

@Component
/**
 * <p>OnmsCategoryResource class.</p>
 *
 * @author ranger
 * @version $Id: $
 * @since 1.8.1
 */
@PerRequest
@Scope("prototype")
@Path("categories")
@Transactional
public class OnmsCategoryResource extends OnmsRestService {
    @Autowired
    private NodeDao m_nodeDao;
    
    @Autowired
    private CategoryDao m_categoryDao;
    
    /**
     * <p>getCategories</p>
     *
     * @param nodeCriteria a {@link java.lang.String} object.
     * @return a {@link org.opennms.netmgt.model.OnmsCategoryCollection} object.
     */
    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public OnmsCategoryCollection getCategories(@PathParam("nodeCriteria") String nodeCriteria) {
        OnmsNode node = m_nodeDao.get(nodeCriteria);
        if (node == null) {
            throwException(Status.BAD_REQUEST, "getCategories: Can't find node " + nodeCriteria);
        }
        return new OnmsCategoryCollection(node.getCategories());
    }

    /**
     * <p>getCategory</p>
     *
     * @param nodeCriteria a {@link java.lang.String} object.
     * @param categoryName a {@link java.lang.String} object.
     * @return a {@link org.opennms.netmgt.model.OnmsCategory} object.
     */
    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Path("{categoryName}")
    public OnmsCategory getCategory(@PathParam("nodeCriteria") String nodeCriteria, @PathParam("categoryName") String categoryName) {
        OnmsNode node = m_nodeDao.get(nodeCriteria);
        if (node == null) {
            throwException(Status.BAD_REQUEST, "getCategory: Can't find node " + nodeCriteria);
        }
        return getCategory(node, categoryName);
    }

    /**
     * <p>addCategory</p>
     *
     * @param nodeCriteria a {@link java.lang.String} object.
     * @param category a {@link org.opennms.netmgt.model.OnmsCategory} object.
     * @return a {@link javax.ws.rs.core.Response} object.
     */
    @POST
    @Consumes(MediaType.APPLICATION_XML)
    public Response addCategory(@PathParam("nodeCriteria") String nodeCriteria, OnmsCategory category) {
        OnmsNode node = m_nodeDao.get(nodeCriteria);
        if (node == null) {
            throwException(Status.BAD_REQUEST, "addCategory: Can't find node " + nodeCriteria);
        }
        OnmsCategory found = m_categoryDao.findByName(category.getName());
        if (found == null) {
            log().debug("addCategory: Saving category " + category);
            m_categoryDao.save(category);
        } else {
            category = found;
        }
        log().debug("addCategory: Adding category " + category + " to node " + nodeCriteria);
        node.addCategory(category);
        m_nodeDao.save(node);
        return Response.ok().build();
    }
    
    /**
     * <p>updateCategory</p>
     *
     * @param nodeCriteria a {@link java.lang.String} object.
     * @param categoryName a {@link java.lang.String} object.
     * @param params a {@link org.opennms.web.rest.MultivaluedMapImpl} object.
     * @return a {@link javax.ws.rs.core.Response} object.
     */
    @PUT
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Path("{categoryName}")
    public Response updateCategory(@PathParam("nodeCriteria") String nodeCriteria, @PathParam("categoryName") String categoryName, MultivaluedMapImpl params) {
        OnmsNode node = m_nodeDao.get(nodeCriteria);
        if (node == null) {
            throwException(Status.BAD_REQUEST, "updateCategory: Can't find node " + nodeCriteria);
        }
        OnmsCategory category = getCategory(node, categoryName);
        if (category == null) {
            throwException(Status.BAD_REQUEST, "updateCategory: Category " + categoryName + " not found on node " + nodeCriteria);
        }
        log().debug("updateCategory: updating category " + category);
        BeanWrapper wrapper = PropertyAccessorFactory.forBeanPropertyAccess(category);
        for(String key : params.keySet()) {
            if (wrapper.isWritableProperty(key)) {
                String stringValue = params.getFirst(key);
                Object value = wrapper.convertIfNecessary(stringValue, (Class<?>)wrapper.getPropertyType(key));
                wrapper.setPropertyValue(key, value);
            }
        }
        log().debug("updateCategory: category " + category + " updated");
        m_nodeDao.saveOrUpdate(node);
        return Response.ok().build();
    }
    
    /**
     * <p>deleteCaegory</p>
     *
     * @param nodeCriteria a {@link java.lang.String} object.
     * @param categoryName a {@link java.lang.String} object.
     * @return a {@link javax.ws.rs.core.Response} object.
     */
    @DELETE
    @Path("{categoryName}")
    public Response deleteCaegory(@PathParam("nodeCriteria") String nodeCriteria, @PathParam("categoryName") String categoryName) {
        OnmsNode node = m_nodeDao.get(nodeCriteria);
        if (node == null) {
            throwException(Status.BAD_REQUEST, "deleteCaegory: Can't find node " + nodeCriteria);
        }
        OnmsCategory category = getCategory(node, categoryName);
        if (category == null) {
            throwException(Status.BAD_REQUEST, "deleteCaegory: Category " + categoryName + " not found on node " + nodeCriteria);
        }
        log().debug("deleteCaegory: deleting category " + categoryName + " from node " + nodeCriteria);
        node.getCategories().remove(category);
        m_nodeDao.saveOrUpdate(node);
        return Response.ok().build();
    }
    
    private OnmsCategory getCategory(OnmsNode node, String categoryName) {
        for (OnmsCategory category : node.getCategories()) {
            if (category.getName().equals(categoryName)) {
                return category;
            }
        }
        return null;
    }
    
}

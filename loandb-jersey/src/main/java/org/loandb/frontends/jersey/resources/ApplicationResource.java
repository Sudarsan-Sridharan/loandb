package org.loandb.frontends.jersey.resources;

import com.sun.jersey.api.NotFoundException;
import com.saliman.entitypruner.EntityPrunerHibernateJpa;
import org.loandb.persistence.model.Application;
import org.loandb.persistence.service.ApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

/**
 * This code is licensed under Apache License Version 2.0.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * @author <a href="mailto:aruld@acm.org">Arulazi Dhesiaseelan</a>
 * @since May 10, 2009
 */
@Path("/query/")
@Component
@Scope
public class ApplicationResource {

    @Autowired
    protected ApplicationService applicationService;

    @GET
    @Produces(MediaType.APPLICATION_XML)
    public Application getApplication(@QueryParam("appid") String appid) {
        Application application = applicationService.getApp(Long.valueOf(appid));
        if (application == null) {
            throw new NotFoundException("Application not found for id : " + appid);
        }
        EntityPrunerHibernateJpa pruner = new EntityPrunerHibernateJpa();
        pruner.prune(application, 2);
        return application;
    }

}

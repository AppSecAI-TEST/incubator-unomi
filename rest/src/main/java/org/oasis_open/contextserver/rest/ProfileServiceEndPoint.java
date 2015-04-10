package org.oasis_open.contextserver.rest;

/*
 * #%L
 * context-server-rest
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2014 - 2015 Jahia Solutions
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import org.apache.cxf.rs.security.cors.CrossOriginResourceSharing;
import org.oasis_open.contextserver.api.*;
import org.oasis_open.contextserver.api.conditions.Condition;
import org.oasis_open.contextserver.api.services.EventService;
import org.oasis_open.contextserver.api.services.ProfileService;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.*;

@WebService
@Produces(MediaType.APPLICATION_JSON)
@CrossOriginResourceSharing(
        allowAllOrigins = true,
        allowCredentials = true
)
public class ProfileServiceEndPoint {

    private ProfileService profileService;

    private EventService eventService;

    private LocalizationHelper localizationHelper;

    public ProfileServiceEndPoint() {
        System.out.println("Initializing profile service endpoint...");
    }

    @WebMethod(exclude = true)
    public void setProfileService(ProfileService profileService) {
        this.profileService = profileService;
    }

    @WebMethod(exclude = true)
    public void setEventService(EventService eventService) {
        this.eventService = eventService;
    }

    @WebMethod(exclude = true)
    public void setLocalizationHelper(LocalizationHelper localizationHelper) {
        this.localizationHelper = localizationHelper;
    }

    @GET
    @Path("/count")
    public long getAllProfilesCount() {
        return profileService.getAllProfilesCount();
    }

    @GET
    @Path("/search")
    public PartialList<Profile> getProfiles(@QueryParam("q") String query,
                                            @QueryParam("offset") @DefaultValue("0") int offset,
                                            @QueryParam("size") @DefaultValue("50") int size,
                                            @QueryParam("sort") String sortBy) {
        return profileService.getProfiles(query, offset, size, sortBy);
    }

    @GET
    @Path("/{profileId}")
    public Profile load(@PathParam("profileId") String profileId) {
        return profileService.load(profileId);
    }

    @POST
    @Path("/{profileId}")
    public void save(Profile profile) {
        Event profileUpdated = new Event("profileUpdated", null, profile, null, null, profile, new Date());
        profileUpdated.setPersistent(false);
        eventService.send(profileUpdated);
        profileService.save(profile);
    }

    @DELETE
    @Path("/{profileId}")
    public void delete(@PathParam("profileId") String profileId, @QueryParam("persona") @DefaultValue("false") boolean persona) {
        profileService.delete(profileId, false);
    }

    @GET
    @Path("/{profileId}/sessions")
    public PartialList<Session> getProfileSessions(@PathParam("profileId") String profileId,
                                                   @QueryParam("offset") @DefaultValue("0") int offset,
                                                   @QueryParam("size") @DefaultValue("50") int size,
                                                   @QueryParam("sort") String sortBy) {
        return profileService.getProfileSessions(profileId, offset, size, sortBy);
    }

    @GET
    @Path("/properties/mappings/{fromPropertyTypeId}")
    public String getPropertyTypeMapping(@PathParam("fromPropertyTypeId") String fromPropertyTypeId) {
        return profileService.getPropertyTypeMapping(fromPropertyTypeId);
    }

    @GET
    @Path("/personas")
    public PartialList<Persona> getPersonas(@QueryParam("offset") @DefaultValue("0") int offset,
                                            @QueryParam("size") @DefaultValue("50") int size,
                                            @QueryParam("sort") String sortBy) {
        return profileService.getPersonas(offset, size, sortBy);
    }

    @GET
    @Path("/personas/{personaId}")
    public Persona loadPersona(@PathParam("personaId") String personaId) {
        return profileService.loadPersona(personaId);
    }

    @GET
    @Path("/personasWithSessions/{personaId}")
    public PersonaWithSessions loadPersonaWithSessions(@PathParam("personaId") String personaId) {
        return profileService.loadPersonaWithSessions(personaId);
    }

    @POST
    @Path("/personas/{personaId}")
    public void savePersona(Persona persona) {
        profileService.save(persona);
    }

    @DELETE
    @Path("/personas/{personaId}")
    public void deletePersona(Persona persona) {
        profileService.delete(persona.getItemId(), true);
    }

    @PUT
    @Path("/personas/{personaId}")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public void createPersona(@PathParam("personaId") String personaId) {
        profileService.createPersona(personaId);
    }

    @GET
    @Path("/personas/{personaId}/sessions")
    public PartialList<Session> getPersonaSessions(@PathParam("personaId") String personaId,
                                                   @QueryParam("offset") @DefaultValue("0") int offset,
                                                   @QueryParam("size") @DefaultValue("50") int size,
                                                   @QueryParam("sort") String sortBy) {
        return profileService.getPersonaSessions(personaId, offset, size, sortBy);
    }

    @GET
    @Path("/sessions/{sessionId}")
    public Session loadSession(@PathParam("sessionId") String sessionId, @QueryParam("dateHint") Date dateHint) {
        return profileService.loadSession(sessionId, dateHint);
    }

    @POST
    @Path("/sessions/{sessionId}")
    public boolean saveSession(Session session) {
        return profileService.saveSession(session);
    }

    @WebMethod(exclude = true)
    public PartialList<Session> findProfileSessions(String profileId) {
        return null;
    }

    @WebMethod(exclude = true)
    public boolean matchCondition(Condition condition, Profile profile, Session session) {
        return profileService.matchCondition(condition, profile, session);
    }

    @GET
    @Path("/existingProperties")
    public Collection<RESTPropertyType> getExistingProperties(@QueryParam("itemType") @DefaultValue("profile") String itemType, @HeaderParam("Accept-Language") String language) {
        Set<PropertyType> properties = profileService.getExistingProfileProperties(itemType);
        return localizationHelper.generatePropertyTypes(properties, language);
    }

}

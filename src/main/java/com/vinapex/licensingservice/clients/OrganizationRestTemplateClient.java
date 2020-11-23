package com.vinapex.licensingservice.clients;

import com.vinapex.licensingservice.model.Organization;
import com.vinapex.licensingservice.repository.OrganizationRedisRepository;
import com.vinapex.licensingservice.utils.UserContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.stereotype.Component;

@Component
public class OrganizationRestTemplateClient {
    /*@Autowired
    OAuth2RestTemplate oAuth2RestTemplate;

    private static final Logger logger = LoggerFactory.getLogger(OrganizationRestTemplateClient.class);

    public Organization getOrganization(String organizationId){
        logger.info("In Licensing Service .getOrganization: {}", UserContext.get);
        ResponseEntity<Organization> restExchange =
                oAuth2RestTemplate.exchange(
                        "http://organizationservice/v1/organizations/{organizationId}",
                        HttpMethod.GET,
                        null, Organization.class, organizationId);

        return restExchange.getBody();
    }*/

    @Autowired
    OAuth2RestTemplate restTemplate;

    @Autowired
    OrganizationRedisRepository organizationRedisRepository;

    private static final Logger logger = LoggerFactory.getLogger(OrganizationRestTemplateClient.class);


    private Organization checkRedisCache(String organizationId) {
        try {
            return organizationRedisRepository.findOrganization(organizationId);
        } catch (Exception exception) {
            logger.error("Error encountered while trying to retrieve organization {} check Redis Cache."
                    + " Exception {} ", organizationId, exception);
            return  null;
        }
    }

    private void cacheOrganizationObject(Organization org) {
        try {
            organizationRedisRepository.saveOrganization(org);
        } catch (Exception e) {
            logger.error("Unable to cache organization {} in Redis. Exception {}", org.getId(), e);
        }
    }

    public Organization getOrganization(String organizationId) {
        logger.debug("In Licensing Service.getOrganization: {}", UserContext.getCorrelationId());

        Organization org = checkRedisCache(organizationId);

        if(org != null) {
            logger.debug("Returning value from cache : {}", org);
            return org;
        }

        logger.debug("Unable to locate the Org in the cache {}", organizationId);

        ResponseEntity<Organization> restExchange =
                restTemplate.exchange(
                        "http://localhost:5555/api/organization/v1/organizations/{organizationId}",
                        HttpMethod.GET,
                        null, Organization.class, organizationId);

        org = restExchange.getBody();

        if(org != null) {
            cacheOrganizationObject(org);
        }

        return org;
    }
}

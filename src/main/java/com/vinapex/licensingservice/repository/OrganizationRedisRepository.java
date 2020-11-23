package com.vinapex.licensingservice.repository;

import com.vinapex.licensingservice.model.Organization;

public interface OrganizationRedisRepository {
    void saveOrganization(Organization organization);
    void updateOrganization(Organization organization);
    void deleteOrganization(String organizationId);
    Organization findOrganization(String organizationId);
}

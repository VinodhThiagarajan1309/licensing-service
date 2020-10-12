package com.vinapex.licensingservice.services;

import com.vinapex.licensingservice.config.ServiceConfig;
import com.vinapex.licensingservice.model.License;
import com.vinapex.licensingservice.repository.LicenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class LicenseService {

    @Autowired
    LicenseRepository licenseRepository;

    @Autowired
    ServiceConfig config;

    public License getLicense(String organizationId,String licenseId) {
        License license =  licenseRepository.findByOrganizationIdAndLicenseId(organizationId,licenseId);
        license.setComment(config.getExampleProperty());
        return license;
    }

    public List<License> getLicensesByOrg(String organizationId) {
        return licenseRepository.findByOrganizationId(organizationId);
    }

    public void saveLicense(License license) {
        license.setLicenseId(UUID.randomUUID().toString());
        licenseRepository.save(license);
    }

    public void updateLicense(License license){
        licenseRepository.save(license);
    }

    public void deleteLicense(License license){
        licenseRepository.delete(license);
    }
}

package com.vinapex.licensingservice.services;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import com.vinapex.licensingservice.clients.OrganizationDiscoveryClient;
import com.vinapex.licensingservice.clients.OrganizationFeignClient;
import com.vinapex.licensingservice.clients.OrganizationRestTemplateClient;
import com.vinapex.licensingservice.config.ServiceConfig;
import com.vinapex.licensingservice.model.License;
import com.vinapex.licensingservice.model.Organization;
import com.vinapex.licensingservice.repository.LicenseRepository;
import com.vinapex.licensingservice.utils.UserContextHolder;
import org.apache.tomcat.jni.Time;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Service
public class LicenseService {

    private static final Logger logger = LoggerFactory.getLogger(LicenseService.class);

    @Autowired
    LicenseRepository licenseRepository;

    @Autowired
    OrganizationDiscoveryClient organizationDiscoveryClient;

    @Autowired
    OrganizationFeignClient organizationFeignClient;

    @Autowired
    OrganizationRestTemplateClient organizationRestClient;

    @Autowired
    ServiceConfig config;

    public License getLicense(String organizationId,String licenseId) {
        License license =  licenseRepository.findByOrganizationIdAndLicenseId(organizationId,licenseId);
        license.setComment(config.getExampleProperty());
        return license;
    }

    private void randomlyRunLong() {
        Random rand = new Random();
        int randomNum = rand.nextInt((3 - 1) + 1) + 1;

        if(randomNum == 3) {
            sleep();
        }
    }

    private void sleep() {
        try {
            Thread.sleep(11000);
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }
    }
    @HystrixCommand(fallbackMethod = "buildFallBackLicenseList",
    threadPoolKey = "licenseByOrgThreadPool",
    threadPoolProperties = {
            @HystrixProperty(name="coreSize",value = "30"),
            @HystrixProperty(name="maxQueueSize",value = "10")
    },
    commandProperties = {
            @HystrixProperty(
                    name = "circuitBreaker.requestVolumeThreshold",
                    value = "10"
            ),
            @HystrixProperty(
                    name = "circuitBreaker.errorThresholdPercentage",
                    value = "75"
            ),
            @HystrixProperty(
                    name = "circuitBreaker.sleepWindowInMilliseconds",
                    value = "7000"
            ),
            @HystrixProperty(
                    name = "metrics.rollingStats.timeInMilliseconds",
                    value = "15000"
            ),
            @HystrixProperty(
                    name = "metrics.rollingStats.numBuckets",
                    value = "5"
            )
    })
    public List<License> getLicensesByOrg(String organizationId) {
        logger.info("LicenseService.getLicensesByOrg  Correlation id: {}",
                UserContextHolder.getContext().getCorrelationId());
        randomlyRunLong();
        return licenseRepository.findByOrganizationId(organizationId);
    }

    private List<License> buildFallBackLicenseList(String organizationId) {
        List<License> fallbackList = new ArrayList<>();
        License license = new License()
                .withId("0000000-00-00000")
                .withOrganizationId(organizationId)
                .withProductName("Sorry no product information available.");
        fallbackList.add(license);
        return fallbackList;
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

    public License getLicense(String organizationId,String licenseId, String clientType) {
        License license = licenseRepository.findByOrganizationIdAndLicenseId(organizationId, licenseId);

        Organization org = retrieveOrgInfo(organizationId, clientType);

        return license
                .withOrganizationName( org.getName())
                .withContactName( org.getContactName())
                .withContactEmail( org.getContactEmail() )
                .withContactPhone( org.getContactPhone() )
                .withComment(config.getExampleProperty());
    }

    private Organization retrieveOrgInfo(String organizationId, String clientType){
        Organization organization = null;

        switch (clientType) {
            case "feign":
                System.out.println("I am using the feign client");
                organization = organizationFeignClient.getOrganization(organizationId);
                break;
            case "rest":
                System.out.println("I am using the rest client");
                organization = organizationRestClient.getOrganization(organizationId);
                break;
            case "discovery":
                System.out.println("I am using the discovery client");
                organization = organizationDiscoveryClient.getOrganization(organizationId);
                break;
            default:
                organization = organizationRestClient.getOrganization(organizationId);
        }

        return organization;
    }
}

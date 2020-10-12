package com.vinapex.licensingservice.controllers;


import com.vinapex.licensingservice.model.License;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/v1/organizations/{organizationId}/licenses")
public class LicenseController {

    @RequestMapping(value = "/{licenseId}" ,method = RequestMethod.GET)
    public License getLicenses(
            @PathVariable("organizationId") String organizationId,
            @PathVariable("licenseId") String licenseId
    ) {
        return License.builder().licenseId(licenseId)
                .productName("Teleco")
                .licenseType("Seat")
                .organizationId(organizationId).build();
    }
}

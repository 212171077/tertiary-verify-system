package com.tertiary.verify.swagger;

import java.util.ArrayList;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.VendorExtension;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SpringFoxConfig {

  /* ----------------URLs------------------
   *  http://localhost:port/swagger-ui.html
   *  http://localhost:port/v2/api-docs
   * */

  @Bean
  public Docket api() {
    return new Docket(DocumentationType.SWAGGER_2)
        .apiInfo(buildAppInfo())
        .select()
        .apis(RequestHandlerSelectors.any())
        .paths(PathSelectors.any())
        .build();
  }

  private ApiInfo buildAppInfo() {
    List<VendorExtension> vendorExtensionList = new ArrayList<>();
    Contact contact = new Contact("Christoph Sibiya", "N/A", "christophsibiya1993gmail.com");
    ApiInfo apiInfo =
        new ApiInfo(
            "Tertiary verify service",
            "Tertiary verify rest web service",
            "1.0",
            "termsOfServiceUrl",
            contact,
            "license",
            "licenseUrl",
            vendorExtensionList);
    return apiInfo;
  }
}

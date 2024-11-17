package com.unisys.udb.user.config;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieModule;
import org.kie.api.runtime.KieContainer;
import org.kie.internal.io.ResourceFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DroolsApplicationConfig {

    private static final KieServices KIE_SERVICES = KieServices.Factory.get();
    private static final String RULES_CUSTOMER_RULES_DRL = "rules/countryValidation.drl";

    @Bean
    public KieContainer kieContainer() {
        KieFileSystem kieFileSystem = KIE_SERVICES.newKieFileSystem();
        kieFileSystem.write(ResourceFactory.newClassPathResource(RULES_CUSTOMER_RULES_DRL));
        KieBuilder kb = KIE_SERVICES.newKieBuilder(kieFileSystem);
        kb.buildAll();
        KieModule kieModule = kb.getKieModule();
        return KIE_SERVICES.newKieContainer(kieModule.getReleaseId());

    }
}
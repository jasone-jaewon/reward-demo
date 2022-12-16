package com.example.rewarddemo.adapter.hateoas.link;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.LinkRelation;

public class LinkGenerator {
    private static final String SWAGGER_PATH = "/swagger-ui/index.html#";
    private static final LinkRelation PROFILE_REL = LinkRelation.of("profile");

    public static <T> Link profileLink(Class<T> controllerClass) {
        String controllerClassName = controllerClass.getSimpleName();
        String translatedClassName = PropertyNamingStrategies.KebabCaseStrategy.INSTANCE.translate(controllerClassName);
        return Link.of(SWAGGER_PATH + "/" + translatedClassName).withRel(PROFILE_REL);
    }
}

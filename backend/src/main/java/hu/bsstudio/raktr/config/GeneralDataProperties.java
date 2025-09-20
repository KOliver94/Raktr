package hu.bsstudio.raktr.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("generalData")
public record GeneralDataProperties(
    String groupNameKey,
    String groupLeaderNameKey,
    String firstSignerNameKey,
    String firstSignerTitleKey,
    String secondSignerNameKey,
    String secondSignerTitleKey
) {
}

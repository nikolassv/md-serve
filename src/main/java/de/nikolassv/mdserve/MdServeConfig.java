package de.nikolassv.mdserve;

import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;

@ConfigMapping(prefix = "md-serve")
public interface MdServeConfig {

    @WithDefault(".")
    String sourceDir();

    @WithDefault("20")
    int maxTreeDepth();

    @WithDefault("8080")
    int port();
}

package de.nikolassv.mdserve;

import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;

@ConfigMapping(prefix = "md-serve")
public interface MdServeConfig {

    @WithDefault("./docs")
    String sourceDir();

    @WithDefault("20")
    int maxTreeDepth();
}

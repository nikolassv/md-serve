package de.nikolassv.mdserve;

import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;

import java.util.Optional;

@ConfigMapping(prefix = "md-serve")
public interface MdServeConfig {

    @WithDefault("./docs")
    String sourceDir();

    Optional<String> template();

    @WithDefault("20")
    int maxTreeDepth();
}

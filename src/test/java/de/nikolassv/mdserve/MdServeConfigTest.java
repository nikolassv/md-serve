package de.nikolassv.mdserve;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
class MdServeConfigTest {

    @Inject
    MdServeConfig config;

    @Test
    void sourceDirDefaultsToDocsDir() {
        assertEquals("./docs", config.sourceDir());
    }

    @Test
    void templateIsAbsentByDefault() {
        assertTrue(config.template().isEmpty());
    }
}

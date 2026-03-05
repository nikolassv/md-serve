# Step 2: Configuration

## Goal

Provide a typed, injectable configuration bean that exposes all application settings. Other components depend on this bean, so it must be stable before implementing business logic.

## Implementation Plan

- Create `MdServeConfig` using `@ConfigMapping(prefix = "md-serve")`:
  ```java
  @ConfigMapping(prefix = "md-serve")
  interface MdServeConfig {
      @WithDefault("./docs")
      String sourceDir();

      Optional<String> template();
  }
  ```
- Add defaults to `application.properties`:
  ```properties
  md-serve.source-dir=./docs
  ```
- `template` is optional (absence means use built-in default)

## Definition of Done

- `MdServeConfig` is injectable across the application
- `md-serve.source-dir` defaults to `./docs` when not set
- `md-serve.template` is absent by default and can be set to an arbitrary path
- A simple unit test or Quarkus `@QuarkusTest` verifies the defaults are applied correctly

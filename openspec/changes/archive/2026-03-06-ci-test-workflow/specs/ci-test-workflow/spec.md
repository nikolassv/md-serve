## ADDED Requirements

### Requirement: Tests run on push to main
The system SHALL execute all Maven tests automatically whenever a commit is pushed to the `main` branch.

#### Scenario: Commit pushed to main
- **WHEN** a commit is pushed to the `main` branch
- **THEN** the CI workflow is triggered and all tests are executed via `mvn verify`

#### Scenario: Tests pass
- **WHEN** all tests pass
- **THEN** the workflow run completes with a success status

#### Scenario: Tests fail
- **WHEN** one or more tests fail
- **THEN** the workflow run completes with a failure status and the failed test output is visible in the Actions log

### Requirement: Tests run on pull requests
The system SHALL execute all Maven tests automatically whenever a pull request is opened or updated.

#### Scenario: Pull request opened
- **WHEN** a pull request is opened against any branch
- **THEN** the CI workflow is triggered and all tests are executed via `mvn verify`

#### Scenario: Pull request updated
- **WHEN** new commits are pushed to an open pull request
- **THEN** the CI workflow is re-triggered and all tests are executed again

### Requirement: Correct Java version used
The system SHALL build and test using Java 21.

#### Scenario: Java version matches project requirement
- **WHEN** the CI workflow runs
- **THEN** Java 21 (Temurin distribution) is configured before any Maven commands are executed

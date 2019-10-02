# Data-Sets

**Data-Sets** allow providers to share immutable sets of data. A dataset may contain multiple versions of the data. Data-Sets may be owned by an individual user or by a project.

```gherkin
Feature: Basic Features of Data-Sets

  Background: 
    Given we have the following users
      | Username    | Roles                 |
      | alice       | admin                 |
      | bob         | a-team, b-team        |
      | clair       | a-team                |
```

Thus datasets can be created in two ways either directly or via an existing project.

```gherkin
  Scenario: Create a user dataset
    When "bob" creates a dataset called "my-first-dataset"
    Then "bob" should find "my-first-dataset" when listing his own datasets
    
  Scenario: Create a project dataset
    Given we have the following additional role-owned projects
      | Project               | Owned by            | Private |
      | some-project          | a-team              | no      |
    And "clair" creates a dataset called "some-dataset" in project "some-project"
    Then "clair" should find "some-dataset" when listing her own datasets
```
# Datasets Access Control

**Datasets** allow providers to share immutable sets of data. A dataset may contain multiple versions of the set. Datasets must be created as resources of a project.

```gherkin
Feature: Datasets Access Control

  Background: 
    Given we have the following users
      | Username    | Roles                 |
      | alice       | admin                 |
      | bob         | a-team, b-team        |
      | clair       | a-team                |
      | debra       | b-team                |
    And we have the following role-owned projects
      | Project               | Owned by            | Private |
      | some-project          | a-team              | no      |
      | some-private-project  | b-team              | yes     |
```

```gherkin
  Scenario: Create a project dataset
    Given "clair" creates a dataset called "some-dataset" in project "some-project"
    Then "clair" should find "some-dataset" when listing her own datasets
    And dataset "some-dataset" of project "some-project" should be owned by role "a-team"
```

## Dataset Grant

Foo bar ...
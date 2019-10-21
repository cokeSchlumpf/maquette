# Projects

All resources within Marquette are managed within Projects. Projects can be created by any user. A project maybe public or private. Public projects might be discovered by other users which have no access to the project, private projects can not be found by user which have no granted access to the project.

Any user can create projects, the user who creates the project, becomes the owner of the project.

```gherkin
Feature: Projects

  Background: 
    Given we have the following users
      | Username    | Roles                 |
      | alice       | a-team                |
      | bob         | a-team                |

  Scenario: Create a project
    When "bob" creates a project "my-first-project"
    Then "bob" should find the project when listing his own projects
    And "alice" should find the project when searching for projects
    
  Scenario: Create private project
    When "bob" creates a private project "my-second-project"
    Then "bob" should find the project when listing his own projects
    And "alice" should not find the project when searching for projects
    
    When "bob" changes owner of project to role "a-team"
    Then "alice" should find the project when searching for projects
    And "alice" should find the project when listing her own projects
```
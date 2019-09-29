# Data-Shop Authorizations and Access Rights

This documentation describes some details about access management within Maquette Data Shop. For all of the following explanations we assume the following given namespaces and datasets:

```gherkin
Feature: Authorizations and Access Rights

  Background: 
    Given we have the following users
      | Username    | Roles                 |
      | alice       | admin                 |
      | bob         | a-team, b-team        |
      | clair       | a-team                |
      | debra       | b-team                |
      | edgar       | c-team                |
    
    Given we have the following additional role-owned namespaces
      | Namespace             | Owned by            | Private |
      | a-team-space-public   | a-team              | no      |
      | a-team-space-private  | a-team              | yes     |
      | b-team-space          | b-team              | no      |
    
    Given namespace "a-team-space-public" contains the following datasets
      | Dataset       | Created by          | Description
      | ds-a-1        | clair               | Sample data for A1
      | ds-a-2        | bob                 | Sample data for A2
      
    And namespace "a-team-space-private" contains the following datasets
      | Datasets      | Created by          | Description
      | ds-a-3        | bob                 | Private sample data A3
      | ds-a-4        | bob                 | Private sample data A4
```

```gherkin
  Scenario: Some first scenario
    Given today is Sunday
```
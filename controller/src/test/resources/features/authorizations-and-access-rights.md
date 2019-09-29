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
      | Dataset       | Created by          | Description         | Private |
      | ds-a-1        | clair               | Sample data for A1  | yes     |
      | ds-a-2        | bob                 | Sample data for A2  | no      |
    And namespace "a-team-space-private" contains the following datasets
      | Datasets      | Created by          | Description             | Private |
      | ds-a-3        | bob                 | Private sample data A3  | no      |
      | ds-a-4        | bob                 | Private sample data A4  | yes     |
    And namespace "b-team-space" contains the following datasets
      | Dataset       | Created by          | Description         | Private |
      | ds-b-1        | debra               | Sample data for B1  | no      |
      | ds-b-2        | bob                 | Sample data for B2  | yes     |
      
    Given user "bob" has the following datasets in his private namespace
      | Dataset       | Description         | Private |
      | ds-bob-1      | Sample data for Bob | yes     |
      | ds-bob-2      | Sample data for Bob | no      |
```

## Listing Namespaces

When listing own datasets, a user should see all datasets where she is the owner or is member of a role who is owner of the dataset. The list should also include the datasets where ... 

```gherkin
  Scenario: Some first scenario
    Given today is Sunday
```
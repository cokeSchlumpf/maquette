# Datasets

**Datasets** allow providers to share immutable sets of data. A dataset may contain multiple versions of the set. Datasets may be owned by an individual user or by a project.

```gherkin
Feature: Datasets

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
    Given we have the following role-owned projects
      | Project               | Owned by            | Private |
      | some-project          | a-team              | no      |
    And "clair" creates a dataset called "some-dataset" in project "some-project"
    Then "clair" should find "some-dataset" when listing her own datasets
    And dataset "some-dataset" of project "some-project" should be owned by role "a-team"
```

## Adding data to a dataset

When adding data to a dataset, one must define the schema for the data to be added. Maquette uses Avro to transport structured data, thus also the schema must be defined as an Avro schema. As soon as data is pushed to the dataset it's committed and becomes immutable. 

To identify the version of a dataset, version numbers are calculated based on the schema of the data. Starting from `1.0.0` the minor version number is increased for each new version which has the same schema as the previous version; if the schema changes, the major version is increased.

```gherkin
  Scenario: Adding data to datasets
    Given we have the following role-owned projects
      | Project               | Owned by            | Private |
      | some-project          | a-team              | no      |
    And we have the following datasets in project "some-project"
      | Dataset               | Private |
      | some-data             | no      |
    
    Given we have a dataset of the following schema:
      """
      {
         "namespace": "example.avro",
         "type": "record",
         "name": "Feedback",
         "fields": [
            {"name": "id", "type": "string"},
            {"name": "feedback",  "type": "string"}
         ] 
      }
      """
    And we have the following data:
      | id    | feedback       |
      | 0     | Feedback A     |
      | 1     | Feedback B     |
      | 2     | Feedback C     |
      | 3     | Feedback D     |
      | 4     | Feedback E     |
      | 5     | Feedback F     |
    
    When we push this data to dataset "some-data"
    Then we expect 1 existing version(s) in the dataset
    And we expect that version "1.0.0" exists in the dataset
    And we expect that version "1.0.0" contains 6 tuples
    
    When we push this data again to dataset "some-data"
    Then we expect 2 existing version(s) in the dataset
    And we expect that version "1.1.0" exists in the dataset
    And we expect that version "1.1.0" contains 6 tuples
``` 

## Description

Datasets can have a description which might be written in Markdown and which can be updated by owners or administrators of the dataset.

```gherkin
  Scenario: Update dataset description.
    Given we have the following role-owned projects
      | Project               | Owned by            | Private |
      | some-project          | a-team              | no      |
    Given we have the following datasets in project "some-project"
      | Dataset               | Private |
      | some-data             | no      |
    When user "bob" updates the description of dataset "some-data" to
      """
      # Some Data
      
      Lorem ipsum dolor sit amet del conum.
      
        * Foo
        * Bar
        
      Lorem ipsum dolor sit amet.
      """
    Then the dataset details should contain this description.
```
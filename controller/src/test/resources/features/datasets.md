# Datasets

**Datasets** allow providers to share immutable sets of data. A dataset may contain multiple versions of the set. Datasets must be created as resources of a project.

```gherkin
Feature: Datasets

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

## Adding data to a dataset

When adding data to a dataset, one must define the schema for the data to be added. Maquette uses Avro to transport structured data, thus also the schema must be defined as an Avro schema. As soon as data is pushed to the dataset it's committed and becomes immutable. 

To identify the version of a dataset, version numbers are calculated based on the schema of the data. Starting from `1.0.0` the minor version number is increased for each new version which has the same schema as the previous version; if the schema changes, the major version is increased.

```gherkin
  Scenario: Adding data to datasets
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

    Given we have a dataset of the following schema:
      """
      {
         "namespace": "example.avro",
         "type": "record",
         "name": "Feedback",
         "fields": [
            {"name": "id", "type": "string"},
            {"name": "country",  "type": "string"},
            {"name": "capital", "type": "string"}
         ] 
      }
      """
    And we have the following data:
      | id    | country        | capital          |
      | 0     | Germany        | Berlin           |
      | 1     | Switzerland    | Berne            |
      | 2     | Austria        | Vienna           |
      | 3     | France         | Paris            |
      
    When we push this data to dataset "some-data"
    Then we expect 3 existing version(s) in the dataset
    And we expect that version "2.0.0" exists in the dataset
    And we expect that version "2.0.0" contains 4 tuples
```

## Reading data from a dataset

Data from datasets can be retrieved by requesting a specified version; or by just requesting data from a dataset version which defaults to the latest version of the dataset. Data is retrieved in Avro format in general. When using the Python SDK Avro format is implicitly converted to Pandas DataFrames. When using the Java SDK the Avro Records can be easily mapped to Java POJOs.

```gherkin
    When we read data from dataset "some-data"
    Then we expect that we received 4 tuples
    
    When we read data from dataset "some-data" with version "1.1.0"
    Then we expect that we received 6 tuples
    
    When receiving data from version "3.0.0" we expect an exception
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

## Access Control

Datasets have various options to allow or restrict access to their data and details.

### Private Datasets

Private Datasets can only be discovered and seen by users which have granted access to the dataset. By default a dataset is not private; this allows users to find the dataset and read their metadata (e.g. description, schema, existing versions), but not the data itself.

In the following example `clair` can find the dataset of `b-team` and read its metadata, but she can not read or produce data from the dataset.

```gherkin
  Scenario: Private datasets
    Given we have the following role-owned projects
      | Project               | Owned by            | Private |
      | some-project          | b-team              | no      |
    And we have the following datasets in project "some-project"
      | Dataset               | Private |
      | some-data             | no      |
    And dataset "some-data" contains 2 versions
      
    Then "clair" should be able to see dataset "some-data" when browsing available datasets
    And "clair" should be able to see details of dataset "some-data"
    
    When "clair" receives data from dataset "some-data" we expect an exception
    When "clair" produces data to dataset "some-data" we expect an exception
```

But if the dataset becomes private, `clair` should also not be able to find the dataset anymore.

```gherkin
    Given dataset "some-data" is set to be private
    
    Then "clair" should not be able to see dataset "some-data" when browsing available datasets
    And "clair" cannot see details of the dataset
    
    When "clair" receives data from dataset "some-data" we still expect an exception
    When "clair" produces data to dataset "some-data" we still expect an exception
```

When the dataset is private and `clair` should have access to it, then she needs to be granted to the dataset. Users can be granted
to become a consumer of a dataset only, like in the following example.

```gherkin
    Given "clair" becomes a consumer of dataset "some-data"
    
    Then "clair" should be able to see dataset "some-data" when browsing available datasets
    And "clair" should be able to see details of dataset "some-data"
    
    When "clair" reads data from dataset "some-data"
    Then we expect that we received at least 1 tuple(s)
    
    When "clair" produces data to dataset "some-data" we still expect an exception
```

A user can also only be a producer which allows upload of data, but no download.

```gherkin
    When consumer access for dataset "some-data" is revoked from "clair"
    And "clair" becomes a producer of dataset "some-data"
    
    Then "clair" should be able to see dataset "some-data" when browsing available datasets
    And "clair" should find "some-data" when listing her own datasets
    And "clair" should be able to see details of dataset "some-data"
    
    When "clair" receives data from dataset "some-data" we expect an exception
    
    Given we have a dataset of the following schema:
      """
      {
         "namespace": "example.avro",
         "type": "record",
         "name": "Feedback",
         "fields": [
            {"name": "id", "type": "string"},
            {"name": "country",  "type": "string"},
            {"name": "capital", "type": "string"}
         ] 
      }
      """
    And we have the following data:
      | id    | country        | capital          |
      | 0     | Germany        | Berlin           |
      | 1     | Switzerland    | Berne            |
      | 2     | Austria        | Vienna           |
      | 3     | France         | Paris            |
    When "clair" pushes this data to dataset "some-data"
    Then we expect 3 existing version(s) in the dataset
```

### Requesting access to datasets

When a dataset is not private a user can find and access the metadata of a dataset. A user might also request access to the dataset.

```gherkin
  Scenario: Dataset Access Request 
    Given we have the following role-owned projects
      | Project               | Owned by            | Private |
      | some-project          | b-team              | no      |
    And we have the following datasets in project "some-project"
      | Dataset               | Private |
      | some-data             | no      |
    And dataset "some-data" contains 2 versions
      
    Then "clair" should be able to see dataset "some-data" when browsing available datasets
    And "clair" is able to create a dataset access request

    Given "clair" requests consumer access to the dataset
    Then "bob" can see the request when viewing the dataset details
    And "bob" has received a notification to review the request
    And "clair" sees the request when viewing her personal profile info
```

The data owner receives a notification to review the request. He can decide whether he wants to approve or deny the request. When the request is approved, the requester receives a notification; the requester can now access the data. 

```gherkin
    When "bob" approves the request
    Then "clair" receives a notification that her request was approved
    When "clair" reads data from dataset "some-data"
    Then we expect that we received at least 1 tuple(s)
```
# Maquette [![Actions Status](https://github.com/cokeSchlumpf/maquette/workflows/Gradle%20Build/badge.svg)](https://github.com/cokeSchlumpf/maquette/actions)

## Concepts

### Data Shop

The data shop can be used to find the right data and to easily request access to data. The catalog offers two methods to store and retrieve data:

* **Datastreams** provide a mechanism to publish and store events for a provided retention time.

* **Datasets** allow providers to share immutable sets of data. A dataset may contain multiple versions of the data.

To push data to streams or datasets, data providers can use the maquette's **Catalog SDK** to push data from various data sources into the catalog. The SDK is available for Java.

In addition to Datastreams and Datasets the catalog also offers **Datasources**. Datasources allow useres to browse available data sources and request access and data from the sources.
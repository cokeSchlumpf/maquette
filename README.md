# Maquette [![Actions Status](https://github.com/cokeSchlumpf/maquette/workflows/Gradle%20Build/badge.svg)](https://github.com/cokeSchlumpf/maquette/actions)

## Concepts

### Data Shop

The data shop can be used to find the right data and to easily request access to data. The catalog offers three methods to retrieve data:

* **Data-Streams** provide a mechanism to publish and store events for a provided retention time.

* **Datasets** allow providers to share immutable sets of data. A dataset may contain multiple versions of the data. Detailed concepts can be found in th [Dataset features](controller/src/test/resources/features/datasets.md).

* **Data-Collections** allow providers to share collections of raw binary files.

* **Data-Sources** allows consumers to directly fetch data from existing data sources.

To push data to streams or datasets, data providers can use the maquette's **Python SDK** or the **Java SDK** to push data from various data sources into the catalog.

In addition to Data-Streams and Datasets the catalog also offers **Datasources**. Datasources allow useres to browse available data sources and request access and data from the sources.
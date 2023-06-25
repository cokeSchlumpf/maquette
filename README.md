# Maquette Data Science and Machine Learning Platform

Maquette is an open, extendable Data Science and Machine Learning Platform. It aims to connect several technologies, toolsets, frameworks and platforms into a single platform which connects all the dots in an end-to-end data science workflow.

## Development

The project consists of the following components:

* [Maquette Hub](./backend) is the main component of Maquette. A Spring Boot application which is serves the Maquette API, the frontend and integrates all other Maquette services and external providers.

* [Documentation](./docs) contains documentation artefacts. Maquette uses [MkDocs](https://www.mkdocs.org/) to create a documentation page. From the project root directory.

## Get Started

### Documentation

To work with the MkDocs documentation, install the required dependencies.

```bash
$ conda create -p ./env python=3.10
$ conda activate ./env
$ poetry install
```

Once the environment and dependencies are installed, you can run MkDocs commands to work with the documentation.

```bash
$ mkdocs serve
```
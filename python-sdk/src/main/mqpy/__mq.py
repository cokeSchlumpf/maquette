import pandas as pd

from enum import Enum
from typing import Optional

from .__client import Client
from .__user_config import UserConfiguration

client = Client.from_config(UserConfiguration('/foo'))


class EAuthorizationType(Enum):
    USER = "user"
    ROLE = "role"
    WILDCARD = "*"


class ENamespacePrivilege(Enum):
    MEMBER = "member"
    PRODUCER = "producer"
    CONSUMER = "consumer"
    ADMIN = "admin"


class EDatasetPrivilege(Enum):
    PRODUCER = "producer"
    CONSUMER = "consumer"
    ADMIN = "admin"


class DatasetVersion:

    __namespace: str = None

    __dataset: str = None

    __version: str = None

    def __init__(self, dataset: str, version: Optional[str] = None, namespace: Optional[str] = None):
        self.__namespace = namespace
        self.__dataset = dataset
        self.__version = version

    def get(self) -> pd.DataFrame:
        pass

    def print(self) -> 'DatasetVersion':
        resp = client.command('dataset version show', {
            'namespace': self.__namespace,
            'dataset': self.__dataset,
            'version': self.__version
        })

        print(resp['output'])

        return self

    def __str__(self):
        resp = client.command('dataset version show', {
            'namespace': self.__namespace,
            'dataset': self.__dataset,
            'version': self.__version
        })

        return resp['output']

    def __repr__(self):
        return self.__str__()


class Dataset:

    __namespace: str = None

    __name: str = None

    def __init__(self, name: str, namespace: str = None):
        self.__name = name
        self.__namespace = namespace

    def create(self) -> 'Dataset':
        client.command('datasets create', {'dataset': self.__name, 'namespace': self.__namespace})
        return self

    def grant(self, grant: EDatasetPrivilege, to_auth: EAuthorizationType, to_name: str = None) -> 'Dataset':
        client.command('dataset grant', {
            'dataset': self.__name,
            'namespace': self.__namespace,
            'privilege': grant.value,
            'authorization': to_auth.value,
            'to': to_name
        })

        return self

    def revoke(self, revoke: EDatasetPrivilege, auth: EAuthorizationType, from_name: str = None) -> 'Dataset':
        client.command('dataset revoke', {
            'dataset': self.__name,
            'namespace': self.__namespace,
            'privilege': revoke.value,
            'authorization': auth.value,
            'from': from_name
        })

        return self

    def print(self):
        resp = client.command('dataset show', {'dataset': self.__name, 'namespace': self.__namespace})
        print(resp['output'])
        return self

    def put(self, data: pd.DataFrame) -> DatasetVersion:
        pass

    def versions(self) -> pd.DataFrame:
        resp = client.command('dataset versions', {'dataset': self.__name, 'namespace': self.__namespace})
        return resp['data'][0]

    def version(self, version: Optional[str] = None):
        return DatasetVersion(self.__name, version, self.__namespace)

    def __str__(self):
        resp = client.command('dataset show', {'dataset': self.__name, 'namespace': self.__namespace})
        return resp['output']

    def __repr__(self):
        return self.__str__()


class Namespace:

    __name: str = None

    def __init__(self, name: str = None):
        self.__name = name

    def create(self) -> 'Namespace':
        client.command('namespaces create', {'namespace': self.__name})
        return self

    def datasets(self) -> pd.DataFrame:
        resp = client.command('datasets', {'namespace': self.__name})
        return resp['data'][0]

    def dataset(self, name: str) -> Dataset:
        return Dataset(name, self.__name)

    def grant(self, grant: ENamespacePrivilege, to_auth: EAuthorizationType, to_name: str = None) -> 'Namespace':
        client.command('namespace grant', {
            'namespace': self.__name,
            'privilege': grant.value,
            'authorization': to_auth.value,
            'to': to_name
        })

        return self

    def revoke(self, grant: ENamespacePrivilege, to_auth: EAuthorizationType, to_name: str = None) -> 'Namespace':
        client.command('namespace revoke', {
            'namespace': self.__name,
            'privilege': grant.value,
            'authorization': to_auth.value,
            'from': to_name
        })

        return self

    def print(self) -> 'Namespace':
        resp = client.command('namespace show', {'namespace': self.__name})
        print(resp['output'])
        return self

    def __str__(self):
        resp = client.command('namespace show', {'namespace': self.__name})
        return resp['output']

    def __repr__(self):
        return self.__str__()


def namespaces() -> pd.DataFrame:
    resp = client.command('namespaces')
    return resp['data'][0]
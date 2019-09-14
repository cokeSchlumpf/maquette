import pandas as pd

from io import StringIO

from .__client import Client
from .__user_config import UserConfiguration

client = Client.from_config(UserConfiguration('/foo'))


class Dataset:

    __namespace: str = None

    __name: str = None

    def __init__(self, name: str, namespace: str = None):
        self.__name = name
        self.__namespace = namespace

    def create(self):
        client.command('datasets create', {'name': self.__name, 'namespace': self.__namespace})


class Namespace:

    __name: str = None

    def __init__(self, name: str = None):
        self.__name = name

    def create(self):
        client.command('namespaces create', {'name': self.__name})

    def datasets(self):
        resp = client.command('datasets', {'namespace': self.__name})
        return pd.read_csv(StringIO(resp['data'][0]), sep = ';')

    def dataset(self, name: str):
        return Dataset(name, self.__name)

    def print(self):
        resp = client.command('namespace show', {'namespace': self.__name})
        print(resp['output'])


def namespaces() -> pd.DataFrame:
    resp = client.command('namespaces')
    return pd.read_csv(StringIO(resp['data'][0]), sep = ';')
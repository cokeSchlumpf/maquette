import pandas as pd

from io import StringIO

from .__client import Client
from .__user_config import UserConfiguration

client = Client.from_config(UserConfiguration('/foo'))


def create_namespace(name: str) -> None:
    client.post('/cli', {'command': 'namespaces create', 'name': name})


def namespaces() -> pd.DataFrame:
    resp = client.post('/cli', {'command': 'namespaces'}).json()
    return pd.read_csv(StringIO(resp['data'][0]), sep = ';')
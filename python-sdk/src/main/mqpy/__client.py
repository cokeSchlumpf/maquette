import requests

from typing import List

from .__user_config import UserConfiguration

class Client:

    __base_url: str = None
    __headers: dict

    def __init__(self, base_url: str, user_id: str, roles: List):
        self.__base_url = base_url
        self.__headers = {
            'x-user-id': user_id,
            'x-user-roles': ','.join(roles)
        }

    @staticmethod
    def from_config(config: UserConfiguration) -> 'Client':
        return Client(config.url(), config.user(), [])

    def command(self, cmd: str, args: dict = None) -> dict:
        request = { 'command': cmd }
        if args is not None:
            request.update(args)

        response = requests.post(self.__base_url + '/cli', json = request, headers = self.__headers)

        if response.status_code < 200 or response.status_code > 299:
            raise RuntimeError("call to Maquette controller was not successful ¯\\_(ツ)_/¯")
        elif response.json()['error'] is not None:
            raise RuntimeError(response.json()['error'])
        else:
            return response.json()

    def get(self, url: str) -> requests.Response:
        return requests.get(self.__base_url + url, headers = self.__headers)

    def put(self, url: str, json = None) -> requests.Response:
        return requests.put(self.__base_url + url, json = json, headers = self.__headers)

    def post(self, url: str, json = None) -> requests.Response:
        return requests.post(self.__base_url + url, json = json, headers = self.__headers)
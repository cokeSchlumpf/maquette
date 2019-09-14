class UserConfiguration:

    __mq_home: str = None

    def __init__(self, mq_home: str):
        self.__mq_home = mq_home

    def url(self) -> str:
        return 'http://localhost:8080/api/v1'

    def user(self) -> str:
        return 'hippo'
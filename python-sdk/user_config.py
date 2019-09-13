import yaml

class Configuration:

    __mq_home: str = None

    def __init__(self, mq_home: str):
        self.__mq_home
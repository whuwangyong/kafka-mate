def _init_():
    global _global_dict
    _global_dict = {}


def set_value(k, v):
    _global_dict[k] = v


def get_value(k):
    try:
        return _global_dict[k]
    except KeyError:
        print('key not found')
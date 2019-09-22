import _ from 'lodash';

export default (strings, prefix = '') => {
    return _.reduce(strings, (obj, string) => _.assign(obj, { [string]: `${prefix}${string}` }), {});
};
import _ from 'lodash';
import serviceA from './_template/epics';
import serviceB from './_template/epics';

export default _.concat(
    serviceA,
    serviceB);
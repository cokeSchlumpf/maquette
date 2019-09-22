import serviceA, { types as serviceATypes } from './_template/actions';
import serviceB, { types as serviceBTypes } from './_template/actions';

export const types = {
    serviceA: serviceATypes,
    serviceB: serviceBTypes
};

export default {
    serviceA,
    serviceB
};
/**
 * This module extends the well known JS library underscore (http://underscorejs.org)
 * with additional util methods which can be used within the project for common
 * problems.
 *
 * @module underscore.
 */

import _ from 'underscore';

export default _.extend({}, _, {

    /**
     * Creates an array from string.
     * @param {string} string which contains an array.
     * @param {string} separator which separates the elements within the string. Default ','.
     * @param {boolean} stripWhitespace identifies if whitespaces should be stripped from elements.
     * @return {array} an array from the defined string.
     */
    arrayFromString(string, separator = ',', stripWhitespace = true) {
        return string.split(separator).map(s => {
            let result;
            if (stripWhitespace) {
                result = s.replace(/ /g, '');
            } else {
                result = s;
            }

            return result;
        });
    },

    /**
     * Creates a 2-dimensional matrix from an array.
     * @param {array} array is the initial array.
     * @param {number} elementsPerSubArray is the number of elements per sub array.
     * @return {array} a two-dimensional array.
     */
    arrayToMatrix(array, elementsPerSubArray) {
        let matrix = [];

        if (array) {
            for (let i = 0, k = -1; i < array.length; i === i + 1) {
                if (i % elementsPerSubArray === 0) {
                    k += 1;
                    matrix[k] = [];
                }

                matrix[k].push(array[i]);
            }
        }

        return matrix;
    },

    /**
     * Checks whether func is defined, if yes it's called with all parameters after func
     * @param {function} func to be called if defined
     * @param {...any} argumentList to be passsed to func
     * @return {any} return value of the function or undefined if func is not defined
     */
    callFunction(func) {
        return this.doIfElse(func, () => {
            const args = _.toArray(arguments).slice(1);
            func.apply(null, args);
        }, () => {
            // Do nothing.
        });
    },

    /**
     * Executes checks on variables as given values of a `smart` object. E.g.
     *
     * {
     *    isEqual: [ [ value1, expectedValue1 ], [ value2, expectedValue2 ] ],
     *    isFunction [ [ func1 ], [ func2 ] ]
     * }
     *
     * The key of the object  must match a valid test function. The value is always an
     * array of arrays. Each inner array is the argument list for the given check function.
     *
     * @param {object} checks to be performed.
     * @return {boolean} true or throw error if checks are not successful.
     */
    check(checks) {
        const self = this;
        const allowedChecks = [
            'contains', 'isEqual', 'isMatch', 'isEmpty', 'isElement', 'isArray', 'isDefined', 'isObject',
            'isFunction', 'isString', 'isNonEmptyString', 'isNumber', 'isBoolean',
            'isDate', 'isRegExp', 'isNaN', 'isNull', 'isUndefined', 'isUnique' ];

        _.each(_.keys(checks), key => {
            if (!_.contains(allowedChecks, key)) {
                throw new Error(`${key} is not a valid check. Please check your checks ${checks}.`);
            } else {
                _.each(checks[key], (args, index) => {
                    if (!self[key].apply(self, args)) {
                        throw new Error(`${key} check was not successful on supplied value(s): ${self.mkString(args)} (parameterlist ${index} for this check).`);
                    }
                });
            }
        });

        return true;
    },

    /**
     * Creates an object which serves constant values. E.g: { CONSTANT_1: "CONSTANT_1", CONSTANT_2: "CONSTANT_2" }
     * @param {array} strings which are constant names. You can also have multiple string arguments instead of an array.
     * @param {string} prefix all constants with a prefix (only if 1st parameter is an array).
     * @param {suffix} suffix all constants with a suffix (only if 1st parameter is an array).
     * @return {object} which contains each string as key -> value pair.
     */
    constantsFromArray(strings, prefix, suffix) {
        return _.reduce(strings, (obj, string) => _.assign(obj, { [string]: `${prefix}${string}` }), {});
    },

    /**
     * Helper method to provide a functional if statement.
     * @param {boolean} condition which decides whether to execute trueFunc or falseFunc.
     * @param {function} trueFunc which will be executed if condition is true.
     * @param {falseFunc} falseFunc which will be executed if condition is false.
     * @param {object} [context] which should be injected to the functions.
     * @return {any} the result of trueFunc/ falseFunc.
     */
    doIfElse(condition, trueFunc, falseFunc, context) {
        let result;

        if (condition) {
            result = (context ? _.bind(trueFunc, context) : trueFunc).apply();
        } else {
            result = (context ? _.bind(falseFunc, context) : falseFunc).apply();
        }

        return result;
    },

    /**
     * Checks whether obj is unequal to undefined.
     * @param {object} obj to check
     * @return {booolean} if object is defined
     */
    isDefined(obj) {
        return !_.isUndefined(obj);
    },

    /**
     * Checks whether the parameter is an string and if the string is not empty.
     * @param {object} obj which may be a string
     * @return {boolean} true if obj is a string and not empty, else false
     */
    isNonEmptyString(obj) {
        return _.isString(obj) && obj.length > 0;
    },

    /**
     * Checks whether an object is includey only once in the collection.
     * @param {array} collection to check
     * @param {object} element to look for
     * @return {boolean} true if element is only included once, false otherwise
     */
    isUnique(collection, element) {
        const predicate = item => {
            return item === element;
        };

        const firstIndex = _.findIndex(collection, predicate);
        const lastIndex = _.findLastIndex(collection, predicate);

        return firstIndex >= 0 && firstIndex === lastIndex;
    },

    /**
     * Makes a string defined by an array, a separator, a prefixed string,
     * and a suffixed string.
     * @param {array} array the defined array of elements.
     * @param {string} separator the defined separator.
     * @param {string} before the defined prefixed string.
     * @param {string} after the defined suffixed string.
     * @return {string} a string representation of the defined parameters.
     */
    mkString(array, separator = ', ', before = '', after = '') {
        const result = array.reduce((concat, item, index) => {
            let newResult = concat;
            if (index > 0) {
                newResult += separator;
            }

            return newResult + item;
        }, before);

        return result + after;
    },

    /**
     * Returns value if condition is true.
     * @param {any} value which will be returned if condition is true.
     * @param {any} elseValue will be returned if condition is false.
     * @param {function} [condition], optional function which will be called with (value, elseValue). If not set the function will check if value is defined.
     * @return {any} either value or elseValue.
     */
    orElse(value, elseValue, condition) {
        const func = this.isFunction(condition) ? condition : this.isDefined;
        return this.doIfElse(func.apply(this, [ value, elseValue ]), () => value, () => elseValue);
    },

    /**
     * Returns an object with the parser URL parameters.
     * @param {string} value the defined URL (windows.location.search)
     * @return {object} an object which represents the parsed URL parameters.
     */
    parseURL(value) {
        return _.object(_.compact(_.map(value.slice(1).split('&'), function(item) {
            let result = null;
            if (item) {
                result = item.split('=');
            }
            return result;
        })));
    }
});

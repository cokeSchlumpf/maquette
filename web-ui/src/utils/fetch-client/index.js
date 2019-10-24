import UrlUtil from './url-util';
import _ from '../underscore';
import __ from 'lodash';

export default class FetchClient {
    /**
     * Creates a new instance of FetchClient.
     * @param {string} servicePath of the server resource. E.g. '/api/books'.
     * @param {object} [requestConfig] of the service.
     * @param {object} [responseConfig] of the service.
     * @return {FetchClient} instance
     */
    constructor(servicePath, requestConfig = {}, responseConfig = {}) {
        _.check({
            isNonEmptyString: [ [ servicePath ] ]
        });

        const baseURL = UrlUtil.baseURL();

        this.serviceURL = _.doIfElse(
            servicePath.indexOf('http://') === -1 && servicePath.indexOf('https://') === -1,
            () => _.doIfElse(baseURL.charAt(baseURL.length - 1) === '/' || servicePath.charAt(0) === '/',
                () => `${baseURL}${servicePath}`,
                () => `${baseURL}/${servicePath}`),
            () => servicePath);

        if (_.isDefined(requestConfig) && typeof requestConfig === 'undefined') {
            this.requestConfig = requestConfig;
        } else {
            this.requestConfig = {
                timeout: 30000,
                noDelay: true,
                keepAlive: true
            };
        }

        if (_.isDefined(responseConfig) && typeof responseConfig === 'undefined') {
            this.responseConfig = responseConfig;
        } else {
            this.responseConfig = {
                timeout: 30000
            };
        }
    }

    /**
     * Internal method which calls the webservice client method with default request and response config,
     * adds error handling and binds handlers.
     * @param {string} method HTTP method to be called.
     * @param {object} args to be passed to the function (will be merged with configuration).
     * @return {Promise} promise.
     */
    _callMethod(method, args) {
        const path = _.isDefined(args.path) ? _.isDefined(args.path.id) ? `${args.path.id}` : '' : '';

        const urlParams = _.isDefined(args.path) ? _.isDefined(args.path.params) ? Object.keys(args.path.params).map(function(key) {
            return encodeURIComponent(key) + '=' + encodeURIComponent(args.path.params[key]);
        }).join('&') : '' : '';

        const pathParameters = _.isDefined(args.path) ? _.isDefined(args.path.params) ? `?${urlParams}` : '' : '';
        const uri = `${this.serviceURL}/${path}${pathParameters}`;

        const options = {
            method: method,
            mode: 'cors',
            redirect: 'follow',
            credentials: 'include',
            headers: args.headers,
            body: _.isDefined(args.body) ? JSON.stringify(args.body) : undefined
        };

        return fetch(uri, options)
            .then(response => {

                if (!response.ok) {
                    return response.json()
                        .then(body => {
                            throw body
                        });
                }

                if (response.status === 204) {
                    return {};
                }

                return response.json().catch(error => {
                    throw error;
                });
            });
    }

    /**
     * Lists a number of entities defined by start, count, and sortation.
     * @param {headers} headers is a map if headers past to the server.
     * @param {number} start is the first delivered entities.
     * @param {number} count is the number of delivered entities.
     * @param {string} orderBy is the name of the column the entities should be ordered. An array of strings will be transformed to a comma-speperated string.
     * @param {boolean} asc identifies the sorting order, default is true.
     * @param {object} where is a map where you define options to filter your entities. E.g. { "foo": "*bar" }.
     * @return {array} a list of JSON entities.
     */
    list(headers = {}, start = 0, count = 0, orderBy = '', asc = true, where = {}) {
        const data = {};

        if (start !== 0) {
            data.start = start;
        }

        if (count !== 0) {
            data.count = count;
        } else if (start !== 0) {
            data.count = 20;
        }

        if (orderBy.length > 0) {
            if (_.isArray(orderBy)) {
                data.orderBy = _.mkString(orderBy);
            } else {
                data.orderBy = orderBy;
            }

            data.asc = asc;
        }

        Object.keys(where).forEach(key => {
            data[key] = where[key];
        });

        const args = {
            path: {
                params: data
            },
            headers: headers
        };

        return this._callMethod('GET', args);
    }

    /**
     * Creates an entity.
     * @param {object} entity the defined entity to be created.
     * @param {object} headers the defined headers.
     * @return {object} a JSON entity.
     */
    create(entity, headers = {}) {
        const args = {
            body: entity,
            headers: _.extend({
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            }, headers)
        };

        return this._callMethod('POST', args);
    }

    /**
     * Submits a command towards the backend.
     *
     * @param cmd {string} the command name
     * @param args {object} arguments for the command
     * @param headers {object} additional headers
     * @returns {object} aa JSON response entity
     */
    command(cmd, args = {}, headers = {}) {
        const entity = _.extend({ "command": cmd }, args);

        const req = {
            body: entity,
            headers: _.extend({
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            }, headers)
        };

        return this
            ._callMethod('POST', req)
            .then(result => __.get(result, 'view', {}));
    }

    /**
     * Reads an entity defined by id.
     * @param {string} id the defined entity id.
     * @param {object} headers the defined headers.
     * @return {object} a JSON entity.
     */
    read(id, headers = {}) {
        const args = {
            path: {
                id: id
            },
            headers: headers
        };

        return this._callMethod('GET', args);
    }

    /**
     * Updates an entity defined by id.
     * @param {object} entity the defined entity id to be updated.
     * @param {object} headers the defined headers.
     * @return {object} a JSON entity.
     */
    update(entity, headers = {}) {
        const args = {
            body: entity,
            path: {
                id: entity.id || entity._id
            },
            headers: _.extend({
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            }, headers)
        };

        return this._callMethod('PUT', args);
    }

    /**
     * Deletes an entity defined by id.
     * @param {string} id the defined entity id to be deleted.
     * @param {object} headers the defined headers.
     * @return {object} a JSON entity.
     */
    delete(id, headers = {}) {
        const args = {
            path: {
                id: id
            },
            headers: headers
        };

        return this._callMethod('DELETE', args);
    }
}
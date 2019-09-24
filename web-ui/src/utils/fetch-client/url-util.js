export default {
    baseURL() {
        return window.location.origin;
    },

    /**
     * Gets a value by key from the hash part of the current url
     * @param {string} query the key
     * @see {@link http://stackoverflow.com/questions/901115/how-can-i-get-query-string-values-in-javascript/901144#901144|stackoverflow}
     * @return {Object} ist session guid.
     */
    getParameterByName(query) {
        let url = window.location.href;
        let key = query.replace(/[[\]]/g, '\\$&');
        let regex = new RegExp('[?&]' + key + '(=([^&#]*)|&|#|$)');
        let results = regex.exec(url);
        if (!results) {
            return null;
        }
        if (!results[2]) {
            return '';
        }
        return decodeURIComponent(results[2].replace(/\+/g, ' '));
    }
};

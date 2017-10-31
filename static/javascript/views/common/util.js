/**
 * Sherlock Util
 *
 * @Author : Mayank Sindwani
 * @Date   : 2017-10-16
 *
 * Description : Defines a set of common view utility functions.
 **/

class SherlockUtils {

    /**
     * Get Query Parameters
     *
     * Description: Fetches the query parameters for the current page.
     * @returns An object with decoded query parameter mappings.
     */
    static getQueryParams(){
        let lookup,
            params,
            kv,
            i;

        params = window.location.search.substring(1).split("&");
        lookup = {};

        for (i = 0; i < params.length; i++) {
            kv = params[i].split("=");
            if (kv[1]) {
                lookup[kv[0]] = decodeURIComponent(kv[1]);
            }
        }

        return lookup;
    }

    /**
     * Get File Extenstion
     *
     * Description: Extracts the extenstion from a path string.
     * @returns The extenstion as a string without the period.
     */
    static getFileExtension(path) {
        return path.substring(path.lastIndexOf(".") + 1, path.length);
    }

    /**
     * Save History
     *
     * Saves the history using the specified query parameters.
     * @param {queries}        // An arary of query parameters.
     * @param {replaceHistory} // If true, replaces the history.
     */
    static saveHistory(queries=[], replaceHistory=false) {
        const params = SherlockUtils.getQueryParams();
        let isSamePage = true;
        let search = '';

        if (Object.keys(params).length !== queries.length) {
            isSamePage = false;
        }

        // Build the query string.
        for (let i = 0; i < queries.length; i++) {
            let key = queries[i][0];
            let value = queries[i][1];

            if (i == 0) {
                search += `?${key}=${value}`;
            } else {
                search += `&${key}=${value}`;
            }
            isSamePage &= params[key] === value;
        }
        search = encodeURI(search);

        // Only make changes if the url is different.
        if (!isSamePage) {
            if (!replaceHistory) {
                history.pushState({}, '', `/${search}`);
            } else {
                history.replaceState({}, '', `/${search}`);
            }
        }
    }

}


export default SherlockUtils;

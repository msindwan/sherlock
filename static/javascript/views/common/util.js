/**
 * Sherlock Util
 *
 * @Author : Mayank Sindwani
 * @Date   : 2017-10-16
 *
 * Description : Defines a set of common view utility functions.
 **/

class SherlockUtils {

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

    static isEmptyString(str) {
        return (!str || /^\s*$/.test(str));
    }

    static getFileExtension(path) {
        const i = path.lastIndexOf(".");
        const ext = i > 0 ? path.substring(i + 1, path.length) : null;
        return ext;
    }


    static saveHistory(queries=[]) {
        let search = '';
        var len = queries.length;

        for (var i = 0; i < len; i++) {
            let key = queries[i][0];
            let value = queries[i][1];

            if (i == 0) {
                search += `?${key}=${value}`;
            } else {
                search += `&${key}=${value}`;
            }

        }
        search = encodeURI(search);
        if (window.location.search !== search) {
            history.pushState({}, '', `/${search}`);
        }
    }

    static replaceHistory(queries=[]) {
        let search = '';
        var len = queries.length;

        for (var i = 0; i < len; i++) {
            let key = queries[i][0];
            let value = queries[i][1];

            if (i == 0) {
                search += `?${key}=${value}`;
            } else {
                search += `&${key}=${value}`;
            }

        }
        search = encodeURI(search);
        if (window.location.search !== search) {
            history.replaceState({}, '', `/${search}`);
        }
    }

}


export default SherlockUtils;

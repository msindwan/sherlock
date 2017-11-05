/**
 * Sherlock API
 *
 * @Author : Mayank Sindwani
 * @Date   : 2017-10-21
 *
 * Description : Defines a set of api functions.
 **/

import axios from 'axios';

class SherlockAPI {

    /**
     * Fetch Files
     *
     * Description: Fetches files from the director in the path provided.
     * @param {path}    // An arary of strings representing the path to a directory.
     * @param {success} // The success callback.
     * @param {failure} // The failure callback.
     */
    static fetchFiles(path, success, failure) {
        let uri;
        if (path.length > 0) {
            uri = `/api/search/files?path=${JSON.stringify(path)}`;
        } else {
            uri = '/api/search/files';
        }
        axios.get(encodeURI(uri))
            .then(success)
            .catch(failure);
    }

    /**
     * Fetch File
     *
     * Description: Fetches the contents of a file and returns it as a string.
     * @param {path}    // An arary of strings representing the path to a file.
     * @param {success} // The success callback.
     * @param {failure} // The failure callback.
     */
    static fetchFile(path, success, failure) {
        const uri  = `/api/search/file?path=${JSON.stringify(path)}`;
        axios.get(encodeURI(uri), { headers: { 'Accept': 'text/plain' } })
            .then(success)
            .catch(failure);
    }

    /**
     * Search Files
     *
     * Description: Searches files for the given search string.
     * @param {searchText} // The text to search for.
     * @param {success}    // The success callback.
     * @param {failure}    // The failure callback.
     */
    static searchFiles(searchText, success, failure) {
        const uri = `/api/search/?query=${searchText}`;
        axios.get(encodeURI(uri))
            .then(success)
            .catch(failure);
    }

}

export default SherlockAPI;

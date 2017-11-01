/**
 * Sherlock Browser component
 *
 * @Author : Mayank Sindwani
 * @Date   : 2017-09-22
 *
 * Description : Defines the component to browse contents.
 **/

import React from 'react';
import axios from 'axios';
import SherlockUtils from '../../common/util';
import SherlockAPI from '../../common/api';
import FileViewer from './FileViewer';
import FileBrowser from './FileBrowser';
import SearchViewer from './SearchViewer';
import ErrorMessage from '../common/ErrorMessage';
import Crums from './Crums';

const Browser = React.createClass({

    getInitialState() {
        return {
            isLoading: false,
            searchResults: null,
            searchText: null,
            language: null,
            file: null,
            files: [],
            root: [],
            error: null
        };
    },

    componentDidMount() {
        window.addEventListener("popstate", () => this.loadPageArgs());
        this.loadPageArgs();
    },

    /**
     * Set Files State
     *
     * Description: Sets the browser view's state based on the path and the collection of files.
     * @param {path}  // An arary of strings representing the path to a directory.
     * @param {files} // The array of files.
     */
    setFilesState(path, files) {
        this.setState({
            error: null,
            isLoading: false,
            searchResults: null,
            searchText: this.state.searchText,
            language: null,
            file: null,
            files: files,
            root: path ? path : []
        });
        if (path.length == 0) {
            // We're at the root, so go back to the index page.
            SherlockUtils.saveHistory();
        } else {
            // Update the URL with the new path.
            SherlockUtils.saveHistory([ ['path', JSON.stringify(path)] ]);
        }
    },

    /**
     * Set File State
     *
     * Description: Sets the browser view's state based on the path and the selected file.
     * @param {path} // An arary of strings representing the path to a file.
     * @param {file} // The selected file.
     * @param {line} // An optional line to highlight.
     */
    setFileState(path, file, line) {
        this.setState({
            error: null,
            isLoading: false,
            searchResults: null,
            searchText: this.state.searchText,
            language: SherlockUtils.getFileExtension(path[path.length - 1]),
            file: file,
            files: this.state.files,
            root: path
        });
        this.bindLineNumberEventListeners([ ['file', JSON.stringify(path)] ], line);
    },

    /**
     * Set Search State
     *
     * Description: Sets the browser view's state based on the searched text.
     * @param {searchText}    // The curent search text.
     * @param {searchResults} // The retrieved results.
     */
    setSearchState(searchText, searchResults) {
        if (searchText) {
            // Update the search text and results.
            this.setState({
                error: null,
                isLoading: false,
                searchResults : searchResults,
                searchText: searchText
            });
            SherlockUtils.saveHistory([ [ 'query', searchText ] ]);
        } else {
            // Revert to the index page.
            this.setState(this.getInitialState());
            this.fetchFiles();
        }
    },

    /**
     * Load Page Args
     *
     * Description: Loads the state of the page based on the query parameters provided.
     */
    loadPageArgs() {
        const params = SherlockUtils.getQueryParams();
        const line = params['line'];
        const file = params['file'];
        const path = params['path'];
        const query = params['query'];

        // Reset the page.
        this.setState(this.getInitialState());

        try {
            if (query) {
                this.searchFiles(query);
            } else if (file) {
                this.fetchFile(JSON.parse(file), line);
            } else if (path) {
                this.fetchFiles(JSON.parse(path));
            } else {
                this.fetchFiles();
            }
        } catch (err) {
            // TODO: Handle exception.
        }
    },

    /**
     * Bind Line Number Event Listeners
     *
     * Description: Binds event listeners to each line in order to trigger highlighting.
     * @param {history} // The current history as an array of query parameter pairs.
     * @param {line}    // The optional line to highlight.
     */
    bindLineNumberEventListeners(history, line) {
        const lineElems = document.querySelectorAll('.react-syntax-highlighter-line-number');

        // highlight the line if provided.
        if (typeof line !== 'undefined') {
            const lineElem = lineElems[parseInt(line) - 1];
            lineElem.classList.add('active');
            lineElem.scrollIntoView();
            history.push([ 'line', line ]);
        }

        // Bind event listeners.
        lineElems.forEach((elem, i) => {
            elem.addEventListener('click', e => {
                // If the line is already set, reset and remove the old line
                // number from the history.
                if (typeof line !== 'undefined') {
                    lineElems[parseInt(line) - 1].classList.remove('active');
                    history.pop();
                }
                elem.classList.add('active');
                line = i + 1;
                history.push([ 'line', line ]);
                SherlockUtils.saveHistory(history, true);
            });
        });

        SherlockUtils.saveHistory(history);
    },

    /**
     * Fetch Files
     *
     * Description: Calls the Sherlock API to fetch files from a directory.
     * @param {path} // The path represented as an array.
     */
    fetchFiles(path=[]) {
        this.setState({ isLoading : true });
        SherlockAPI.fetchFiles(path, response => {
            // TODO: Handle error.
            this.setFilesState(path, response.data);
        },  error => {
            this.setState({
                isLoading: false,
                error: error.message
            });
        });
    },

    /**
     * Fetch File
     *
     * Description: Calls the Sherlock API to fetch a single file.
     * @param {path} // The path represented as an array.
     * @param {line} // An optional line number to highlight.
     */
    fetchFile(path, line) {
        this.setState({ isLoading : true });
        SherlockAPI.fetchFile(path, response => {
            this.setFileState(path, response.request.responseText, line);
        }, error => {
            this.setState({
                isLoading: false,
                error: error.message
            });
        });
    },

    /**
     * Search Files
     *
     * Description: Calls the Sherlock API to search files.
     * @param {searchText} // The text to use for the search.
     */
    searchFiles(searchText) {
        this.setState({ isLoading : true });
        if (searchText) {
            // Execute the search.
            SherlockAPI.searchFiles(searchText, response => {
                this.setSearchState(searchText, response.data);
            }, error => {
                this.setState({
                    isLoading: false,
                    error: error.message
                });
            });
        } else {
            // Reset the search state.
            this.setSearchState();
        }
    },

    /**
     * On Folder Nav Clicked
     *
     * Description: Handler triggered when navigating folders.
     * @param {e} // The synthetic event.
     * @param {i} // The index of the file/folder.
     */
    onFolderNavClicked(e, i) {
        this.fetchFiles(this.state.root.slice(0, i));
    },

    /**
     * On File Folder Clicked
     *
     * Description: Handler triggered when a file or folder is clicked.
     * @param {e} // The synthetic event.
     * @param {i} // The index of the file/folder.
     */
    onFileFolderClicked(e, i) {
        const fileFolder = this.state.files[i];
        const path = [...this.state.root, fileFolder.filename];
        if (fileFolder.isDir) {
            this.fetchFiles(path);
        } else {
            this.fetchFile(path);
        }
    },

    /**
     * On Search Text Chnage
     *
     * Description: Handler triggered when the search input changes.
     * @param {e} // The synthetic event.
     */
    onSearchTextChanged(e) {
        this.setState({ searchText: e.target.value });
    },

    /**
     * On Search
     *
     * Description: Handler triggered when a search is submitted.
     * @param {e} // The synthetic event.
     */
    onSearch(e) {
        e.preventDefault();
        this.searchFiles(this.state.searchText);
    },

    /**
     * Entry point.
     */
    render() {
        let browserView;

        if (this.state.error !== null) {
            browserView = (
                <ErrorMessage message={this.state.error} />
            );
        } else if (this.state.searchResults !== null) {
            // Browse search results.
            browserView = (
                <div className="file-browser">
                    <SearchViewer
                        searchResults={this.state.searchResults}
                        onSearchResultClicked={this.fetchFile} />
                </div>
            );
        } else if (this.state.file !== null) {
            // View the file.
            browserView = (
                <div className="file-browser">
                    <Crums
                        isFile={true}
                        path={[ 'home', ...this.state.root ]}
                        onCrumClick={this.onFolderNavClicked} />
                    <FileViewer
                        language={this.state.language}
                        file={this.state.file} />
                </div>
            );
        } else {
            // View the curent directory.
            browserView = (
                <div className="file-browser">
                    <Crums
                        path={[ 'home', ...this.state.root ]}
                        onCrumClick={this.onFolderNavClicked} />
                    <FileBrowser
                        files={this.state.files}
                        onFileFolderClicked={this.onFileFolderClicked} />
                </div>
            );
        }

        return (
            <div className={this.state.isLoading ? "file-browser-container loading" : "file-browser-container"}>
                <form onSubmit={this.onSearch} className="search-bar">
                    <input
                        value={this.state.searchText}
                        onChange={this.onSearchTextChanged}
                        type="text"
                        placeholder="Search here..."
                    />
                    <button type="submit">Search</button>
                </form>
                { browserView }
            </div>
        );
    }

});

export default Browser;

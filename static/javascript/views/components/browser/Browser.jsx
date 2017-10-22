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
import Crums from './Crums';

const Browser = React.createClass({

    // Initial page state.
    getInitialState() {
        return {
            searchResults: null,
            searchText: null,
            language: null,
            file: null,
            files: [],
            root: []
        };
    },

    resetState(newState={}) {
        this.setState(Object.assign({}, this.getInitialState(), newState));
    },

    componentDidMount() {
        window.addEventListener("popstate", e => this.loadPageArgs(e));
        this.loadPageArgs();
    },

    loadPageArgs(e) {
        const params = SherlockUtils.getQueryParams();
        const line = params['line'];
        const file = params['file'];
        const path = params['path'];
        const query = params['query'];

        this.resetLineNumbers();

        if (query) {
            // TODO
        } else if (file) {
            // TODO: Handle parse errors
            this.fetchFile(JSON.parse(file), line);
        } else if (path) {
            // TODO: Handle parse errors
            this.fetchFiles(JSON.parse(path));
        } else {
            this.fetchFiles();
        }
    },

    resetLineNumbers() {
        const lineElems = document.querySelectorAll('.react-syntax-highlighter-line-number');
        for (let i = 0; i < lineElems.length; i++) {
            lineElems[i].classList.remove('active');
        }
    },

    bindLineNumberEventListeners(history, line) {
        const lineElems = document.querySelectorAll('.react-syntax-highlighter-line-number');
        lineElems.forEach((elem, i) => {
            elem.addEventListener('click', e => {
                this.resetLineNumbers();
                elem.classList.add('active');
                if (line) {
                    history.pop();
                }
                line = i + 1;
                history.push([ 'line', line ]);
                SherlockUtils.replaceHistory(history);
            });
        });

        if (line) {
            const lineElem = lineElems[parseInt(line) - 1];
            lineElem.classList.add('active');
            lineElem.scrollIntoView();
            history.push([ 'line', line ]);
        }
        SherlockUtils.saveHistory(history);
    },

    fetchFiles(path=[]) {
        SherlockAPI.fetchFiles(path, response => {
            this.resetState({
                files : response.data,
                file: null,
                root: path ? path : []
            });
            if (path.length == 0) {
                SherlockUtils.saveHistory();
            } else {
                SherlockUtils.saveHistory([ ['path', JSON.stringify(path)] ]);
            }
        },  error => {
            console.log(error);
        });
    },

    fetchFile(path, line) {
        SherlockAPI.fetchFile(path, response => {
            this.resetState({
                root: path,
                file : response.request.responseText,
                language : SherlockUtils.getFileExtension(path[path.length - 1])
            });
            this.bindLineNumberEventListeners([ ['file', JSON.stringify(path)] ], line);
        }, error => {
            console.log(error);
        });
    },

    searchFiles(searchText) {
        if (searchText) {
            SherlockAPI.searchFiles(searchText, response => {
                this.resetState({
                    searchResults : response.data
                });
            }, error => {
                console.log(error);
            });
        } else {
            this.resetState();
        }
    },

    onFolderNavClicked(e, i) {
        if (this.state.file === null || i < this.state.root.length) {
            this.fetchFiles(this.state.root.slice(0, i));
        }
    },

    onFileFolderClicked(e, i) {
        const fileFolder = this.state.files[i];
        const path = [...this.state.root, fileFolder.filename];
        if (fileFolder.isDir) {
            this.fetchFiles(path);
        } else {
            this.fetchFile(path);
        }
    },

    onSearchTextChanged(e) {
        this.setState({
            searchText: e.target.value
        });
    },

    onSearch(e) {
        e.preventDefault();
        this.searchFiles(this.state.searchText);
    },

    render() {
        let browserView;

        if (this.state.file !== null) {
            browserView = (
                <div className="file-browser">
                    <Crums
                        path={[ 'home', ...this.state.root ]}
                        onCrumClick={this.onFolderNavClicked} />
                    <FileViewer
                        language={this.state.language}
                        file={this.state.file} />
                </div>
            );
        } else if (this.state.searchResults !== null) {
            browserView = (
                <SearchViewer
                    searchResults={this.state.searchResults} />
            );
        } else {
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
            <div>
                <form onSubmit={this.onSearch} className="search-bar">
                    <input
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

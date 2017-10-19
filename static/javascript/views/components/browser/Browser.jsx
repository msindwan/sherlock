/**
 * Sherlock Browser component
 *
 * @Author : Mayank Sindwani
 * @Date   : 2017-09-22
 *
 * Description :
 **/

import React from 'react';
import axios from 'axios';
import FileViewer from './FileViewer';
import FileBrowser from './FileBrowser';
import Crums from './Crums';

const Browser = React.createClass({

    // Initial page state.
    getInitialState() {
        return {
            searchText: null,
            searchResults: null,
            file: null,
            root: [],
            files: []
        };
    },

    componentDidMount() {
        this.fetchFiles();
    },

    fetchFiles(path) {
        let uri;
        if (typeof path !== 'undefined') {
            uri = `/api/search/files?path=${JSON.stringify(path)}`;
        } else {
            uri = '/api/search/files';
        }
        axios.get(encodeURI(uri))
            .then((response) => {
                this.setState({
                    files : response.data,
                    file: null,
                    root: path ? path : []
                });
            })
            .catch((error) => {
                console.log(error);
            });
    },

    onFolderNavClicked(e, i) {
        this.fetchFiles(this.state.root.slice(0, i));
    },

    onFileFolderClicked(e, i) {
        const fileFolder = this.state.files[i];
        const path = [...this.state.root, fileFolder.filename];
        if (fileFolder.isDir) {
            this.fetchFiles(path);
        } else {
            this.setState({
                file: JSON.stringify(path),
                root: path
            });
        }
    },

    onSearch(e) {
        e.preventDefault();
        if (this.state.searchText) {
            const uri = `/api/search/?query=${this.state.searchText}`;
            axios.get(encodeURI(uri))
                .then((response) => {
                    console.log(response.data);
                    this.setState({
                        searchResults : response.data
                    });
                })
                .catch((error) => {
                    console.log(error);
                });
        } else {
            this.setState({
                searchResults : null
            });
        }
    },

    render() {
        return (
            <div>
                <form onSubmit={this.onSearch} className="search-bar">
                    <input
                        onChange={e => { this.setState({ searchText: e.target.value }); }}
                        type="text"
                        placeholder="Search here..."
                    />
                    <button type="submit">Search</button>
                </form>
                { this.state.searchResults === null && this.state.file === null && (
                    <div className="file-browser">
                        <Crums
                            path={[ 'home', ...this.state.root ]}
                            onCrumClick={this.onFolderNavClicked} />
                        <FileBrowser
                            files={this.state.files}
                            onFileFolderClicked={this.onFileFolderClicked}/>
                    </div>
                )}
                { this.state.searchResults === null && this.state.file !== null && (
                    <div className="file-browser">
                        <Crums
                            path={[ 'home', ...this.state.root ]}
                            onCrumClick={this.onFolderNavClicked} />
                        <FileViewer file={this.state.file} />
                    </div>
                )}
                { this.state.searchResults !== null && this.state.file === null && (
                    <div>
                        {
                            this.state.searchResults.map((result, i) => {
                                return (
                                    <div key={i}
                                        className="file-browser-cell">
                                            {result.path}
                                            <div>
                                                {
                                                    result.frags.map((frag, j) => {
                                                        return (
                                                            <div key={j} dangerouslySetInnerHTML={{ __html: frag }}></div>
                                                        )
                                                    })
                                                }
                                            </div>
                                    </div>
                                );
                            })
                        }
                    </div>
                )}
            </div>
        );
    }

});

export default Browser;

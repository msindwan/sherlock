/**
 * Sherlock File Browser component
 *
 * @Author : Mayank Sindwani
 * @Date   : 2017-09-22
 *
 * Description :
 **/

import React from 'react';
import axios from 'axios';

const FileBrowser = React.createClass({

    // Initial page state.
    getInitialState() {
        return {
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
                    root: path ? path : []
                });
            })
            .catch((error) => {
                console.log(error);
            });
    },

    onFolderNavClicked(i) {
        this.fetchFiles(this.state.root.slice(0, i));
    },

    onFileFolderClicked(fileFolder) {
        if (fileFolder.isDir) {
            this.fetchFiles([...this.state.root, fileFolder.filename]);
        }
    },

    render() {
        return (
            <div>
                <div className="search-bar">
                    <input
                        type="text"
                        placeholder="Search here..."
                        required />
                    <button type="submit">Search</button>
                </div>
                <div className="file-browser">
                    <div className="file-browser-nav">
                        {
                            [ 'home', ...this.state.root ].map((path, i) => {
                                return (
                                    <div key={i}
                                        onClick={() => { this.onFolderNavClicked(i); }}
                                        className="crum">
                                            { i > 0 && (<div className="crum-separator">/</div>) }
                                            <div className="crum-value">{path}</div>
                                    </div>
                                );
                            })
                        }
                    </div>
                    <div>
                        {
                            this.state.files.map((file, i) => {
                                return (
                                    <div key={i}
                                        onClick={() => { this.onFileFolderClicked(file); }}
                                        className="file-browser-cell">
                                            {file.isDir ?
                                                <i className="fa fa-folder"></i> :
                                                <i className="fa fa-file-o"></i>
                                            }
                                            {file.filename}
                                    </div>
                                );
                            })
                        }
                    </div>
                </div>
            </div>
        );
    }

});

export default FileBrowser;

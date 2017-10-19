/**
 * Sherlock File Viewer component
 *
 * @Author : Mayank Sindwani
 * @Date   : 2017-09-22
 *
 * Description :
 **/

import { Default } from 'react-syntax-highlighter/dist/styles';
import SyntaxHighlighter from 'react-syntax-highlighter';
import SherlockUtils from '../../common/util';
import React from 'react';
import axios from 'axios';

const FileViewer = React.createClass({

    getInitialState() {
        return {
            file: null
        }
    },

    componentDidMount() {
        this.fetchFile(this.props.file);
    },

    fetchFile(path) {
        // TODO: Handle invalid path
        const uri  = `/api/search/file?path=${path}`;
        axios.get(encodeURI(uri))
            .then((response) => {
                path = JSON.parse(path);
                this.setState({
                    file : response.data,
                    language : SherlockUtils.getFileExtension(path.pop())
                });
            })
            .catch((error) => {
                console.log(error);
            });
    },

    render() {
        return (
            <div className="file-viewer">
                { this.state.file !== null && (
                    <SyntaxHighlighter showLineNumbers={true} language={this.state.language} style={Default}>
                        { this.state.file }
                    </SyntaxHighlighter>
                )}
            </div>
        );
    }

});

export default FileViewer;

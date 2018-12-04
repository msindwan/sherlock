/**
 * Sherlock File Viewer component
 *
 * @Author : Mayank Sindwani
 * @Date   : 2017-09-22
 *
 * Description : Displays a file with syntax highlighting.
 **/

import { Default } from 'react-syntax-highlighter/dist/styles';
import SyntaxHighlighter from 'react-syntax-highlighter';
import React from 'react';

const FileViewer = ({ file, language }) => (
    <div className="file-viewer">
        <SyntaxHighlighter showLineNumbers={true} language={language} style={Default}>
            { file }
        </SyntaxHighlighter>
    </div>
);

export default FileViewer;

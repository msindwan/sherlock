/**
 * Sherlock Browser component
 *
 * @Author : Mayank Sindwani
 * @Date   : 2017-09-22
 *
 * Description : Displays the contents of a folder.
 **/

import React from 'react';

const FileBrowser = ({ files, onFileFolderClicked }) => (
    <div>
    {
        files.map((file, i) => {
            return (
                <div key={i}
                    onClick={e => { onFileFolderClicked(e, i); }}
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
);

export default FileBrowser;

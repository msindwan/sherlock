/**
 * Sherlock Crums component
 *
 * @Author : Mayank Sindwani
 * @Date   : 2017-09-22
 *
 * Description : Displays bread crums for file paths.
 **/

import React from 'react';

const Crums = ({ path, onCrumClick, isFile }) => (
    <div className="crums-nav">
    {
        path.map((crum, i) => {
            return (
                <div key={i}
                    onClick={(e) => {
                        if (!isFile || i < path.length - 1) {
                            onCrumClick(e, i);
                        }
                    }}
                    className={`crum ${isFile && i == path.length - 1 ? '' : 'crum-folder'}`}>
                        { i > 0 && (<div className="crum-separator">/</div>) }
                        <div className="crum-value">{crum}</div>
                </div>
            );
        })
    }
    </div>
);

export default Crums;

/**
 * Sherlock Crums component
 *
 * @Author : Mayank Sindwani
 * @Date   : 2017-09-22
 *
 * Description :
 **/

import React from 'react';

const Crums = ({ path, onCrumClick }) => (
    <div className="crums-nav">
    {
        path.map((crum, i) => {
            return (
                <div key={i}
                    onClick={(e) => { onCrumClick(e, i); }}
                    className="crum">
                        { i > 0 && (<div className="crum-separator">/</div>) }
                        <div className="crum-value">{crum}</div>
                </div>
            );
        })
    }
    </div>
);

export default Crums;

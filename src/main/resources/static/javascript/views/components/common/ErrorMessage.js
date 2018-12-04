/**
 * Sherlock Error component
 *
 * @Author : Mayank Sindwani
 * @Date   : 2017-09-22
 *
 * Description : App Error.
 **/

import React from 'react';

const ErrorMessage = ({ message }) => (
    <div className="error">
        <div className="title">
            <i className="fa fa-warning"></i>
            <span className="message">{ message }</span>
        </div>
    </div>
);

export default ErrorMessage;

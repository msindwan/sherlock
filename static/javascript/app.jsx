/**
 * Sherlock Javascript Entry Point
 *
 * @Author : Mayank Sindwani
 * @Date   : 2017-09-18
 *
 * Description : Defines the entry point for the app.
 **/

import ReactDOM from 'react-dom';
import React from 'react';

// App entry point.
window.addEventListener("load", function() {
    ReactDOM.render((
        <div>Hello World!</div>
    ), document.getElementById("app"));
});

/**
 * Sherlock Javascript Entry Point
 *
 * @Author : Mayank Sindwani
 * @Date   : 2017-09-18
 *
 * Description : Defines the entry point for the app.
 **/

import { BrowserRouter as Router, Route, Switch } from 'react-router-dom';
import NotFoundContainer from './views/containers/NotFoundContainer';
import SearchContainer from './views/containers/SearchContainer';
import ReactDOM from 'react-dom';
import React from 'react';

// App entry point.
window.addEventListener("load", function() {
    ReactDOM.render((
        <Router>
            <Switch>
                <Route exact path="/" component={SearchContainer}/>
                <Route component={NotFoundContainer}/>
            </Switch>
        </Router>
    ), document.getElementById("app"));
});

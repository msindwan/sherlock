/**
 * Sherlock Javascript Entry Point
 *
 * @Author : Mayank Sindwani
 * @Date   : 2017-09-18
 *
 * Description : Defines the entry point for the app.
 **/

import { BrowserRouter as Router, Route } from 'react-router-dom';
import SearchContainer from './views/containers/search';
import ReactDOM from 'react-dom';
import React from 'react';

// App entry point.
window.addEventListener("load", function() {
   ReactDOM.render((
      <Router >
         <Route exact path="/" component={SearchContainer}></Route>
      </Router>
   ), document.getElementById("app"));
});

/**
 * Sherlock Javascript Entry Point
 *
 * @Author : Mayank Sindwani
 * @Date   : 2017-09-18
 *
 * Description : Defines the entry point for the app.
 **/

import { BrowserRouter as Router, Route, Switch } from 'react-router-dom';
import SearchContainer from './views/containers/SearchContainer';
import TermsContainer from './views/containers/TermsContainer';
import ReactDOM from 'react-dom';
import React from 'react';

// App entry point.
window.addEventListener("load", function() {
   ReactDOM.render((
      <Router>
          <Switch>
              <Route exact path="/" component={SearchContainer}/>
              <Route exact path="/terms" component={TermsContainer}/>
          </Switch>
      </Router>
   ), document.getElementById("app"));
});

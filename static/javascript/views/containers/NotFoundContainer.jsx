/**
 * Sherlock NotFoundContainer
 *
 * @Author : Mayank Sindwani
 * @Date   : 2017-09-22
 *
 * Description : Application 404 container.
 **/

import Header from '../components/common/Header';
import Footer from '../components/common/Footer';
import React from 'react';

const NotFoundContainer = () => (
    <div className="container">
        <Header />
        <div className="error-container">
            <div className="code">404</div>
            <div className="message">The requested resource was not found</div>
        </div>
        <Footer />
    </div>
);

export default NotFoundContainer;

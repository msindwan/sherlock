/**
 * Sherlock SearchContainer
 *
 * @Author : Mayank Sindwani
 * @Date   : 2017-09-22
 *
 * Description : Main application container.
 **/

import Browser from '../components/browser/Browser';
import Header from '../components/common/Header';
import Footer from '../components/common/Footer';
import React from 'react';

const SearchContainer = () => (
    <div className="container">
        <Header />
        <Browser />
        <Footer />
    </div>
);

export default SearchContainer;

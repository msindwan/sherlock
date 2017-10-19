/**
 * Search Container
 *
 * @Description: Creates a container to house the search view.
 * @Date: 2017-09-222
 * @Author: Mayank Sindwani
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

/**
 * Search Container
 *
 * @Description: Creates a container to house the search view.
 * @Date: 2017-09-222
 * @Author: Mayank Sindwani
 **/

import FileBrowser from '../components/file_browser';
import Header from '../components/header';
import Footer from '../components/footer';
import React from 'react';

const SearchContainer = () => (
    <div className="container">
        <Header />
        <FileBrowser />
        <Footer />
    </div>
);

export default SearchContainer;

/**
 * Copyright 2018 Mayank Sindwani
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Webpack Config
 *
 * @Date : 2018-12-04
 * @Description : Webpack configuration for all builds.
 **/

const MiniCssExtractPlugin = require("mini-css-extract-plugin");
const WriteFilePlugin = require('write-file-webpack-plugin');
const path = require('path');

const extractSass = new MiniCssExtractPlugin({
    filename: "app.css",
    chunkFilename: "[id].css"
});

module.exports = {
    devtool: 'source-map',
    entry: [
        // App
        path.join(__dirname, 'src', 'main', 'resources', 'static', 'javascript', 'App.js'),
        path.join(__dirname, 'src', 'main', 'resources', 'static', 'sass', 'app.scss'),
    ],

    output: {
        filename: "app.js",
        path: path.join(__dirname,  'src', 'main', 'resources', 'static', 'dist'),
    },

    module: {
        rules: [{
            test: /\.js?$/,
            exclude: /node_modules/,
            use: {
                loader: 'babel-loader',
                options: {
                  presets: ['@babel/preset-env']
                }
            }
        }, {
            test: /\.js?$/,
            exclude: /node_modules/,
            use: {
                loader: 'eslint-loader',
                options: {
                    presets: ['@babel/preset-env'],
                    fix: true
                }
            }
        }, {
            test: /\.(ttf|eot|svg)(\?v=[0-9]\.[0-9]\.[0-9])?$/,
            use: [{
                loader: 'file-loader',
                options: {
                    name:'fonts/[hash].[ext]'
                }
            }]
        }, {
            test: /\.(sa|sc|c)ss$/,
            use: [
              MiniCssExtractPlugin.loader,
              'css-loader',
              'sass-loader',
            ],
        }]
    },
    plugins: [
        extractSass,
        new WriteFilePlugin()
    ]
};

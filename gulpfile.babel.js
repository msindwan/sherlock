/**
 * Sherlock Gulpfile
 *
 * @Author : Mayank Sindwani
 * @Date   : 2017-09-18
 *
 * Description : Tasks for building Sherlock front-end assets.
 **/

import source from 'vinyl-source-stream';
import gplugins from 'gulp-load-plugins';
import browserify from 'browserify';
import buffer from 'vinyl-buffer';
import babelify from 'babelify';
import watchify from 'watchify';
import help from 'gulp-help';
import tasks from 'gulp';
import del from 'del';

let gulp = help(tasks);
let plugins = gplugins();

// Application Config Properties
const IS_PRODUCTION = process.env.NODE_ENV === 'production';
const JS_FILES = 'static/javascript/**/*.{js, jsx}';
const JS_ENTRY = 'static/javascript/app.jsx';
const SASS_FILES = 'static/sass/**/*.scss';
const SASS_ENTRY = 'static/sass/app.scss';
const DIST = 'src/main/resources/dist';
const INDEX = 'static/index.html';

gulp.task('js', 'Builds the javascript files.', () => {
    // Bundles the javascript into a single module.
    let b = browserify({
      cache: {},
      debug: !IS_PRODUCTION,
      entries: JS_ENTRY,
      extensions: ['.js', '.jsx'],
      fullPaths: false,
      packageCache: {},
      transform: [ babelify ]
    });

    const bundle = function (files) {

        plugins.util.log("Starting '"
            + plugins.util.colors.cyan('browserify') + "' "
            + plugins.util.colors.magenta((files ? ' => ' + files : '')));

        return b.bundle()
            .on('error', function (err) {
                plugins.util.log(plugins.util.colors.red(err.message));
                this.emit('end');
            })
            .pipe(source('app.js'))
            .pipe(buffer())
            // Map bundled files to their original source code.
            .pipe(plugins.if(!IS_PRODUCTION, plugins.sourcemaps.init({
                loadMaps: true
            })))
            // Minify if in deployment mode.
            .pipe(plugins.if(IS_PRODUCTION, plugins.uglify()))
            // Output the result to the distribution folder.
            .pipe(plugins.if(IS_PRODUCTION, plugins.sourcemaps.write('./')))
            .pipe(gulp.dest(DIST))
    };

    if (!IS_PRODUCTION) {
        // Rebunde the javascript on update.
        b = watchify(b)
        .on('update', bundle)
        .on('log', function (msg) {
            plugins.util.log("Finished '"
                + plugins.util.colors.cyan('browserify')
                + "' "
                + msg);
            plugins.util.log("Reload the browser to view changes.");
        });
    }

    return bundle();
});

gulp.task('css', 'Builds the css files.', () => {
    return gulp.src(SASS_ENTRY)
        .pipe(plugins.sass({
            includePaths: [
                //'node_modules/bootstrap-sass/assets/stylesheets',
                //'node_modules/font-awesome/scss',
                SASS_ENTRY
            ]
        }))
        .on('error', function (err) {
            plugins.util.log(plugins.util.colors.red(err.message));
            this.emit('end');
        })
        // Minification
        .pipe(plugins.if(IS_PRODUCTION, plugins.cleanCss()))
        .pipe(gulp.dest(DIST));
});

gulp.task('lint', 'Lints all of the modules', () => {
    // Run sass lint on the provided sass files.
    gulp.src(SASS_FILES)
        .pipe(plugins.eol("\n"))
        .pipe(plugins.sassLint())
        .pipe(plugins.sassLint.format())
        .pipe(plugins.sassLint.failOnError())

    // Run eslint on the provided js(x) files.
    gulp.src(JS_FILES)
        .pipe(plugins.eslint())
        .pipe(plugins.eslint.format())
        .pipe(plugins.eslint.failAfterError());
});

gulp.task('test', 'Tests all of the modules.', ['test:build'], () => {
    // TODO
});

gulp.task('clean', 'Cleans the build folder', (cb) => {
   // Delete the build folder.
   return del(
      DIST, cb
   );
});

gulp.task('default', 'Builds all of the modules.', ['js', 'css'], () => {
    gulp.src(INDEX).pipe(gulp.dest(DIST))
    if (!IS_PRODUCTION) {
        return gulp.watch(
            SASS_FILES, ['css']
        );
    }
});

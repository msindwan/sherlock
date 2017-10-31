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
import child_process from 'child_process';
import browserify from 'browserify';
import buffer from 'vinyl-buffer';
import babelify from 'babelify';
import help from 'gulp-help';
import tasks from 'gulp';
import del from 'del';

let gulp = help(tasks);
let plugins = gplugins();

// Application Config Properties
const IS_PRODUCTION = process.env.NODE_ENV === 'production';
const SERVER_FILES = 'src/main/**/*';
const JS_FILES = 'static/javascript/**/*.{js,jsx}';
const JS_ENTRY = 'static/javascript/App.jsx';
const SASS_FILES = 'static/sass/**/*.scss';
const SASS_ENTRY = 'static/sass/app.scss';
const DIST = 'src/main/resources/dist';
const INDEX = 'static/index.html';
const EXT = 'static/ext/';
let server = null;

gulp.task('js', 'Builds the javascript files.', () => {
    // Bundles the javascript into a single module.
    const b = browserify({
      cache: {},
      debug: !IS_PRODUCTION,
      entries: JS_ENTRY,
      extensions: ['.js', '.jsx'],
      fullPaths: false,
      packageCache: {},
      transform: [ babelify ]
    });

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
        .pipe(plugins.if(!IS_PRODUCTION, plugins.sourcemaps.write('./')))
        .pipe(gulp.dest(DIST));
});

gulp.task('css', 'Builds the css files.', () => {
    return gulp.src(SASS_ENTRY)
        .pipe(plugins.sass({
            includePaths: [ SASS_ENTRY ]
        }))
        .on('error', function (err) {
            plugins.util.log(plugins.util.colors.red(err.message));
            this.emit('end');
        })
        // Minification
        .pipe(plugins.if(IS_PRODUCTION, plugins.cleanCss()))
        .pipe(gulp.dest(DIST));
});

gulp.task('server', 'Builds the server.', ['js', 'css'], () => {
    if (IS_PRODUCTION) {
        const compiler = child_process.exec('mvn compile', (error) => {
            if (!error) {
                // TODO: deploy
            }
        });
        compiler.stdout.pipe(process.stdout);
        compiler.stderr.pipe(process.stderr);
    }
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

gulp.task('test', 'Tests all of the modules.', () => {
    // TODO
});

gulp.task('clean', 'Cleans the build folder', (cb) => {
   // Delete the build folder.
   return del(DIST, cb);
});

gulp.task('default', 'Builds all of the modules.', ['server'], () => {
    gulp.src([EXT + "/fonts/**/*"])
        .pipe(gulp.dest(DIST + "/fonts"));

    gulp.src([EXT + "/font-awesome-4.7.0/**/*"])
        .pipe(gulp.dest(DIST + "/font-awesome-4.7.0"));

    gulp.src([INDEX]).pipe(gulp.dest(DIST));
    if (!IS_PRODUCTION) {
        gulp.watch(SASS_FILES, ['css']);
        gulp.watch(JS_FILES, ['js']);
    }
});

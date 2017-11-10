# Sherlock

Inspired by [Opengrok](https://opengrok.github.io/OpenGrok/), Sherlock is a tool that exposes a web interface for
searching files over a network. It aims to be minimalistic, reliable, and performance-driven by providing a simple user
interface with core features needed to quickly search for keyword arguments.

**By using Sherlock you agree to the [terms of use and privacy policy](
    https://github.com/msindwan/sherlock/wiki/Terms-of-Use-and-Privacy-Policy)**

## Deployment

Sherlock requires Java 7 or higher. You can download the latest jar file
[here](https://github.com/msindwan/sherlock/releases). You will also need to index files before you can start
Sherlock in order to optimize search speeds.

To get started, first index the desired root directory by running

```bash
 java -cp sherlock-${SHERLOCK_VERSION}.jar io.sherlock.index.FileIndexer -w -t <target_directory_path> -o <output_directory_path>
```

The `-w` argument watches for directory changes and automatically updates the indexes. Use the `-h` option for help on
the list of input parameters and their descriptions.

Once the directory of indexes is available, start the server by running

```bash
 java -cp sherlock-${SHERLOCK_VERSION}.jar io.sherlock.core.Server -r <root_directory_path> -i <indexes_directory_path>
```

The `root_directory_path` is where files are served from and the `indexes_directory_path` is the directory where the
file indexer saved the corresponding indexes to. Use the `-h` option for help on the list of input parameters and their
descriptions.

### Sherlock File

In the root directory where files are served from, you can create a `.sherlock` file that lists extensions to ignore
both for indexing and browsing. Each extension should exist on a separate line.
E.g:

```
.class
.ignore
.sample
```

## Development
Development requirements include

* Java 7 or higher
* Maven v3.5.0 or higher
* Node.js v6.10.0 or higher

To start development against **the FileIndexer and the Server**:

1. From the root directory, run `mvn install` if you haven't already.
2. Run `mvn compile` to compile your source code.
3. Run `mvn exec:java -Dexec.mainClass="io.sherlock.index.FileIndexer" -Dexec.args="..."` to start the FileIndexer.
4. Run `mvn exec:java -Dexec.mainClass="io.sherlock.core.Server" -Dexec.args="..."` to start the server.

To start development on the **front-end assets**:

1. Run `npm install` in the root directory.
2. Run `gulp` to build and watch front-end assets (Note that you'll need to restart the server and clear the cache when front-end assets change).

To build Sherlock:

1. Run `gulp` with `NODE_ENV` set to `production` to build front-end assets.
2. Run `mvn package`.

### Testing and liniting

TODO

## Third-party Libraries Used

Sherlock is built on incredibly robust and well-designed third-party libraries:

| Library     | Licence                     | Link                                           |
|-------------|-----------------------------|------------------------------------------------|
| vertx       | Apache License, Version 2.0 | http://vertx.io/                               |
| lucene      | Apache License, Version 2.0 | https://lucene.apache.org/                     |
| commons-cli | Apache License, Version 2.0 | https://commons.apache.org/proper/commons-cli/ |
| slf4j       | MIT                         | https://www.slf4j.org/                         |

## License

Sherlock is licensed under the Apache License, Version 2.0

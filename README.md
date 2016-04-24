# sbt-versioninfo

This sample plugin generates a `version.json` file with details about your build version including the latest git commits
and a report on certain dependencies.
Versioning itself is handled by plugins such as `GitVersioning`.

### Dependency Report

`net.virtual-void:sbt-dependency-graph` is a well known and super helpful cmd line tool. But it's actually much more than that:

The data model of `sbt-dependency-graph` provides a powerful, but yet simple way to programatically generate insights from your dependencies.

This plugin illustrates the usage by extracting and aggregating versions of your organization's dependencies based on a name pattern,
such as shared libraries (*commons* in the example below) or client libraries to identify remote dependency.

### Example usage

```scala
enablePlugins(VersionInfoPlugin)

versionDependencyPattern := Some("(avro-schema|commons(?:-\\w+))?_.*".r)
versionClientNamePattern := Some("(\\w+)-client_.*".r)
```

```json
{
    "name": "comment-service",
    "version": "0.1.1",
    "versionDate": "20160424T192423",
    "remoteDependencies": {
        "user-service": "0.5.9",
        "user-preference-service": "1.0.24"
    },
    "dependencies": {
        "avro-schema": [ "4.1.1" ],
        "commons": [ "1.0.31", "1.0.44" ]
    },
    "git": {
        "head": "23c89eaf02e6c84045c2be2c4778f4e5b315fa97",
        "lastCommits": [
            {
                "author": "mosche",
                "date": "20160424T192424",
                "message": "commit message"
            }
        ]
    }
}
```
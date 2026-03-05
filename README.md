# md-serve

A Quarkus-based HTTP server that serves Markdown files from a configured directory as rendered HTML.

## Features

- Serves Markdown files as HTML via configurable Handlebars template
- Default template included out of the box
- Configurable source directory and template path

## Configuration

| Property | Description | Default |
|---|---|---|
| `md-serve.source-dir` | Directory containing Markdown files | `./docs` |
| `md-serve.template` | Path to Handlebars template file | built-in default |

## Running

```sh
./mvnw quarkus:dev
```

## Docs

- [User Documentation](docs/user/)
- [Developer Documentation](docs/dev/)

# Getting Started

md-serve is a small HTTP server that reads Markdown files from a directory on disk and serves them as styled HTML pages in your browser. Point it at any folder of `.md` files and you have an instant local documentation site — no build step, no static site generator.

## 1. Build the server

You need Java 21. Clone the repository and build once:

```sh
git clone <repository-url>
cd md-serve
./mvnw package -q
```

## 2. Run

```sh
./mvnw quarkus:dev
```

The server starts on `http://localhost:8080`. By default it serves the `./docs` directory relative to where you run the command.

To serve a different directory, set the `md-serve.source-dir` property:

```sh
./mvnw quarkus:dev -Dmd-serve.source-dir=/path/to/your/markdown/files
```

Or add it to `src/main/resources/application.properties`:

```properties
md-serve.source-dir=/path/to/your/markdown/files
```

## 3. Open your browser

Navigate to `http://localhost:8080`. If your source directory contains a `README.md` or `index.md`, open it directly at `http://localhost:8080/README` or `http://localhost:8080/index`.

Subdirectories are listed automatically. Clicking a directory shows the files it contains; clicking a file renders it as HTML.

## Minimal example

Create a directory with one file:

```
my-docs/
  hello.md
```

`hello.md`:

```markdown
# Hello, world

This is my first md-serve page.
```

Run:

```sh
./mvnw quarkus:dev -Dmd-serve.source-dir=./my-docs
```

Open `http://localhost:8080/hello` — you will see the rendered page with a styled heading and paragraph.

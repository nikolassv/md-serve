# Getting Started

md-serve is a small HTTP server that reads Markdown files from a directory on disk and serves them as styled HTML pages in your browser. Point it at any folder of `.md` files and you have an instant local documentation site — no build step, no static site generator.

## 1. Download a pre-built release (recommended)

Pre-built artifacts are available on the [GitHub Releases page](https://github.com/nikolassv/md-serve/releases):

| Archive | Platform | Contains |
|---|---|---|
| `md-serve.jar` | Any platform with Java 21 | run with `java -jar md-serve.jar` |
| `md-serve-linux-x86_64.tar.gz` | Linux x86_64 | `md-serve` |
| `md-serve-macos-x86_64.tar.gz` | macOS Intel | `md-serve` |
| `md-serve-macos-aarch64.tar.gz` | macOS Apple Silicon | `md-serve` |
| `md-serve-windows-x86_64.zip` | Windows x86_64 | `md-serve.exe` |

For native executables, extract the archive first:

```sh
# Linux / macOS
tar -xzf md-serve-linux-x86_64.tar.gz
chmod +x md-serve
```

Then jump to [Run](#3-run).

## 2. Build from source

You need Java 21. Clone the repository and build once:

```sh
git clone <repository-url>
cd md-serve
./mvnw package -q
```

## 3. Run

```sh
./mvnw quarkus:dev
```

The server starts on `http://localhost:8080`. By default, it serves the working directory where you run the command.

To serve a different directory, set the `md-serve.source-dir` property:

```sh
./mvnw quarkus:dev -Dmd-serve.source-dir=/path/to/your/markdown/files
```

Or add it to `src/main/resources/application.properties`:

```properties
md-serve.source-dir=/path/to/your/markdown/files
```

## 4. Open your browser

Navigate to `http://localhost:8080`. If your source directory contains a `README.md` or `index.md`, open it directly at `http://localhost:8080/README` or `http://localhost:8080/index`.

Subdirectories are listed automatically. Clicking a directory shows the files it contains; clicking a file renders it as HTML.

## 5. Minimal example

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

## 6. Custom templates

To customise the look of your pages, create `.hbs` (Handlebars) files in a `.md-serve/templates/` directory inside your source directory:

```
my-docs/
  .md-serve/
    templates/
      default.hbs    ← overrides the page template
      directory.hbs  ← overrides the directory listing template
      error.hbs      ← overrides the error page template
  hello.md
```

Any file you omit falls back to the bundled default. A minimal custom `default.hbs`:

```hbs
<!DOCTYPE html>
<html>
<head><title>{{title}}</title></head>
<body>
  {{{content}}}
</body>
</html>
```

See [configuration.md](configuration.md) for the full list of available template variables and the `treeNav` helper.

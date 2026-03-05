# Step 3: Path Resolution

## Goal

Safely map an arbitrary URL path to a file system path within the configured source directory. This component is the security boundary for file access.

## Implementation Plan

- Create `PathResolver` as an `@ApplicationScoped` bean injecting `MdServeConfig`
- Resolution logic:
  1. Normalize the URL path (collapse `..`, `.`, double slashes)
  2. Resolve against `source-dir` to get an absolute candidate path
  3. Verify the candidate path starts with the absolute `source-dir` path — reject otherwise
  4. If the candidate is a directory, return `DIRECTORY` result
  5. If the candidate is a file, return `FILE` result
  6. If the candidate does not exist but adding `.md` yields an existing file, return `FILE` result for the `.md` path
  7. Otherwise return `NOT_FOUND`
- Return type: a sealed result type or simple record with a `Kind` enum (`FILE`, `DIRECTORY`, `NOT_FOUND`) and the resolved `Path`

## Definition of Done

- Unit tests cover:
  - Normal file path resolves correctly
  - Path without `.md` extension resolves to `.md` file
  - Directory path is identified as `DIRECTORY`
  - Path with `..` traversal is rejected (`NOT_FOUND` or exception)
  - Path pointing outside source-dir is rejected
  - Non-existent path returns `NOT_FOUND`

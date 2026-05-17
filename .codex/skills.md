# Codex Skills

## Kotlin style rules

- Always use block bodies with braces (`{}`) for functions.
- Do not use expression bodies with `=` for functions.
- SQL and JPQL query strings must be multiline, never one line. For annotations such as `@Query`, use a triple-quoted string with one clause per line.
- Never clean the database in integration tests. Use unique random values for test data so tests do not depend on database cleanup.

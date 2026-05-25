# Codex Skills

## Kotlin style rules

- Always use block bodies with braces (`{}`) for functions.
- Do not use expression bodies with `=` for functions.
- SQL and JPQL query strings must be multiline, never one line. For annotations such as `@Query`, use a triple-quoted string with one clause per line.
- In the test source set, raw JDBC SQL must also use triple-quoted multiline strings with one clause per line, for example:
  ```kotlin
  """
      select id from refresh_tokens
      where user_id = ?
  """
  ```
- Never clean the database in integration tests. Use unique random values for test data so tests do not depend on database cleanup.
- Controller test names must include the expected HTTP status, written as `HTTP 200`, `HTTP 400`, `HTTP 401`, etc.

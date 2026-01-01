# Contributing to FHIR Authentication Library

Thank you for your interest in contributing to FHIR Authentication Library! This
document provides guidelines and instructions for contributing.

## Code of Conduct

This project adheres to
the [Contributor Covenant Code of Conduct](CODE_OF_CONDUCT.md).
By participating, you are expected to uphold this code. Please report
unacceptable behaviour to pathling@csiro.au.

## How to contribute

### Reporting bugs

Before creating a bug report, please check existing issues to avoid duplicates.
When creating a bug report, include:

- A clear, descriptive title
- Steps to reproduce the issue
- Expected behaviour vs actual behaviour
- Environment details (OS, language version, etc.)
- Any relevant logs or error messages

### Suggesting enhancements

Enhancement suggestions are welcome. When suggesting an enhancement:

- Use a clear, descriptive title
- Provide a detailed description of the proposed functionality
- Explain why this enhancement would be useful
- Consider any potential drawbacks or alternatives

### Pull requests

1. Fork the repository and create your branch from `main`.
2. If you've added code that should be tested, add tests.
3. Ensure the test suite passes.
4. Make sure your code follows the existing style conventions.
5. Write clear, descriptive commit messages.
6. Include relevant issue numbers in your PR description.

## Development setup

1. Clone the repository:

   ```bash
   git clone https://github.com/aehrc/fhir-auth-java.git
   cd fhir-auth-java
   ```

2. Ensure you have Java 11+ and Maven 3.6+ installed.

3. Build the project:
   ```bash
   mvn clean install
   ```

## Coding standards

- Follow existing code style conventions in the project.
- Use meaningful variable and method names.
- Add Javadoc comments to public classes and methods.
- Keep methods focused and concise.

## Testing

Run the test suite with:

```bash
mvn test
```

All new code should include appropriate unit tests.

## Licence

By contributing to FHIR Authentication Library, you agree that your
contributions will be licensed under the Apache License 2.0.

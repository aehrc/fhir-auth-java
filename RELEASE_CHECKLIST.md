# Release checklist

This document describes the release process for the FHIR Auth Client for Java.

## Prerequisites

Before performing a release, ensure the following are configured:

### GitHub secrets

The following secrets must be configured in the repository settings:

| Secret           | Description                                                                              |
| ---------------- | ---------------------------------------------------------------------------------------- |
| `GPG_KEY`        | Private GPG key for signing artifacts (exported with `gpg --armor --export-secret-keys`) |
| `GPG_PASSPHRASE` | Passphrase for the GPG key                                                               |
| `OSSRH_USERNAME` | Maven Central (Sonatype) account username                                                |
| `OSSRH_PASSWORD` | Maven Central (Sonatype) account password or token                                       |

### GitHub environment

A `maven-central` environment must be configured in the repository settings. The
release workflows require this environment for deployment protection.

### Local requirements

For manual release steps:

- Git with push access to the repository
- Access to create GitHub releases

## Release workflow

### 1. Pre-release checks

- [ ] All tests pass on the main branch
- [ ] The verify workflow completed successfully
- [ ] Code has been reviewed and approved
- [ ] Documentation is up to date

### 2. Update version

Update the version in `pom.xml` from SNAPSHOT to release version:

```xml
<!-- Change from -->
<version>1.0.1-SNAPSHOT</version>

<!-- To -->
<version>1.0.1</version>
```

Commit and push this change:

```bash
git add pom.xml
git commit -m "Prepare release 1.0.1"
git push origin main
```

### 3. Create release tag

Create and push a tag matching the pattern `vX.Y.Z`:

```bash
git tag v1.0.1
git push origin v1.0.1
```

This triggers the **draft-release** workflow, which creates a draft GitHub
release.

### 4. Review draft release

1. Navigate to the repository's Releases page on GitHub
2. Review the draft release created by the workflow
3. Edit the release notes if needed
4. When ready, click **Publish release**

### 5. Automatic deployment

Publishing the release triggers the **release** workflow, which:

1. Signs all artifacts with GPG
2. Deploys to Maven Central via the Central Publishing Plugin
3. Artifacts become available on Maven Central (may take up to 30 minutes to
   sync)

### 6. Post-release

Update `pom.xml` to the next SNAPSHOT version:

```xml
<version>1.0.2-SNAPSHOT</version>
```

Commit and push:

```bash
git add pom.xml
git commit -m "Prepare next development iteration"
git push origin main
```

### 7. Verify release

- [ ] Release appears on GitHub releases page
- [ ] Artifacts are available on
      [Maven Central](https://central.sonatype.com/artifact/au.csiro.fhir/fhir-auth)
- [ ] Release workflow completed successfully

## Snapshot releases

Snapshot releases allow testing pre-release versions. To deploy a snapshot:

1. Ensure `pom.xml` version ends with `-SNAPSHOT`
2. Go to Actions → Maven Central Snapshot Deployment
3. Click **Run workflow**

Snapshots are deployed to the Sonatype snapshot repository.

## Version format

| Type     | Format           | Example          |
| -------- | ---------------- | ---------------- |
| Release  | `X.Y.Z`          | `1.0.0`          |
| Snapshot | `X.Y.Z-SNAPSHOT` | `1.0.1-SNAPSHOT` |
| Tag      | `vX.Y.Z`         | `v1.0.0`         |

## Troubleshooting

### Draft release not created

- Verify the tag matches the pattern `v**` (e.g., `v1.0.0`)
- Check that `pom.xml` version is not a SNAPSHOT
- Review the draft-release workflow logs

### Deployment failed

- Check the release workflow logs for errors
- Verify all secrets are correctly configured
- Ensure the `maven-central` environment is properly set up
- Check that GPG key has not expired

### Artifacts not appearing on Maven Central

- Allow up to 30 minutes for sync
- Check the Central Publishing portal for deployment status
- Review workflow logs for publishing errors

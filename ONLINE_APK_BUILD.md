# Build Fuel Finder APK Online

This project includes `.github/workflows/build-apk.yml`.

## GitHub Actions
1. Create a GitHub repository and upload this project.
2. In repository Settings -> Secrets and variables -> Actions, add a secret named `MAPS_API_KEY`.
3. Open Actions -> Build Fuel Finder APK -> Run workflow.
4. When the job finishes, download the artifact `Fuel-Finder-debug-apk`.
5. Inside it is `app-debug.apk`.

The APK can compile without a real key by using the placeholder, but Maps/Places/Navigation will not work until a valid Google Maps Platform key is provided.

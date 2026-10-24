# My Delivery APK: mobile-only build guide

This project includes a GitHub Actions workflow that builds a debug APK online. You do not need a computer, but you do need a GitHub account and internet access.

1. On your phone, open https://github.com and sign in or create an account.
2. Create a new repository named `MyDeliveryAndroid`. Public is easiest for a first test; do not upload passwords, keys, or private data.
3. Extract this ZIP using your phone's file manager or an archive app.
4. Upload the *contents* of the `MyDeliveryAndroid` folder to the repository root. Include the hidden `.github/workflows/build-apk.yml` file. If the app/site hides dot-folders or cannot upload folders, use GitHub's web interface to create `.github/workflows/build-apk.yml` and paste the workflow content from this file.
5. Open the repository's **Actions** tab, select **Build My Delivery APK**, and tap **Run workflow** if it did not start automatically.
6. When the run finishes successfully, open that run and download the artifact named `My-Delivery-debug-APK`. GitHub may download it as a ZIP; extract it to get `app-debug.apk`.
7. Tap `app-debug.apk` to install. Android may ask you to allow installs from that browser or file manager. Only enable this for the app you trust, then turn it off again afterward.

Notes:
- This creates a **debug/test APK**, not a Play Store-ready signed release.
- Test bill totals, saved history, WhatsApp sharing, and UPI intent before relying on it.
- The UPI button only opens a payment request. It does not automatically verify that payment arrived.

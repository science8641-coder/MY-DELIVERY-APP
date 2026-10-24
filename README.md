# My Delivery Android App

A starter Android Studio project for a personal delivery bill maker.

## Included
- Customer name, item name, item price
- Custom delivery charge and optional extra expenses
- Total calculation
- Save bill history locally on the phone
- Share a bill using Android's share menu (WhatsApp can be selected)
- Launch an installed UPI app with a UPI payment intent
- Cash or UPI payment method label

## Important payment limitation
The UPI button opens a UPI app with a payment request. This starter app does not verify payment, collect card details, or include a payment gateway. Confirm payment in your UPI app before marking a bill paid. For automatic payment confirmation, integrate a compliant payment gateway and backend.

## Build in Android Studio
1. Install Android Studio on a computer.
2. Extract this ZIP and open the `MyDeliveryAndroid` folder in Android Studio.
3. Let Gradle sync and install the required Android SDK (compile SDK 35).
4. Connect an Android phone with USB debugging enabled, then press Run.
5. Test bill creation, share, history and UPI on your own device.

## Preparing for Google Play
- Set your final app name, icon, screenshots, privacy policy, and support contact.
- Create a signed Android App Bundle (.aab) from Android Studio: Build > Generate Signed Bundle / APK > Android App Bundle.
- Complete Play Console developer registration and app declarations. Google Play requirements and target API rules can change, so check current Play Console guidance before publishing.
- The project source ZIP is not itself a Play Store upload. It must be built, tested, signed, and reviewed first.

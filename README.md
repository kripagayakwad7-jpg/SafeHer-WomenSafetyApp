# SafeHer — Women Safety System

A native Android (Java + XML) personal safety application, backed by a Spring Boot + MySQL REST API.

Suggested folder layout on your machine:
```
Women Safety System/
├── WomenSafetyApp/        ← Android app (Java + XML)
└── WomenSafetyBackend/    ← Spring Boot backend
```

Open the `WomenSafetyApp` folder directly in **Android Studio** (not plain IntelliJ IDEA — it lacks the Android SDK/tooling this project needs). File → Open → select this folder.

---

## 1. What's included

### Core
- **Authentication** — register, login, session persistence
- **Editable Profile** — name, email, phone, and a profile photo (compressed and stored in the database as Base64, no separate file storage needed)
- **Trusted Contacts** — add, call, delete, and **drag-to-reorder by priority**; the top contact is dialed first during an SOS
- **SOS System**:
  - Large SOS button + shake-to-trigger (3 shakes, accelerometer-based)
  - 5-second countdown with cancel option, plus vibration feedback
  - Sends SMS with a live Google Maps link to all trusted contacts
  - Auto-calls the highest-priority contact;
  - Automatically starts continuous live location sharing when triggered
- **SOS History** — chronological list of past events; tap to view on Google Maps; mark each as **Active / Resolved / False Alarm**

### Guardian Mode & Live Location Sharing
Any registered account can act as a **Guardian** for someone else, using the same login — no separate app or role needed.
- **Share My Live Location** toggle on Home runs a foreground service (with a persistent notification, as Android requires) that posts GPS location to the backend every ~15 seconds
- When adding a trusted contact, you can optionally enter **their registered email** — if it matches an account, that person can open **Guardian Mode** and see your live status
- **Guardian Dashboard** — lists everyone who's added you as a trusted contact, with a live/offline indicator
- **Live Location screen** — auto-refreshing coordinates with a one-tap "Open in Google Maps" button

### Everyday Safety Tools
- **Emergency Numbers** — one-tap dial (via `ACTION_DIAL`, so nothing calls without your confirmation) for Police (100), Ambulance (108), Fire (101), and India's unified emergency number (112)
- **Fake Incoming Call** — tap, wait 3 seconds (time to put the phone away), then a realistic full-screen incoming call appears with ringtone + vibration; "Answer" leads to a fake in-call screen with a live timer — a discreet way to exit an uncomfortable situation


### Polish
- **Dark mode toggle** (Settings) — applies app-wide via `values-night` resources and `AppCompatDelegate`
- **Animated splash screen** — fade/scale-in shield and title
- Custom app icon, consistent Material Design visual language (gradients, elevated cards, rounded inputs) throughout

---

## 2. Project structure

```
WomenSafetyApp/
├── app/src/main/java/com/womensafety/app/
│   ├── SafeHerApplication.java   (applies saved dark-mode preference on launch)
│   ├── activities/                (all screens)
│   ├── adapters/                  (RecyclerView adapters: contacts, SOS history, wards)
│   ├── models/                    (User, Contact, SOS)
│   ├── api/                       (Retrofit service + request/response DTOs)
│   └── utils/                     (SessionManager, ShakeDetector, LocationHelper, SmsHelper,
│                                    ImageUtils, LocationSharingService)
├── app/src/main/res/
│   ├── layout/                    (one XML per screen + list-item layouts)
│   ├── drawable/                  (gradients, icons, SOS button art)
│   ├── values/ + values-night/    (colors, strings, themes — light and dark)
│   └── mipmap*/                   (app icon)
└── app/src/main/AndroidManifest.xml
```

---

## 3. One-time setup

> **Used Android Studio, not plain IntelliJ IDEA.** They share an editor core, but plain
> IntelliJ lacks the Android SDK and Gradle Android Plugin support this project needs.

1. **Open in Android Studio**, let Gradle sync (downloads Retrofit, Material Components, Glide, CircleImageView, Play Services Location/Maps, etc. — first sync can take several minutes).

2. **Backend integration is wired to match the Spring Boot backend exactly** — endpoints, request/response shapes, and field names in `api/ApiService.java` and `api/*.java` all mirror the backend's controllers and DTOs (the `ApiResponse<T>{success,message,data}` wrapper, `userId` passed via URL path/body, `sosDate`/`sosTime`, `priority`, `status`, etc.). If the backend's DTOs change, update the matching class here — they're named the same for easy comparison.

3. **Point it at your backend.** Open `api/RetrofitClient.java` and set `BASE_URL`:
   - **Android emulator**, backend running on the same PC: `http://10.0.2.2:8080/api/`
   - **Real phone, same Wi-Fi as your PC**: your PC's LAN IP, e.g. `http://192.168.1.5:8080/api/` (find it via `ipconfig`)
   - **Real phone, no shared Wi-Fi available**: use a USB cable + `adb reverse tcp:8080 tcp:8080`, then set `BASE_URL` to `http://localhost:8080/api/` (must be re-run each time the phone reconnects)
   - **Deployed backend**: your real server URL, e.g. `https://api.yourdomain.com/api/`

4. **Run your Spring Boot backend first.** Make sure MySQL is running (the backend's `application.properties` has `createDatabaseIfNotExist=true`, so the database itself is created automatically). Start the Spring Boot app so it's listening on port 8080 before launching the Android app.

---

## 4. Permissions

Declared in the manifest: `INTERNET`, `ACCESS_FINE_LOCATION`, `ACCESS_COARSE_LOCATION`, `SEND_SMS`, `CALL_PHONE`, `READ_CONTACTS`, `FOREGROUND_SERVICE`, `FOREGROUND_SERVICE_LOCATION`, `POST_NOTIFICATIONS`, `VIBRATE`.

Location, SMS, and Call permissions are requested at runtime (`SOSActivity` and the location-sharing toggle handle this automatically). Test SMS, real calls, live GPS, and shake detection on a **real device** — the emulator can't send real SMS or place real calls, and its GPS must be set manually (Emulator → Extended Controls → Location).

---

## 5. Where to customize

| Want to change...                    | Edit this file                                    |
|----------------------------------------|----------------------------------------------------|
| Colors / gradient (light & dark)       | `res/values/colors.xml`, `res/values-night/colors.xml` |
| App name, button text                  | `res/values/strings.xml`                           |
| Button/input/card shapes               | `res/values/themes.xml`                             |
| SOS countdown duration (currently 5s)  | `SOSActivity.java` → `startCountdown()`             |
| Shake sensitivity / shake count        | `utils/ShakeDetector.java`                          |
| SOS message wording                    | `utils/SmsHelper.java`                              |
| Live-location update frequency (15s)   | `utils/LocationSharingService.java`                 |
| Emergency numbers (100/108/101/112)    | `activity_home.xml` + `HomeActivity.java`           |
| Backend URL                            | `api/RetrofitClient.java`                           |

---

## 6. Known simplifications (intentional, room to extend)

- **"Call Next Contact" is manual**, not automatic — Android doesn't reliably expose whether a call was actually answered to third-party apps, so auto-redialing on "no answer" isn't something that can be built safely without a third-party telephony SDK.
- **Guardian Mode updates via polling** (every 8–10s while open), not real push notifications. Adding Firebase Cloud Messaging would let a Guardian's phone buzz instantly even with the app closed — a natural next step.
- **Live Location links out to Google Maps** rather than showing an embedded map in-app; the Maps SDK dependency is already included if you want to build that out.
- **No in-app VoIP calling** — real phone calls (`ACTION_CALL`) are used instead, since in-app calling would require a third-party SDK (e.g. Agora, Twilio).
- **This build only works while your backend is running and reachable** — there's no offline mode, and the backend isn't deployed to the cloud, so your PC needs to be on and running the Spring Boot app during any real-device testing or demo.

---

## 7. Testing on a real device

1. Generate a build: **Run ▶** directly (debug) for iterative testing, or **Build → Generate Signed Bundle / APK** for a proper release build.
2. Make sure MySQL + backend are running, and your phone can reach `BASE_URL` (see Section 3).
3. Suggested pass: register → edit profile + photo → add/reorder contacts → trigger SOS (button and shake) → check History status marking → toggle live location sharing → check Guardian Mode from a second account → test emergency numbers, fake call, dark mode.

## 8. Building a signed release APK

**Build → Generate Signed Bundle / APK → APK → Create new... keystore** (save the `.jks` file and both passwords somewhere safe — the *same* keystore is required for all future updates to this app). Select the **release** build variant, both V1 and V2 signature versions, then **Finish**. Output lands at `app/release/app-release.apk`.

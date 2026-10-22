# Subybara

**Subscription Tracker | MO-IT114 Mobile Development Fundamentals**

Subybara is an Android subscription tracker that helps users manage subscriptions, monitor upcoming renewals, and view estimated subscription costs in one place.

Subscriptions are entered manually. The app does not process payments or cancel subscriptions directly with service providers.

## Group Members

**Group 2 – H3101**

- Joyce Mariel Poblete
- Alexaire Raymundo
- Harty Joy Villegas

## Screenshots

| Welcome | Dashboard | Notifications | My Subscription |
|---|---|---|---|
| ![Welcome screen](docs/screenshots/welcome.png) | ![Dashboard](docs/screenshots/home.png) | ![Notifications](docs/screenshots/notifications.png) | ![My Subscription](docs/screenshots/subscriptions.png) |

| Subscription details | Undo after removing | Renewals | Insights |
|---|---|---|---|
| ![Subscription details](docs/screenshots/subscription_details.png) | ![Undo after removing a subscription](docs/screenshots/undo_delete.png) | ![Renewals](docs/screenshots/renewals.png) | ![Insights](docs/screenshots/insights.png) |

## Main Features

- **Account Access:** Welcome, Sign In, Sign Up, Forgot Password, and Profile screens with simulated authentication.
- **Subscription Management:** Add, edit, remove, search, and filter subscriptions. Users can also pause, resume, or cancel tracking.
- **Dashboard:** Estimated subscription costs, active subscriptions, upcoming renewals, and projected monthly bills.
- **Insights:** Six-month projected billing charts and estimated spending breakdown by category.
- **Upcoming Renewals:** Upcoming billing dates with search and date filters.
- **Notifications:** In-app reminders for active subscriptions renewing within seven days.
- **Empty States:** Guidance when users have no subscriptions or upcoming renewals.

## MS2 Demo Account

For evaluation, the app includes a demo account with seven sample subscriptions.

**Email:** `test@email.com`  
**Password:** `Test1234!`

From the Welcome screen, select **I Already Have an Account** to access Sign In.

The demo account contains sample subscriptions with different billing cycles, categories, and renewal dates. These populate the Dashboard, Insights, Renewals, and Notifications screens.

To test empty states, select **Get Started** and complete Sign Up using a different email address.

Account and subscription data are stored in memory and reset when the app process restarts.

## Technologies Used

- **Kotlin**
- **Jetpack Compose** and **Material 3**
- **Compose State Management**
- **Custom Compose Navigation**
- **Gradle** (Kotlin DSL)
- **JUnit 4**, **Compose UI Test**, and **Espresso**

**Minimum SDK:** API 24 (Android 7.0)  
**Target SDK:** API 36

## How to Run

### Requirements

- Android Studio with Android SDK support
- JDK 17 or newer
- Android emulator or physical device running Android 7.0 or newer

### Steps

1. Clone the repository:

   ```bash
   git clone https://github.com/MsYelkyy/Subybara-Subscription-Tracker-App.git
   ```

2. Open the project in Android Studio.
3. Allow Gradle to sync and install the required dependencies.
4. Select an emulator or connected Android device.
5. Click **Run** to launch Subybara.

No backend, database, or API configuration is required.


## Changes from MS1 Prototype to MS2

The Android implementation generally follows the approved MS1 Figma prototype. The following adjustments were made:

| Change | Reason |
|---|---|
| Spending Trend and Insights charts use projected monthly bills instead of historical spending | The app does not record actual payments, so forecasts are calculated from subscription billing details. |
| Chart month labels are generated dynamically | Display the current month and the next five months instead of the fixed months shown in Figma. |
| Added a demo account with sample subscriptions | Allow the mentor to explore populated screens while new users can still test empty states. |
| Some Profile settings remain nonfunctional placeholders | These settings are outside the approved MS1 use cases and are not part of the MS2 implementation scope. |

## Testing

The application includes:

- **Unit tests:** Subscription calculations, renewal dates, account validation, and data handling.
- **UI tests:** Account navigation, subscription management, notifications, Back navigation, and screen rotation.
- **Manual testing:** Navigation, screen behavior, and visual alignment with the approved Figma prototype.

To run automated checks:

```bash
# Unit tests (no device required)
./gradlew testDebugUnitTest

# UI tests (requires an emulator or connected device)
./gradlew connectedDebugAndroidTest

# Lint
./gradlew lintDebug
```

## Known Limitations

- **In-memory storage:** Account and subscription data reset when the app process restarts.
- **Simulated authentication:** No backend authentication or actual password recovery emails.
- **Estimated costs:** Charts project upcoming bills based on subscription details, not actual payment history.
- **In-app notifications only:** No scheduled push notifications.
- **Profile settings:** Some settings remain outside the implemented MS2 functionality.

---

**Subybara is developed for educational purposes as part of MO-IT114 Mobile Development Fundamentals.**

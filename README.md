# Flip Calendar Clock (Android)

A split-flap "flip" clock app for Android — black housing, white flap
cards, black bold text, with a seam line down the middle of each flap
like a real mechanical display. Includes:

- **The app**: hour, minute, day name, date, month, each card animates
  a real flip whenever its value changes.
- **A resizable home-screen widget**: drag it to any size on your home
  screen; it shows the same flap-card look and refreshes automatically.

## You do not need Android Studio. Here's how to get the app onto your phone.

### Step 1 — Put this project on GitHub (free, just a browser)

1. Go to [github.com](https://github.com) and make a free account if you
   don't have one.
2. Click the **+** in the top right → **New repository**. Name it
   anything (e.g. `flip-clock`). Leave it Public. Click **Create repository**.
3. On the new repo's page, click **uploading an existing file**.
4. Unzip `FlipCalendarClock.zip` on your computer, then drag the
   **contents** of the `FlipCalendarClock` folder (not the folder itself)
   into the GitHub upload box.
5. Scroll down, click **Commit changes**.

### Step 2 — Let GitHub build the APK for you

1. On your repo page, click the **Actions** tab.
2. You'll see a workflow called **Build APK** running automatically
   (it started the moment you uploaded the files). Click on it.
3. Wait 2–3 minutes for the green checkmark.
4. Scroll down to **Artifacts** and click
   **FlipCalendarClock-debug-apk** to download it. It's a zip containing
   `app-debug.apk`.

### Step 3 — Install it on your phone

1. Unzip that download to get `app-debug.apk`.
2. Transfer it to your Android phone (email it to yourself, use Google
   Drive, or a USB cable).
3. Tap the file on your phone. Android will ask to allow installs from
   this source the first time — allow it, then tap **Install**.
4. Open the app, or long-press your home screen → **Widgets** → find
   **Flip Clock** → drag it onto your home screen and resize it however
   you like.

That's it — no coding tools, no accounts beyond GitHub, nothing installed
on your computer.

## If you ever do want to open it in Android Studio

It's a completely standard Gradle project — **File > Open**, pick the
`FlipCalendarClock` folder, let it sync, click Run. No special setup.

## What's inside (for reference)

- `MainActivity.kt` — the app itself. `FlipCard` is the flip component:
  it clips a digit into top/bottom halves and rotates the top half down
  whenever the value changes, mimicking a real split-flap leaf turning.
- `FlipClockWidgetProvider.kt` + `widget_flip_clock.xml` — the home
  screen widget. Widgets can't run custom animations, so it shows a
  static flap-card snapshot and refreshes on a timer (roughly once a
  minute).
- Colors: black background (`Color.Black`), white flap cards
  (`cardBg`), black text (`cardText`) — all in `MainActivity.kt` if you
  want to adjust them; the widget's colors live in
  `widget_flip_clock.xml` and `flap_card_bg.xml`.

## Customizing

- **Font**: for a closer match to real flip-clock hardware, add a bold
  condensed font (like Eurostile or Bebas Neue) to `res/font/` and apply
  it via `FontFamily` in the `Text` composables.
- **Flip speed**: `durationMillis = 350` in `FlipCard`.
- **Widget refresh rate**: `scheduleNextTick` in
  `FlipClockWidgetProvider.kt`.
- **Keep screen on** (handy if you mount an old phone as a desk clock):
  already set via `android:keepScreenOn="true"` in the manifest.

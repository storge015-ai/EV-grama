EV - grama charge App - Setup Guide

MindMatrix VTU Internship Program | Project 72

What This App Does
EV - grama app will solve the problem of lack of EV charging station in rural area .

Post a need ("Need help with Ev customer and make a side income to shops/ homes ")

travller needs to open this app when charge is low ("need to choose nearest charging station by going through google map")

The hosts ( shop or home) they also need to open this app for one time registration with shop name , price per hour , address and contact number

once the traveller is acceptable in price and location he can confirm the slot and visit the shop with in 1 hrs ( slot is reserved )

after the charging process is completed than he can use the smart calculator avialable in App that will calculate hosts paying amount and travelling distance based on the charged completed



⚙️ SETUP STEPS (Required Before Running)

Step 1: Create Firebase Project

Go to https://console.firebase.google.com

Click "Add Project" → Name it "EV grama charge app"

Enable Authentication → Sign-in method → Email/Password

Enable Firestore Database → Start in test mode

Step 2: Add Android App to Firebase
In Firebase Console → Project Settings → Add App → Android
Package name: com.Ev-gramacharge
Download google-services.json
Replace the placeholder app/google-services.json with your real file
Step 3: Set Up Firestore Indexes
In Firestore Console → Indexes → Add these composite indexes:

Collection	Fields
slot_booking	status ASC, timestamp DESC
slot_booking  status ASC, Ev-gramaapp ASC, timestamp DESC
smart_calculator	fromUserId ASC, timestamp DESC
smart_calculator	toUserId ASC, timestamp DESC
Step 4: Open in Android Studio
Open Android Studio
File → Open → Select the EV-gramacharge folder
Wait for Gradle sync to complete
Run on emulator or device (minSdk 24 / Android 7.0+)
📁 Project Structure

EV-grama charge/
├── app/src/main/
│   ├── java/com/skillexchange/
│   │   ├── data/
│   │   │   ├── model/Models.kt          # traveller and seller, slot booking, smart charging, reviews status
│   │   │   └── repository/FirebaseRepository.kt  # All Firebase operations
│   │   └── ui/
│   │       ├── auth/
│   │       │   ├── LoginActivity.kt
│   │       │   └── RegisterActivity.kt
│   │       ├── home/
│   │       │   ├── MainActivity.kt      # Bottom nav host
│   │       │   ├── welcomescreen.kt      # Skill Board
│   │       │   ├── onboardingscreen.kt
│   │       │   ├── Hostsdetailscreen.kt
│   │       │   └── travellerdeatilscreen.kt
                    hostssucessScreen.kt
│   │       ├── slot booking/
│   │       │   ├── evgramamapscreen.kt      # My Swaps
│   │       │   ├── paycalculatocreen.kt
│   │       │   ├── shopselectionscreen.kt   # Negotiation chat
│   │       │   └── reviewScreen.kt
│   │       └── profile/
│   │           └── ProfileFragment.kt  # EV-gramacharge, charging Score
│   └── res/
│       ├── layout/                      # All XML layouts
│       ├── navigation/nav_graph.xml
│       ├── menu/bottom_nav_menu.xml
│       ├── drawable/                    # Chat bubbles
│       └── values/                      # Colors, themes, strings

✅ Success Criteria Met
adding details of traveller and hosts ✓
Connecting the two user based to details  ✓
Community-focused, simple UI ✓
Firestore "Ev grama app" with real-time capability ✓
Slot booking (1 hour = 1 Reserving) ✓
enabling the smart pay calculator ✓

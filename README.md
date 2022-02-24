# CyberScouter - Team 195 Scouting Workstation
This is an implementation for Android devices -- specifically Android 10 inch tablet devices in
landscape mode -- of an application used to scout FIRST robotics competitions.  Since the games
and rules change every year, this app should be expected to change every year.

There are several other components that this app must be used with.  The required components
depend on the mode the app is operating in:
- **On-site Mode** - this is intended to be the mode of operation at a FIRST-sponsored event at a public
venue.  Because wifi hotspots are not allowed at competition venues, this mode requires:
  - A local server with a bluetooth interface
  - A MariaDb database running on the local server with the proper schema
  - The bluetooth server python scripts built as a companion to this project
  
- **Off-site Mode** - this is intended to be the mode of operation for remote scouting (via video feed)
or during testing.  Since this does not take place at a FIRST venue, the tablet is free to join 
  any wifi hotspot and communicate on public networks.
  
Please note the following:
- This app running in the Android Studio emulator will always use Off-site Mode, since the emulator
is incapable of communicating through any bluetooth interface.

- This app running on an Android device will default to On-site Mode, and will seek out the
    bluetooth server by name.
      
- This app running on an Android device can be forced into Off-site Mode by placing a letter "a" at
the end of the device name (usually editable in Settings on the About page)
  - The Name in the AllianceStation table should not include this letter "a"
  - no AllianceStation name should ever end with the letter "a".
    

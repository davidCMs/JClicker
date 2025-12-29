## This is a autoclicker for linux because the only one i could find at the ime of writing did not support any kind of global shortcuts.

This project is also a spaggheti mess of ductaped code probobly abusing dbus in many ways but it works.

The ui is also a bit of a mess since i am using a gui lib that last got updated in 2006 which is before i was born... but on the bright side thanks to [flatlaf](https://www.formdev.com/flatlaf/)
it can look somewhat presentable. Its also not the best looking because i have absolutly no artistic ability whatsoever.

<img width="698" height="441" alt="image" src="https://github.com/user-attachments/assets/fa53dd51-5dcf-40d0-b2ec-004c7b3700e6" />


The delay controls have absolutly no checking and if you so wish you can set them to 0 which at least on my machine has a habit of almost crashing whatever i use it on so you have been warned!

The shortcuts are managed by whatever XDG portal implementation your using for me on KDE plasma you can change them under Settings>Keyboard>Shortcuts for other DE's i have no idea



## If anyone is so inclined the build instructions are:

Clone the repository and open up a terminal it its root.

Depending on how you want to build it it differs

- jar executalbe
   Type `./gradlew shadowJar` and wait for it to complete.
   Your jar should be at build/libs if there multiple you te correct one is the one that ends in -all.

- JPackage
   Type `./gradlew jPackage` and wait for it to complete.
   Your output should be at build/jPackage the executable is at build/jPackage/bin.

- AppImage
   Type `./gradlew buildAppImage-x86_64` and wait for it to complete.
   Your AppImage should be at build/appimage.

A note on aarch64 while there does exist the gradle task `buildAppImage-aarch64` i have no clue if it works.




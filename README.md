# Android_Activity-Recognition


In this assignment, you will build an activity recognition application for Android. The app should be able to distinguish three activities 
(1) laying down (sleeping) 
(2) sitting and 
(3) walking or running. 
You do not need to distinguish walking from running (both are considered to be the same activity). You can assume that the phone will always be in your pocket. If you are using a tablet, you can attach your tablet to your waist, if it is too large for your pocket. You are allowed to use the following sensors (1) accelerometer (2) gyroscope and (3) location (that could be GPS or network localization, it is upto you). Here are the specific requirements for the application.

(1) You will define an activity that will have a rudimentary UI (you can design any UI you want). The activity will display the last 1-10 activities that your application infers with timestamps. Your activity will also write to a file on external storage (you will have to attach a SD card to your tablet or phone) that will keep track of the activities that the system infers. An example of the file would be the following.

12:00 PM – 12:02 PM Walking 
12:02 PM – 12:04 PM Sitting

2) Note that the above file writes activities occurring in two minute intervals. Your main activity will spawn a service (bound service) that will be collecting the sensor data continuously (you are allowed to use an accelerometer, gyroscope, and location data). The service will chunk the data collected into 2 minute intervals and run an algorithm that determines the activity for that two minutes. The activities and the time period can be stored locally in the service in a data structure. The main activity at regular intervals should poll for the set of activities from the service and write it to external storage.
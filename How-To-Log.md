# How to generate log files and plot them

1. Run the main method in from our testbed, and select the LogWorld in the GUI.
2. Let the world run for the time you like, and then quit it (ESC). This should generate a positions.log file in the main root folder.
3. Set AutopilotFactory to generate a LogPilot, and export a jar to use in the provided testbed.
4. Collect the provided testbed, the generated jar and the joml jar from the lib folder in a the provided_testbed folder, and run the following command:
```
Windows: java -cp ProvidedTestbed.jar;joml-1.9.3.jar;[exported-jar].jar testbed.TestBedGUI
Other:   java -cp ProvidedTestbed.jar:joml-1.9.3.jar:[exported-jar].jar testbed.TestBedGUI
```
5. Run the provided testbed by setting time rate to 1, and quit after the same time you used in step 2. This generates a second positions.log file, but now in the other folder.
6. Now run the PlotPhysicsTests file, this wil plot x, y, z, heading, pitch and roll on 6 different graphs, with the time on the x-axis. It will only plot data if it has the value from both simulations so pay attention to the time you let them run.

### Non default settings

If you want to use other settings then the default, you have to change them in LogWorld before step 1, and in the provided testbed before you excecute step 5. Note that the provided testbed does not offer all posibilities we have in our testbed. Also pay attention if you change something in the AutopilotConfig of the LogWorld (not in startupGUI), you will have to change them everytime you run the provided testbed.

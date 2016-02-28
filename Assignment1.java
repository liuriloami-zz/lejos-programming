import lejos.nxt.*;

public class Assignment1 {
    public static void main (String[] args) {
        // State 1: Print "Assignment 1" on the screen
        System.out.println("Assignment 1");

        // State 2: Wait for a button to be pressed
        Button.waitForAnyPress();

        // State 3: Clear the screen
        LCD.clear();

        // TODO!
        // State 4: Wait for clapping and print "Claps" on the screen when it happens
        SoundSensor soundSensor = new SoundSensor(SensorPort.S1);
        while (!isClap()) {
            int soundLevel = soundSensor.readValue();
            processSound();
        }

        // TODO!
        // State 5: Move forward until detecting a hard surface underneath

        // State 6: Turn 90 degrees to the left
        pilot.rotate(-180);
        
        // State 7: Move forward until a hard surface is detected by sonar at less than 25 cm distante
        UltrasonicSensor ultrasonicSensor = new UltrasonicSensor(SensorPort.S2);
        DifferentialPilot pilot = new DifferentialPilor(2.1f, 2.1f, Motor.A, Motor.B, true);
        pilot.setRobotSpeed(15);

        while (ultrasonicSensor.getDistance() > 25)
            pilot.forward();

        // State 8: Stop and turn 180 degrees
        // TODO: Is it really necessary to stop manually?
        pilot.stop();
        pilot.rotate(180);

        // State 9: Move forward 20 units, then stop
        // TODO: Is it really necessary to stop manually?
        pilot.travel(20);
        pilot.stop();

        // State 10: Turn 90 degrees to the right
        pilot.rotate(90);

        // State 11: Move forward until the touch sensor causes the robot to stop.
        TouchSensor touchSensor = new TouchSensor(SensorPort.S3);
        while (!isPressed())
            pilot.forward();

        // State 12: Print "Stop" on the screen.
        System.out.println("Stop");
    }
}

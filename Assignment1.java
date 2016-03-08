import lejos.nxt.*;
import lejos.robotics.navigation.*;
import java.lang.Math;
public class Assignment1 {
    public static void main (String[] args) {
        SoundSensor soundSensor = new SoundSensor(SensorPort.S1);
        DifferentialPilot pilot = new DifferentialPilot(2.1f, 12f, Motor.A, Motor.B, true);
		UltrasonicSensor ultrasonicSensor = new UltrasonicSensor(SensorPort.S2);
        LightSensor lightSensor = new LightSensor(SensorPort.S4);
		
		// State 1: Print "Assignment 1" on the screen
        System.out.println("Assignment 1");

        // State 2: Wait for a button to be pressed
        Button.waitForAnyPress();

        // State 3: Clear the screen
        LCD.clear();

        // State 4: Wait for clapping and print "Claps" on the screen when it happens
        int soundLevel = soundSensor.readValue();
		int maxlvl = 0;
		while (soundLevel < 70) {
			maxlvl = Math.max(maxlvl, soundLevel);
			soundLevel = soundSensor.readValue();
		}
		System.out.println("Claps");

        // State 5: . Move forward until detecting a light surface underneath
        pilot.setTravelSpeed(5);
		while (lightSensor.readNormalizedValue() < 300)
            pilot.backward();
        
		// State 6: Turn 90 degrees to the left
		pilot.rotate(-90);
        
        // State 7: Move forward until a hard surface is detected by sonar at less than 25 cm distante
        pilot.setTravelSpeed(5);

        while (ultrasonicSensor.getDistance() > 25)
			pilot.backward();
            
        // State 8: Stop and turn 180 degrees
        // TODO: Is it really necessary to stop manually?
        pilot.stop();
        pilot.rotate(180);

        // State 9: Move forward 20 units, then stop
        // TODO: Is it really necessary to stop manually?
        pilot.travel(-20);
        pilot.stop();

        // State 10: Turn 90 degrees to the right
        pilot.rotate(90);

        // State 11: Move forward until the touch sensor causes the robot to stop.
        TouchSensor touchSensor = new TouchSensor(SensorPort.S3);
        while (!touchSensor.isPressed())
            pilot.backward();

        // State 12: Print "Stop" on the screen.
        System.out.println("Stop");
    }
}

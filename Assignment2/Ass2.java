/*
For whom it may concern,
We are sorry for the delay submiting this assignment. We finish it yesterday on the lab, but we completely forgot to submit it. 

Group B6:
Liuri Loami Ruyz Jorge - D14128549
Matheus Connolyn Quirino Santos - D14128350 
*/

import lejos.nxt.*;
import lejos.robotics.navigation.*;
import java.lang.Math;
import lejos.robotics.subsumption.*;


public class Assignment1 {
    public static void main (String[] args) {
    
        //Sensors
        SoundSensor soundSensor = new SoundSensor(SensorPort.S1);
        DifferentialPilot pilot = new DifferentialPilot(2.25f, 4.25f, Motor.A, Motor.B, true);
		UltrasonicSensor ultrasonicSensor = new UltrasonicSensor(SensorPort.S2);
		TouchSensor touchSensor = new TouchSensor(SensorPort.S3);
        LightSensor lightSensor = new LightSensor(SensorPort.S4);
		pilot.setTravelSpeed(5);
		
		try {
			Thread.sleep(1000);
		} catch (Exception e){}
		
		//Behaviours
		Behavior b0 = new toRight();
		Behavior[] behaviors = {b0};
		
		Arbitrator arby = new Arbitrator(behaviors);
		arby.start();
    }
}

//Behavior 
class template  implements Behavior {
	private boolean suppressed = false;
	private argument boolean;
	
	public template(boolean argument) {
		this.argument = argument;
	}
	
	public boolean takeControl() {
		return //when 
	}

	public void suppress() {
		suppressed = true;
	}

	public void action() {
		suppressed = false;
		//action 
		while( !suppressed )
			Thread.yield();
	}
}

//Next line to the right ===dir?, x?, y?
class toRight  implements Behavior {
	private boolean suppressed = false;
	private UltrasonicSensor ultrasonicSensor;
	private DifferentialPilot pilot;
	
	public template(UltrasonicSensor ultrasonicSensor, DifferentialPilot pilot) {
		this.ultrasonicSensor = ultrasonicSensor;
		this.pilot = pilot;
	}
	
	public boolean takeControl() {
		return (getDistance()>20 && dir==1);
	}

	public void suppress() {
		suppressed = true;
	}

	public void action() {
		suppressed = false;
		
		this.pilot.rotate(90);
		this.pilot.travel(7);
		this.pilot.rotate(90);
		
		dir = -1;
		x+=7;
		
		//action 
		while( !suppressed )
			Thread.yield();
	}
	
	public int getDistance(){
		return (
			this.ultrasonicSensor.getDistance() > y ?
			this.ultrasonicSensor.getDistance() - y:
			y - this.ultrasonicSensor.getDistance() );
	}
	
}


import lejos.nxt.*;
import lejos.robotics.navigation.*;
import java.lang.Math;
import lejos.robotics.subsumption.*;

class PrintAssignment  implements Behavior {
   private boolean suppressed = false;
   private boolean firstExec = true;
   public boolean takeControl() {
	  return firstExec;
   }

   public void suppress() {
	  suppressed = true;
   }

   public void action() {
	 suppressed = false;
	 firstExec = false;
	 System.out.println("Assignment 1");
	 while( !suppressed )
		Thread.yield();
   }
}

class ClearScreen  implements Behavior {
   private boolean suppressed = false;
   
   public boolean takeControl() {
	  return Button.readButtons() > 0;
   }

   public void suppress() {
	  suppressed = true;
   }

   public void action() {
	 suppressed = false;
	 LCD.clear();
	 while( !suppressed )
		Thread.yield();
   }
}

class WaitClaps implements Behavior {
	private boolean suppressed = false;
	private SoundSensor soundSensor;
	private DifferentialPilot pilot;
	
	public WaitClaps(SoundSensor soundSensor, DifferentialPilot pilot) {
		this.soundSensor = soundSensor;
		this.pilot = pilot;
	}
	
	public boolean takeControl() {
		return this.soundSensor.readValue() > 60;
	}

	public void suppress() {
		suppressed = true;
	}

	public void action() {
		suppressed = false;
		System.out.println("Claps");
		this.pilot.backward();
		while( !suppressed )
			Thread.yield();
	}
}

class LightSurfaceUnderneath  implements Behavior {
	private boolean suppressed = false;
	private LightSensor lightSensor;
	DifferentialPilot pilot;
	
	public LightSurfaceUnderneath(LightSensor lightSensor, DifferentialPilot pilot) {
		this.lightSensor = lightSensor;
		this.pilot = pilot;
	}
	
	public boolean takeControl() {
		return this.lightSensor.readNormalizedValue() > 500;
	}

	public void suppress() {
		suppressed = true;
	}

	public void action() {
		suppressed = false;
		this.pilot.rotate(90);
		this.pilot.backward();
		while( !suppressed )
			Thread.yield();
	}
}

class SonarHardSurface  implements Behavior {
	private boolean suppressed = false;
	private UltrasonicSensor ultrasonicSensor;
	private DifferentialPilot pilot;
	
	public SonarHardSurface(UltrasonicSensor ultrasonicSensor, DifferentialPilot pilot) {
		this.ultrasonicSensor = ultrasonicSensor;
		this.pilot = pilot;
	}
	
	public boolean takeControl() {
		return this.ultrasonicSensor.getDistance() < 25;
	}

	public void suppress() {
		suppressed = true;
	}

	public void action() {
		suppressed = false;
		this.pilot.rotate(180);
		this.pilot.travel(-20);
		this.pilot.rotate(-90);
		this.pilot.backward();
		while( !suppressed )
			Thread.yield();
	}
}

class TouchStop  implements Behavior {
	private boolean suppressed = false;
   private boolean firstExec = true;
   
	private TouchSensor touchSensor;
	DifferentialPilot pilot;
	
	public TouchStop(TouchSensor touchSensor, DifferentialPilot pilot) {
		this.touchSensor = touchSensor;
		this.pilot = pilot;
	}
	
	public boolean takeControl() {
		return this.touchSensor.isPressed() && firstExec;
	}

	public void suppress() {
		suppressed = true;
	}

	public void action() {
		suppressed = false;
		firstExec = false;
		System.out.println("Stop");
		while( !suppressed )
			Thread.yield();
		this.pilot.stop();
	}
}
	
public class Assignment1 {
    public static void main (String[] args) {
        SoundSensor soundSensor = new SoundSensor(SensorPort.S1);
        DifferentialPilot pilot = new DifferentialPilot(2.25f, 4.25f, Motor.A, Motor.B, true);
		UltrasonicSensor ultrasonicSensor = new UltrasonicSensor(SensorPort.S2);
		TouchSensor touchSensor = new TouchSensor(SensorPort.S3);
        LightSensor lightSensor = new LightSensor(SensorPort.S4);
		pilot.setTravelSpeed(5);
		
		try {
			Thread.sleep(1000);
		} catch (Exception e){}
		
		Behavior b0 = new PrintAssignment();
		Behavior b1 = new ClearScreen();
		Behavior b2 = new WaitClaps(soundSensor, pilot);
		Behavior b3 = new LightSurfaceUnderneath(lightSensor, pilot);
		Behavior b4 = new SonarHardSurface(ultrasonicSensor, pilot);
		Behavior b5 = new TouchStop(touchSensor, pilot);
		Behavior[] behaviors = {b0, b1, b2, b3, b4, b5};
		
		Arbitrator arby = new Arbitrator(behaviors);
		arby.start();
    }
}

/*
Group B6:
Liuri Loami Ruyz Jorge - D14128549
Matheus Connolyn Quirino Santos - D14128350 
*/

import lejos.nxt.*;
import lejos.robotics.navigation.*;
import lejos.robotics.subsumption.*;

class MultableDouble {
    private double value;
	
	public MultableDouble() {
		value = 0;
	}
	
    public double getValue() {
        return value;
    }
    public void setValue(double value) {
        this.value = value;
    }
}

class MultableInt {
    private int value;
	
	public MultableInt() {
		value = 0;
	}
	
    public int getValue() {
        return value;
    }
    public void setValue(int value) {
        this.value = value;
    }
}

//Behaviour 1 - State machine
class StateMachine implements Behavior {
    private DifferentialPilot pilot;    
    private UltrasonicSensor ultrasonicSensor;
    private boolean suppressed = false;
    private int state = 0, count = 0;
    private MultableInt dir;
	private MultableDouble room_x, room_y, traveled;

    public StateMachine (DifferentialPilot pilot, UltrasonicSensor ultrasonicSensor, MultableInt dir, MultableDouble room_x, MultableDouble room_y, MultableDouble traveled) {
        this.pilot = pilot;
        this.ultrasonicSensor = ultrasonicSensor;
        this.dir = dir;
        this.room_x = room_x;
        this.room_y = room_y;
		this.traveled = traveled;
    }

    public boolean takeControl() {
        return true;
    }

    public void suppress() {
	    suppressed = true;
    }

    public void action() {
 	    suppressed = false;
	    while( !suppressed ) {

            //State machine
	        switch(state) {

                //State 0 - Find next wall
                case 0:
                    count++;
                    pilot.reset();
                    pilot.forward();
					
                    while (ultrasonicSensor.getDistance() > 20);
                    if (count % 2 == 0)
                        room_x.setValue(pilot.getMovement().getDistanceTraveled());
                    else
                        room_y.setValue(pilot.getMovement().getDistanceTraveled());
					
                    pilot.rotate(-90);
                    
                    if (count == 4)
                        state = 1;
                    break;

                //State 1 - Prepare to cover all floor area
                case 1:
                    dir.setValue(1);
                    pilot.reset();
                    pilot.forward();
                    state = 2;
                    break;

                //State 2 - Cover room
                case 2:
					traveled.setValue(traveled.getValue() + pilot.getMovement().getDistanceTraveled());
					pilot.forward();
                    break;
            }
	    }
		Thread.yield();
    }
}

//Behaviour 2 - Next line to the right
class NextRightLine implements Behavior {
    private boolean suppressed = false;
    private DifferentialPilot pilot;
    private UltrasonicSensor ultrasonicSensor;
    private MultableDouble room_x, room_y, traveled;
	private MultableInt dir;
    
    public NextRightLine (DifferentialPilot pilot, MultableInt dir, MultableDouble room_x, MultableDouble room_y, MultableDouble traveled, UltrasonicSensor ultrasonicSensor) {
        this.pilot = pilot;
        this.room_x = room_x;
        this.room_y = room_y;
		this.traveled = traveled;
        this.dir = dir;
		this.ultrasonicSensor = ultrasonicSensor;
    }
    
    public boolean takeControl() {
	    return room_y.getValue() - traveled.getValue() < 10 && ultrasonicSensor.getDistance() < 20 && dir.getValue() == -1;
    }

    public void suppress() {
	    suppressed = true;
    }

    public void action() {
	    suppressed = false;
        dir.setValue(1);
        
        pilot.rotate(90);
        pilot.travel(7);
        pilot.rotate(90);
		
		traveled.setValue(0);
    }
}

//Behaviour 3 - Next line to the left
class NextLeftLine implements Behavior {
    private boolean suppressed = false;
    private DifferentialPilot pilot;
    private UltrasonicSensor ultrasonicSensor;
    private MultableDouble room_x, room_y, traveled;
	private MultableInt dir;
    
    public NextLeftLine (DifferentialPilot pilot, MultableInt dir, MultableDouble room_x, MultableDouble room_y, MultableDouble traveled, UltrasonicSensor ultrasonicSensor) {
        this.pilot = pilot;
        this.room_x = room_x;
        this.room_y = room_y;
		this.traveled = traveled;
        this.dir = dir;
		this.ultrasonicSensor = ultrasonicSensor;
    }
    
    public boolean takeControl() {
	    return room_y.getValue() - traveled.getValue() < 10 && ultrasonicSensor.getDistance() < 20 && dir.getValue() == 1;
    }

    public void suppress() {
	    suppressed = true;
    }

    public void action() {
	    suppressed = false;
        dir.setValue(-1);
        
        pilot.rotate(-90);
        pilot.travel(7);
        pilot.rotate(-90);
		
		traveled.setValue(0);
    }
}

//Behaviour 4 - Avoid obstacle
class AvoidObstacle implements Behavior {
    private boolean suppressed = false;
    private UltrasonicSensor ultrasonicSensor;
    private DifferentialPilot pilot;
    private int CONST_AVOID = 15;
    private MultableDouble room_y, traveled;
	
    public AvoidObstacle (DifferentialPilot pilot, UltrasonicSensor ultrasonicSensor, MultableDouble room_y, MultableDouble traveled) {
        this.ultrasonicSensor = ultrasonicSensor;
        this.pilot = pilot;
		this.room_y = room_y;
		this.traveled = traveled;
    }
   
    public boolean takeControl() {
	    return room_y.getValue() - traveled.getValue() > 10 && ultrasonicSensor.getDistance() < 20;
    }

    public void suppress() {
	    suppressed = true;
    }

    public void action() {
	    suppressed = false;

        pilot.rotate(45);
        pilot.travel(CONST_AVOID);
        pilot.rotate(-90);
        pilot.travel(CONST_AVOID);
		traveled.setValue(traveled.getValue()+1.4*(CONST_AVOID));
        pilot.rotate(45);
    }
}

//Behaviour 5 - Carpet
class Carpet implements Behavior {
   private boolean suppressed = false;
   private LightSensor lightSensor;
   int oldValue;
   boolean inCarpet = false;
   
	public boolean takeControl() {
		int newValue = this.lightSensor.readNormalizedValue();
        int dif = Math.abs(oldValue - newValue);
        oldValue = newValue;
		if (dif > 100)
			action();
		return false;
   }
   
   public Carpet(LightSensor lightSensor) {
       this.lightSensor = lightSensor;
	   this.oldValue = this.lightSensor.readNormalizedValue();
   }

   public void suppress() {
	  suppressed = true;
   }

   public void action() {
	 inCarpet = !inCarpet;
	 if (inCarpet) {
		System.out.println("Carpet!");
	 } else {
		LCD.clear();
	 }
   }
}

//Behaviour 6 - Colission
class Collision implements Behavior {
    private boolean suppressed = false;
    private TouchSensor touchSensor;
    private DifferentialPilot pilot;
   
    public Collision (DifferentialPilot pilot, TouchSensor touchSensor) {
        this.touchSensor = touchSensor;
        this.pilot = pilot;
    }
   
    public boolean takeControl() {
	    return touchSensor.isPressed();
    }

    public void suppress() {
	    suppressed = true;
    }

    public void action() {
	    suppressed = false;

        pilot.stop();
        System.exit(0);

	    while( !suppressed )
		    Thread.yield();
    }
}

public class Assignment2 {
    public static void main (String[] args) {
    
        //Measures
        MultableInt dir = new MultableInt();
        MultableDouble room_x = new MultableDouble();
        MultableDouble room_y = new MultableDouble();
		MultableDouble traveled = new MultableDouble();
        
        //Sensors
        DifferentialPilot pilot = new DifferentialPilot(2.1f, 4.4f, Motor.A, Motor.B, false);
		UltrasonicSensor ultrasonicSensor = new UltrasonicSensor(SensorPort.S2);
		TouchSensor touchSensor = new TouchSensor(SensorPort.S3);
        LightSensor lightSensor = new LightSensor(SensorPort.S4);
		pilot.setTravelSpeed(5);
		
		
		//Behaviours
		Behavior stateMachine = new StateMachine(pilot, ultrasonicSensor, dir, room_x, room_y, traveled);
		Behavior nextRightLine = new NextRightLine(pilot, dir, room_x, room_y, traveled, ultrasonicSensor);
		Behavior nextLeftLine = new NextLeftLine(pilot, dir, room_x, room_y, traveled, ultrasonicSensor);
		Behavior avoidObstacle = new AvoidObstacle(pilot, ultrasonicSensor, room_y, traveled);
		Behavior carpet = new Carpet(lightSensor);
		Behavior collision = new Collision(pilot, touchSensor);
		
		Behavior[] behaviors = {stateMachine, nextRightLine, nextLeftLine, avoidObstacle, carpet, collision};
		
		Arbitrator arby = new Arbitrator(behaviors);
		arby.start();
    }
}

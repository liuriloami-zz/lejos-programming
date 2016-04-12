/*
Group B6:
Liuri Loami Ruyz Jorge - D14128549
Matheus Connolyn Quirino Santos - D14128350 
*/

import lejos.nxt.*;
import lejos.robotics.navigation.*;
import lejos.robotics.subsumption.*;

class MultableInt {
    private int value;
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
    private MultableInt dir, room_x, room_y;

    public StateMachine (DifferentialPilot pilot, UltrasonicSensor ultrasonicSensor, MultableInt dir, MultableInt room_x, MultableInt room_y) {
        this.pilot = pilot;
        this.ultrasonicSensor = ultrasonicSensor;
        this.dir = dir;
        this.room_x = room_x;
        this.room_y = room_y;
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

                //State 0 - Find Wall
                case 0:
                    pilot.forward();
                    if (ultrasonicSensor.getDistance() < 30)
                        state = 1;
                    break;
    
                //State 1 - Find first wall
                case 1:
                    int minDist = 255;
                    for (int i = 0; i < 36; i++) {
                        pilot.rotate(10);
                        minDist = Math.min(minDist, ultrasonicSensor.getDistance());
                    }
                    for (int i = 0; i < 36; i++) {
                        if (Math.abs(ultrasonicSensor.getDistance() - minDist) < 5) break;
                        pilot.rotate(10);
                    }
                    pilot.rotate(90);
                    state = 2;
                    break;

                //State 2 - Find edge
                case 2:
                    pilot.forward();
                    if (ultrasonicSensor.getDistance() < 20)
                        state = 3;
                    break;
                
                //State 3 - Prepare for perimeter inspection
                case 3:
                    pilot.rotate(180);
                    count = 0;
                    state = 4;
                    break;

                //State 3 - Find next wall
                case 4:
                    count++;
                    pilot.reset();
                    
                    while (ultrasonicSensor.getDistance() > 20)
                        pilot.forward();
                    
                    if (count % 2 == 0)
                        room_x.setValue((int)pilot.getMovement().getDistanceTraveled());
                    else
                        room_y.setValue((int)pilot.getMovement().getDistanceTraveled());
                    
                    pilot.rotate(-90);
                    
                    if (count == 4)
                        state = 5;
                    break;

                //State 5 - Prepare to cover all floor area
                case 5:
                    dir.setValue(1);
                    pilot.reset();
                    state = 6;
                    break;

                //State 6 - Cover room
                case 6:
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
    private MultableInt room_x, room_y, dir;
    
    public NextRightLine (DifferentialPilot pilot, MultableInt dir, MultableInt room_x, MultableInt room_y) {
        this.pilot = pilot;
        this.room_x = room_x;
        this.room_y = room_y;
        this.dir = dir;
    }
    
    public boolean takeControl() {
	    return Math.abs(room_y.getValue() - pilot.getMovement().getDistanceTraveled()) < 40 && dir.getValue() == -1;
    }

    public void suppress() {
	    suppressed = true;
    }

    public void action() {
	    suppressed = false;
        
        pilot.rotate(90);
        pilot.travel(7);
        pilot.rotate(90);
          
        dir.setValue(1);
        pilot.reset();
                      
	    while( !suppressed )
		    Thread.yield();
    }
}

//Behaviour 3 - Next line to the left
class NextLeftLine implements Behavior {
    private boolean suppressed = false;
    private DifferentialPilot pilot;
    private MultableInt room_x, room_y, dir;
    
    public NextLeftLine (DifferentialPilot pilot, MultableInt dir, MultableInt room_x, MultableInt room_y) {
        this.pilot = pilot;
        this.room_x = room_x;
        this.room_y = room_y;
        this.dir = dir;
    }
    
    public boolean takeControl() {
	    return Math.abs(room_y.getValue() - pilot.getMovement().getDistanceTraveled()) < 40 && dir.getValue() == 1;
    }

    public void suppress() {
	    suppressed = true;
    }

    public void action() {
	    suppressed = false;
        
        pilot.rotate(-90);
        pilot.travel(7);
        pilot.rotate(-90);
          
        dir.setValue(1);
        pilot.reset();
                      
	    while( !suppressed )
		    Thread.yield();
    }
}

//Behaviour 4 - Avoid obstacle
class AvoidObstacle implements Behavior {
    private boolean suppressed = false;
    private UltrasonicSensor ultrasonicSensor;
    private DifferentialPilot pilot;
    private int CONST_AVOID = 15;
    
    public AvoidObstacle (DifferentialPilot pilot, UltrasonicSensor ultrasonicSensor) {
        this.ultrasonicSensor = ultrasonicSensor;
        this.pilot = pilot;
    }
   
    public boolean takeControl() {
	    return ultrasonicSensor.getDistance() < 20;
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
        pilot.rotate(45);

	    while( !suppressed )
		    Thread.yield();
    }
}

//Behaviour 5 - Carpet - TODO
class Carpet implements Behavior {
   private boolean suppressed = false;
   
public boolean takeControl() {
	  return false;
   }

   public void suppress() {
	  suppressed = true;
   }

   public void action() {
	 suppressed = false;

	 while( !suppressed )
		Thread.yield();
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
        MultableInt room_x = new MultableInt();
        MultableInt room_y = new MultableInt();
        
        //Sensors
        DifferentialPilot pilot = new DifferentialPilot(2.25f, 4.25f, Motor.A, Motor.B, true);
		UltrasonicSensor ultrasonicSensor = new UltrasonicSensor(SensorPort.S1);
		TouchSensor touchSensor = new TouchSensor(SensorPort.S2);
        LightSensor lightSensor = new LightSensor(SensorPort.S3);
		pilot.setTravelSpeed(5);
		
		try {
			Thread.sleep(1000);
		} catch (Exception e){}
		
		//Behaviours
		Behavior stateMachine = new StateMachine(pilot, ultrasonicSensor, dir, room_x, room_y);
		Behavior nextRightLine = new NextRightLine(pilot, dir, room_x, room_y);
		Behavior nextLeftLine = new NextLeftLine(pilot, dir, room_x, room_y);
		Behavior avoidObstacle = new AvoidObstacle(pilot, ultrasonicSensor);
		Behavior carpet = new Carpet();
		Behavior collision = new Collision(pilot, touchSensor);
		
		Behavior[] behaviors = {avoidObstacle, collision};
		
		Arbitrator arby = new Arbitrator(behaviors);
		arby.start();
    }
}

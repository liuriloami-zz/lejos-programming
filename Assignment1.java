import lejos.nxt.*;
import lejos.robotics.navigation.*;
import java.lang.Math;
import lejos.robotics.subsumption.*;

public class Assignment1 {

    public class PrintAssignment  implements Behavior {
       private boolean suppressed = false;
       
       public boolean takeControl() {
          return true;
       }

       public void suppress() {
          suppressed = true;
       }

       public void action() {
         suppressed = false;
         System.out.println("Assignment 1");
         while( !suppressed )
            Thread.yield();
       }
    }
    
    public class ClearScreen  implements Behavior {
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
    
    public class WaitClaps  implements Behavior {
        private boolean suppressed = false;
        private SoundSensor soundSensor;
        
        public WaitClaps(SoundSensor soundSensor) {
            this.soundSensor = soundSensor;
        }
        
        public boolean takeControl() {
            return this.soundSensor.readValue() < 70;
        }

        public void suppress() {
            suppressed = true;
        }

        public void action() {
            suppressed = false;
            System.out.println("Claps");
            pilot.forward();
            while( !suppressed )
                Thread.yield();
        }
    }
    
    public class LightSurfaceUnderneath  implements Behavior {
        private boolean suppressed = false;
        private LightSensor lightSensor;
        
        public WaitClaps(LightSensor lightSensor) {
            this.lightSensor = lightSensor;
        }
        
        public boolean takeControl() {
            return this.lightSensor.readNormalizedValue() < 300;
        }

        public void suppress() {
            suppressed = true;
        }

        public void action() {
            suppressed = false;
            pilot.rotate(-90);
            pilot.forward();
            while( !suppressed )
                Thread.yield();
        }
    }
    
    public class SonarHardSurface  implements Behavior {
        private boolean suppressed = false;
        private UltrasonicSensor ultrasonicSensor;
        
        public WaitClaps(UltrasonicSensor ultrasonicSensor) {
            this.ultrasonicSensor = ultrasonicSensor;
        }
        
        public boolean takeControl() {
            return this.ultrasonicSensor.getDistance() > 25;
        }

        public void suppress() {
            suppressed = true;
        }

        public void action() {
            suppressed = false;
            pilot.rotate(180);
            pilot.travel(-20);
            pilot.rotate(90);
            pilot.forward();
            while( !suppressed )
                Thread.yield();
        }
    }
    
    public class SonarHardSurface  implements Behavior {
        private boolean suppressed = false;
        private TouchSensor touchSensor;
        
        public WaitClaps(TouchSensor touchSensor) {
            this.touchSensor = touchSensor;
        }
        
        public boolean takeControl() {
            return this.touchSensor.isPressed();
        }

        public void suppress() {
            suppressed = true;
        }

        public void action() {
            suppressed = false;
            System.out.println("Stop");
        }
    }
    
    public static void main (String[] args) {
        //Sensors
        SoundSensor soundSensor = new SoundSensor(SensorPort.S1);
        DifferentialPilot pilot = new DifferentialPilot(2.1f, 12f, Motor.A, Motor.B, true);
		UltrasonicSensor ultrasonicSensor = new UltrasonicSensor(SensorPort.S2);
        LightSensor lightSensor = new LightSensor(SensorPort.S4);
		
		pilot.setTravelSpeed(5);
		
		Behaviour[] behaviours = {};
		behaviours[0] = new PrintAssignment();
		behaviours[1] = new ClearScreen();
		behaviours[2] = new WaitClaps(soundSensor);
		behaviours[3] = new LightSurfaceUnderneath(lightSensor);
		behaviours[4] = new SonarHardSurface(ultrasonicSensor);
		behaviours[6] = new TouchStop(touchSensor);
	
		Arbritrator arby = new Arbritrator(behaviours, true);
		arby.start();
    }
}

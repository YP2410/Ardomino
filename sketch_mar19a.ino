#include <SoftwareSerial.h>
SoftwareSerial ArduinoUno(0,1);

#include <AccelStepper.h>
#include <MultiStepper.h>
#include <Stepper.h>
#include <Servo.h>
// The X Stepper pins
#define STEPPER1_DIR_PIN 5
#define STEPPER1_STEP_PIN 2
// The Y stepper pins
#define STEPPER2_DIR_PIN 6
#define STEPPER2_STEP_PIN 3
// The Y stepper pins
#define STEPPER3_DIR_PIN 7
#define STEPPER3_STEP_PIN 4

#include <Servo.h>
Servo myservoDoorLeft;
Servo myservoDoorRight;
Servo myservoDomino;


// Define some steppers and the pins the will use
MultiStepper steppers;
MultiStepper steppers_domino;
AccelStepper stepper1(AccelStepper::DRIVER, STEPPER1_STEP_PIN, STEPPER1_DIR_PIN);
AccelStepper stepper2(AccelStepper::DRIVER, STEPPER2_STEP_PIN, STEPPER2_DIR_PIN);
AccelStepper stepper3(AccelStepper::DRIVER, STEPPER3_STEP_PIN, STEPPER3_DIR_PIN);

int steps=600;
Stepper motor(steps, STEPPER1_STEP_PIN, STEPPER1_DIR_PIN);


long left = 0;
long right = 0;
long temp = 0;
long sum_left = 0;
long sum_right = 0;
long sum_domino = 0;
int falseIndex = 0;
//long derarr[] = {100, 100, 100, 0, 100, 100, 0, 100};
unsigned long myTime;

void setup(){
  
  Serial.begin(9600);
  ArduinoUno.begin(4800);
  stepper1.setMaxSpeed(100.0);
  steppers.addStepper(stepper1);
  stepper1.setAcceleration(100.0);
  stepper2.setMaxSpeed(100.0);
  steppers.addStepper(stepper2);
  stepper2.setAcceleration(100.0);
  stepper3.setMaxSpeed(100.0);
  steppers_domino.addStepper(stepper3);
  stepper3.setAcceleration(100.0);
  
  
  pinMode(8,OUTPUT);
  digitalWrite(8,LOW);
  
  motor.setSpeed(80);
  myservoDoorLeft.attach(10); 
  myservoDoorRight.attach(9); 
  myservoDomino.attach(11);
 myservoDoorLeft.write(20); 
  myservoDoorRight.write(160);
 //myservoDoorLeft.write(65);
  //myservoDoorRight.write(120);

  //  myTime = millis();
  //dropDomino();
  //Serial.println(millis() - myTime); // prints time since program started
  //delay(1000);          // wait a second so as not to send massive amounts of data
}


void dropDomino(){
  sum_domino=sum_domino+200;
  long arr[] = {sum_domino};
  steppers_domino.moveTo(arr);
  steppers_domino.runSpeedToPosition();
  delay(750);
  
}

void openDoors(){
  myservoDoorLeft.write(65);
  delay(500); 
  myservoDoorRight.write(120);
  delay(750); 
}

void closeDoors(){
  myservoDoorLeft.write(20);
  delay(500); 
  myservoDoorRight.write(160);
  delay(500);
}

void loop(){
  //Serial.println("shtak");
  int i =0;
  left = 0;
  right = 0;
  Serial.println("i got");
  while(i<2){
    if (ArduinoUno.available()>0){
      temp = ArduinoUno.parseFloat();
      
      if(temp != 0){
        i++;
      
        if(temp == -3){
          temp = 0;
        }
        if(i==1){
          left = temp;
          Serial.println(left);
        }
        if(i==2){
          right = temp;
          Serial.println(right);
        }
      }
    }
  }
  if(left == -5){
    sum_domino=sum_domino-100;
  long arr[] = {sum_domino};
  steppers_domino.moveTo(arr);
  steppers_domino.runSpeedToPosition();
  delay(750);
  }
  else{
  sum_left=sum_left+left;
  sum_right=sum_right+right;
  Serial.println("sum left, sum right");
  Serial.println(sum_left);
  Serial.println(sum_right);
  long arr[] = {sum_left, sum_right};
  steppers.moveTo(arr);
  steppers.runSpeedToPosition();
  delay(1000);
  
  if((left+right)!=0){

  closeDoors();
  dropDomino();
  openDoors();
  }
  else{
    delay(3000);
  }
  }
  
  //myservoDoorLeft.write(0);
  
  
  
}

 // myservo1.write(180);  // tell servo to go to a particular angle
  //myservo2.write(0);
  //delay(500);
  
  //myservo1.write(50);    
  //myservo2.write(0);          
  //delay(500); 
  
  //myservo.write(135);              
  //delay(500);
  
  //myservo.write(180);              
  //delay(1500);

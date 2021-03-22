
/**
 * Created by K. Suwatchai (Mobizt)
 * 
 * Email: k_suwatchai@hotmail.com
 * 
 * Github: https://github.com/mobizt
 * 
 * Copyright (c) 2020 mobizt
 *
*/

//This example shows how to set array data through FirebaseJsonArray object then read the data back and parse them.
#include <SoftwareSerial.h>
#include <ESP8266WiFi.h>
#include <FirebaseESP8266.h>
SoftwareSerial NodeMCU(TX,RX);
#define WIFI_SSID "AndroidAP2B31"
#define WIFI_PASSWORD "gdqr3060"

#define FIREBASE_HOST "ardomino-5dea7.firebaseio.com"

/** The database secret is obsoleted, please use other authentication methods, 
 * see examples in the Authentications folder. 
*/
#define FIREBASE_AUTH "MofP8yWt8inYVf253C51P7Cw5O1IQ97CoYpj60U7"

//Define Firebase Data Object
FirebaseData fbdo;

FirebaseJsonArray arr;

void printResult(FirebaseData &data);

unsigned long sendDataPrevMillis = 0;

String path = "/Test/Array";

uint16_t count = 0;
int first = 0;
void setup()
{

    Serial.begin(9600);
    NodeMCU.begin(4800);
    pinMode(TX,INPUT);
    pinMode(RX,OUTPUT);
    WiFi.begin(WIFI_SSID, WIFI_PASSWORD);
    Serial.print("Connecting to Wi-Fi");
    while (WiFi.status() != WL_CONNECTED)
    {
        Serial.print(".");
        delay(300);
    }
    Serial.println();
    Serial.print("Connected with IP: ");
    Serial.println(WiFi.localIP());
    Serial.println();

    Firebase.begin(FIREBASE_HOST, FIREBASE_AUTH);
    Firebase.reconnectWiFi(true);

    //Set the size of WiFi rx/tx buffers in the case where we want to work with large data.
    fbdo.setBSSLBufferSize(1024, 1024);

    //Set the size of HTTP response buffers in the case where we want to work with large data.
    fbdo.setResponseSize(1024);

    if (!Firebase.beginStream(fbdo, path))
    {
        Serial.println("------------------------------------");
        Serial.println("Can't begin stream connection...");
        Serial.println("REASON: " + fbdo.errorReason());
        Serial.println("------------------------------------");
        Serial.println();
    }
}

void loop()
{

    if (millis() - sendDataPrevMillis > 15000)
    {
        sendDataPrevMillis = millis();
        count++;

        Firebase.getBool(fbdo, "/Empty");
        while(fbdo.boolData() == true){
          Firebase.getBool(fbdo, "/Empty");
          delay(1);
          Serial.println("no dominos");
        }

        Serial.println("------------------------------------");
        Serial.println("Get Array...");
        if (Firebase.get(fbdo, "/arr"))
        {
            Serial.println("PASSED");
            Serial.println("PATH: " + fbdo.dataPath());
            Serial.println("TYPE: " + fbdo.dataType());
            Serial.print("VALUE: ");
            printResult(fbdo);
            Serial.println("------------------------------------");
            Serial.println();
        }
        else
        {
            Serial.println("FAILED");
            Serial.println("REASON: " + fbdo.errorReason());
            Serial.println("------------------------------------");
            Serial.println();
        }
    }

    if (!Firebase.readStream(fbdo))
    {
        Serial.println("------------------------------------");
        Serial.println("Can't read stream data...");
        Serial.println("REASON: " + fbdo.errorReason());
        Serial.println("------------------------------------");
        Serial.println();
    }

    if (fbdo.streamTimeout())
    {
        Serial.println("Stream timeout, resume streaming...");
        Serial.println();
    }

    
}

void printResult(FirebaseData &data)
{

    if (data.dataType() == "int")
        Serial.println(data.intData());
    else if (data.dataType() == "float")
        Serial.println(data.floatData(), 5);
    else if (data.dataType() == "double")
        printf("%.9lf\n", data.doubleData());
    else if (data.dataType() == "boolean")
        Serial.println(data.boolData() == 1 ? "true" : "false");
    else if (data.dataType() == "string")
        Serial.println(data.stringData());
    else if (data.dataType() == "json")
    {
        Serial.println();
        FirebaseJson &json = data.jsonObject();
        //Print all object data
        Serial.println("Pretty printed JSON data:");
        String jsonStr;
        json.toString(jsonStr, true);
        Serial.println(jsonStr);
        Serial.println();
        Serial.println("Iterate JSON data pikapika1:");
        Serial.println();
        size_t len = json.iteratorBegin();
        String key, value = "";
        int type = 0;
        for (size_t i = 0; i < len; i++)
        {
            json.iteratorGet(i, type, key, value);
            Serial.print(i);
            Serial.print(", ");
            Serial.print("Type: ");
            Serial.print(type == FirebaseJson::JSON_OBJECT ? "object" : "array");
            if (type == FirebaseJson::JSON_OBJECT)
            {
                Serial.print(", Key: ");
                Serial.print(key);
            }
            Serial.print(", Value: ");
            Serial.println(value);
        }
        json.iteratorEnd();
    }
    else if (data.dataType() == "array")
    {
        Serial.println();
        
        //get array data from FirebaseData using FirebaseJsonArray object
        FirebaseJsonArray &arr = data.jsonArray();
        //Print all array values
        Serial.println("Pretty printed Array:");
        Serial.println("I got to here1");
        String arrStr;
        arr.toString(arrStr, true);
        Serial.println(arrStr);
        Serial.println();
        Serial.println("Iterate array values:");
        Serial.println();
        for (size_t i = 0; i < arr.size(); i++)
        {
            Serial.print(i);
            Serial.print(", Value: ");

            FirebaseJsonData &jsonData = data.jsonData();
            //Get the result data from FirebaseJsonArray object
            arr.get(jsonData, i);
            if (jsonData.typeNum == FirebaseJson::JSON_BOOL)
                Serial.println(jsonData.boolValue ? "true" : "false");
            else if (jsonData.typeNum == FirebaseJson::JSON_INT){
                Serial.println("I got to here6");
                
                if(jsonData.intValue != -1 && jsonData.intValue != -2){
                Serial.println(jsonData.intValue);
                NodeMCU.print(jsonData.intValue);
                NodeMCU.println("\n");
                delay(2666.5);
                }
                else{
                  if(Firebase.setBool(fbdo, "/Empty", true))
                  {
                    //Success
                    Serial.println("Set bool data success");

                  }else{
                    //Failed?, get the error reason from fbdo

                    Serial.print("Error in setBool, ");
                    Serial.println(fbdo.errorReason());
                  }
                }
                
            }
            else if (jsonData.typeNum == FirebaseJson::JSON_FLOAT){
                Serial.println("I got to here7");
                Serial.println(jsonData.floatValue);
            }
            else if (jsonData.typeNum == FirebaseJson::JSON_DOUBLE){
                Serial.println("I got to here8");
                printf("%.9lf\n", jsonData.doubleValue);
            }
            else if (jsonData.typeNum == FirebaseJson::JSON_STRING ||
                     jsonData.typeNum == FirebaseJson::JSON_NULL ||
                     jsonData.typeNum == FirebaseJson::JSON_OBJECT ||
                     jsonData.typeNum == FirebaseJson::JSON_ARRAY){
                     Serial.println("I got to here2");
                Serial.println(jsonData.stringValue);
                     }
            if(jsonData.typeNum == FirebaseJson::JSON_ARRAY)
                Serial.println("I got to here3");
            if(jsonData.typeNum == FirebaseJson::JSON_OBJECT)
                Serial.println(jsonData.typeNum);
                Serial.println("I got to here4");
        }
    }
    else if (data.dataType() == "blob")
    {

        Serial.println();

        for (int i = 0; i < data.blobData().size(); i++)
        {
            if (i > 0 && i % 16 == 0)
                Serial.println();

            if (i < 16)
                Serial.print("0");

            Serial.print(data.blobData()[i], HEX);
            Serial.print(" ");
        }
        Serial.println();
    }
    else if (data.dataType() == "file")
    {

        Serial.println();

        File file = data.fileStream();
        int i = 0;

        while (file.available())
        {
            if (i > 0 && i % 16 == 0)
                Serial.println();

            int v = file.read();

            if (v < 16)
                Serial.print("0");

            Serial.print(v, HEX);
            Serial.print(" ");
            i++;
        }
        Serial.println();
        file.close();
    }
    else
    {
        Serial.println(data.payload());
    }
}

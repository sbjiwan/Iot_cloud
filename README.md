# Iot_cloud
### - aws: {lambda, dynamo, api gateway}/ android: mobile/ arduino: mkr1010

#### Monitoring : SleepTime Monitoring

### APIs

+ GET :/dynamo/{Date}
  - get sleep time information.
  - Response: dyanmo json

+ GET :/dynamo body= {sleep::string, awake::string, timestamp::string}
  - create a timestamp
  - Response: generated Date

### Services

+ Arduino
  - save sleep time
  - turn on light
  - alarm clock sound
  
  
+ Android
  - monitoring to sleep time 
  


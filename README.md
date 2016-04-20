# java-event-framework
Simple event programming framework for Java and Android.

# Features
* Send events
* Subscribe to events
* Fully customizable filtering system

#Download
**Maven:**
```xml
<dependency>
   <groupId>com.sianav</groupId>
   <artifactId>event_framework</artifactId>
   <version>0.0.1</version>
</dependency>
```
**Gradle:**
```groovy
compile 'com.sianav:event_framework:0.0.1'
```

#How to use
Let's take an example of an Android app for lights control. The purpose is to get notified when an app with a certain id
is turned on or off:

####The light entity class:
```java
public class Light {

    private String id;
    private boolean isOn;

    public Light(String id) {
        this.id = id;
    }

    public void setLightOn() {
        this.isOn = true;
        EventService.getInstance().publish(new LightStateChangeEvent(id, isOn));
    }

    public void setLightOff() {
        this.isOn = false;
        EventService.getInstance().publish(new LightStateChangeEvent(id, isOn));
    }

    public String getId() {
        return id;
    }

    public boolean isOn() {
        return isOn;
    }
}
```

A LightStateChangeEvent is sent when the light state changes.

####The LightStateChangeEvent class:
```java
public class LightStateChangeEvent implements Event {

    private String mLightId;
    private boolean mIsOn;

    public LightStateChangeEvent(String lightId, boolean isOn) {
        mLightId = lightId;
        mIsOn = isOn;
    }

    public boolean isOn() {
        return mIsOn;
    }

    public String getLightId() {
        return mLightId;
    }
}
```
The lightId and the new state are passed to the event so they can be retrieved by the subscriber.

####The LightFilter class:
```java
public class LightFilter implements Filter<LightStateChangeEvent> {

    private String mLightId;

    public LightFilter(Light light) {
        mLightId = light.getId();
    }

    //Filter condition. If true returned, the event will be sent
    public boolean apply(LightStateChangeEvent lightStateChangeEvent) {
        return lightStateChangeEvent.getLightId().equals(mLightId);
    }

    //Used to compare two filters to avoid subscribing multiple times to the same event
    public boolean equals(Filter filter) {
        if(!(filter instanceof LightFilter)) {
            return false;
        }
        else {
            return (((LightFilter) filter).getLightId().equals(mLightId));
        }
    }

    public String getLightId() {
        return mLightId;
    }
}
```
With filters, you could also for example receive notification only when a specific light (or a list of light..) is turned on.

####LightActivity class
```java
public class LightActivity implements Subscriber {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Create two lights
        Light light1 = new Light("lightId1");
        Light light2 = new Light("lightId2");

        //Subscribe only on state change for light1
        LightFilter lightFilter = new LightFilter(light1);
        EventService.getInstance().subscribe(LightStateChangeEvent.class, lightFilter, this);

        //inform() method will be called
        light1.setLightOn();

        //inform() method won't be called
        light2.setLightOn();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventService.getInstance().unsubscribe(this);
    }

    public void inform(Event event) {
        if(event instanceof LightStateChangeEvent) {
            //Do something...
        }
    }
}
```
# License
Java Event Framework is released under the [Apache 2.0 license](LICENSE).

```
Copyright 2016 Sianav SAS

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
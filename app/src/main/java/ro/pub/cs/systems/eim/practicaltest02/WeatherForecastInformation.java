package ro.pub.cs.systems.eim.practicaltest02;

public class WeatherForecastInformation {

    public String temperature;
    public String humidity;
    public String windSpeed;
    public String condition;
    public String pressure;

    public WeatherForecastInformation() {
        temperature = null;
        humidity = null;
        windSpeed = null;
        condition = null;
        pressure = null;
    }

    public WeatherForecastInformation(String temperature, String humidity, String windSpeed, String condition, String pressure) {
        this.temperature = temperature;
        this.humidity = humidity;
        this.windSpeed = windSpeed;
        this.condition = condition;
        this.pressure = pressure;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getTemperature(){
        return temperature;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }

    public String getHumidity() {
        return humidity;
    }

    public void setWindSpeed(String windSpeed) {
        this.windSpeed = windSpeed;
    }

    public String getWindSpeed() {
        return windSpeed;
    }

    public void setPressure(String pressure) {
        this.pressure = pressure;
    }

    public String getPressure() {
        return pressure;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getCondition(){
        return condition;
    }

    public String toString() {
        return "WeatherForecastInformation{ \n"
                + "Temperature is: " + temperature + "\n"
                + "Humidity is: " + humidity + "\n"
                + "Pressure is: " + pressure + "\n"
                + "Condition is: " + condition + "\n"
                + "WindSpeed is: " + windSpeed + "\n}";
    }
}

package com.example.smartpack;

public class WeatherResponse {
    private Main main;
    private Weather[] weather;

    public Main getMain() {
        return main;
    }

    public Weather[] getWeather() {
        return weather;
    }

    public class Main {
        private float temp;

        public float getTemp() {
            return temp;
        }
    }

    public class Weather {
        private String description;

        public String getDescription() {
            return description;
        }
    }
}

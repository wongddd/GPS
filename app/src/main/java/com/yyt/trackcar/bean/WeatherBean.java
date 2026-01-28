package com.yyt.trackcar.bean;

import java.util.List;

public class WeatherBean {

    /**
     * 经度
     */
    private double lat;
    /**
     * 纬度
     */
    private double lon;
    /**
     * 请求位置的时区名称
     */
    private String timezone;
    /**
     * 以秒为单位从UTC转换
     */
    private int timezone_offset;

    private Current current;

    static class Weather {
        private int id;
        /**
         * 天气
         */
        private String main;
        /**
         * 天气描述
         */
        private String description;
        /**
         * 天气配图
         * https://openweathermap.org/weather-conditions#How-to-get-icon-URL
         * 例如：http://openweathermap.org/img/wn/{$icon}@2x.png
         */
        private String icon;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getMain() {
            return main;
        }

        public void setMain(String main) {
            this.main = main;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }
    }

    static class Current{
        /**
         * 查找日期
         */
        private Long dt;
        /**
         * 日出时间
         */
        private Long sunrise;
        /**
         * 日落时间
         */
        private Long sunset;
        /**
         * 气温
         */
        private float temp;
        /**
         * 体感温度
         */
        private float feels_like;
        /**
         * 压强
         */
        private float pressure;
        /**
         * 空气湿度
         */
        private int humidity;
        /**
         * 大气温度
         */
        private float dew_point;
        /**
         * 午间紫外线指数 %
         */
        private float uvi;
        /**
         * 云量 %
         */
        private int clouds;
        /**
         * 可见度 最大显示为10Km(10000)
         */
        private int visibility;
        /**
         * 风速
         */
        private float wind_speed;
        /**
         * 阵风风速
         */
        private float wind_gust;
        /**
         * 风向
         */
        private int wind_deg;

        private List<Weather> weather;

        public Long getDt() {
            return dt;
        }

        public void setDt(Long dt) {
            this.dt = dt;
        }

        public Long getSunrise() {
            return sunrise;
        }

        public void setSunrise(Long sunrise) {
            this.sunrise = sunrise;
        }

        public Long getSunset() {
            return sunset;
        }

        public void setSunset(Long sunset) {
            this.sunset = sunset;
        }

        public float getTemp() {
            return temp;
        }

        public void setTemp(float temp) {
            this.temp = temp;
        }

        public float getFeels_like() {
            return feels_like;
        }

        public void setFeels_like(float feels_like) {
            this.feels_like = feels_like;
        }

        public float getPressure() {
            return pressure;
        }

        public void setPressure(float pressure) {
            this.pressure = pressure;
        }

        public int getHumidity() {
            return humidity;
        }

        public void setHumidity(int humidity) {
            this.humidity = humidity;
        }

        public float getDew_point() {
            return dew_point;
        }

        public void setDew_point(float dew_point) {
            this.dew_point = dew_point;
        }

        public float getUvi() {
            return uvi;
        }

        public void setUvi(float uvi) {
            this.uvi = uvi;
        }

        public int getClouds() {
            return clouds;
        }

        public void setClouds(int clouds) {
            this.clouds = clouds;
        }

        public int getVisibility() {
            return visibility;
        }

        public void setVisibility(int visibility) {
            this.visibility = visibility;
        }

        public float getWind_speed() {
            return wind_speed;
        }

        public void setWind_speed(float wind_speed) {
            this.wind_speed = wind_speed;
        }

        public float getWind_gust() {
            return wind_gust;
        }

        public void setWind_gust(float wind_gust) {
            this.wind_gust = wind_gust;
        }

        public int getWind_deg() {
            return wind_deg;
        }

        public void setWind_deg(int wind_deg) {
            this.wind_deg = wind_deg;
        }

        public List<Weather> getWeather() {
            return weather;
        }

        public void setWeather(List<Weather> weather) {
            this.weather = weather;
        }
    }

    public Current getCurrent() {
        return current;
    }

    public void setCurrent(Current current) {
        this.current = current;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public int getTimezone_offset() {
        return timezone_offset;
    }

    public void setTimezone_offset(int timezone_offset) {
        this.timezone_offset = timezone_offset;
    }
}

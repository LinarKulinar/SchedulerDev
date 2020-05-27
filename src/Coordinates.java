public class Coordinates {
    private double latitude; // Широта (в градусах)
    private double longitude; // Долгота (в градусах)

    public Coordinates(double latitude, double longitude){
        if (latitude < -90 || latitude > 90){
            throw new IllegalArgumentException("latitude must be: (latitude >= -90) and (latitude <= 90)");
        }
        if (longitude < -180 || longitude > 180){
            throw new IllegalArgumentException("longitude must be: (longitude >= -180) and (longitude <= 180)");
        }
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public void setLatitude(double latitude) {
        if (latitude < -90 || latitude > 90){
            throw new IllegalArgumentException("latitude must be: (latitude >= -90) and (latitude <= 90)");
        }
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        if (longitude < -180 || longitude > 180){
            throw new IllegalArgumentException("longitude must be: (longitude >= -180) and (longitude <= 180)");
        }
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    /**
     * Метод вычисляет расстояние между двумя точками на поверхности Земли.
     * В нем вычисляется длина дуги большого круга - кратчайшего расстояния между любыми двумя поверхностями сферы.
     * Код портирован из:
     * https://www.kobzarev.com/programming/calculation-of-distances-between-cities-on-their-coordinates/
     * @param point1 Первая точка
     * @param point2 Вторая точка
     * @return Расстояние между двумя точками
     */
    public static double AsTheCrowFlies(Coordinates point1, Coordinates point2){
        final double earthRadius = 6372795; // радиус земли

        double lat1 = point1.getLatitude();
        double long1 = point1.getLongitude();
        double lat2 = point2.getLatitude();
        double long2 = point2.getLongitude();
        // переводим координаты в радианы
        lat1 = lat1 * Math.PI / 180;
        lat2 = lat2 * Math.PI / 180;
        long1 = long1 * Math.PI / 180;
        long2 = long2 * Math.PI / 180;

        // косинусы и синусы широт и разницы долгот
        double cl1 = Math.cos(lat1);
        double cl2 = Math.cos(lat2);
        double sl1 = Math.sin(lat1);
        double sl2 = Math.sin(lat2);
        double delta = long2 - long1;
        double cdelta = Math.cos(delta);
        double sdelta = Math.sin(delta);

        // вычисления длины большого круга
        double y = Math.sqrt(Math.pow(cl2 * sdelta, 2) + Math.pow(cl1 * sl2 - sl1 * cl2 * cdelta, 2));
        double x = sl1 * sl2 + cl1 * cl2 * cdelta;
        //
        double ad = Math.atan2(y, x);
        double dist = ad * earthRadius;

        return dist;
    }
}

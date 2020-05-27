public class ResultSimulationData {
    private int t0; //Время, с которого
    private double tourTime; //время в маршруте
    private int cviols; // количество заказов, на которые опоздали (constraint violations)
    private double arrival[]; // время прибытия на i-ую точку
    private double delay[]; // время отбытия с i-ой точки
    private double resultMetric;

    public ResultSimulationData(double tourTime, int cviols, double arrival[], double delay[], double resultMetric){
        this.tourTime = tourTime;
        this.cviols = cviols;
        this.arrival = arrival;
        this.delay = delay;
        this.resultMetric = resultMetric;
    }

    public double getTourTime() {
        return tourTime;
    }

    public int getCviols() {
        return cviols;
    }

    public double[] getArrival() {
        return arrival;
    }

    public double[] getDelay() {
        return delay;
    }

    public double getResultMetric() {
        return resultMetric;
    }
}

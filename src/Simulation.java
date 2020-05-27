import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Locale;
import java.util.Scanner;

/**
 * Данный класс хранит считанную из файл информацию, необходимую для симуляции работы курьерской службы
 * Этот код частично переписан и передан из: http://lopez-ibanez.eu/tsptw-instances
 */
public class Simulation {
    private int n; // количество точек в графе
    private Coordinates points[];
    private double timeTripBetweenPoint[]; // Время, которое необходимо на преодоление расстояния между точками
    private int beginTW[]; // Начало временного окна (в мин)
    private int endTW[]; // Конец временного окна (в мин)
    private int loadingOrderTime[];
    private int unLoadingOrderTime[];
    private double weight[]; // вес (кг)
    private double velocity; // скорость транспорта, (км/ч)
    private double capacity; // вместимость одного грузового автомобиля

    private int permutation[]; // маршрут, по которому мы идем

    public void setPermutation(int[] permutation) {
        if (permutation.length != n - 1) {
            throw new IllegalArgumentException("The permutation length must match the number of -orders in the input");
        }
        this.permutation = permutation;
    }

    public int getBeginTimeBase(){
        return beginTW[0];
    }

    public int getEndTimeBase(){
        return endTW[0];
    }


    public void loadData(String filename) throws FileNotFoundException {
        Scanner in = new Scanner(new File(filename)).useLocale(Locale.US);

        velocity = in.nextDouble();
        capacity = in.nextDouble();

        n = in.nextInt();
        points = new Coordinates[n];
        for (int i = 0; i < n; i++) {
            double latitude = in.nextDouble();
            double longitude = in.nextDouble();
            points[i] = new Coordinates(latitude, longitude);
        }

        timeTripBetweenPoint = new double[n];
        for (int i = 1; i < n; i++) {
            double distance = Coordinates.AsTheCrowFlies(points[i - 1], points[i]);
            timeTripBetweenPoint[i] = distance/velocity;
        }
        double distance = Coordinates.AsTheCrowFlies(points[n - 1], points[0]);
        timeTripBetweenPoint[0] = distance/velocity;

        beginTW = new int[n];
        endTW = new int[n];
        for (int i = 0; i < n; i++) {
            beginTW[i] = in.nextInt();
            endTW[i] = in.nextInt();
        }

        loadingOrderTime = new int[n]; // время, необходимое на погрузку заказа в распределительном центре (мин)
        unLoadingOrderTime = new int[n]; // время, необходимое на разгрузку заказа у клиента (мин)
        weight = new double[n];
        for (int i = 1; i < n; i++) {
            loadingOrderTime[i] = in.nextInt();
            unLoadingOrderTime[i] = in.nextInt();
            weight[i] = in.nextDouble();
        }

        unLoadingOrderTime[0] = 0; // время, необходимое на загрузку на базе
        for (int i = 1; i < n; i++) {
            unLoadingOrderTime[0] += loadingOrderTime[i];
        }

        checkValidData();
    }

    /**
     * В данном методе происходит "грубая" и быстрая проверка входных значений"
     */
    private void checkValidData() {
        //проверяем на перегруз машины
        double sumOrderWeight = 0;
        for (int i = 1; i < n; i++) {
            sumOrderWeight += weight[i];
        }
        if (sumOrderWeight > capacity) {
            System.err.println("Машина поедет из депо с перегрузом");
        }

        //проверим на возможность разгрузиться в временном окне
        for (int i = 0; i < n; i++) {
            if (endTW[i] - beginTW[i] < unLoadingOrderTime[i]) {
                System.err.println("Машина на " + i + "-ом заказе не сможет разгрузиться в предоставленном временном окне");
            }
        }

        //проверяем на физическую возможность проехать по маршруту за рабочее время депо
        double minTimeToTrip = 0;
        for (int i = 0; i < n; i++) {
            minTimeToTrip += timeTripBetweenPoint[i];
        }
        if (minTimeToTrip > endTW[0] - beginTW[0]){
            System.err.println("Машина физически не успеет объехать заданные точки за рабочее время депо");
        }
    }

    /**
     * Возвращает результат симуляции движения по маршруту
     *
     * @param t0 - момент времени, в который машина начинает загрузку на базе
     * @return возвращет структуру данных, в которой записаны результы симуляции
     */
    public ResultSimulationData evaluate(double t0) {

        int cviols = 0; // количество заказов, на которые опоздали (constraint violations)

        double[] arrival = new double[n + 2]; // сюда пишем время прибытия
        double[] delay = new double[n + 2]; // сюда пишем время отбытия

        // начинаем маршрут в депо
        double makeSpan = Math.max(t0, beginTW[0]) ; // переменная времени
        arrival[0] = makeSpan;
        makeSpan += unLoadingOrderTime[0];
        delay[0] = makeSpan; // в депо разгружаться не надо

        // едем по всем заказам
        for (int i = 1; i < n; i++) {
            int node = permutation[i - 1];

            makeSpan = Math.max(makeSpan + timeTripBetweenPoint[node], beginTW[node]); //время прибытия на текущую ноду
            arrival[i] = makeSpan;
            makeSpan += unLoadingOrderTime[node]; // учитываем время разгрузки, теперь тут время убытия с ноды
            delay[i] = makeSpan;

            if (delay[i] > endTW[node]) {
                cviols++;
            }
        }
        // возвращаемся в депо
        makeSpan = Math.max(makeSpan + timeTripBetweenPoint[0], beginTW[0]);
        arrival[n] = makeSpan;
        delay[n] = makeSpan; // в депо разгружаться не надо

        if (makeSpan > endTW[0]) {
            cviols++;
        }

        double tourTime = makeSpan - t0;
        return new ResultSimulationData(tourTime, cviols, arrival, delay, getMetric(tourTime, cviols));
    }

    public double getMetric(double tourcost, int cviols) {
        final double penalty = 40; // временной штраф за опоздание

        return tourcost + cviols * penalty;
    }

    public void printResult(ResultSimulationData result, String filename) throws FileNotFoundException {
        PrintWriter out = new PrintWriter(new File(filename));
        out.println("n = " + n);
        out.println("TourTime = " + Math.round(result.getTourTime()));
        out.println("Cviols = " + result.getCviols());
        out.println("Result metric = " + result.getResultMetric());
        for (int i = 0; i < n + 1; i++) {
            out.println(Math.round(result.getArrival()[i]) + " " + Math.round(result.getDelay()[i]));
        }
        out.close();
    }
}

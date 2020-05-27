import java.io.FileNotFoundException;

public class TestSpace {

    public static void main(String[] args) {
        System.out.println("Приложение вычисляет оптимальное время начала выхода на рейс машины");
        Simulation sim = new Simulation();
        try {
            sim.loadData("src/data.txt");
            sim.setPermutation(new int[]{1, 2}); // говорим каким маршрутом идти

            // тест на корректность работы
            ResultSimulationData result = sim.evaluate(0);
            sim.printResult(result, "src/resultTiming.txt");

            // ищем опимальное время начала работы
            double ResultMetricOptim = Double.MAX_VALUE;
            ResultSimulationData resultOptim = null;
            for (int t = sim.getBeginTimeBase(); t <= sim.getEndTimeBase(); t++) { // тут менее 60*24 итераций
                ResultSimulationData resultTmp = sim.evaluate(t);
                if (resultTmp.getResultMetric() < ResultMetricOptim) { // если метрика улучшилась
                    ResultMetricOptim = resultTmp.getResultMetric();
                    resultOptim = resultTmp;
                }
            }
            sim.printResult(resultOptim, "src/resultOptimTiming.txt");

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.err.println("Не удалось открыть файл");
            return;
        }

    }
}

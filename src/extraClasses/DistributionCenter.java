public class DistributionCenter {
    Coordinates point; // координаты центра
    double startTime; // время начала работы, (в мин)
    double endTime; // время конца работы, (в мин)
    int numResource; // число грузов, которые необходимо развести

    public DistributionCenter(Coordinates point, double startTime, double endTime, int numResource){
        this.point = point;
        this.startTime = startTime;
        this.endTime = endTime;
        this.numResource = numResource;
    }
}

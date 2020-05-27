public class Order {
    Coordinates point; // координаты, куда необходимо доставить заказ
    double weight; // вес (кг)
    int startTime; // время начала работы (в мин)
    int endTime; // время конца работы (в мин)
    int loadingOrderTime; // время, необходимое на погрузку заказа в распределительном центре (мин)
    int unloadingOrderTime; // время, необходимое на разгрузку заказа у клиента (мин)

    public Order(Coordinates point, double weight, int startTime, int endTime, int loadingOrderTime, int unloadingOrderTime) {
        this.point = point;
        this.weight = weight;
        this.startTime = startTime;
        this.endTime = endTime;
        this.loadingOrderTime = loadingOrderTime;
        this.unloadingOrderTime = unloadingOrderTime;
    }
}

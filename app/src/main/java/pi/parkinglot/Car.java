package pi.parkinglot;

public class Car {

    private long id;
    private String brand;
    private long brandid;
    private String model;
    private long modelid;
    private String licenseNumber;

    public Car(long id, String brand, long brandid, String model, long modelid, String licenseNumber){
        this.id = id;
        this.brand = brand;
        this.brandid = brandid;
        this.model = model;
        this.modelid = modelid;
        this.licenseNumber = licenseNumber;
    }

    public Car(String parcelFormat){
        String[] arr = parcelFormat.split("@");
        this.id = Long.parseLong(arr[0]);
        this.brand = arr[1];
        this.brandid = Long.parseLong(arr[2]);
        this.model = arr[3];
        this.modelid = Long.parseLong(arr[4]);
        this.licenseNumber = arr[5];
    }

    public long getId() {
        return id;
    }

    public String getBrand() {
        return brand;
    }

    public long getBrandid() {
        return brandid;
    }

    public String getModel() {
        return model;
    }

    public long getModelid() {
        return modelid;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public String toLog() {
        return id+": "+brand+" "+model+" - "+licenseNumber;
    }

    public String toParcelFormat() {
        return id+"@"+brand+"@"+brandid+"@"+model+"@"+modelid+"@"+licenseNumber;
    }

    public static String toStaticParcelFormat(Car car){
        return car.getId()+"@"+car.getBrand()+"@"+car.getBrandid()+"@"+car.getModel()+"@"+car.getModelid()+"@"+car.getLicenseNumber();
    }

    public static Car toCar(String encodedString){
        return new Car(encodedString);
    }
}

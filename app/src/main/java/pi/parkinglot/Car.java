package pi.parkinglot;

public class Car {

    private long id;
    private String brand;
    private String model;
    private String licenseNumber;

    public Car(long id, String brand, String model, String licenseNumber){
        this.id = id;
        this.brand = brand;
        this.model = model;
        this.licenseNumber = licenseNumber;
    }

    public Car(String parcelFormat){
        String[] arr = parcelFormat.split("@");
        this.id = Long.parseLong(arr[0]);
        this.brand = arr[1];
        this.model = arr[2];
        this.licenseNumber = arr[3];
    }

    public long getId() {
        return id;
    }

    public String getBrand() {
        return brand;
    }

    public String getModel() {
        return model;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public String toParcelFormat() {
        return id+"@"+brand+"@"+model+"@"+licenseNumber;
    }

}

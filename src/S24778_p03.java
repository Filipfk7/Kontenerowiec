import java.io.File;
import java.io.FileOutputStream;
import java.util.Scanner;

public class S24778_p03 {
    final static int CONTAINERS_AMOUNT = 15_000;

    public static void main(String[] args) {
        Container[] containers = generateContainers();
        saveContainers(containers);

        Container[] containersFromFile = loadContainers();

        ContainerShip containerShip = new ContainerShip();
        containerShip.loadContainersOnShip(containersFromFile);

        saveManifestFile(containerShip);
    }


    private static Container[] generateContainers() {
        Container[] containers = new Container[CONTAINERS_AMOUNT];

        for (int i = 0; i < containers.length; i++) {

            int random = (int) (Math.random() * 7) + 1;
            Container container = null;
            if (random == 1) {
                container = new Container();
            } else if (random == 2) {
                container = new CoolingContainer();
            } else if (random == 3) {
                container = new FoodContainer();
            } else if (random == 4) {
                container = new CarContainer();
            } else if (random == 5) {
                container = new TankContainer();
            } else if (random == 6) {
                container = new ChemicalContainer();
            } else if (random == 7) {
                container = new BulkContainer();
            }

            container.generateRandom();
            System.out.println(i+1 + " " + container);
            containers[i] = container;
        }
        return containers;
    }

    private static void saveContainers(Container[] containers) {

        File file = new File("containers.txt");
        try {
            FileOutputStream fos = new FileOutputStream(file);

            for(int i = 0; i < containers.length; i++) {
                fos.write(containers[i].toSaveString().getBytes());
                fos.write('\n');
            }
            fos.close();


        } catch (Exception e) {
            System.out.println("Cannot write to a file!");
        }
    }

    private static void saveManifestFile(ContainerShip containerShip) {

        File file = new File("manifest.txt");
        Container[][][] containersOnShip = containerShip.getContainers();
        int id = 0;
        try {
            FileOutputStream fos = new FileOutputStream(file);
            for (int level = 0; level < 10; level++) {
                for (int row = 0; row < 50; row++) {
                    for (int col = 0; col < 30; col++) {
                        Container container = containersOnShip[row][col][level];
                        String output = id + "\tx:"+row+",y:"+col+",z:"+level+"\t"+container.getWeight()+"kg\t"+container.getGoods();
                        fos.write(output.getBytes());
                        fos.write('\n');
                        id++;
                    }
                }
            }
            fos.close();
        } catch(Exception e) {
            System.out.println("Cannot write to a file!");
        }
    }


    private static Container[] loadContainers() {
        File file = new File("containers.txt");
        Container[] containers = new Container[CONTAINERS_AMOUNT];

        try {
            int lineNumber = 0;
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();

                String[] split = line.split(";");
                String containerType = split[0];
                double weight = Double.parseDouble(split[1]);
                String goods = split[2];
                String color = split[3];

                if (containerType.equals("Container")) {
                    Container loadedContainer = new Container(weight, goods, color);
                    containers[lineNumber] = loadedContainer;
                } else if (containerType.equals("CoolingContainer")) {
                    double minTemp = Double.parseDouble(split[4]);
                    double maxTemp = Double.parseDouble(split[5]);
                    double desireTemp = Double.parseDouble(split[6]);
                    boolean isPowerRequired = Boolean.parseBoolean(split[7]);
                    CoolingContainer loadedContainer = new CoolingContainer(weight, color, goods, minTemp, maxTemp, desireTemp, isPowerRequired);
                    containers[lineNumber] = loadedContainer;
                } else if (containerType.equals("ChemicalContainer")) {
                    double volumeOfSubstance = Double.parseDouble(split[4]);
                    double densityOfSubstance = Double.parseDouble(split[5]);
                    double desiredTemperature = Double.parseDouble(split[6]);
                    int dangerLevel = Integer.parseInt(split[7]);
                    boolean isRadioactive = Boolean.parseBoolean(split[8]);
                    ChemicalContainer loadedContainer = new ChemicalContainer(weight, color, goods, volumeOfSubstance, densityOfSubstance, desiredTemperature, dangerLevel, isRadioactive);
                    containers[lineNumber] = loadedContainer;
                } else if (containerType.equals("FoodContainer")) { //hasNitrogen()+ ";"+hasAirtightSeal()+";"+getDaysTillExpire()+";";
                    double minTemp = Double.parseDouble(split[4]);
                    double maxTemp = Double.parseDouble(split[5]);
                    double desireTemp = Double.parseDouble(split[6]);
                    boolean isPowerRequired = Boolean.parseBoolean(split[7]);
                    boolean hasNitrogen = Boolean.parseBoolean(split[8]);
                    boolean hasAirtightSeal = Boolean.parseBoolean(split[9]);
                    int daysTillExpire = Integer.parseInt(split[10]);
                    FoodContainer loadedContainer = new FoodContainer(weight, color, goods, minTemp, maxTemp, desireTemp, isPowerRequired, hasNitrogen, hasAirtightSeal, daysTillExpire);
                    containers[lineNumber] = loadedContainer;
                } else if (containerType.equals("TankContainer")) {
                    double volumeOfSubstance = Double.parseDouble(split[4]);
                    double densityOfSubstance = Double.parseDouble(split[5]);
                    double desiredTemperature = Double.parseDouble(split[6]);
                    TankContainer loadedContainer = new TankContainer(weight, color, goods, volumeOfSubstance, densityOfSubstance, desiredTemperature);
                    containers[lineNumber] = loadedContainer;
                } else if (containerType.equals("BulkContainer")) {
                    BulkContainer bulkContainer = new BulkContainer(weight, color, goods);
                    containers[lineNumber] = bulkContainer;
                } else if (containerType.equals("CarContainer")) {
                    boolean isCarBroken = Boolean.parseBoolean(split[4]);
                    boolean isTankFull = Boolean.parseBoolean(split[5]);
                    int carWidth = Integer.parseInt(split[6]);
                    int carLength = Integer.parseInt(split[7]);
                    int carHeight = Integer.parseInt(split[8]);
                    containers[lineNumber] = new CarContainer(weight, color, goods, isCarBroken, isTankFull, carWidth, carLength, carHeight);
                } else {
                    System.err.println("Cannot recognise container: " + containerType);
                }
                lineNumber++;
            }
        } catch (Exception e) {
            System.out.println("Cannot load from file!");
            e.printStackTrace();
        }
        return containers;
    }
}


class ContainerShip {
    private Container[][][] containersOnShip;


    public ContainerShip() {
        containersOnShip = new Container[50][30][10];
    }

    public Container[][][] getContainersOnShip() {
        return containersOnShip;
    }

    public void loadContainersOnShip(Container[] containers) {
        int[][] distributionPlan = getDistributionPlan();
        sortContainers(containers);

        for(int level = 0; level < 10; level++) {
            for (int row = 0; row < 50; row++) {
                for (int col = 0; col < 30; col++) {
                    int containerId = distributionPlan[row][col] + (1500*level);
                    Container containerToPlace = containers[containerId];

                    containersOnShip[row][col][level] = containerToPlace;
                }
            }
        }
    }

    public static void sortContainers(Container[] tab) {
        for(int j = 0; j < tab.length; j++) {
            Container maxContainer = tab[j];
            int maxContainerId = j;

            for (int i = j; i < tab.length; i++) {
                if (tab[i].getWeight() > maxContainer.getWeight()) {
                    maxContainer = tab[i];
                    maxContainerId = i;
                }
            }

            if(maxContainer.getWeight() > tab[j].getWeight()) {
                Container temp = tab[j];
                tab[j] = maxContainer;
                tab[maxContainerId] = temp;
            }
        }
    }

    // plan rozmieszczenia
    private static int[][] getDistributionPlan() {
        int[][] placesOnShip = new int[50][30];

        int containerID = 0;
        for(int i = 0; i < 15; i++) {
            int minX = i;
            int maxX = 29 - i;

            for (int col = minX; col <= maxX; col++) {

                if (containerID % 2 == 0) {
                    // liczby parzyste umieszczaj na X 49 (od 29 do 0)
                    placesOnShip[49 - i][29 - col] = containerID++;
                    placesOnShip[i][col] = containerID++;
                } else {
                    //liczby nieparzyste umieszczaj na osi X 0 (od 0 do 29)
                    placesOnShip[i][col] = containerID++;
                    placesOnShip[49 - i][29 - col] = containerID++;
                }
            }

            int minY = i + 1;
            int maxY = 49 - i - 1;
            for(int row = minY; row <= maxY; row++){

                if (containerID % 2 == 0){
                    // liczby parzyste umeiszczaj na osi Y =0 (od 48 do 1)
                    placesOnShip[49 - row][i] = containerID++;
                    placesOnShip[row][29 - i] = containerID++;
                }
                else {
                    //liczby nieparzyste umiezczaj na osi Y=29 (od 1 do 48)
                    placesOnShip[row][29 - i] = containerID++;
                    placesOnShip[49 - row][i] = containerID++;
                }
            }
        }

        return placesOnShip;
    }

    public Container[][][] getContainers() {
        return containersOnShip;
    }
}

class BulkContainer extends Container {


    public BulkContainer() {
    }

    public BulkContainer(double weight, String color, String goods) {
        super(weight, color, goods);
    }

    @Override
    public void generateRandom() {
        super.generateRandom();
        String[] good = {"Quartz", "Cereal", "Cement"};
        String goods = good[(int) (Math.random() * good.length)];
        super.setGoods(goods);

    }

    @Override
    public String toString() {
        return super.toString();
    }
}

class ChemicalContainer extends TankContainer {
    private int explosiveDangerLevel; //0-10
    private boolean isRadioactive;

    public ChemicalContainer() {
    }

    public ChemicalContainer(double weight, String color, String goods, double volumeOfSubstance, double densityOfSubstance, double desiredTemperature, int explosiveDangerLevel, boolean isRadioactive) {
        super(weight, color, goods, volumeOfSubstance, densityOfSubstance, desiredTemperature);
        this.explosiveDangerLevel = explosiveDangerLevel;
        this.isRadioactive = isRadioactive;
    }

    @Override
    public void generateRandom() {
        super.generateRandom();
        explosiveDangerLevel = (int) (Math.random() * 11);
        isRadioactive = Math.random() > 0.5;
        String[] good = {"Poisonous substance", "Flammable liquid", "Corrosive substance"};
        String goods = good[(int) (Math.random() * good.length)];
        super.setGoods(goods);

    }

    public int getExplosiveDangerLevel() {
        return explosiveDangerLevel;
    }

    public boolean isRadioactive() {
        return isRadioactive;
    }

    @Override
    public String toSaveString() {
        return super.toSaveString() +getExplosiveDangerLevel()+";"+isRadioactive()+";";
    }

    @Override
    public String toString() {
        return super.toString() + "\tExplosive Danger Level: " + getExplosiveDangerLevel() + "\tIs Substance Radioactive: " + isRadioactive();
    }
}


class TankContainer extends Container {
    private double volumeOfSubstance;
    private double densityOfSubstance;
    private double desiredTemperature;

    public TankContainer() {
    }

    public TankContainer(double weight, String color, String goods, double volumeOfSubstance, double densityOfSubstance, double desiredTemperature) {
        super(weight, color, goods);
        this.volumeOfSubstance = volumeOfSubstance;
        this.densityOfSubstance = densityOfSubstance;
        this.desiredTemperature = desiredTemperature;
    }

    @Override
    public void generateRandom() {
        super.generateRandom();
        volumeOfSubstance = Math.random() * (20000 - 16000) + 16000;
        densityOfSubstance = Math.random() * 1;
        desiredTemperature = Math.random() * (30 - 20) - 20;
        String[] good = {"Gas", "Gasoline", "Petroleum"};
        String goods = good[(int) (Math.random() * good.length)];
        super.setGoods(goods);
    }

    public double getVolumeOfSubstance() {
        return volumeOfSubstance;
    }

    public double getDensityOfSubstance() {
        return densityOfSubstance;
    }

    public double getDesiredTemperature() {
        return desiredTemperature;
    }

    @Override
    public String toSaveString() {
        return super.toSaveString() +getVolumeOfSubstance()+";"+getDensityOfSubstance()+";"+getDesiredTemperature()+";";
    }

    @Override
    public String toString() {
        return super.toString() + "\tVolume of substance: " + getVolumeOfSubstance() + "\tdensityOfSubstance: " + getDensityOfSubstance() + "\tdesiredTemperature: " + getDesiredTemperature();
    }
}


class CarContainer extends Container {
    private boolean isCarBroken;
    private boolean isTankFull;
    private int carWidth;
    private int carLength;
    private int carHeight;

    public CarContainer() {

    }

    public CarContainer(double weight, String color, String goods, boolean isCarBroken, boolean isTankFull, int carWidth, int carLength, int carHeight) {
        super(weight, color, goods);
        this.isCarBroken = isCarBroken;
        this.isTankFull = isTankFull;
        this.carWidth = carWidth;
        this.carLength = carLength;
        this.carHeight = carHeight;
    }

    @Override
    public void generateRandom() {
        super.generateRandom();
        isCarBroken = Math.random() > 0.5;
        isTankFull = Math.random() > 0.5;
        carWidth = (int) (Math.random() * (2500 - 1380)) + 1380;
        carLength = (int) (Math.random() * (13600 - 3000)) + 3000;
        carHeight = (int) (Math.random() * (3000 - 1330)) + 1330;
        setGoods("cars");
    }

    public boolean isCarBroken() {
        return isCarBroken;
    }

    public boolean isTankFull() {
        return isTankFull;
    }

    public int getCarWidth() {
        return carWidth;
    }

    public int getCarLength() {
        return carLength;
    }

    public int getCarHeight() {
        return carHeight;
    }

    @Override
    public String toSaveString() {
        return super.toSaveString() +isCarBroken()+";"+isTankFull()+";"+getCarWidth()+";"+getCarLength()+";"+getCarHeight()+";";
    }

    @Override
    public String toString() {
        return super.toString() + "\tIs car broken: " + isCarBroken() + "\tIs car fully tank: " + isTankFull() + "\tCar width: " + getCarWidth() + "\tCar length: " + getCarLength() + "\tCar height: " + getCarHeight();
    }
}

class FoodContainer extends CoolingContainer {

    boolean hasNitrogen;
    boolean hasAirtightSeal;
    int daysTillExpire;


    public FoodContainer() {
    }

    public FoodContainer(double weight, String color, String goods, double minTemperature, double maxTemperature, double desiredTemperature, boolean isPowerRequired, boolean hasNitrogen, boolean hasAirtightSeal, int daysTillExpire) {
        super(weight, color, goods, minTemperature, maxTemperature, desiredTemperature, isPowerRequired);
        this.hasNitrogen = hasNitrogen;
        this.hasAirtightSeal = hasAirtightSeal;
        this.daysTillExpire = daysTillExpire;
    }

    public int getDaysTillExpire() {
        return daysTillExpire;
    }

    public boolean hasNitrogen() {
        return hasNitrogen;
    }

    public boolean hasAirtightSeal() {
        return hasAirtightSeal;
    }

    @Override
    public void generateRandom() {
        super.generateRandom();
        hasNitrogen = Math.random() > 0.5;
        hasAirtightSeal = Math.random() > 0.5;
        daysTillExpire = (int) (Math.random() * 60 + 3);
        String[] good = {"Fishes", "Meat", "Apples"};
        String goods = good[(int) (Math.random() * good.length)];
        super.setGoods(goods);
    }

    @Override
    public String toSaveString() {
        return super.toSaveString() + hasNitrogen()+ ";"+hasAirtightSeal()+";"+getDaysTillExpire()+";";
    }


    @Override
    public String toString() {
        return super.toString() + "\tDays Till Expire:" + getDaysTillExpire() + "\tHas nitrogen:" + hasNitrogen() + "\thasAirtightSeal: " + hasAirtightSeal() + "\tisPowerRequired: " + isPowerRequired();
    }
}

class CoolingContainer extends Container {
    private double minTemperature;
    private double maxTemperature;
    private double desiredTemperature;
    private boolean isPowerRequired;

    public CoolingContainer() {
    }
    public CoolingContainer(double weight, String color, String goods, double minTemperature, double maxTemperature, double desiredTemperature, boolean isPowerRequired) {
        super(weight, color, goods);
        this.minTemperature = minTemperature;
        this.maxTemperature = maxTemperature;
        this.desiredTemperature = desiredTemperature;
        this.isPowerRequired = isPowerRequired;
    }

    @Override
    public void generateRandom() {
        super.generateRandom();
        minTemperature = -30;
        maxTemperature = 20;
        isPowerRequired = Math.random() > 0.5;
        desiredTemperature = (int) (Math.random() * 50) - 30;
        String[] good = {"Medicines"};
        String goods = good[(int) (Math.random() * good.length)];
        super.setGoods(goods);
    }

    public double getMinTemperature() {
        return minTemperature;
    }

    public double getMaxTemperature() {
        return maxTemperature;
    }

    public double getDesiredTemperature() {
        return desiredTemperature;
    }

    public boolean isPowerRequired() {
        return isPowerRequired;
    }

    @Override
    public String toSaveString() {
        return super.toSaveString() +getMinTemperature()+";"+getMaxTemperature()+";"+getDesiredTemperature()+";"+isPowerRequired()+";";
    }

    @Override
    public String toString() {
        return super.toString() + "maxTemperature: " + getMaxTemperature() + " minTemperature: " + getMinTemperature() + " desiredTemperature: " + getDesiredTemperature() + " powerRequired: " + isPowerRequired();
    }
}


class Container extends Object {
    private double weight; // in kg
    private String color;
    private String goods;

    public Container() {
    }

    public Container(double weight, String color, String goods) {
        this.weight = weight;
        this.color = color;
        this.goods = goods;
    }

    public void generateRandom() {
        double weight = (Math.random() * 25_000) + 2_300;
        String[] colors = {"Blue", "Red", "White", "Black"};
        String color = colors[(int) (Math.random() * colors.length)];
        String[] good = {"Clothes","Electronics","Furniture"};
        String goods = good[(int) (Math.random() * good.length)];
        this.weight = weight;
        this.color = color;
        this.goods = goods;
    }

    public void setGoods(String goods) {
        this.goods = goods;
    }

    public double getWeight() {
        return weight;
    }

    public String getColor() {
        return color;
    }

    public String getGoods() {
        return goods;
    }

    public String toSaveString() {
        return getClass().getName() + ";"+getWeight()+";"+getGoods()+";"+getColor()+";";
    }

    @Override
    public String toString() {
        return getClass().getName() + "\tWeight:" + getWeight() + "\tGoods:" + getGoods() + "\tColor:" + getColor() + " ";
    }
}
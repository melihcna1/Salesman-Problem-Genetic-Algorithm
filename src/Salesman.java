import java.util.*;

class City {
    public int x, y;

    public City() {
        this.x = (int)(Math.random()*200);
        this.y = (int)(Math.random()*200);
    }

    public double distanceTo(City city) {
        int xDistance = Math.abs(this.x - city.x);
        int yDistance = Math.abs(this.y - city.y);
        double distance = Math.sqrt((xDistance*xDistance) + (yDistance*yDistance));
        return distance;
    }
    //overriding the toString method
    public String toString() {
        return "City: (" + x + ", " + y + ")";
    }
}

class Tour {
    private ArrayList<City> tour = new ArrayList<City>();
    private double fitness = 0;
    private int distance = 0;

    public Tour() {
        for (int i = 0; i < TourManager.numberOfCities(); i++) {
            tour.add(null);
        }
    }

    public Tour(ArrayList<City> tour) {
        this.tour = tour;
    }

    public void generateIndividual() {
        for (int i = 0; i < TourManager.numberOfCities(); i++) {
            setCity(i, TourManager.getCity(i));
        }
        Collections.shuffle(tour);
    }

    public City getCity(int tourPosition) {
        return tour.get(tourPosition);
    }

    public void setCity(int tourPosition, City city) {
        tour.set(tourPosition, city);
        fitness = 0;
        distance = 0;
    }

    public double getFitness() {
        if (fitness == 0) {
            fitness = 1/(double)getDistance();
        }
        return fitness;
    }

    public int getDistance() {
        if (distance == 0) {
            int tourDistance = 0;
            for (int i=0; i < tourSize(); i++) {
                City fromCity = getCity(i);
                City destinationCity;
                if (i+1 < tourSize()){
                    destinationCity = getCity(i+1);
                }
                else{
                    destinationCity = getCity(0);
                }
                tourDistance += fromCity.distanceTo(destinationCity);
            }
            distance = tourDistance;
        }
        return distance;
    }

    public int tourSize() {
        return tour.size();
    }

    public boolean containsCity(City city) {
        return tour.contains(city);
    }

    @Override
    public String toString() {
        String geneString = "|";
        for (int i = 0; i < tourSize(); i++) {
            geneString += getCity(i)+"|";
        }
        return geneString;
    }
}

class Population {
    private ArrayList<Tour> tours = new ArrayList<Tour>();
    private double mutationRate = 0.015;
    private boolean elitism = true;
    private int tournamentSize = 5;

    public Population(int populationSize) {
        for (int i = 0; i < populationSize; i++) {
            Tour newTour = new Tour();
            newTour.generateIndividual();
            tours.add(newTour);
        }
    }

    public void saveTour(int index, Tour tour) {
        tours.set(index, tour);
    }

    public Tour getTour(int index) {
        return tours.get(index);
    }

    public Tour getFittest() {
        Tour fittest = tours.get(0);
        for (int i = 1; i < populationSize(); i++) {
            if (fittest.getFitness() <= getTour(i).getFitness()) {
                fittest = getTour(i);
            }
        }
        return fittest;
    }

    public int populationSize() {
        return tours.size();
    }
}

class GA {

    public static void main(String[] args) {

        TourManager.addCity(new City());
        TourManager.addCity(new City());
        TourManager.addCity(new City());
        TourManager.addCity(new City());
        TourManager.addCity(new City());
        TourManager.addCity(new City());
        TourManager.addCity(new City());
        TourManager.addCity(new City());
        TourManager.addCity(new City());
        TourManager.addCity(new City());

        Population population = new Population(50);
        population.saveTour(0, new Tour(TourManager.getTour()));

        int generationCount = 0;
        while (generationCount < 1000) {
            generationCount++;
            population = evolvePopulation(population, true,1.5);
        }

        System.out.println("Finished");
        System.out.println("Final distance: " + population.getFittest().getDistance());
        System.out.println("Solution:");
        System.out.println(population.getFittest());
    }

    public static Population evolvePopulation(Population population, boolean elitism, double mutationRate) {
        Population newPopulation = new Population(population.populationSize());
        int elitismOffset = 0;
        if (elitism) {
            newPopulation.saveTour(0, population.getFittest());
            elitismOffset = 1;
        }
        for (int i = elitismOffset; i < newPopulation.populationSize(); i++) {
            Tour parent1 = selection(population);
            Tour parent2 = selection(population);
            Tour child = crossover(parent1, parent2);
            newPopulation.saveTour(i, child);
        }

        for (int i = elitismOffset; i < newPopulation.populationSize(); i++) {
            mutate(newPopulation.getTour(i), mutationRate);
        }
        return newPopulation;
    }

    public static Tour crossover(Tour parent1, Tour parent2) {
        Tour child = new Tour();

        int startPos = (int) (Math.random() * parent1.tourSize());
        int endPos = (int) (Math.random() * parent1.tourSize());

        for (int i = 0; i < child.tourSize(); i++) {
            if (startPos < endPos && i > startPos && i < endPos) {
                child.setCity(i, parent1.getCity(i));
            } else if (startPos > endPos) {
                if (!(i < startPos && i > endPos)) {
                    child.setCity(i, parent1.getCity(i));
                }
            }
        }

        for (int i = 0; i < parent2.tourSize(); i++) {
            if (!child.containsCity(parent2.getCity(i))) {
                for (int ii = 0; ii < child.tourSize(); ii++) {
                    if (child.getCity(ii) == null) {
                        child.setCity(ii, parent2.getCity(i));
                        break;
                    }
                }
            }
        }
        return child;
    }
    public static void mutate(Tour tour, double mutationRate) {
        for(int tourPos1=0; tourPos1 < tour.tourSize(); tourPos1++){
            if(Math.random() < mutationRate){
                int tourPos2 = (int)(tour.tourSize() * Math.random());
                City city1 = tour.getCity(tourPos1);
                City city2 = tour.getCity(tourPos2);
                tour.setCity(tourPos2, city1);
                tour.setCity(tourPos1, city2);
            }
        }
    }
    private static Tour selection(Population pop) {
        int tournamentSize=5;
        Population tournament = new Population(tournamentSize);
        for (int i = 0; i < tournamentSize; i++) {
            int randomId = (int) (Math.random() * pop.populationSize());
            tournament.saveTour(i, pop.getTour(randomId));
        }
        Tour fittest = tournament.getFittest()

                ;
        return fittest;
    }
}

class TourManager {

    private static ArrayList<City> destinationCities = new ArrayList<City>();

    public static void addCity(City city) {
        destinationCities.add(city);
    }

    public static City getCity(int index) {
        return destinationCities.get(index);
    }

    public static int numberOfCities() {
        return destinationCities.size();
    }

    public static ArrayList<City> getTour(){
        return destinationCities;
    }
}

package nl.rug.oop.flaps.aircraft_editor.model;

import lombok.Getter;
import nl.rug.oop.flaps.simulation.model.aircraft.Aircraft;
import nl.rug.oop.flaps.simulation.model.aircraft.CargoArea;
import nl.rug.oop.flaps.simulation.model.aircraft.FuelTank;
import nl.rug.oop.flaps.simulation.model.airport.Airport;
import nl.rug.oop.flaps.simulation.model.cargo.CargoType;
import nl.rug.oop.flaps.simulation.model.cargo.CargoUnit;
import nl.rug.oop.flaps.simulation.model.world.WorldSelectionModel;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.List;

import static javax.swing.UIManager.get;

public class EditorModel {
    @Getter
    private Image image;

    private Aircraft aircraft;

    @Getter
    private final List<Point2D> fuelPoints;
    @Getter
    private final List<Point2D> cargoPoints;

    @Getter
    private Point2D selectedPoint;

    private List<FuelTank> fuelTanks;
    private List <CargoArea> cargoAreas;

    @Getter
    private FuelTank selectedFuelTank;
    @Getter
    private CargoArea selectedCargoArea;

    private Set<EditorModelListener> listeners;

    public EditorModel(Aircraft aircraft) {
        this.aircraft = aircraft;
        fuelTanks =  aircraft.getType().getFuelTanks();
        fuelPoints = new ArrayList<>();
        cargoAreas =  aircraft.getType().getCargoAreas();
        cargoPoints = new ArrayList<>();
        listeners = new HashSet<>();
        try {
            image = ImageIO.read(Path.of("images", "readme", "blueprint_coordinates.png").toFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
        initFuelTanks();
        initCargoAreas();
        selectedFuelTank = null;
        selectedCargoArea = null;
    }

    private void initFuelTanks() {
        Point2D fuelPoint;
        for (int i = 0; i < fuelTanks.size(); i++) {
        fuelPoint = fuelTanks.get(i).getCoords();
        Point2D airCoords = imgToAirCoords(fuelPoint);
        fuelPoints.add(airCoords);
        }
    }

    private void initCargoAreas() {
        Point2D cargoPoint;
        for (int i = 0; i < cargoAreas.size(); i++) {
            cargoPoint = cargoAreas.get(i).getCoords();
            Point2D cargoAirCoords = imgToAirCoords(cargoPoint);
            cargoPoints.add(cargoAirCoords);
        }
    }

    private Point2D imgToAirCoords(Point2D point) {
        double length = aircraft.getType().getLength();
        int height = image.getHeight(null);
        int scale = (int) (height/ length);
        double x = point.getX();
        double y = point.getY();

        return new Point2D.Double((x * scale + image.getWidth(null)) / 2.0, y * scale);
    }

    private Point2D airToImgCoords(Point2D point) {
        double length = aircraft.getType().getLength();
        int height = image.getHeight(null);
        int scale = (int) (height/ length);
        double x = point.getX();
        double y = point.getY();

        return new Point2D.Double((x * 2.0 - image.getWidth(null) ) / scale, y / scale);
    }

    public void selectPoint(Point2D clickPosition, int radius) {
        for(Point2D fuelPoint : fuelPoints) {
            if(fuelPoint.distance(clickPosition) < radius) {
                selectedPoint = fuelPoint;
                listeners.forEach(EditorModelListener::selectedPointUpdate);
                selectedCargoArea = null;
                Point2D imgCoords = airToImgCoords(selectedPoint);
                for(FuelTank fuelTank : fuelTanks) {
                    if (imgCoords.distance(fuelTank.getCoords()) < radius) {
                        selectedFuelTank = fuelTank;
                    }
                }
                return;
            }
        }
        selectedPoint = null;
        listeners.forEach(EditorModelListener::selectedPointUpdate);
        for(Point2D cargoPoint : cargoPoints) {
            if(cargoPoint.distance(clickPosition) < radius) {
                selectedPoint = cargoPoint;
                listeners.forEach(EditorModelListener::selectedPointUpdate);
                selectedFuelTank = null;
                Point2D imgCoords = airToImgCoords(selectedPoint);
                for(CargoArea cargoArea : cargoAreas) {
                    if (imgCoords.distance(cargoArea.getCoords()) < radius) {
                        selectedCargoArea = cargoArea;
                    }
                }
                return;
            }
        }
        selectedPoint = null;
        listeners.forEach(EditorModelListener::selectedPointUpdate);
    }

    public void addListener(EditorModelListener listener) {
        listeners.add(listener);
    }

    public String getFuelTankName() {
        return selectedFuelTank.getName();
    }

    public int getFuelTankCapacity() {
        return selectedFuelTank.getCapacity();
    }

    public double getFuelTankAmount() {
        return aircraft.getFuelAmountForFuelTank(selectedFuelTank);
    }

    public String getCargoAreaName() {
        return selectedCargoArea.getName();
    }

    public double getCargoAreaCapacity() {
        return selectedCargoArea.getMaxWeight();
    }

    public double getCargoAreaAmount() {
        Set<CargoUnit> cargoUnits = aircraft.getCargoAreaContents(selectedCargoArea);
        double weight = 0;
        for(CargoUnit cargoUnit:cargoUnits) {
           weight += cargoUnit.getWeight();
        }
        return weight;
    }

    public Map<String, CargoType> getCargoTypes() {
        return aircraft.getWorld().getCargoTypes();
    }

    public double getRange() {
        return (aircraft.getTotalFuel() / aircraft.getType().getFuelConsumption()) * aircraft.getType().getCruiseSpeed();
    }

    public double getProfitEstimation() {
        Airport origin = aircraft.getWorld().getSelectionModel().getSelectedAirport();
        Airport destination = aircraft.getWorld().getSelectionModel().getSelectedDestinationAirport();

        double fuelPriceOrigin = origin.getFuelPriceByType(aircraft.getType().getFuelType());
        double fuelLoaded = aircraft.getTotalFuel();
        double fuelCost = fuelPriceOrigin * fuelLoaded;

        double cargoRevenue = 0.0;
        Set<CargoUnit> cargoUnits = aircraft.getCargoAreaContents(selectedCargoArea);
        for(CargoUnit cargoUnit:cargoUnits) {
            double weight = cargoUnit.getWeight();
            double priceDestination = destination.getCargoPriceByType(cargoUnit.getCargoType());
            cargoRevenue += weight * priceDestination;
        }

        return cargoRevenue - fuelCost;
    }

    public double getTotalWeight() {
        double totalWeight = aircraft.getType().getEmptyWeight();
        Set<CargoUnit> cargoUnits = aircraft.getCargoAreaContents(selectedCargoArea);
        for(CargoUnit cargoUnit : cargoUnits) {
            totalWeight += cargoUnit.getWeight();
        }
        totalWeight += aircraft.getTotalFuel();
        return totalWeight;
    }

    public void addCargo(CargoType cargoType, double weight) {
        aircraft.addCargo(selectedCargoArea, cargoType, weight);
        listeners.forEach(EditorModelListener::selectedPointUpdate);
    }

    public void removeCargo(CargoType cargoType, double weight) {
        aircraft.removeCargo(selectedCargoArea, cargoType, weight);
        listeners.forEach(EditorModelListener::selectedPointUpdate);
    }

    public void addFuelToTank(double amount) {
        double currentAmount = aircraft.getFuelAmountForFuelTank(selectedFuelTank);
        aircraft.setFuelAmountForFuelTank(selectedFuelTank, currentAmount + amount);
        listeners.forEach(EditorModelListener::selectedPointUpdate);
    }

    public Point2D getCOG() {
        double aircraftWeight = aircraft.getType().getEmptyWeight();
        Point2D emptyCog = getEmptyCog();
        double xCoord = emptyCog.getX() * aircraftWeight;
        double yCoord = emptyCog.getY() * aircraftWeight;
        double totalWeight = aircraftWeight;
        for(FuelTank fuelTank : fuelTanks) {
            Point2D fuelTankCoords = imgToAirCoords(fuelTank.getCoords());
            Double fuelTankWeight = aircraft.getFuelAmountForFuelTank(fuelTank);
            xCoord += fuelTankWeight * fuelTankCoords.getX();
            yCoord += fuelTankWeight * fuelTankCoords.getY();
            totalWeight += fuelTankWeight;
        }
        for(CargoArea cargoArea : cargoAreas) {
            Point2D cargoAreaCoords = imgToAirCoords(cargoArea.getCoords());
            Double cargoAreaWeight = aircraft.getCargoAreaWeight(cargoArea);
            xCoord += cargoAreaWeight * cargoAreaCoords.getX();
            yCoord += cargoAreaWeight * cargoAreaCoords.getY();
            totalWeight += cargoAreaWeight;
        }

        return new Point2D.Double(xCoord/totalWeight, yCoord/totalWeight);
    }

    public Point2D getEmptyCog() {
        double emptyCogX = aircraft.getType().getEmptyCgX();
        double emptyCogY = aircraft.getType().getEmptyCgY();
        return imgToAirCoords(new Point2D.Double(emptyCogX, emptyCogY));
    }

    public boolean canDepart() {
        Airport origin = aircraft.getWorld().getSelectionModel().getSelectedAirport();
        Airport destination = aircraft.getWorld().getSelectionModel().getSelectedDestinationAirport();
        if(destination == null) {
            return false;
        }
        // destinations never have capacity
        //boolean destinationHasCapacity = destination.canAcceptIncomingAircraft();
        double distance = origin.getGeographicCoordinates().distanceTo(destination.getGeographicCoordinates());
        // range is never large enough
        boolean enoughRange = distance <= getRange() * 10000;
        double cogOffset = getEmptyCog().distance(getCOG());
        double cogTolerance = aircraft.getType().getCgTolerance() * aircraft.getType().getLength();
        boolean cogWithinTolerance = cogOffset <= cogTolerance;
        double maxTakeOffWeight = aircraft.getType().getMaxTakeoffWeight();
        boolean weightWithinLimit = getTotalWeight() <= maxTakeOffWeight;
        if(enoughRange && cogWithinTolerance && weightWithinLimit) {
            return true;
        }
        return false;
    }

    public WorldSelectionModel getWorldSelectionModel() {
        return aircraft.getWorld().getSelectionModel();
    }
}

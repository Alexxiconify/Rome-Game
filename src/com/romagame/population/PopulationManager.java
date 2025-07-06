package com.romagame.population;

import com.romagame.map.Province;
import com.romagame.map.Province.PopType;
import java.util.*;
public class PopulationManager {
    private Map<String, DevelopmentProject> activeProjects;
    private Map<String, BuildingProject> buildingProjects;
    
    public PopulationManager() {
        this.activeProjects = new HashMap<>();
        this.buildingProjects = new HashMap<>();
    }
    
    public void update() {
        // Update all active development projects
        for (DevelopmentProject project : activeProjects.values()) {
            project.update();
            if (project.isCompleted()) {
                completeDevelopmentProject(project);
            }
        }
        
        // Update building projects
        for (BuildingProject project : buildingProjects.values()) {
            project.update();
            if (project.isCompleted()) {
                completeBuildingProject(project);
            }
        }
        
        // Clean up completed projects
        activeProjects.entrySet().removeIf(entry -> entry.getValue().isCompleted());
        buildingProjects.entrySet().removeIf(entry -> entry.getValue().isCompleted());
    }
    
    public boolean startDevelopmentProject(Province province, DevelopmentType type, int workers) {
        if (province.getPopulation() < workers) {
            return false; // Not enough population
        }
        
        String projectId = province.getId() + "_" + type.name() + "_" + System.currentTimeMillis();
        DevelopmentProject project = new DevelopmentProject(projectId, province, type, workers);
        activeProjects.put(projectId, project);
        
        // Deduct workers from population
        province.setPop(PopType.PEASANTS, province.getPop(PopType.PEASANTS) - workers);
        
        return true;
    }
    
    public boolean startBuildingProject(Province province, BuildingType type, int workers) {
        if (province.getPopulation() < workers) {
            return false; // Not enough population
        }
        
        String projectId = province.getId() + "_" + type.name() + "_" + System.currentTimeMillis();
        BuildingProject project = new BuildingProject(projectId, province, type, workers);
        buildingProjects.put(projectId, project);
        
        // Deduct workers from population
        province.setPop(PopType.CRAFTSMEN, province.getPop(PopType.CRAFTSMEN) - workers);
        
        return true;
    }
    
    private void completeDevelopmentProject(DevelopmentProject project) {
        Province province = project.getProvince();
        DevelopmentType type = project.getType();
        
        switch (type) {
            case INFRASTRUCTURE:
                province.setDevelopment(province.getDevelopment() + 2.0);
                province.addModifier("Improved Infrastructure");
                break;
            case AGRICULTURE:
                province.setDevelopment(province.getDevelopment() + 1.5);
                province.addModifier("Agricultural Development");
                break;
            case TRADE:
                province.setDevelopment(province.getDevelopment() + 1.0);
                province.addModifier("Trade Development");
                break;
            case MILITARY:
                province.setDevelopment(province.getDevelopment() + 1.0);
                province.addModifier("Military Infrastructure");
                break;
        }
        
        // Return workers to population
        province.setPop(PopType.PEASANTS, province.getPop(PopType.PEASANTS) + project.getWorkers());
    }
    
    private void completeBuildingProject(BuildingProject project) {
        Province province = project.getProvince();
        BuildingType type = project.getType();
        
        // Add building to province
        province.addBuilding(type.name());
        
        // Apply building effects
        switch (type) {
            case FORUM:
                province.addModifier("Trade Income +10%");
                break;
            case TEMPLE:
                province.addModifier("Population Happiness +5%");
                break;
            case AQUEDUCT:
                province.addModifier("Population Growth +15%");
                break;
            case WALLS:
                province.addModifier("Defense +20%");
                break;
            case BARRACKS:
                province.addModifier("Recruitment Speed +25%");
                break;
            case MARKET:
                province.addModifier("Tax Income +15%");
                break;
            case BATHHOUSE:
                province.addModifier("Population Happiness +10%");
                break;
            case WORKSHOP:
                province.addModifier("Production +20%");
                break;
        }
        
        // Return workers to population
        province.setPop(PopType.CRAFTSMEN, province.getPop(PopType.CRAFTSMEN) + project.getWorkers());
    }
    
    public List<DevelopmentProject> getActiveDevelopmentProjects() {
        return new ArrayList<>(activeProjects.values());
    }
    
    public List<BuildingProject> getActiveBuildingProjects() {
        return new ArrayList<>(buildingProjects.values());
    }
    
    public enum DevelopmentType {
        INFRASTRUCTURE, AGRICULTURE, TRADE, MILITARY
    }
    
    public enum BuildingType {
        FORUM, TEMPLE, AQUEDUCT, WALLS, BARRACKS, MARKET, BATHHOUSE, WORKSHOP
    }
} 
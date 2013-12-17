package com.example.buildinggl;

public class Floor {
	private String floorName;
	private int layerId;

	public Floor(String floorName, int layerId) {
		this.floorName = floorName;
		this.layerId = layerId;
	}

	public String getFloorName() {
		return floorName;
	}

	public void setFloorName(String floorName) {
		this.floorName = floorName;
	}

	public int getLayerId() {
		return layerId;
	}

	public void setLayerId(int layerId) {
		this.layerId = layerId;
	}
}

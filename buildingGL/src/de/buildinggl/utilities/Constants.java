package de.buildinggl.utilities;
public class Constants {
    public static final int BYTES_PER_FLOAT = 4;
    public static final int POSITION_COMPONENT_COUNT = 3; //x,y,z
    public static final int COLOR_COMPONENT_COUNT = 4; //r,g,b,a  
    public static final int TEXTURE_COORDINATES_COMPONENT_COUNT = 2; //x,y   
    public static final int POSITION_TEXTURE_STRIDE = (POSITION_COMPONENT_COUNT 
    + TEXTURE_COORDINATES_COMPONENT_COUNT) * BYTES_PER_FLOAT;
    public static final int POSITION_COLOR_STRIDE = (POSITION_COMPONENT_COUNT 
    + COLOR_COMPONENT_COUNT) * BYTES_PER_FLOAT;
}

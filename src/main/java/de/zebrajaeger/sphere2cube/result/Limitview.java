package de.zebrajaeger.sphere2cube.result;

/**
 * https://krpano.com/docu/xml/#view.limitview
  */
public enum Limitview {
    /**
     * no limiting at all
     */
    OFF("off "),
    /**
     * automatic limiting (default)
     */
    AUTO("auto"),
    /**
     * limit the lookat variables direct to "hlookatmin", "hlookatmax", "vlookatmin", "vlookatmax"
     */
    LOOKAT("lookat"),
    /**
     *  limit to the area set by "hlookatmin", "hlookatmax", "vlookatmin", "vlookatmax", allow viewing only INSIDE this range
     */
    RANGE("range"),
    /**
     * limit to the area set by "hlookatmin", "hlookatmax", "vlookatmin", "vlookatmax", but allow zooming out to see the whole image (usefull for FLAT panos!)
     */
    FULLRANGE("fullrange"),
    /**
     * limit to the area set by "hlookatmin", "hlookatmax", "vlookatmin", "vlookatmax", but don't limit the zooming in any way.
     */
    OFFRANGE("offrange")
    ;

    private String name;

    Limitview(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }}

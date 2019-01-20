# Sphere2Cube

~~Today more a sandbox than a project~~

## Based on
* https://stackoverflow.com/questions/29678510/convert-21-equirectangular-panorama-to-cube-map 

## Find some equirectangular images for tests

* http://photopin.com/free-photos/equirectangular

## Description

### JSON
imagename - extension + ".json"

    {
      "title": "foo",
      "description": "bar",
      "created": "22.02.2019 10:33",
      "tags": [
        "urlaub",
        "blah",
        "meep",
        "narf"
      ]
    }

### XML
imagename - extension + ".xml"

    <description>
        <title>foo</title>
        <description>foo</description>
        <created>22.02.2019 10:33</created>
        <tags>
            <tag>urlaub</tag>
            <tag>blah</tag>
            <tag>meep</tag>
            <tag>narf</tag>
        </tags>
    </description>


### YAML
imagename - extension + ".yaml"

    title: foo
    description: bar
    created: 22.02.2019 10:33
    tags:
      - urlaub
      - blah
      - meep
      - narf
      
## TODO (top items are more important)
*~~~Zip Result folder~~~

*~~~Create surrounding border of black tiles instead of all or nothing (configurable)~~~ PWA instead

*~~~Check whole border of every tile instead of four corner edges only. (As configurable Option)~~~ PWA instead

* Progressive Web App to create black images on the fly instead deliver it via web server
   * ~~~File to identify black images~~~
  
  * ~~~Black image references (all needed sizes)~~~
  
  * PWA stuff
  
* ~~Autopano Giga PSD/PSB info reader~~

* ~~Pannellum output (TileNameGenerator, HtmlGenerator, ConfigGenerator)~~
  * https://pannellum.org/documentation/reference/ 
  
* krPano preview (cubic preview)
  * https://krpano.com/docu/xml/#preview
  
* ~~Better preview scaling algorithm (Lanczos3)~~ 
  * https://stackoverflow.com/questions/24745147/java-resize-image-without-losing-quality
  * ~~For pano~~
  * ~~For preview~~ 
  
* ~~~File which contains information which image has content and which is only a black dummy (and Size)~~~
  * Prevents to keep all the empty image tiles in partial panoramas (with a httpserver that can handle this)
  * Generator for all dummy-Image sizes 
   
* ~~As Maven Plugin~~

* CLI Interface

* UI (Simple Droptarget)

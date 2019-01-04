package de.zebrajaeger.sphere2cube.tile;

import de.zebrajaeger.sphere2cube.Face;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class KrPanoTileNameGeneratorTest {

    @Test
    public void testFace() {
        KrPanoTileNameGenerator g = KrPanoTileNameGenerator.of("aaa%xzzz");
        assertEquals("aaa0zzz", g.generateName(Face.BACK, 0, 0, 0));
        assertEquals("aaa666zzz", g.generateName(Face.BACK, 0, 666, 0));
    }

    @Test
    public void testLayer() {
        KrPanoTileNameGenerator g = KrPanoTileNameGenerator.of("aaa%lzzz");
        assertEquals("aaa0zzz", g.generateName(Face.BACK, 0, 0, 0));
        assertEquals("aaa666zzz", g.generateName(Face.BACK, 666, 0, 0));
    }

    @Test
    public void testHIndex() {
        KrPanoTileNameGenerator g1 = KrPanoTileNameGenerator.of("aaa%hzzz");
        assertEquals("aaa0zzz", g1.generateName(Face.BACK, 0, 0, 0));
        assertEquals("aaa666zzz", g1.generateName(Face.BACK, 0, 666, 0));

        KrPanoTileNameGenerator g2 = KrPanoTileNameGenerator.of("aaa%xzzz");
        assertEquals("aaa0zzz", g2.generateName(Face.BACK, 0, 0, 0));
        assertEquals("aaa666zzz", g2.generateName(Face.BACK, 0, 666, 0));

        KrPanoTileNameGenerator g3 = KrPanoTileNameGenerator.of("aaa%czzz");
        assertEquals("aaa0zzz", g3.generateName(Face.BACK, 0, 0, 0));
        assertEquals("aaa666zzz", g3.generateName(Face.BACK, 0, 666, 0));

        KrPanoTileNameGenerator g4 = KrPanoTileNameGenerator.of("aaa%uzzz");
        assertEquals("aaa0zzz", g4.generateName(Face.BACK, 0, 0, 0));
        assertEquals("aaa666zzz", g4.generateName(Face.BACK, 0, 666, 0));
    }

    @Test
    public void testVIndex() {
        KrPanoTileNameGenerator g1 = KrPanoTileNameGenerator.of("aaa%vzzz");
        assertEquals("aaa0zzz", g1.generateName(Face.BACK, 0, 0, 0));
        assertEquals("aaa666zzz", g1.generateName(Face.BACK, 0, 0, 666));

        KrPanoTileNameGenerator g2 = KrPanoTileNameGenerator.of("aaa%yzzz");
        assertEquals("aaa0zzz", g2.generateName(Face.BACK, 0, 0, 0));
        assertEquals("aaa666zzz", g2.generateName(Face.BACK, 0, 0, 666));

        KrPanoTileNameGenerator g3 = KrPanoTileNameGenerator.of("aaa%vzzz");
        assertEquals("aaa0zzz", g3.generateName(Face.BACK, 0, 0, 0));
        assertEquals("aaa666zzz", g3.generateName(Face.BACK, 0, 0, 666));

        KrPanoTileNameGenerator g4 = KrPanoTileNameGenerator.of("aaa%000vzzz");
        assertEquals("aaa000zzz", g4.generateName("aaa%000vzzz", Face.BACK, 0, 0, 0));

        KrPanoTileNameGenerator g5 = KrPanoTileNameGenerator.of("aaa%00000vzzz");
        assertEquals("aaa00666zzz", g5.generateName(Face.BACK, 0, 0, 666));
    }

    @Test
    public void testCombination() {
        KrPanoTileNameGenerator g = KrPanoTileNameGenerator.of("l%l_%s(%000xx%000y).jpg");
        assertEquals("l6_b(017x002).jpg", g.generateName(Face.BACK, 6, 17, 2));
    }
}
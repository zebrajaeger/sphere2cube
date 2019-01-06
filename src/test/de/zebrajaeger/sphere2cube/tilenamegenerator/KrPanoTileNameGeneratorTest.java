package de.zebrajaeger.sphere2cube.tilenamegenerator;

import de.zebrajaeger.sphere2cube.converter.Face;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GenericTileNameGeneratorTest {

    @Test
    public void testFace() {
        GenericTileNameGenerator g = GenericTileNameGenerator.of("aaa%xzzz");
        assertEquals("aaa0zzz", g.generateName(Face.BACK, 0, 0, 0, 0, 0));
        assertEquals("aaa666zzz", g.generateName(Face.BACK, 0, 0, 666, 0, 0));
    }

    @Test
    public void testLayer() {
        GenericTileNameGenerator g = GenericTileNameGenerator.of("aaa%lzzz");
        assertEquals("aaa0zzz", g.generateName(Face.BACK, 0, 0, 0, 0, 0));
        assertEquals("aaa666zzz", g.generateName(Face.BACK, 666, 0, 0, 0, 0));
    }

    @Test
    public void testHIndex() {
        GenericTileNameGenerator g1 = GenericTileNameGenerator.of("aaa%hzzz");
        assertEquals("aaa0zzz", g1.generateName(Face.BACK, 0, 0, 0, 0, 0));
        assertEquals("aaa666zzz", g1.generateName(Face.BACK, 0, 0, 666, 0, 0));
        assertEquals("aaa00zzz", g1.generateName(Face.BACK, 0, 11, 0, 0, 0));
        assertEquals("aaa000zzz", g1.generateName(Face.BACK, 0, 101, 0, 0, 0));
        assertEquals("aaa066zzz", g1.generateName(Face.BACK, 0, 101, 66, 0, 0));
        assertEquals("aaa666zzz", g1.generateName(Face.BACK, 0, 101, 666, 0, 0));
        assertEquals("aaa6666zzz", g1.generateName(Face.BACK, 0, 101, 6666, 0, 0));

        GenericTileNameGenerator g2 = GenericTileNameGenerator.of("aaa%xzzz");
        assertEquals("aaa0zzz", g2.generateName(Face.BACK, 0, 0, 0, 0, 0));
        assertEquals("aaa666zzz", g2.generateName(Face.BACK, 0, 0, 666, 0, 0));

        GenericTileNameGenerator g3 = GenericTileNameGenerator.of("aaa%czzz");
        assertEquals("aaa0zzz", g3.generateName(Face.BACK, 0, 0, 0, 0, 0));
        assertEquals("aaa666zzz", g3.generateName(Face.BACK, 0, 0, 666, 0, 0));

        GenericTileNameGenerator g4 = GenericTileNameGenerator.of("aaa%uzzz");
        assertEquals("aaa0zzz", g4.generateName(Face.BACK, 0, 0, 0, 0, 0));
        assertEquals("aaa666zzz", g4.generateName(Face.BACK, 0, 0, 666, 0, 0));
    }

    @Test
    public void testVIndex() {
        GenericTileNameGenerator g1 = GenericTileNameGenerator.of("aaa%vzzz");
        assertEquals("aaa0zzz", g1.generateName(Face.BACK, 0, 0, 0, 0, 0));
        assertEquals("aaa666zzz", g1.generateName(Face.BACK, 0, 0, 0, 0, 666));

        GenericTileNameGenerator g2 = GenericTileNameGenerator.of("aaa%yzzz");
        assertEquals("aaa0zzz", g2.generateName(Face.BACK, 0, 0, 0, 0, 0));
        assertEquals("aaa666zzz", g2.generateName(Face.BACK, 0, 0, 0, 0, 666));

        GenericTileNameGenerator g3 = GenericTileNameGenerator.of("aaa%vzzz");
        assertEquals("aaa0zzz", g3.generateName(Face.BACK, 0, 0, 0, 0, 0));
        assertEquals("aaa666zzz", g3.generateName(Face.BACK, 0, 0, 0, 0, 666));

        GenericTileNameGenerator g4 = GenericTileNameGenerator.of("aaa%000vzzz");
        assertEquals("aaa0000zzz", g4.generateName("aaa%000vzzz", Face.BACK, 0, 0, 0, 0, 0));

        GenericTileNameGenerator g5 = GenericTileNameGenerator.of("aaa%00000vzzz");
        assertEquals("aaa000666zzz", g5.generateName(Face.BACK, 0, 0, 0, 0, 666));
    }

    @Test
    public void testCombination() {
        GenericTileNameGenerator g = GenericTileNameGenerator.of("l%l_%s(%xx%y).jpg");
        assertEquals("l6_b(017x002).jpg", g.generateName(Face.BACK, 6, 101, 17, 101, 2));
    }
}
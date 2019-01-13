package de.zebrajaeger.sphere2cube.indexhtml;

import org.junit.jupiter.api.Test;

import java.io.IOException;

class IndexHtmGeneratorTest {
    @Test
    public void test1() throws IOException {
        String result = IndexHtmGeneratorKrPano.of().generate(new IndexHtmGeneratorKrPano.IndexHtml("Test1"));
        System.out.println(result);
    }
}